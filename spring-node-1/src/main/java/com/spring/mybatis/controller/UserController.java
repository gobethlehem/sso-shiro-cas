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
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.StringRequestEntity;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.PrincipalCollection;
import org.pac4j.cas.client.rest.AbstractCasRestClient;
import org.pac4j.cas.client.rest.CasRestFormClient;
import org.pac4j.cas.profile.CasProxyProfile;
import org.pac4j.cas.profile.CasRestProfile;
import org.pac4j.core.credentials.TokenCredentials;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.spring.mybatis.model.Response;
import com.spring.mybatis.model.User;
import com.spring.mybatis.service.UserService;

import io.buji.pac4j.subject.Pac4jPrincipal;

@Controller
@RequestMapping("/users")
public class UserController {
	
	private static final Logger logger = LoggerFactory.getLogger(UserController.class);
	
	@Resource
	private UserService userService;
	
	@Resource
	private AbstractCasRestClient casRestClient;
	
	
	/**
	 * cas rest 登录接口
	 * @return
	 */
	@RequestMapping(value="/login",method=RequestMethod.GET)
	public @ResponseBody Response<String> login(){
		String sessionId = this.getSessionId();
		PrincipalCollection principals = SecurityUtils.getSubject().getPrincipals(); 
        Pac4jPrincipal principal =  principals.byType(Pac4jPrincipal.class).iterator().next(); 
        CasRestProfile profile = (CasRestProfile)principal.getProfile();
        String tgt = profile.getTicketGrantingTicketId();
        String ticketUrlSt = this._getTicket("ticket");
        return new Response<String>("0","sessionId="+sessionId+", TGT="+tgt+", ST="+ticketUrlSt);
	}
	
	@RequestMapping(value="/ticket",method=RequestMethod.GET)
	public @ResponseBody Response<String> getTicket(String service){
		String sessionId = this.getSessionId();
		if(StringUtils.isEmpty(service)){
			return new Response<String>("1","sessionId="+sessionId+", serviceUrl=null");
		}
		String st = this._getTicket(service);
        return new Response<String>("0","sessionId="+sessionId+", serviceUrl=https://sso.lifengyun.com:9443/node1/users/"+service+"?ticket="+st);
	}
	
	private String _getTicket(String service){
		PrincipalCollection principals = SecurityUtils.getSubject().getPrincipals(); 
        Pac4jPrincipal principal =  principals.byType(Pac4jPrincipal.class).iterator().next(); 
        CasRestProfile profile = (CasRestProfile)principal.getProfile();
        TokenCredentials token = casRestClient.requestServiceTicket("https://sso.lifengyun.com:9443/node1/users/"+service, profile);
        String st = token.getToken();
        return st;
	}
	
	private String getSessionId(){
		Session session = SecurityUtils.getSubject().getSession();
		return (String)session.getId();
	}
	
	@RequestMapping("/loginSuccess")
	public String loginSuccess(){
		
		logger.info("登录成功");
		
		return "index";
	}
	
	@RequestMapping("/postbyproxy")
    public void makeRequest(HttpServletResponse response) throws IOException {
        HttpClient client = new HttpClient();
        
        //call by proxy
        String targetUrl = "https://sso.lifengyun.com:9443/node2/users/secureRequest";
        PrincipalCollection principals = SecurityUtils.getSubject().getPrincipals(); 
        Pac4jPrincipal principal =  principals.byType(Pac4jPrincipal.class).iterator().next(); 
        CasProxyProfile profile = (CasProxyProfile)principal.getProfile();
        String ticket = profile.getProxyTicketFor(targetUrl); 
        PostMethod m = new PostMethod(targetUrl+"?ticket=" + ticket);
        String json = "{\"id\":1111,\"username\":\"yuhao\"}";
        m.setRequestEntity(new StringRequestEntity(json,"application/json", "UTF-8"));
        m.setFollowRedirects(false);
        client.executeMethod(m);
        PrintWriter out = response.getWriter();
        renderResponse(m, out);
    }
	
	@RequestMapping("/getbyproxy")
    public void makeDRequest(HttpServletResponse response) throws IOException {
        HttpClient client = new HttpClient();
        String targetUrl = "http://localhost:9090/node2/users/getSecureRequest";
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
