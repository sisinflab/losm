package it.poliba.sisinflab.semanticweb.lod.losm.sparl;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Scanner;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.jena.atlas.json.JsonArray;
import org.apache.jena.atlas.json.JsonObject;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;


public class MapMan {

	

    Map<String , ArrayList<CoarseTriple>> sky = new HashMap<>();
    Map<String , ArrayList<CoarseTriple>> skybis = new HashMap<>();
    Map<String , ArrayList<Integer>> weights = new HashMap<>();
    Map<String , Boolean> solvedVars = new HashMap<String , Boolean>();
    
	public MapMan(Map<String , ArrayList<CoarseTriple>> input) {
		
		this.sky = input;
		this.skybis = input;
		// TODO Auto-generated constructor stub
	}
	
	

	public MapMan() {
		// TODO Auto-generated constructor stub
	}
	
	
	public Map<String , ArrayList<String>> go(){
			
			if (!ResultMap.recursion){
			ResultObject controller;
			try {
				controller = new ResultObject(skybis);
				ResultMap.childhood.add(controller);
				
				
				
				
			} catch (IOException | SAXException | ParserConfigurationException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			int counter = 0;
			boolean condition=true;
			int max=1;
			while (condition){
				
				try {
					
					
					ArrayList<ResultObject> additiveList = ResultMap.childhood.get(counter).returninGo();
					ResultMap.childhood.addAll(additiveList);
					max = ResultMap.childhood.size();
					
					
					
				} catch (IOException | SAXException
						| ParserConfigurationException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				
				
				
				counter++;
				if (!(counter<max)){
					condition=false;
				}
			}
			
			}else{
				
				ResultObject controller;
				try {
					controller = new ResultObject(skybis);
					
				} catch (IOException | SAXException | ParserConfigurationException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
			

			if (!ResultMap.totalResults.isEmpty()){
				cleanResults();
				
				removeDuplicates();
			}else {
				

				System.out.println("No results");
			}
			return ResultMap.totalResults;
			
	}
	
	public void cleanResults(){
		if (!(ResultMap.selectList.isEmpty())){
			ArrayList<String> deleteList = new ArrayList<String>();
			Iterator iterator = ResultMap.totalResults.keySet().iterator();
			while(iterator.hasNext()){
			 String key = iterator.next().toString();
			 boolean eliminate =true;
			 for (FineElement temp : ResultMap.selectList){
				 
				 if (temp.getContent().equals(key)){
					 eliminate = false;
				 }
			 }
			 if (eliminate){
				 deleteList.add(key);
				 
			 }
			}
			
			
			for(String temp : deleteList){
				
				ResultMap.totalResults.remove(temp);
			}
		}
	}
	
	public void removeDuplicates(){
		
		
		Map<String , ArrayList<Integer>> duplicates = new HashMap<String , ArrayList<Integer>>();
		Iterator iterator = ResultMap.totalResults.keySet().iterator();
		 String firstKey = iterator.next().toString();
		for (int i = 0; i < ResultMap.totalResults.get(firstKey).size(); i++) {
			
			int frequency = Collections.frequency(ResultMap.totalResults.get(firstKey), ResultMap.totalResults.get(firstKey).get(i));
			
			if (frequency>1){
				 System.out.println("elemento ripetuto "+firstKey+"  "+ResultMap.totalResults.get(firstKey).get(i));
				if (duplicates.containsKey(ResultMap.totalResults.get(firstKey).get(i).toString())){
					ArrayList<Integer> tempArrayList = new ArrayList<Integer>();
					tempArrayList.addAll(duplicates.get(ResultMap.totalResults.get(firstKey).get(i).toString()));
					tempArrayList.add(i);
					duplicates.put(ResultMap.totalResults.get(firstKey).get(i).toString(), tempArrayList);
				} else {
					ArrayList<Integer> tempArrayList = new ArrayList<Integer>();
					tempArrayList.add(i);
					duplicates.put(ResultMap.totalResults.get(firstKey).get(i).toString(), tempArrayList);
				}
				

			}
			
		}
		
		ArrayList<Integer> deleteList = new ArrayList<Integer>();
		Iterator iterator2 = duplicates.keySet().iterator();
		while (iterator2.hasNext()) {
		    String key = iterator2.next().toString();

		    ArrayList<Integer> indexArray = duplicates.get(key);
		    while(!indexArray.isEmpty()){
				    //while
				    int first = indexArray.get(0);
				    ArrayList<Integer> dropIndexArray = new ArrayList<Integer>();
				    for(int i=1;i<indexArray.size();i++){
		
					    boolean eliminate = true;
				    	Iterator iterator3 = ResultMap.totalResults.keySet().iterator();
						while (iterator3.hasNext()) {
						    String swingKey = iterator3.next().toString();
						    if (!(ResultMap.totalResults.get(swingKey).get(first).equals(ResultMap.totalResults.get(swingKey).get(indexArray.get(i))))){
						    	eliminate = false;
						    }
						}
		
						 if (eliminate){
							 deleteList.add(indexArray.get(i));
							 dropIndexArray.add(indexArray.get(i));
							 
						 }
				    }
				    indexArray.remove(0);
				    for (Integer dropIndex : dropIndexArray){
				    	indexArray.remove(dropIndex);
				    }
		    }
		    
		}

		Collections.sort(deleteList);

		Collections.reverse(deleteList);
		 for(int i=0;i<deleteList.size();i++){
			 
			 int indexToDelete = deleteList.get(i);
			 
			 Iterator iterator3 = ResultMap.totalResults.keySet().iterator();
				while (iterator3.hasNext()) {
				    String swingKey = iterator3.next().toString();
				    ArrayList<String> ArrayValue = ResultMap.totalResults.get(swingKey);
				    ArrayValue.remove(indexToDelete);
				    ResultMap.totalResults.put(swingKey, ArrayValue);
				}
			 
			 
			 
		 }
		
		
	}
	
}
