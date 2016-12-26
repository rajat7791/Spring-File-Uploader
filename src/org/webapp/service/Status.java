package org.webapp.service;

import org.springframework.stereotype.Component;

@Component
public class Status {

	private String message;
	private boolean isStatusChanged;
	
	@Override
	public String toString() {
		return "Status [message=" + message + ", isStatusChanged=" + isStatusChanged + "]";
	}
	
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public boolean isStatusChanged() {
		return isStatusChanged;
	}
	public void setStatusChanged(boolean isStatusChanged) {
		this.isStatusChanged = isStatusChanged;
	}

	
}
