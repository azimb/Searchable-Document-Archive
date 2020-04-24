package edu.carleton.comp4601.resources;


import java.util.ArrayList;

import org.bson.Document;
import org.jsoup.Jsoup;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

public class DocumentStore {
	
	static String ID = "id";
	static String URL = "url";
	static String CONTENT = "content";
	static String LEN = "length";
	static String GRAPH = "graph";
	static String TIME = "time";
	static String TEXT = "text";
	static String LINKS = "links";
	static String IMAGES = "images";
	
	static DocumentStore instance;
	private MongoClient mongoClient;
	private MongoDatabase db;
	private MongoCollection<Document> pages;
	
	public DocumentStore() {
		mongoClient = new MongoClient("localhost", 27017);
		db = mongoClient.getDatabase("crawler");
		pages = db.getCollection("pages");
	}

	public static DocumentStore getInstance() {
		if (instance == null)
			instance = new DocumentStore();
		return instance;
	}
	
	public void add(int _id, String _url, String _content, int _len, long _time) {
		org.jsoup.nodes.Document doc = Jsoup.parse(_content);
		org.jsoup.select.Elements htmlText = doc.body().select("h1, h2, h3, h4, h5, h6, p");
		ArrayList<String> text = new ArrayList<>();
		org.jsoup.select.Elements htmlLinks = doc.body().select("a");
		ArrayList<String> links = new ArrayList<>();
		org.jsoup.select.Elements htmlImages = doc.body().select("img, svg");
		ArrayList<String> images = new ArrayList<>();
		
		htmlText.forEach((n) -> text.add(n.outerHtml()));
		htmlLinks.forEach((n) -> links.add(n.outerHtml()));
		htmlImages.forEach((n) -> images.add(n.outerHtml()));
		
		pages.insertOne(new Document(ID, _id).append(URL, _url).append(CONTENT, _content).append(LEN, _len).append(TIME, _time).append(TEXT, text).append(LINKS,  links).append(IMAGES,  images));
	}
}
