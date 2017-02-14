package com.landmaster.springboot;

import javax.servlet.http.HttpServletRequest;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.http.HttpServletRequestWrapper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.client.RestTemplate;

import java.util.Map;
import java.util.Set;

public class AuthFilter extends ZuulFilter {
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
    private static Logger log = LoggerFactory.getLogger(AuthFilter.class);

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
        ObjectMapper mapper = new ObjectMapper();
        RequestContext ctx = RequestContext.getCurrentContext();
        HttpServletRequest request = ctx.getRequest();
        String token=request.getParameter("access_token");
        if(token==null){token="";}
        log.info("====token:"+token);
        String srcurl = getUrlPartten(request);
        /*
        data={cuUser={guid=70f3db16f874466d99844177fcd2fd46, medguid=145821f0c3ff44a692393a31c3bb3014,
        logonuser=18600261713, pwd=pbfGX7ezR9o=, realname=樊明, helpcode=FM, sex=1, mobiletel=18600261713,
           source=PC, userType=H, userName=樊明, userId=70f3db16f874466d99844177fcd2fd46}, senData=null}
         */
        Map checkResult=sampleClient.isPermitted(token,srcurl);
        String code= checkResult.get("errorCode").toString();
        log.info("====================check auth end :"+checkResult);
        if(!code.equals("0")){
            String message= null;
            try {
                setFailedRequest(mapper.writeValueAsString(checkResult),Integer.parseInt(code));
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
            return null;
        }
        Map data=(Map) checkResult.get("data");
        try {
            Map userMap=createUserMap(data);
            ctx.addZuulRequestHeader("action_user",mapper.writeValueAsString(userMap));
            ctx.addZuulRequestHeader("action_user_name",data.get("userName").toString());
            ctx.addZuulRequestHeader("action_source",data.get("source").toString());
            ctx.addZuulRequestHeader("action_user_type",data.get("userType").toString());
            ctx.addZuulRequestHeader("action_user_id",data.get("userId").toString());
        } catch (Exception e) {
            e.printStackTrace();
            log.info("====================error :"+e.getMessage());
        }
        log.info(String.format("============================%s request to %s", request.getMethod(), request.getRequestURL().toString()));

        return null;
    }

    private String getUrlPartten(HttpServletRequest request) {
        String srcurl = request.getRequestURI().substring(request.getContextPath().length());
        srcurl=srcurl.replaceAll("^/\\w+/?(.*)","/$1");
        log.info("-===srurl:"+srcurl);
        return srcurl;
    }

    private Map createUserMap(Map checkResult) {
        return (Map) checkResult.get("cuUser");
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
