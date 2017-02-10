package com.landmaster.springboot;

import javax.servlet.http.HttpServletRequest;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.ZuulFilter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.Map;

public class SimpleFilter extends ZuulFilter {
    /**
     *  RequestContext ctx = RequestContext.getCurrentContext();
     ctx.addZuulRequestHeader("Test", "TestSample");
     */
    @Autowired
    private RestTemplate restTemplate;
    @Autowired
    private App.SampleClient sampleClient;
    @Autowired
    App.MysrpingClient mysrpingClient;
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
        System.out.println("-------------------------xxxxxxxxxxxxxxxxxxxxxxx---------------------------------------");
        ObjectMapper mapper = new ObjectMapper();
        RequestContext ctx = RequestContext.getCurrentContext();
        HttpServletRequest request = ctx.getRequest();
        //ctx.addZuulRequestHeader("user","test_user");
        String token=request.getParameter("access_token");
        if(token==null){token="";}
        log.info("====token:"+token);
        String srcurl = request.getRequestURI().substring(request.getContextPath().length());
        srcurl=srcurl.replaceAll("^/\\w+/?(.*)","/$1");
        log.info("-===srurl:"+srcurl);
        Map checkResult=sampleClient.isPermitted(token,srcurl);
        //String checkResult=sampleClient.isPermitted();
        log.info("==================== complete  ispemitted request"+checkResult);
        String code= checkResult.get("errorCode").toString();
        log.info("====================error code:"+code);
        if(!code.equals("0")){
            String message= null;
            try {
                setFailedRequest(mapper.writeValueAsString(checkResult),Integer.parseInt(code));
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
            return null;
        }
        try {
            ctx.addZuulRequestHeader("action_user",mapper.writeValueAsString(checkResult.get("data")));
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        log.info(String.format("%s request to %s", request.getMethod(), request.getRequestURL().toString()));

        return null;
    }
    private void setFailedRequest(String body, int code) {
        log.debug("Reporting error ({}): {}", code, body);
        RequestContext ctx = RequestContext.getCurrentContext();
        ctx.setResponseStatusCode(code);
        if (ctx.getResponseBody() == null) {
            ctx.setResponseBody(body);
            ctx.setSendZuulResponse(true);

            throw new RuntimeException("Code: " + code + ", " + body); //optional
        }
    }
    public static  void main(String[] args){
        String srcurl="/xx";
        srcurl=srcurl.replaceAll("^/\\w+/?(.*)","/$1");
        System.out.println(srcurl);
        srcurl="/xx/123";
        srcurl=srcurl.replaceAll("^/\\w+/?(.*)","/$1");
        System.out.println(srcurl);
    }

}
