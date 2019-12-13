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

We used STS (Spring Tool Suite) to complete our project, so we'll be explaining how to run 
the project using a STS4 fresh download.

-Project is using Java13 (but I got it working with Java8 setup also)
-Download STS4
-Execute the JAR file associated with that download
-Download Maven if you don't have it yet (we got v3.6.3)
-Add path to mvn executable commmand in apache-maven<version>/apache-maven/src/bin
-Open the IDE once that's done being installed 
(If you get an error, you most likely need 64-bit or 32-bit STS, Java, and OS (depends on your machine). Or you don't have path set to your java executable file.
-Clone the repo into any directory of your liking
-Wait for all of you dependencies to be downloaded and indexed
-Run project as -> Spring Boot Project
-Default port once the project is running is 8080 (localhost:8080)
-Once the program successfully runs, you are shown a form with 2 text boxes and submit button
-First field is your query
-Second field is how many tweets do you want to collect before Lucene creates documents and analyzes
-No matter what, you will only get 10 results
-Your results will be sorted by rank (top-most tweet is highest rank)
-That's basically how to run our project

NOTE: 
If there's a problem with the embedded tweet being displayed you can try one of two things:
1) Remove the async keyword for "<script async src="https://platform.twitter.com/widgets.js"></script>"
If that doesn't work and you still have a problem using twitter's tweat widget, try
2) Get rid of everything inside of the <c:forEach></c:forEach> tags EXCEPT the commented code.
   For that code, go to the end of that <blockquote> and change "${tweet_url}" to "${tweet_id}"
      Also consider removing the async keyword like before (when using the widget)
