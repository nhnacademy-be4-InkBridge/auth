package com.nhnacademy.inkbridge.auth.config;

import com.nhnacademy.inkbridge.auth.dto.response.KeyResponseDto;
import com.nhnacademy.inkbridge.auth.exception.KeyManagerException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.util.List;
import java.util.Objects;
import javax.net.ssl.SSLContext;
import lombok.RequiredArgsConstructor;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContextBuilder;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

/**
 * class: KeyMangerConfig.
 *
 * @author devminseo
 * @version 3/26/24
 */
@ConstructorBinding
@RequiredArgsConstructor
@ConfigurationProperties(prefix = "secure-key-manager")
public class KeyMangerConfig {
    private  String password;
    private  String url;
    private  String path;
    private  String appKey;
    public String keyStore(String keyId) {
        try {
            KeyStore clientStore = KeyStore.getInstance("PKCS12");  // PKCS12형식의 키 저장소를 가져온다
            InputStream result = new ClassPathResource(
                    "inkBridge.p12").getInputStream(); // 클래스 경로에 있는 inkBridge.p12 파일 읽기, 인증서와 개인키를 포함
            clientStore.load(result, password.toCharArray()); // 키스토어에 인증서와 개인키를 로드

            SSLContext sslContext = SSLContextBuilder.create()  // SSL 통신 설정
                    .setProtocol("TLS")
                    .loadKeyMaterial(clientStore, password.toCharArray())
                    .loadTrustMaterial(new TrustSelfSignedStrategy())
                    .build();

            SSLConnectionSocketFactory sslConnectionSocketFactory = // SSL 소켓 팩토리 생성
                    new SSLConnectionSocketFactory(sslContext);
            CloseableHttpClient httpClient = HttpClients.custom()   //  SSL 소켓 팩토리로  HTTP 클라이언트 생성
                    .setSSLSocketFactory(sslConnectionSocketFactory)
                    .build();

            HttpComponentsClientHttpRequestFactory requestFactory =    // HTTP 클라이언트로 HTTP 요청 팩토리 생성
                    new HttpComponentsClientHttpRequestFactory(httpClient);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setAccept(List.of(MediaType.APPLICATION_JSON));

            RestTemplate restTemplate = new RestTemplate(requestFactory);

            URI uri = UriComponentsBuilder
                    .fromUriString(url)
                    .path(path)
                    .encode()
                    .build()
                    .expand(appKey, keyId)
                    .toUri();
            return Objects.requireNonNull(restTemplate.exchange(uri,
                                    HttpMethod.GET,
                                    new HttpEntity<>(headers),
                                    KeyResponseDto.class)
                            .getBody())
                    .getBody()
                    .getSecret();
        } catch (KeyStoreException | IOException | CertificateException
                 | NoSuchAlgorithmException
                 | UnrecoverableKeyException
                 | KeyManagementException e) {
            throw new KeyManagerException(e.getMessage());
        }
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getAppKey() {
        return appKey;
    }

    public void setAppKey(String appKey) {
        this.appKey = appKey;
    }
}
