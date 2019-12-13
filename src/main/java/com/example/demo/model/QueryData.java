package com.example.demo.model;

public class QueryData 
{
	private String inputQuery;
	private Integer numTweets;
	
	public String getInputQuery() {
		return inputQuery;
	}
	
	public void setInputQuery(String inputQuery) {
		this.inputQuery = inputQuery;
	}
	
	public Integer getNumTweets() {
		return numTweets;
	}
	
	public void setNumTweets(int numTweets) {
		this.numTweets = numTweets;
	}
	
	@Override
	public String toString() {
		return "QueryData [inputQuery=" + inputQuery + ", numTweets=" + numTweets + "]";
	}
}
