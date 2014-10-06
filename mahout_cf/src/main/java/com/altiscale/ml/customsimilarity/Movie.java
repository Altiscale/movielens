package com.altiscale.ml.customsimilarity;

public class Movie {
	private String title;
	private String actors;
	private String directors;
	private String locations;

	public Movie(String title, String actors, String directors, String locations) {
		// TODO Auto-generated constructor stub
		setTitle(title);
	}
	
	public String getTitle() {
		// TODO Auto-generated method stub
		return title;
	}

	public String getActors() {
		// TODO Auto-generated method stub
		return "";
	}

	public String getDirectors() {
		// TODO Auto-generated method stub
		return "";
	}

	public String getLocations() {
		// TODO Auto-generated method stub
		return "";
	}

	public void setTitle(String title) {
		this.title = title.toLowerCase();
	}

}
