package pe.edu.pucp.dovah.asignaciones.service;

import org.apache.http.client.utils.URIBuilder;
import org.apache.tika.Tika;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import pe.edu.pucp.dovah.asignaciones.model.Documento;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Objects;

@Service
public class S3Service {
    @Value("${AWS_ACCESS_KEY}")
    private String accesKey;

    @Value("${AWS_SECRET_ACCESS_KEY}")
    private String secretKey;

    @Value("${AWS_ENDPOINT_URL}")
    private String endpointUrl;

    @Value("${AWS_S3_BUCKET}")
    private String bucketName;

    @Value("${spring.profiles.active}")
    private String activeProfile;

    @Value("${base-url}")
    private String baseUrl;

    private final static Logger log = LoggerFactory.getLogger(S3Service.class);
    private final static Tika tika = new Tika();

    private S3Client getS3Client() throws URISyntaxException {
        var builder = S3Client.builder()
                .region(Region.US_EAST_1)
                .forcePathStyle(true);
        if (Objects.equals(activeProfile, "dev")) {
            var creds = AwsBasicCredentials.create(accesKey, secretKey);
            builder.endpointOverride(new URI(endpointUrl))
                    .credentialsProvider(StaticCredentialsProvider.create(creds));
        }
        return builder.build();
    }

    public byte[] getObjet(Documento doc) throws IOException, URISyntaxException {
        var s3 = getS3Client();
        var request = GetObjectRequest.builder()
                .bucket(bucketName)
                .key(String.format("%s/%s", doc.getUuid(), doc.getNombre()))
                .build();
        return s3.getObject(request).readAllBytes();
    }

    public void putObjet(MultipartFile file, Documento doc) throws IOException, URISyntaxException {
        log.info("S3 insertando archivo " + doc.getNombre());
        var s3 = getS3Client();
        var request = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(String.format("%s/%s", doc.getUuid(), doc.getNombre()))
                .build();
        s3.putObject(request, RequestBody.fromBytes(file.getBytes()));
        doc.setMediaType(tika.detect(file.getBytes()));
        URIBuilder builder = new URIBuilder(baseUrl);
        builder.setPath(String.format("/api/v1/documento/blob/%s/%s", doc.getUuid(), doc.getNombre()));
        doc.setUrl(builder.toString());
    }
}
