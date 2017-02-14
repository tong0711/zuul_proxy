package com.landmaster.springboot;

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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

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
        SpringApplication.run(App.class, args);
    }
   @Bean
    public AuthFilter simpleFilter() {
        return new AuthFilter();
    }
    @Bean
    @LoadBalanced
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
    @FeignClient(name="publicapp")
    public interface SampleClient {
        //@RequestLine("POST /auth/token/isPermitted?access_token={access_token}&resource={resource}")
        @RequestMapping(value = "/auth/token/isPermitted", method = RequestMethod.POST)
        Map isPermitted(@RequestParam("access_token") String access_token, @RequestParam("resource")  String resource);
    }
    @FeignClient(name="myspring")
    public interface MysrpingClient {
        //@RequestLine("POST /auth/token/isPermitted?access_token={access_token}&resource={resource}")
        @RequestMapping(value = "/ttt", method = RequestMethod.GET ,consumes = "application/json")
        String ttt(@RequestParam("access_token") String access_token, @RequestParam("resource")  String resource);
    }

}