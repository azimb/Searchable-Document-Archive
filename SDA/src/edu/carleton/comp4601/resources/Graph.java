package edu.carleton.comp4601.resources;

import org.apache.commons.io.IOUtils;
import org.jgrapht.*;
import org.jgrapht.graph.*;
import org.jgrapht.io.*;
import org.jgrapht.traverse.*;

import java.io.*;
import java.net.*;
import java.util.*;

public class Graph {
	Multigraph<URL, DefaultEdge> g = new Multigraph<>(DefaultEdge.class);
	
	public Graph() {}
	
	public void addEdge(String parent, String child) {
		try {
			URL parentURL = new URL(parent);
			URL childURL = new URL(child);
			
			g.addVertex(parentURL);
			g.addVertex(childURL);
			
			DefaultEdge e = g.addEdge(parentURL, childURL);
			
			System.out.println(e);
		}
		catch (Exception e) {
			System.out.println(e);
		}
	}
}
