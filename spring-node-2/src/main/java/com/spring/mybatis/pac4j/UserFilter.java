package com.spring.mybatis.pac4j;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.web.util.WebUtils;
import org.pac4j.core.context.Pac4jConstants;

public class UserFilter extends org.apache.shiro.web.filter.authc.UserFilter {
	@Override
	protected void saveRequest(ServletRequest request) {
		super.saveRequest(request);
		//save to pac4j
		Subject subject = SecurityUtils.getSubject();
        Session session = subject.getSession();
        HttpServletRequest httpRequest = WebUtils.toHttp(request);
        session.setAttribute(Pac4jConstants.REQUESTED_URL, httpRequest.getRequestURI());
	}
}
