package com.kozak.triangles.models;

public class Requirement {
	private boolean isCarriedOut; // выполнено
	private String description;

	public boolean isCarriedOut() {
		return isCarriedOut;
	}

	public void setCarriedOut(boolean isCarriedOut) {
		this.isCarriedOut = isCarriedOut;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
}
