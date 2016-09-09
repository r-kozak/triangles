package com.kozak.triangles.models;

public class Requirement {
	private boolean carriedOut; // выполнено
	private String description;

	public Requirement(boolean carriedOut, String description) {
		this.carriedOut = carriedOut;
		this.description = description;
	}

	public boolean isCarriedOut() {
		return carriedOut;
	}

	public void setCarriedOut(boolean carriedOut) {
		this.carriedOut = carriedOut;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

}
