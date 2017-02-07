package it.poliba.sisinflab.semanticweb.lod.losm.sparl;
/************ Esempio.java - versione 2 ***********

  Rispetto alla versione 1 sono stati aggiornati i
  nomi dell'analizzatore lessicale, della classe
  che rappresenta i token e del metodo di scansione.

*************************************************/


import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Gate {

	ArrayList<String> errorList= new ArrayList<String>();
    public void main(String args[]) throws IOException {


		String path = new File(".").getCanonicalPath();

		SparqlLexer scanner;
		scanner = new SparqlLexer(new StringReader(args[0]));
	    SparqlCup p = new SparqlCup(scanner);  
	      
	      try {
	    	  p.debug_parse();
			  //p.parse();
			  System.out.println(ResultMap.totalResults);
		} catch (Exception e) {
			e.printStackTrace();
		}
         
    }
    
    public  Map<String , ArrayList<String>> ask(String args[]) throws IOException {


		String path = new File(".").getCanonicalPath();
      
		SparqlLexer scanner;
		scanner = new SparqlLexer(new StringReader(args[0]));
	    SparqlCup p = new SparqlCup(scanner);  
	      
	      try {
	    	  p.parse();
	    	  if (p.getNoErrors()==0){
	    		  p.run();
	    		  
	    	  }else {
	    		  errorList=p.getErrors();
	    		  
	    	  }
		} catch (Exception e) {
			e.printStackTrace();
		}
         return p.getResults();
    }
    
    public int getNoErrors(){
    	
    	return errorList.size();
    }
    
    public ArrayList<String> getErrors(){
    return errorList;
    }
}
