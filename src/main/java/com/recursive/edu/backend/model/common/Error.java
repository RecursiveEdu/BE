/**
 * Copyright (c) 2025 Recursive Education. All rights reserved.
 */
package com.recursive.edu.backend.model.common;

import java.io.Serializable;
import java.util.StringJoiner;

/**
 * @author PrantikGuha
 * CreatedAt: {27-10-2025}
 */

public class Error implements Serializable {

	public static class Builder {
		private String message;
		
		private Builder withMessage(String val) {
			this.message = val;
			return this;
		}
		
		private Error build() {
			return new Error(this);
		}
	}
	private static final long serialVersionUID = -8778879886565865L;
	private String message;
	
	private Error(Builder builder) {
		this.message = builder.message;
	}
	
	public static Builder newInstance() {
		return new Builder();
	}
	
	public static Error createInstance(String msg) {
		return Error.newInstance().withMessage(msg).build();
	}

	public String getMessage() {
		return message;
	}

	@Override
	public String toString() {
		return new StringJoiner(", ", Error.class.getSimpleName() + "[", "]")
				.add("message='" + message + "'")
				.toString();
	}
}
