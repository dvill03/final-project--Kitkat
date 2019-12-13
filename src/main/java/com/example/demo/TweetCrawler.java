package com.example.demo;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

import org.apache.lucene.queryparser.classic.ParseException;

import twitter4j.FilterQuery;
import twitter4j.JSONArray;
import twitter4j.JSONObject;
import twitter4j.StallWarning;
import twitter4j.Status;
import twitter4j.StatusDeletionNotice;
import twitter4j.StatusListener;
import twitter4j.TwitterException;
import twitter4j.TwitterStream;
import twitter4j.TwitterStreamFactory;
import twitter4j.conf.ConfigurationBuilder;
import twitter4j.TwitterObjectFactory;



public class TweetCrawler {
	public static ArrayList<String> main (String inputQuery, Integer numTweets) throws TwitterException, IOException {
		final Object lock = new Object();
//		BufferedWriter outputWriter  = new BufferedWriter(new FileWriter("tweets.txt"));
		ArrayList<ArrayList<String>> pageData = new ArrayList<>();
		ArrayList<String> results = new ArrayList<>();
		ConfigurationBuilder cb = new ConfigurationBuilder();
		cb.setDebugEnabled(true)
		  .setOAuthConsumerKey("d5Lg8ML3yhrllmHKsxSXv39Hh")
		  .setOAuthConsumerSecret("nbeyclbFjJPRKsgoBi2mRhmH3XLkJ1So43EnCLFEPpy9y8WeyO")
		  .setOAuthAccessToken("2944888496-2dEMTRKjUYfe40NeXKlXOjumyd0S0iEZc6nU8Hr")
		  .setOAuthAccessTokenSecret("p4gtP4UeNieHqmJ7KmS24dle63iiL2l2FuIT6jx6D8y6O")
		  .setJSONStoreEnabled(true);
		
		TwitterStream twitterStream = new TwitterStreamFactory(cb.build())
				.getInstance();

		StatusListener listener = new StatusListener() {
		    public void onStatus(Status status) {

		    	String statusStr = TwitterObjectFactory.getRawJSON(status);
		    	JSONObject statusJson = new JSONObject(statusStr);
		    	
		    	ArrayList<String> page = getPage(statusJson, status.isRetweet());
		    	if (page != null) {
//		    		try {
//						outputWriter.write(Arrays.asList(page).toString() + "\n");
//					} catch (IOException e) {
//						e.printStackTrace();
//					}
		    		pageData.add(page);
		    	}
		    	
		    	System.out.println(statusStr);
		    	if (pageData.size() == numTweets) {
					try {
						LuceneSearcher.analyze(pageData, inputQuery, results);
					} catch (IOException e) {
						e.printStackTrace();
					} catch (ParseException e) {
						e.printStackTrace();
					} finally {
						synchronized (lock) {
							lock.notify();
						}
						twitterStream.shutdown();
					}
				}
		    }

			@Override
			public void onException(Exception ex) {	}
			@Override
			public void onDeletionNotice(StatusDeletionNotice statusDeletionNotice) { }
			@Override
			public void onTrackLimitationNotice(int numberOfLimitedStatuses) { }
			@Override
			public void onScrubGeo(long userId, long upToStatusId) { }
			@Override
			public void onStallWarning(StallWarning warning) { }
		    
		};
		
		twitterStream.addListener(listener);
		twitterStream.filter(new FilterQuery(inputQuery.split("\\s+")).language("en"));
//		twitterStream.sample("en");
		try {
			synchronized (lock) {
				lock.wait();
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
//		outputWriter.close();
		return results;
	}
	
	
	
	// Get all fields of page, null if there's an error with url title
	private static ArrayList<String> getPage(JSONObject statusJson, boolean retweet) {
		// Get url string GOOD
    	String title = getURLTitle(statusJson);
    	if (title.equals("ERROR_FINDING_TITLE")) { return null; }
       
    	// Get original text then check for types of text that tweet has GOOD
    	String text, retweet_text, verified_text, quote_text, hashtag_text, reply_text, tweetID, time;
    	retweet_text = verified_text = quote_text = hashtag_text = reply_text = time = "";
    	text = statusJson.getString("text").replaceAll("\n", " ").replaceAll("\r", " ");
    	
    	// Get retweet if retweet GOOD
    	if (retweet) {
    		retweet_text = statusJson.getJSONObject("retweeted_status").getString("text").replaceAll("\n", " ");
    		text = "";
    	}  
    	
    	// Get text as verified if vefified account GOOD
    	if (statusJson.getJSONObject("user").getString("verified").equals("true")) {
    		verified_text = statusJson.getJSONObject("retweeted_status").getString("text").replaceAll("\n", " ");
    		text = "";
    	}
    	
    	// Get quote if quoted tweet GOOD
    	if (statusJson.getString("is_quote_status").equals("true")) {
    		quote_text = statusJson.getJSONObject("quoted_status").getString("text").replaceAll("\n", " ");
    		quote_text += statusJson.getString("text").replaceAll("\n", " ");
    		text = "";
    	}
    	
    	// Get hashtags if tweet contains hashtags GOOD
    	JSONArray hashtags = statusJson.getJSONObject("entities").getJSONArray("hashtags");
    	if (!hashtags.toString().equals("[]")) {
    		for (int i=0; i<hashtags.length(); i++) {
    			hashtag_text += " " + hashtags.getJSONObject(i).getString("text").replaceAll("\n", " ");
    		}
    		text = "";
    	}	    	
    	
    	// Get reply if tweet is reply (couldn't find their tweet) GOOD
    	if (!statusJson.getString("in_reply_to_status_id").equals("null")) {
    		reply_text = statusJson.getString("text").replaceAll("\n\t\r", " ");
    	}
    	
    	// Get tweetID GOOD
    	tweetID = statusJson.getString("id_str");
		
    	// Get time millis
    	time = statusJson.getString("timestamp_ms");
		return new ArrayList<>(
				Arrays.asList(title, text, retweet_text, verified_text, quote_text, hashtag_text, reply_text, tweetID, time)
				);
	}
	
	
	
	// Takes in status JSON string and gets url -> go to url to get innerhtml of title tag
	// Return "ERROR_FINDING_TITLE" if their title has crap that throws exception
	private static String getURLTitle(JSONObject statusJson) {
		String title = "";
        JSONArray urls = statusJson.getJSONObject("entities").getJSONArray("urls");
        
    	if (!urls.toString().equals("[]")) {
    		String url = urls.getJSONObject(0).getString("url");
    		InputStream response = null;
    		try {
    			response = new URL(url).openStream();
    			Scanner scanner = new Scanner(response);
    			String responseBody = scanner.useDelimiter("\\A").next();
    			title = responseBody.substring(responseBody.indexOf("<title>") + 7, responseBody.indexOf("</title>"));
    			scanner.close();
    		} catch (IOException ex) {
    			title = "ERROR_FINDING_TITLE";
    		} finally {
    			try {
    				response.close();
    			} catch (IOException ex) {
    				title = "ERROR_FINDING_TITLE";
    			}
    		}
    	
    	} 
		return title;
	}
}
