package edu.carleton.comp4601.resources;

import edu.carleton.comp4601.dao.Document;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.ws.rs.GET;
import javax.ws.rs.DELETE;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.document.StoredField;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.FSDirectory;

import edu.carleton.comp4601.utility.SDAConstants;
import edu.carleton.comp4601.utility.SearchException;
import edu.carleton.comp4601.utility.SearchResult;
import edu.carleton.comp4601.utility.SearchServiceManager;

@Path("/sda")
public class SearchableDocumentArchive {

	@Context
	UriInfo uriInfo;
	@Context
	Request request;
	
	public static Documents documents;
	
	
	private String name;
	
	public static final String INDEX_DIR = "C:/Users/azimb/Desktop/comp4601a1/index";
	
	public SearchableDocumentArchive() throws Exception
	{
		name = "COMP4601-SDA by Azim and Essam";
		documents = Documents.getInstance();
		
		SearchServiceManager.getInstance().start();
		
	}
	
	@GET
	public String printName()
	{
		return name;
	}
	
	@GET
	@Produces(MediaType.TEXT_HTML)
	public String sayHtml() {
		return "<html> " + "<title>" + name + "</title>" + "<body><h1>" + name
				+ "</body></h1>" + "</html> ";
	}
	
	
	@GET
	@Path("crawl")
	public Response crawl() throws Exception
	{
		try
		{
			BasicCrawlController.crawl();
			
			
		}
		catch(Exception e)
		{
			return Response.status(500).entity("Crawl failed: "+e.getMessage()).build();
		}
		
		
		
		return Response.ok("Crawl successful.").build();
	}
	
	@GET
	@Path("index")
	public Response index() throws Exception
	{
		try
		{	
			indexing();
			
		}
		catch(Exception e)
		{
			return Response.status(500).entity("Indexing failed: "+e.getMessage()).build();
		}
		
		
		return Response.ok("Indexing successful.").build();
	}
	
	
	
	@Path("{doc}")
	public DocumentAction getDocument(@PathParam("doc") String id)
	{
		return new DocumentAction(uriInfo, request, id, documents);
	}
	
	@DELETE
	@Path("{doc}")
	public String deleteDocument(@PathParam("doc") String id)
	{	
		if (documents.delete(Integer.parseInt(id)))
		{
			return "<p> Delete successful. </p>";
		}
		return "<p> Noboost unsuccessful. </p>";
	}
	
	
	@GET
	@Path("documents")
	@Produces(MediaType.TEXT_XML)
	public List<Document> getDocuments(){
		List<Document> lodc = new ArrayList<Document>();
		lodc.addAll(documents.getModel());
		return lodc;
	}
	
	@GET
	@Path("documents")
	@Produces(MediaType.TEXT_HTML)
	public String getDocumentsHTML(){
		List<Document> lodc = new ArrayList<Document>();
		lodc.addAll(documents.getModel());
		return documents.printDocumentList((ArrayList<Document>)lodc);
	}
	
	
	@GET
	@Path("query/{tags}")
	@Produces(MediaType.TEXT_HTML)
	public String queryAsHTML(@PathParam("tags") String tags) throws IOException, ParseException {
		return documents.printDocumentList(localQuery(tags));
	}
	
	@GET
	@Path("query/{tags}")
	@Produces(MediaType.APPLICATION_XML)
	public ArrayList<Document> queryAsXML(@PathParam("tags") String tags) throws IOException, ParseException {
		return localQuery(tags);
	}
	
	@GET
	@Path("search/{tags}")
	@Produces(MediaType.TEXT_HTML)
	public String searchForDocs(@PathParam("tags") String tags) throws Exception {
		// Perform the distributed part of the search
		SearchResult sr = SearchServiceManager.getInstance().search(tags);
		// Perform your local search (this is my specific code, yours differs!)
		ArrayList<Document> docs = localQuery(tags);
		// We will wait for up to 10 seconds or until all distributed searches complete
		// (whichever is shorter) but will then take the documents that we have.
		try {
		sr.await(30, TimeUnit.SECONDS);
		} catch (InterruptedException e) {
		}finally {
		SearchServiceManager.getInstance().reset();
		}
		
		System.out.println("SIZE OF SR DOCS: " + sr.getDocs().size());
		
		// Take the state of the documents
		docs.addAll(sr.getDocs());
		
		// Build the page (not provided here, you write this)
		return documents.printDocumentList(docs);
	}
	
	@GET
	@Path("search/{tags}")
	@Produces(MediaType.APPLICATION_XML)
	public ArrayList<Document> searchForDocsXML(@PathParam("tags") String tags) throws Exception {
		// Perform the distributed part of the search
		SearchResult sr = SearchServiceManager.getInstance().search(tags);
		// Perform your local search (this is my specific code, yours differs!)
		//ArrayList<Document> docs = localQuery(tags);
		// We will wait for up to 10 seconds or until all distributed searches complete
		// (whichever is shorter) but will then take the documents that we have.
		try {
		sr.await(SDAConstants.TIMEOUT, TimeUnit.SECONDS);
		} catch (InterruptedException e) {
		}finally {
		SearchServiceManager.getInstance().reset();
		}
		
		ArrayList<Document> docs = new ArrayList<Document>();
		
		System.out.println("SIZE OF SR DOCS: " + sr.getDocs().size());
		
		// Take the state of the documents
		docs.addAll(sr.getDocs());
		
		// Build the page (not provided here, you write this)
		return docs;
	}
	
	
	@GET
	@Path("delete/{tags}")
	@Produces(MediaType.TEXT_HTML)
	public String deleteDocumentsWithTags(@PathParam("tags") String tags)
	{
		try
		{
			ArrayList<Document> docs = localQuery(tags);
			
			for (Document doc : docs)
			{
				
				documents.delete(doc.getId());
			}
			
			
			
			return "<p>HTTP Response: 200 -- Delete successful.</p>";
		}
		catch(Exception e)
		{
			return "<p>HTTP Response: 204 -- Delete unsuccessful :" + e.getMessage() + ".</p>";
		}
	}
	
	private ArrayList<Document> localQuery(String query) throws IOException, ParseException
	{
		try {
	
			IndexReader reader = DirectoryReader.open(FSDirectory.open(new File(INDEX_DIR).toPath()));
			IndexSearcher searcher = new IndexSearcher(reader);
			Analyzer analyzer = new StandardAnalyzer();
			QueryParser parser = new QueryParser("contents", analyzer);
			
			Query q = parser.parse(query);
	
			TopDocs results = searcher.search(q, 100); // 100 documents
			ScoreDoc[] hits = results.scoreDocs;
			
			
			ArrayList<Document> docs = new ArrayList<Document>();
			
			for (ScoreDoc hit : hits) {
				org.apache.lucene.document.Document indexDoc = searcher.doc(hit.doc);
				String id = indexDoc.get("docId");
				if (id != null) {
					edu.carleton.comp4601.dao.Document d = documents.find(Integer.valueOf(id)-1);
					if (d != null) {
						d.setScore(hit.score); // Used in display to user
						docs.add(d);
					}
				}
			}
			reader.close();
			return docs;
		}
		catch(IOException | ParseException e)
		{
			e.printStackTrace();
		}
		
		return null;
		
	}
	
	public void indexing() throws IOException {
		FSDirectory dir = FSDirectory.open(new File(INDEX_DIR).toPath());
		
		Analyzer analyzer = new StandardAnalyzer();
		
		IndexWriterConfig config = new IndexWriterConfig(analyzer);
		config.setOpenMode(OpenMode.CREATE);
		IndexWriter writer = new IndexWriter(dir, config);
		
		for (Document d : documents.getModel()) {
			org.apache.lucene.document.Document docLucene = new org.apache.lucene.document.Document();
			
			docLucene.add(new TextField("docId", String.valueOf(d.getId()), Store.YES));
			docLucene.add(new StringField("url", d.getUrl(), Store.YES));
			docLucene.add(new TextField("contents", d.getContent(), Store.YES));
			docLucene.add(new TextField("name", d.getName(), Store.YES));
			
			writer.addDocument(docLucene);
		}
		writer.close();
}

	
	@GET
	@Path("reset")
	@Produces(MediaType.TEXT_HTML)
	public String reset() throws Exception
	{
		if (documents.getInstance().deleteAll()) { return "Reset complete."; }
		
		return "SDA is already empty -- reset didn't occur.";
		
	}
	
	@GET
	@Path("noboost")
	@Produces(MediaType.TEXT_HTML)
	public String setNoboost()
	{
		try
		{
			index();
			return "<p> Noboost successful. </p>";
			
		}
		catch(Exception e) {
			return "<p> Noboost unsuccessful. Error: " + e.getMessage()+ " </p>";
	
		}
	}
	
	@GET
	@Path("list")
	public ArrayList<String> listOFServices()
	{
		return SearchServiceManager.getInstance().list();
	}
	
	
}
