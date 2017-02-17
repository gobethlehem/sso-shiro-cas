package com.spring.mybatis.realm;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import javax.annotation.Resource;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.subject.PrincipalCollection;
import com.spring.mybatis.model.User;
import com.spring.mybatis.service.RoleService;
import com.spring.mybatis.service.UserService;

import io.buji.pac4j.realm.Pac4jRealm;
import io.buji.pac4j.subject.Pac4jPrincipal;

/**
 * 用户授权信息域
 * 
 * @author coderhuang
 * 
 */
public class UserRealm extends Pac4jRealm {
	
	@Resource
	private RoleService roleService;

	@Resource
	private UserService userService;

	protected final Map<String, SimpleAuthorizationInfo> roles = new ConcurrentHashMap<String, SimpleAuthorizationInfo>();
	
	/**
	 * 设置角色和权限信息
	 */
	@Override
	protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {

		String account = (String) ((Pac4jPrincipal)principals.getPrimaryPrincipal()).getProfiles().get(0).getId();
		SimpleAuthorizationInfo authorizationInfo = null;
		if (authorizationInfo == null) {
			authorizationInfo = new SimpleAuthorizationInfo();
			authorizationInfo.addStringPermissions(roleService.getPermissions(account));
			authorizationInfo.addRoles(roleService.getRoles(account));
			roles.put(account, authorizationInfo);
		}

		return authorizationInfo;
	}
	
	
	/**
	 * 1、CAS认证 ,验证用户身份
	 * 2、将用户基本信息设置到会话中
	 */
	protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) {

		AuthenticationInfo authc = super.doGetAuthenticationInfo(token);

		String account = (String) ((Pac4jPrincipal)authc.getPrincipals().getPrimaryPrincipal()).getProfiles().get(0).getId();

		User user = userService.getUserByAccount(account);
		
		SecurityUtils.getSubject().getSession().setAttribute("user", user);

		return authc;
	}
	

}
