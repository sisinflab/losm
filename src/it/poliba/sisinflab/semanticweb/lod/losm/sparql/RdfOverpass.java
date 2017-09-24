package sparql;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import com.hp.hpl.jena.ontology.Individual;
import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntProperty;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.util.iterator.ExtendedIterator;
import com.sun.org.apache.xml.internal.utils.URI;


public class RdfOverpass {


	OntModel inf = ModelFactory.createOntologyModel();
	
	public static void main(String[] args) throws IOException {
		
				
	}
	
	public static String searchDouble(String valueToSearch) throws IOException{

		String n3File = ResultMap.path+"sources/lgdo_2014-07-26.n3";

		OntModel inf = ModelFactory.createOntologyModel();
		inf.read(n3File);
		
        OntProperty iProperty2= inf.getOntProperty("sourceTag");
        String baseUri = "http://linkedgeodata.org/ontology/";
        String classUri = baseUri+valueToSearch;
        /////important!!!!!
		  System.out.println("Ricerca per "+classUri);
		OntClass thisClass2 = inf.getOntClass(classUri);
		  
		  /////important
		  
		ExtendedIterator props = thisClass2.listProperties();
		boolean goon = true;
		 String overpassQueryBlock="";
		while (props.hasNext()&&goon)
		{

			Statement s = (Statement) props.next();
			if (s.getPredicate().getLocalName().equals("sourceKey")){
				System.out.println("Found  sourceTagproperty: "+ s.getObject().toString() );
				overpassQueryBlock = "[\""+s.getObject().toString()+"\"]";
				goon = false;
			} else if (s.getPredicate().getLocalName().equals("sourceTag")){
				System.out.println("Found  sourceTagproperty: "+ s.getObject().toString() );
				URI uri = new URI(s.getObject().toString());
				String[] segments = uri.getPath().split("/");
				String key = segments[getValue(segments)-1];
				String value = segments[getValue(segments)+1];
				overpassQueryBlock = "[\""+key+"\"=\""+value+"\"]";
			}
			
			


		}
		

		  return overpassQueryBlock;
		
	}
	
	public static String searchDouble(String keyValue,String tagValue) throws IOException{


		
		String n3File = ResultMap.path+"sources/lgdo_2014-07-26.n3";

		OntModel inf = ModelFactory.createOntologyModel();
		inf.read(n3File);
		
        String baseUri = "http://linkedgeodata.org/ontology/";
        
        String keyClassUri = baseUri+keyValue;
		  System.out.println("Ricerca per "+keyClassUri);
		OntClass keyClass = inf.getOntClass(keyClassUri);		
		ExtendedIterator props = keyClass.listProperties();
		 String keyQueryBlock="";
		while (props.hasNext())
		{
			Statement s = (Statement) props.next();
			if (s.getPredicate().getLocalName().equals("sourceKey")){
				keyQueryBlock = s.getObject().toString();
				
			} 
		}
		
		String tagClassUri = baseUri+tagValue;
		OntClass tagClass = inf.getOntClass(tagClassUri);		
		ExtendedIterator tagProps = tagClass.listProperties();
		 String tagQueryBlock="";
		while (tagProps.hasNext())
		{
			Statement s = (Statement) tagProps.next();
			if (s.getPredicate().getLocalName().equals("sourceTag")){
				URI uri = new URI(s.getObject().toString());
				String[] segments = uri.getPath().split("/");
				tagQueryBlock = segments[getValue(segments)+1];
			} 
		}
		
		
		String overpassQueryBlock = "[\""+keyQueryBlock+"\"=\""+tagQueryBlock+"\"]";
		  return overpassQueryBlock;
		
	}
	
	private static int getValue(String[] segments){
		int exitvalue=-1;
		 for (int i = 0; i < segments.length; i++)
		    {
		        if (segments[i].equals("value")){
		        	exitvalue=i;
		        }
		    }
		
		return exitvalue;
	}
	
	private static void print(OntModel inf) throws IOException{
		//function to write Tag-Value association in a file


		String path = new File(".").getCanonicalPath();
		String n3File = path+"/sources/lgdo_2014-07-26.n3";

		inf.read(n3File);
		



		//create an print writer for writing to a file
	      PrintWriter out = new PrintWriter(new FileWriter(path+"/sources/output.txt"));
	      
        OntProperty iProperty= inf.getOntProperty("sourceTag");
        ExtendedIterator classes = inf.listClasses();
        
        
        
        ////version2
        
        while (classes.hasNext())
		{
		  OntClass thisClass = (OntClass) classes.next();
		  
		  OntClass tempClass = inf.getOntClass(thisClass.toString());
		  Resource res = tempClass.getPropertyResourceValue(iProperty);
		  out.println("1)  Found class: " + thisClass.toString()+"   "+thisClass.getLocalName());
		  String extracted="";
		  ExtendedIterator ite = tempClass.listProperties();
			while (ite.hasNext())
			{

				Statement s = (Statement) ite.next();
				
				if (s.getPredicate().getLocalName().equals("sourceTag")){
					out.println("2)  object: "+ s.getObject().toString() );
					extracted=s.getObject().toString();
				}
			}
		  String overpassQueryBlock="";
		  if (extracted.startsWith("http")&&extracted.contains("value")){
			  URI uri = new URI(extracted);
			  String[] segments = uri.getPath().split("/");
			  String key = segments[getValue(segments)-1];
			  String value = segments[getValue(segments)+1];
			  overpassQueryBlock = "[\""+key+"\"=\""+value+"\"]";
		  }else{
			  overpassQueryBlock=res.toString();
		  }
		  out.println("3)  query: "+overpassQueryBlock);
		  
		  
		  ExtendedIterator instances = thisClass.listInstances();
		
		  while (instances.hasNext())
		  {
		    Individual thisInstance = (Individual) instances.next();
		  }
		}
        
        /////version2 stop
        

		//close the file (VERY IMPORTANT!)
	      out.close();
	}
}
