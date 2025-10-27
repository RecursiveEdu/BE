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

public class FieldError implements Serializable{

	
	public static class Builder {
		private String fieldName;
		private String message;
		
		
		private Builder withFiledName(String val) {
			this.fieldName = val;
			return this;
		}
		
		private Builder withMessage(String val) {
			this.message = val;
			return this;
		}
		
		private FieldError build() {
			return new FieldError(this);
		}
	}
	
	private static final long serialVersionUID = -56568767866712470L;
	private String fieldName;
	private String message;
	
	private FieldError(Builder builder) {
		this.fieldName = builder.fieldName;
		this.message = builder.message;
	}
	
	public static Builder newInstance() {
		return new Builder();
	}
	
	public static FieldError createInstance(String fieldName, String message) {
		return FieldError.newInstance().withFiledName(fieldName).withMessage(message).build();
	}

	public String getFieldName() {
		return fieldName;
	}

	public String getMessage() {
		return message;
	}

	@Override
	public String toString() {
		return new StringJoiner(", ", FieldError.class.getSimpleName() + "[", "]")
				.add("fieldName='" + fieldName + "'")
				.add("message='" + message + "'")
				.toString();
	}
}
