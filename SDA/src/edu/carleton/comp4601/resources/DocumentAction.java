package edu.carleton.comp4601.resources;

import edu.carleton.comp4601.dao.Document;

import javax.ws.rs.GET;
import javax.ws.rs.DELETE;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.UriInfo;



public class DocumentAction {
	@Context
	UriInfo uriInfo;
	@Context
	Request request;
	
	Integer id;
	Documents documents;
	
	public DocumentAction(UriInfo uriInfo, Request request, String id, Documents documents)
	{
		this.uriInfo = uriInfo;
		this.request = request;
		this.id = new Integer(id);
		this.documents = documents;

	}
	
	@GET
	@Produces(MediaType.APPLICATION_XML)
	public Document getDocument()
	{
		Document d = documents.find(id);
		if (d == null)
		{
			throw new RuntimeException("No such document: " + id);
		}
		return d;
	}
	
	// TODO: need a getDocument() here that produces HTML
	/*
	@GET
	@Produces(MediaType.APPLICATION_HTML)
	public Document getDocument() {}
	*/
	
	@DELETE
	public void deleteDocument()
	{
		if (!documents.delete(id))
			throw new RuntimeException("Document " + id + " not found.");
	
	}
	
}


