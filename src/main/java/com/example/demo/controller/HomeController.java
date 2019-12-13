package com.example.demo.controller;

import java.io.IOException;
import java.util.ArrayList;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.example.demo.TweetCrawler;
import com.example.demo.model.QueryData;

import twitter4j.TwitterException;

@Controller
public class HomeController 
{
	@RequestMapping("")
	public ModelAndView home (QueryData data) throws TwitterException, IOException {
		ModelAndView mv = new ModelAndView();
		if (data.getNumTweets() != null ) {
			ArrayList<String> tweet_ids = TweetCrawler.main(data.getInputQuery(), data.getNumTweets());
			mv.addObject("query", data.getInputQuery());
			mv.addObject("tweet_ids", tweet_ids);
		}
		mv.setViewName("home.jsp");
		return mv;
	}
}
