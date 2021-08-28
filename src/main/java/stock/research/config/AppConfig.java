package stock.research.config;

import com.github.benmanes.caffeine.cache.Caffeine;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.springframework.cache.CacheManager;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.retry.backoff.FixedBackOffPolicy;
import org.springframework.retry.policy.SimpleRetryPolicy;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.web.client.RestTemplate;

import javax.net.ssl.SSLContext;
import java.security.cert.X509Certificate;
import java.util.concurrent.TimeUnit;

@Configuration
public class AppConfig {

        @Bean
        public RestTemplate restTemplate() {
                try {
                        TrustStrategy acceptingTrustStrategy = (X509Certificate[] chain, String authType) -> true;
                        SSLContext sslContext;
                        sslContext = org.apache.http.ssl.SSLContexts.custom().loadTrustMaterial(null, acceptingTrustStrategy)
                                .build();
                        SSLConnectionSocketFactory csf = new SSLConnectionSocketFactory(sslContext);
                        CloseableHttpClient httpClient = HttpClients.custom().setSSLSocketFactory(csf).build();
                        HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory();
                        requestFactory.setHttpClient(httpClient);
                        requestFactory.setConnectionRequestTimeout(1000 * 5);
                        requestFactory.setConnectTimeout(1000 * 5);
                        requestFactory.setReadTimeout(1000 * 5);
                        RestTemplate restTemplate = new RestTemplate(requestFactory);
                        return restTemplate;

                } catch(Exception e) {
                        e.printStackTrace();
                }
                return null;
        }

        @Bean
        public RetryTemplate retryTemplate() {

                int maxAttempt = 10;
                int retryTimeInterval = 10000;

                SimpleRetryPolicy retryPolicy = new SimpleRetryPolicy();
                retryPolicy.setMaxAttempts(maxAttempt);

                FixedBackOffPolicy backOffPolicy = new FixedBackOffPolicy();
                backOffPolicy.setBackOffPeriod(retryTimeInterval); // 1.5 seconds

                RetryTemplate template = new RetryTemplate();
                template.setRetryPolicy(retryPolicy);
                template.setBackOffPolicy(backOffPolicy);

                return template;
        }
        @Bean
        public Caffeine caffeineConfig() {
                return Caffeine.newBuilder().expireAfterWrite(3, TimeUnit.HOURS);
        }

        @Bean
        public CacheManager cacheManager(Caffeine caffeine) {
                CaffeineCacheManager caffeineCacheManager = new CaffeineCacheManager();
                caffeineCacheManager.setCaffeine(caffeine);
                return caffeineCacheManager;
        }
}
