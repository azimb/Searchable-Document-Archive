package edu.carleton.comp4601.resources;

import org.jsoup.nodes.Document;

class CustomVertex {
	String url;
	Document parsed_html;
	
	public CustomVertex(String url, Document parsed_html)
	{
		this.url = url;
		this.parsed_html = parsed_html;
	}
	
}
