package com.spring.mybatis.controller;

import java.io.IOException;
import java.io.PrintWriter;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.PrincipalCollection;
import org.pac4j.cas.profile.CasProxyProfile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.spring.mybatis.service.UserService;

import io.buji.pac4j.subject.Pac4jPrincipal;

@Controller
@RequestMapping("/users")
public class UserController {
	
	private static final Logger logger = LoggerFactory.getLogger(UserController.class);
	
	@Resource
	private UserService userService;
	
	@RequestMapping("/loginSuccess")
	public String loginSuccess(){
		
		logger.info("登录成功");
		
		return "../index";
	}
	
	@RequestMapping("/callbyproxy")
    public void makeRequest(HttpServletResponse response) throws IOException {
        HttpClient client = new HttpClient();
        String targetUrl = "http://localhost:9090/node2/users/secureRequest";
        PrincipalCollection principals = SecurityUtils.getSubject().getPrincipals(); 
        Pac4jPrincipal principal =  principals.byType(Pac4jPrincipal.class).iterator().next(); 
        CasProxyProfile profile = (CasProxyProfile)principal.getProfile();
        String ticket = profile.getProxyTicketFor(targetUrl); 
        HttpMethod m = new GetMethod(targetUrl+"?ticket=" + ticket);
        m.setFollowRedirects(false);
        client.executeMethod(m);

        PrintWriter out = response.getWriter();
        renderResponse(m, out);
    }

    private void renderResponse(HttpMethod m, PrintWriter out) throws IOException {
        out.println("Response For: " + m.getPath());
        out.println("Response Code: " +  m.getStatusCode());
        out.println("Reason Phrase: " +  m.getStatusLine().getReasonPhrase());
        out.println("Response Headers:");
        for(Header h: m.getResponseHeaders()) {
            out.println("\t" + h.getName() + ":" + h.getValue());
        }
        out.println("Body: " +  m.getResponseBodyAsString());
        out.println();
        out.println();
        out.println();
    }

}
