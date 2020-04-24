package edu.carleton.comp4601.resources;

import edu.carleton.comp4601.dao.DocumentCollection;
import edu.carleton.comp4601.dao.Document;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class FakeCrawler {

	public static ArrayList<Document> crawl()
	{
		ArrayList<Document> documents = new ArrayList<Document>();
		
		Map<String, Object> documentContent = new HashMap<String, Object>();
		documentContent.put("id", 0);
		documentContent.put("score", 5.0f);
		documentContent.put("name", "documentOne");
		documentContent.put("content", "documentOne fake content.");
		documentContent.put("url", "https://documentOne.url");
		
		
		Document d1 = new Document(documentContent);
		
		documentContent.replace("id", 1);
		documentContent.replace("score", 7.5f);
		documentContent.replace("name", "documentTwo");
		documentContent.replace("content", "documentTwo fake content.");
		documentContent.replace("url", "https://documentTwo.url");
		
		Document d2 = new Document(documentContent);
		
		documents.add(d1);
		documents.add(d2);
		
		return documents;
		
	}
}
