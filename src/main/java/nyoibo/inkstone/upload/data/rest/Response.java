package nyoibo.inkstone.upload.data.rest;

import org.springframework.util.StringUtils;

/**
 * <p>Title:Response.java</p>  
 * <p>Description: </p>  
 * <p>Copyright: Copyright (c) 2019</p>  
 * <p>Company: www.frankdevhub.site</p>
 * <p>github: https://github.com/frankdevhub</p>  
 * @author frankdevhub   
 * @date:2019-04-20 22:36
 */

public class Response<T> {
	private String message;
	private T data;
	private String status;

	public String getStatus() {
		return this.status;
	}

	public Response<T> setStatus(String status) {
		this.status = status;
		return this;
	}

	public T getData() {
		return this.data;
	}

	public Response<T> setData(T data) {
		this.data = data;
		return this;
	}

	public String getMessage() {
		return this.message;
	}

	public Response<T> setMessage(String message) {
		this.message = message;
		return this;
	}

	public Boolean isSuccess() {
		if (StringUtils.isEmpty(getStatus())) {
			return Boolean.FALSE;
		}
		if (getStatus().startsWith("2")) {
			return Boolean.TRUE;
		}
		return Boolean.FALSE;
	}

	public Boolean isFailed() {
		return Boolean.valueOf(!isSuccess().booleanValue());
	}

	public Response<T> success() {
		setStatus("200");
		return this;
	}

	public Response<T> failed() {
		setStatus("400");
		return this;
	}
}
