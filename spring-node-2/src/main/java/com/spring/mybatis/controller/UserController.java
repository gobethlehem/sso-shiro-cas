package com.spring.mybatis.controller;

import java.io.IOException;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.spring.mybatis.model.User;
import com.spring.mybatis.service.UserService;

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
	
	@RequestMapping(value="/getSecureRequest",method=RequestMethod.GET)
    public void mainRequest(HttpServletResponse response) throws IOException {
        response.setContentType("text/plain");
        response.getWriter().println("Hello from node2");
    }
	
	@RequestMapping(value="/secureRequest",method=RequestMethod.POST)
    public void mainRequest(@RequestBody User user , HttpServletResponse response) throws IOException {
        response.setContentType("text/plain");
        response.getWriter().println("Hello from node2: "+user.getUsername());
    }

}
