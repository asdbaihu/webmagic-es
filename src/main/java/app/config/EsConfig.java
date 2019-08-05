

package app.config;

import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.Credentials;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.elasticsearch.rest.RestClientBuilderCustomizer;
import org.springframework.boot.autoconfigure.elasticsearch.rest.RestClientProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.context.properties.PropertyMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * {@link EnableAutoConfiguration Auto-configuration} for Elasticsearch REST clients.
 *
 * @author Brian Clozel
 * @since 2.1.0
 */
@Configuration
@ConditionalOnClass(RestClient.class)
@EnableConfigurationProperties(RestClientProperties.class)
public class EsConfig {

    private final RestClientProperties properties;

    private final ObjectProvider<RestClientBuilderCustomizer> builderCustomizers;

    public EsConfig(RestClientProperties properties,
                    ObjectProvider<RestClientBuilderCustomizer> builderCustomizers) {
        this.properties = properties;
        this.builderCustomizers = builderCustomizers;
    }


    @Bean
    @ConditionalOnMissingBean
    public RestClientBuilder restClientBuilder() {
        HttpHost[] hosts = this.properties.getUris().stream().map(HttpHost::create).toArray(HttpHost[]::new);
        RestClientBuilder builder = RestClient.builder(hosts).setRequestConfigCallback(new RestClientBuilder.RequestConfigCallback() {
            @Override
            public RequestConfig.Builder customizeRequestConfig(RequestConfig.Builder requestConfigBuilder) {
                requestConfigBuilder.setConnectTimeout(5000);
                requestConfigBuilder.setSocketTimeout(5 * 60000);
                requestConfigBuilder.setConnectionRequestTimeout(1000);
                return requestConfigBuilder;
            }
        });
        PropertyMapper map = PropertyMapper.get();
        map.from(this.properties::getUsername).whenHasText().to((username) -> {
            CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
            Credentials credentials = new UsernamePasswordCredentials(this.properties.getUsername(),
                    this.properties.getPassword());
            credentialsProvider.setCredentials(AuthScope.ANY, credentials);
            builder.setHttpClientConfigCallback(
                    (httpClientBuilder) -> httpClientBuilder.setDefaultCredentialsProvider(credentialsProvider)
            );
        });
        this.builderCustomizers.orderedStream().forEach((customizer) -> customizer.customize(builder));
        return builder;
    }


}
