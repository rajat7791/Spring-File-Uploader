package org.webapp.service;

public class Message {
	
	private String message;
	private int id;
	
	@Override
	public String toString() {
		return "Message [message=" + message + ", id=" + id + "]";
	}
	
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	
	

}
