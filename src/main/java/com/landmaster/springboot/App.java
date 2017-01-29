package com.landmaster.springboot;

import com.netflix.config.*;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.circuitbreaker.EnableCircuitBreaker;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.cloud.netflix.feign.EnableFeignClients;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.cloud.netflix.zuul.EnableZuulProxy;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

//@Controller
@EnableZuulProxy
@EnableAutoConfiguration
@SpringBootApplication
@EnableDiscoveryClient
@EnableCircuitBreaker
@EnableFeignClients
//@RestController
//EnableZuulProxy
//@SpringBootApplication
public class App {

    public static void main(String[] args) throws Exception {
//        PolledConfigurationSource source = new ConsulConfigurationSource("");
//        AbstractPollingScheduler scheduler = new FixedDelayPollingScheduler();
//        DynamicConfiguration configuration = new DynamicConfiguration(source, scheduler);
//        ConfigurationManager.install(configuration);
        SpringApplication.run(App.class, args);
    }
   @Bean
    public SimpleFilter simpleFilter() {
        return new SimpleFilter();
    }
    @Bean
    @LoadBalanced
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
    @FeignClient("myspringcloud")
    public interface SampleClient {

        @RequestMapping(value = "/", method = RequestMethod.GET)
        String choose();
    }

}