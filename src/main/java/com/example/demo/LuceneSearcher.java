package com.example.demo;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.ByteBuffersDirectory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LuceneSearcher {
	static class Page {
        String title;
        String text;
        String retweet_text;
        String verified_text;
        String quote_text;
        String hashtag_text;
        String reply_text;
        String tweetID;
        
        Page(String title, String text, String retweet_text, String verified_text, String quote_text, String hashtag_text, String reply_text, String tweetID) {
            this.title = title;
            this.text = text;
            this.retweet_text = retweet_text;
            this.verified_text = verified_text;
            this.quote_text = quote_text;
            this.hashtag_text = hashtag_text;
            this.reply_text = reply_text;
            this.tweetID = tweetID;
        }
    }

    public static ArrayList<String> analyze(ArrayList<ArrayList<String>> pageData, String inputQuery, ArrayList<String> results) 
    																throws IOException, ParseException {
        Analyzer analyzer = new EnglishAnalyzer(); // tokenize based on spaces (no stemmings)
                                                    // EnglishAnalyzer will stem

        // Store the index in memory:
        Directory directory = new ByteBuffersDirectory();         // Cannot stop here and read idx later
        IndexWriterConfig config = new IndexWriterConfig(analyzer);
        IndexWriter indexWriter = new IndexWriter(directory, config);

        // important part
        // foreach doc, idx. convert text to the document (lucene document)
        // title data, content data

        ArrayList<Page> pages = new ArrayList<>();
        for (List<String> tweet : pageData) {
    		pages.add(new Page(tweet.get(0), tweet.get(1), tweet.get(2), tweet.get(3), tweet.get(4), tweet.get(5), tweet.get(6), tweet.get(7)));
    	} 


        // This one actually creates the lucene doc, other one is data
        for (Page page: pages) {
            Document doc = new Document();
            doc.add(new TextField("title", page.title, Field.Store.YES) );
            doc.add(new TextField("text", page.text, Field.Store.YES)); //index and store
            doc.add(new TextField("retweet_text", page.retweet_text, Field.Store.YES));
            doc.add(new TextField("verified_text", page.verified_text, Field.Store.YES) );
            doc.add(new TextField("quote_text", page.quote_text, Field.Store.YES)); //index and store
            doc.add(new TextField("hashtag_text", page.hashtag_text, Field.Store.YES));
            doc.add(new TextField("reply_text", page.reply_text, Field.Store.YES) );
            doc.add(new StringField("tweetID", page.tweetID, Field.Store.YES));
            indexWriter.addDocument(doc);
        }
        indexWriter.close();


  // once you crawl pages, strategize what field you want to index (say 1m english tweets.
  // with info of language, but you don't want unecessary info, so ignore for index)
  // For web pages, have title which prolly has higher weight than content, also has <h1> <h2> <h3>
  // which should be weighted in order: h1, h2, h3
        // Now search the index:
        DirectoryReader indexReader = DirectoryReader.open(directory);
        IndexSearcher indexSearcher = new IndexSearcher(indexReader);

        String[] fields = {"title", "text", "retweet_text", "verified_text", "quote_text", "hashtag_text", "reply_text", "tweetID"};
        Map<String, Float> boosts = new HashMap<>();
        boosts.put(fields[0], 1.00f);  // title weight
        boosts.put(fields[1], 0.68f);  // text weight (original text)
        boosts.put(fields[2], 0.25f);  // retweet weight
        boosts.put(fields[3], 0.75f);  // verified weight
        boosts.put(fields[4], 0.50f);  // quote weight
        boosts.put(fields[5], 0.85f);  // hashtag weight
        boosts.put(fields[6], 0.60f);  // reply weight
        boosts.put(fields[7], 0.00f);  // tweetID no weight
        MultiFieldQueryParser parser = new MultiFieldQueryParser(fields, analyzer, boosts);
        Query query = parser.parse(inputQuery);
        // Query query = parser.parse("UCR discussion");
        // QueryParser parser = new QueryParser("content", analyzer);
        // Query query = parser.parse("(title:ucr)^1.0 (content:ucr)^0.5");
        int topHitCount = 10;
        ScoreDoc[] hits = indexSearcher.search(query, topHitCount).scoreDocs;

        // Iterate through the results:
        for (int rank = 0; rank < hits.length; ++rank) {
            Document hitDoc = indexSearcher.doc(hits[rank].doc);
        	results.add(hitDoc.get("tweetID"));
            System.out.println( rank + ", https://twitter.com/user/status/" + hitDoc.get("tweetID") + ": " + (rank + 1) + " (score:" + hits[rank].score + ") "
                               );
        }
        indexReader.close();
        directory.close();
        return results;
    }
}
