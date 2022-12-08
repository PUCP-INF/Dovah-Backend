package pe.edu.pucp.dovah.auth;


import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import pe.edu.pucp.dovah.RRHH.repository.UsuarioRepository;

import java.util.HashSet;

@Service
public class JWTUserDetailsService implements UserDetailsService {
    private final UsuarioRepository usuarioRepository;

    public JWTUserDetailsService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String correo) throws UsernameNotFoundException {
        var usr = usuarioRepository.queryByCorreoAndActivoIsTrue(correo).orElseThrow();
        var authorities = new HashSet<SimpleGrantedAuthority>();
        authorities.add(new SimpleGrantedAuthority("ROLE_USUARIO"));
        for (var rol: usr.getListaRoles()) {
            authorities.add(new SimpleGrantedAuthority(String.format("ROLE_%s", rol.getNombre())));
        }
        return new User(correo, usr.getPassword(), authorities);
    }
}
