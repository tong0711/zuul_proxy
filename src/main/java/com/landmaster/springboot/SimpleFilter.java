package com.landmaster.springboot;

import javax.servlet.http.HttpServletRequest;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.ZuulFilter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.client.RestTemplate;

public class SimpleFilter extends ZuulFilter {
    /**
     *  RequestContext ctx = RequestContext.getCurrentContext();
     ctx.addZuulRequestHeader("Test", "TestSample");
     */
    @Autowired
    private RestTemplate restTemplate;
//    @Autowired
//    private App.SampleClient sampleClient;
    private static Logger log = LoggerFactory.getLogger(SimpleFilter.class);

    @Override
    public String filterType() {
        return "pre";
    }

    @Override
    public int filterOrder() {
        return 1;
    }

    @Override
    public boolean shouldFilter() {
        return true;
    }

    @Override
    public Object run() {
        RequestContext ctx = RequestContext.getCurrentContext();
        HttpServletRequest request = ctx.getRequest();
        ctx.addZuulRequestHeader("user","test_user");
        log.info("=========================== test feign");

       // log.info("==========================="+sampleClient.choose());
        //this location can  exec auth and add user info to header
        if(request.getRequestURL().toString().contains("ttt")){
            setFailedRequest("un auth", 401);
        }
        log.info(String.format("%s request to %s", request.getMethod(), request.getRequestURL().toString()));

        return null;
    }
    private void setFailedRequest(String body, int code) {
        log.debug("Reporting error ({}): {}", code, body);
        RequestContext ctx = RequestContext.getCurrentContext();
        ctx.setResponseStatusCode(code);
        //System.out.println(sampleClient.choose());

       // restTemplate.getForObject()
        if (ctx.getResponseBody() == null) {
            ctx.setResponseBody(body);
            ctx.setSendZuulResponse(false);
            throw new RuntimeException("Code: " + code + ", " + body); //optional
        }
    }


}
