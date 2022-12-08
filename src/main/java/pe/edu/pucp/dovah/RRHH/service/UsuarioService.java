package pe.edu.pucp.dovah.RRHH.service;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.stereotype.Service;
import pe.edu.pucp.dovah.RRHH.exceptions.UsuarioNotFoundException;
import pe.edu.pucp.dovah.RRHH.model.Usuario;
import pe.edu.pucp.dovah.RRHH.repository.AlumnoRepository;
import pe.edu.pucp.dovah.RRHH.repository.ProfesorRepository;
import pe.edu.pucp.dovah.RRHH.repository.UsuarioRepository;
import pe.edu.pucp.dovah.Reglas.model.Rol;
import pe.edu.pucp.dovah.Reglas.repository.RolRepository;
import pe.edu.pucp.dovah.auth.JWTUtil;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class UsuarioService {
    private final UsuarioRepository usuarioRepository;
    private final JWTUtil jwtUtil;
    private final AuthenticationManager authenticationManager;
    private final PasswordEncoder passwordEncoder;
    private final RolRepository rolRepository;
    private final AlumnoRepository alumnoRepository;
    private final ProfesorRepository profesorRepository;
    private final static Logger log = LoggerFactory.getLogger(UsuarioService.class);

    public UsuarioService(UsuarioRepository usuarioRepository,
                          JWTUtil jwtUtil,
                          AuthenticationManager authenticationManager,
                          RolRepository rolRepository,
                          AlumnoRepository alumnoRepository,
                          ProfesorRepository profesorRepository,
                          PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.jwtUtil = jwtUtil;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.rolRepository = rolRepository;
        this.alumnoRepository = alumnoRepository;
        this.profesorRepository = profesorRepository;
    }

    public Usuario getLoggedInUser() {
        var principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String email;
        String tipoLogin = "user-password";
        if (principal instanceof DefaultOAuth2User oauth) {
            email = oauth.getAttributes().get("email").toString();
            tipoLogin = "google";
        } else {
            email = principal.toString();
        }
        Usuario usuario;
        var usr = usuarioRepository.queryByCorreoWithRoles(email);
        if (usr.isEmpty()) {
            usuario = new Usuario();
            usuario.setNombre("not-found");
            usuario.setCorreo(email);
        } else {
            usuario = usr.get();
            usuario.setTipoLogin(tipoLogin);
        }
        return usuario;
    }

    public Usuario newUser(Usuario usuario) {
        var usr = newSimpleUser(usuario);
        var cur = getLoggedInUser();
        usr.setFacultad(cur.getFacultad());
        usr.setEspecialidad(cur.getEspecialidad());
        return usuarioRepository.save(usr);
    }

    public Usuario newSimpleUser(Usuario usuario) {
        // evitar insertar 2 usuarios con mismo codigo y correo
        var tmp = usuarioRepository
                .queryByCodigoPUCPOrCorreo(usuario.getCodigoPUCP(), usuario.getCorreo());
        if (tmp.isPresent()) return tmp.get();
        if (Objects.equals(usuario.getPassword(), "")) {
            usuario.setPassword(passwordEncoder.encode(UUID.randomUUID().toString()));
        } else {
            usuario.setPassword(passwordEncoder.encode(usuario.getPassword()));
        }
        return usuarioRepository.save(usuario);
    }

    public Usuario modifyUser(JSONObject json) {
        int id = json.getInt("idUsuario");
        var usuario = usuarioRepository.findById(id).orElseThrow(()->new UsuarioNotFoundException(id));
        log.info(String.format("Modificando usuario con id '%d",usuario.getIdUsuario()));
        var passwd = json.getString("password");
        if (Objects.equals(passwd, "")) {
            usuario.setPassword(passwordEncoder.encode(UUID.randomUUID().toString()));
        } else {
            usuario.setPassword(passwordEncoder.encode(passwd));
        }
        usuario.setNombre(json.getString("nombre"));
        usuario.setApellido(json.getString("apellido"));
        usuario.setCorreo(json.getString("correo"));
        usuario.setCodigoPUCP(json.getString("codigoPUCP"));
        return usuarioRepository.save(usuario);
    }

    public void updateUserRoles(Usuario usuario) {
        var roles = new HashSet<Rol>();
        // mantener ciertos roles
        for (var rolStr: Arrays.asList("COORDINADOR", "ADMIN")) {
            var rol = rolRepository.queryByNombre(rolStr).orElseThrow();
            if (usuario.getListaRoles().contains(rol)) roles.add(rol);
        }
        var alumnos = alumnoRepository.queryByUsuarioAndActivoIsTrue(usuario);
        if (!alumnos.isEmpty()) roles.add(rolRepository.queryByNombre("ALUMNO").orElseThrow());
        var profs = profesorRepository.queryAllByUsuarioWithRoles(usuario);
        for (var prof: profs) roles.addAll(prof.getRoles());
        usuario.setListaRoles(roles);
        usuarioRepository.save(usuario);
    }

    public Map<String, Object> authenticateUser(String correo, String password) {
        try {
            var authenticationToken = new UsernamePasswordAuthenticationToken(correo, password);
            authenticationManager.authenticate(authenticationToken);
            String token = jwtUtil.generateToken(correo);
            var usr = usuarioRepository.queryByCorreoAndActivoIsTrue(correo).orElseThrow();
            usr.setLastLogin(Instant.now());
            usuarioRepository.save(usr);
            return Collections.singletonMap("jwt-token", token);
        } catch (AuthenticationException authExc) {
            throw new RuntimeException("Invalid Login Credentials");
        }
    }
}
