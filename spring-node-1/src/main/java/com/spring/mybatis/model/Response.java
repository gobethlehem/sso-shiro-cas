package com.spring.mybatis.model;

public class Response<T> {
	private String code;
	private T data;
	
	public Response(String code, T data) {
		super();
		this.code = code;
		this.data = data;
	}
	
	public String getCode() {
		return code;
	}
	public T getData() {
		return data;
	}

	

}
