package com.robot.pool;

public class HttpClientResult {
	/**
	 * 状态码
	 */
	private int		code;

	/**
	 * 内容
	 */
	private String	content;
	
	public HttpClientResult() {
	}
	
	public HttpClientResult(int code) {
		this.code = code;
	}
	
	public HttpClientResult(int code, String content) {
		this.code = code;
		this.content = content;
	}

	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	@Override
	public String toString() {
		return "HttpClientResult [code=" + code + ", content=" + content + "]";
	}
	
}
