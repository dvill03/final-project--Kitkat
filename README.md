# final-project-kitkat
Our group implemented a Twitter search engine. We used the Twitter4J Java library in order to stream tweets and 
Lucene in order to build and search over our indices. For our extension, we created a web-app using Spring. 

We originally crawled streamed tweets by arbitrary filters in testing, but in the final project, we filtered tweets based on the query
input into the web app. Our crawler extracted various fields we found relevant in scoring the tweets such as text, titles of links, 
hashtags, and various attributes of the tweets such as whether they were replies, quoted retweets, and whether the user who made the 
tweet was verified. We also extracted the tweet id number in order to utilize it in our extension. 

Our project currently does all of the tweet collecting and Lucene analyzing during runtime. That is, we don't save our data into files
which would be later used directly into Lucene.

We kept the BM-25 similarity measure and used an EnglishAnalyzer to account for stemming. We placed boosts on our fields based on what
we believed would be more relevant to the user. We also had to change our values a couple of times, as we progressed in the project.
Since we do everything at runtime, we start from our web app, call our tweet listener to collect a specific number of tweets, pass those
tweets to Lucene to have documents indexed for analyzing, and finally return our results back up the stack. With this information,
we are able to recreate the tweets for our GUI (part 3).

For our GUI, we collect the data (from request) and pass it back (as response) and make use of twitter's twitter-tweet plugin to
create the embedded tweet which we then display by rank. Our rank depends on the results that Lucene returned, but instead of 
having score or other data returned, we notice that the tweets are ranked after being analyzed, so we simply pass the tweet IDs back.

Group Member Contribution
- Daniel Villareal and Daniel Wahyu did the entire first two parts together. We met up at Chung and took turns pair programming 
over the course of multiple meetings. We would also debug together and decided upon the architecture together. For the extension, we 
discussed together how we wanted to format the tweets and decided on using embedded tweets based on the tweet id number. Daniel Villareal
mainly worked extension, as he was the most familiar with web development within the group. 

