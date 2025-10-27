/**
 * Copyright (c) 2025 Recursive Education. All rights reserved.
 */
package com.recursive.edu.backend.model.common;

import java.io.Serializable;
import java.util.List;
import java.util.StringJoiner;

/**
 * @author PrantikGuha
 * CreatedAt: {27-10-2025}
 */

public class ApiResult<T> implements Serializable{

	public static class Builder<T> {
		private List<FieldError> filedErrors;
		private T payload;
		private List<Error> errors;
		private String status;
		
		public Builder withPayload(T val) {
			this.payload = val;
			return this;
		}
		
		public Builder withFiledErrors(List<FieldError> val) {
			this.filedErrors = val;
			return this;
		}
		
		public Builder withErrors(List<Error> val) {
			this.errors = val;
			return this;
		}

		public Builder withStatus(String status) {
			this.status = status;
			return this;
		}

		public ApiResult<T> build() {
			return new ApiResult<T>(this);
		}
	}
	private static final long serialVersionUID = -1812400454603998286L;
	private List<FieldError> filedErrors;
	private T payload;
	private List<Error> errors;
	private String status;
	
	private ApiResult(Builder<T> builder) {
		this.errors = builder.errors;
		this.payload = builder.payload;
		this.filedErrors = builder.filedErrors;
		this.status = builder.status;
	}
	
	public static <T> Builder<T> newInstance() {
		return new Builder();
	}

	public List<FieldError> getFiledErrors() {
		return filedErrors;
	}

	public T getPayload() {
		return payload;
	}

	public List<Error> getErrors() {
		return errors;
	}

	public String getStatus() {
		return status;
	}

	@Override
	public String toString() {
		return new StringJoiner(", ", ApiResult.class.getSimpleName() + "[", "]")
				.add("filedErrors=" + filedErrors)
				.add("payload=" + payload)
				.add("errors=" + errors)
				.add("status=" + status)
				.toString();
	}
}
