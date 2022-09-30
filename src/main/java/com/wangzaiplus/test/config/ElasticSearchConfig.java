package com.wangzaiplus.test.config;



import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;

/**
 * @author Mr.Xu
 * @Description TODO
 * @date 2021年09月15日 15:33
 */
@Configuration
@ConfigurationProperties(prefix = "elasticsearch")
public class ElasticSearchConfig {

    @Value("${elasticsearch.port}")
    private String port;

    @Value("${elasticsearch.ip}")
    private String ip;

    // 注册 rest高级客户端
    @Bean
    @ConditionalOnMissingBean
    public RestHighLevelClient restHighLevelClient(){
        RestHighLevelClient client = new RestHighLevelClient(
                RestClient.builder(
                        new HttpHost(ip, Integer.parseInt(port),"http")
                )
        );
        return client;
    }

}
