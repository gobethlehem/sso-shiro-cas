/**
 * Copyright (c) Windliven 2016 All Rights Reserved
 *
 * @author railway
 * @date 2017年1月19日 下午7:06:11
 * @since V1.0.0
 */
package com.spring.mybatis.pac4j;

import javax.annotation.Resource;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.web.filter.authc.LogoutFilter;
import org.pac4j.cas.client.rest.AbstractCasRestClient;
import org.pac4j.cas.profile.CasRestProfile;
import org.pac4j.core.profile.CommonProfile;

import io.buji.pac4j.subject.Pac4jPrincipal;

/**
 * TODO
 *
 * @author railway
 * @date 2017年1月19日 下午7:06:11
 *
 */
public class ShiroLogoutFilter extends LogoutFilter {
	@Resource
	private AbstractCasRestClient casRestClient;

    @Override
    protected boolean preHandle(ServletRequest request, ServletResponse response) throws Exception {
        //rest登出
        PrincipalCollection principals = SecurityUtils.getSubject().getPrincipals(); 
        Pac4jPrincipal principal =  principals.byType(Pac4jPrincipal.class).iterator().next(); 
        CommonProfile profile = principal.getProfile();
        if(profile instanceof CasRestProfile){
        	this.casRestClient.destroyTicketGrantingTicket((CasRestProfile)profile);
        }
        //
        return super.preHandle(request, response);
    }
}
