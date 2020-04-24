package edu.carleton.comp4601.resources;

import edu.carleton.comp4601.dao.DocumentCollection;
import edu.carleton.comp4601.dao.Document;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


public class Documents {
	private static DocumentCollection dc;
	private ConcurrentHashMap<Integer, Document> documents;
	static Documents instance;
	
	public Documents() throws Exception
	{
		System.out.println("reaching here one ....");
		dc = new DocumentCollection();
		System.out.println("reaching here two ....");
		
		//dc.setDocuments(FakeCrawler.crawl());
		dc.setDocuments(new ArrayList<Document>());
		
		//BasicCrawlController.crawl("https://www.ics.uci.edu/");
		
		System.out.println("reaching here three ....");
		
		/*documents = new ConcurrentHashMap<Integer, Document>();
		
		Map<String, Object> documentContent = new HashMap<String, Object>();
		documentContent.put("id", 0);
		documentContent.put("score", 5.0f);
		documentContent.put("name", "documentOne");
		documentContent.put("content", "documentOne fake content.");
		documentContent.put("url", "https://documentOne.url");
		
		Document d1 = new Document(documentContent);
		documents.put(1,d1);
		
		documentContent.replace("id", 1);
		documentContent.replace("score", 7.5f);
		documentContent.replace("name", "documentTwo");
		documentContent.replace("content", "documentTwo fake content.");
		documentContent.replace("url", "https://documentTwo.url");
		
		Document d2 = new Document(documentContent);
		documents.put(2,d2);*/
	}
	
	// after crawling, call this function to set the documents
	/*public void setDocuments(ArrayList<Document> documents)
	{
		dc.setDocuments(documents);
	}*/
	
	public static Documents getInstance() throws Exception
	{
		if (instance == null)
			instance = new Documents();
		return instance;
	}
	
	public ArrayList<Document> getModel(){
		return dc.getDocuments();
	}

	
	public Document find(int id)
	{
		
		for (Document doc : dc.getDocuments())
		{
			if (doc.getId() == id)
			{
				return doc;
			}
		}
		return null;
		
		// TODO: make sure to store a document d at index i where i = d.getId()
		//return dc.getDocuments().get(new Integer(id));
		//return documents.get(new Integer(id));
	}
	
	public boolean delete(int id)
	{
		ArrayList<Document> temp = dc.getDocuments();
		
		for(int i = 0; i < temp.size(); i+=1)
		{
			if (temp.get(i).getId() == id)
			{
				temp.remove(i);
			}
			dc.setDocuments(temp);
			
		}
		
		return true;
	}
	
	
	public void add(Document d)
	{
		ArrayList<Document> temp = dc.getDocuments();
		temp.add(d);
		dc.setDocuments(temp);
	}
	
	public boolean deleteAll()
	{
		if (dc.getDocuments().size() > 0){
			dc.setDocuments(new ArrayList<Document>());
			return true;
		}
		return false;
	}
	
	/*public String printDocumentList(ArrayList<Document> documents)
	{
		String listAsHtml = "<html>" + "<head>" + "COMP4601 SDA documents: <br><br>" + "</head>";
		listAsHtml += "<body>";
		
		for (int i = 0; i < documents.size(); i++)
		{
			Document d= documents.get(i);
			listAsHtml += "<a href=\"" + d.getUrl() + "\">" +  d.getName() +"</a>" + "<br>";
		}
		
		listAsHtml += "</body>" + "</html>";
		
		if (documents.size() == 0)
		{
			listAsHtml += "No documents found.";
		}
		
		return listAsHtml;
	}*/
	
	public String printDocumentList(ArrayList<Document> documents)
	{
		String listAsHtml = "<html>" + "<head>" + "COMP4601 SDA documents: <br><br>" + "</head>";
		listAsHtml += "<body>";
		
		listAsHtml += "<table style=\"width:100%\" text-align: center>";
		
		listAsHtml += " <tr><th>docId</th><th>URL</th></tr> ";
				
				
		for (int i = 0; i < documents.size(); i++)
		{
			Document d= documents.get(i);
			listAsHtml += "<tr align=\"center\"><td>"+documents.get(i).getId()+"</td><td><a href=\""+documents.get(i).getUrl()+  "\">" +  d.getName() +"</a>" + "</td></tr>";
		}
		
		
		listAsHtml += "</table>";
		listAsHtml += "</body>" + "</html>";
		
		if (documents.size() == 0)
		{
			listAsHtml += "No documents found.";
		}
		
		return listAsHtml;
	}
	
}
