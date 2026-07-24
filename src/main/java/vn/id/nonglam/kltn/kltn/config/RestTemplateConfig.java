package vn.id.nonglam.kltn.kltn.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

@Configuration
public class RestTemplateConfig {

    @Bean
    public RestTemplate restTemplate() {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        // Thời gian tối đa để thiết lập kết nối (Đơn vị: mili-giây -> 10000ms = 10 giây)
        factory.setConnectTimeout(10000);
        // Thời gian tối đa chờ Python chạy xong model (Đơn vị: mili-giây -> 60000ms = 60 giây)
        factory.setReadTimeout(120000);
        return new RestTemplate(factory);
    }
}