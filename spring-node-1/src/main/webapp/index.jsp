<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>首页</title>
</head>
<body>
	<h1>hello node1</h1>
	<shiro:hasRole name="管理员">
		<h2>管理员</h2>
	</shiro:hasRole>
	<shiro:hasPermission name="查询">
		<h2>查询</h2>
	</shiro:hasPermission>
	
	<a href="http://127.0.0.1:9090/node1/">节点1</a>
	
	<a href="http://127.0.0.1:9090/node1/users/postbyproxy">Proxy访问node2 POST请求</a>
	<a href="http://127.0.0.1:9090/node1/users/getbyproxy">Proxy访问node2 GET请求</a>
	
	<a href="http://127.0.0.1:9090/node2/">节点2</a>
	
	<a href="http://127.0.0.1:9090/node1/logout">单点登出</a>
</body>
</html>