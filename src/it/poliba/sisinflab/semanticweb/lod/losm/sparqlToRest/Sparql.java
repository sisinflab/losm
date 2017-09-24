package sparqlToRest;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.commons.lang3.StringUtils;
import org.apache.jena.atlas.json.JsonArray;
import org.apache.jena.atlas.json.JsonObject;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import sparql.Gate;
import sparql.ResultMap;

/**
 * Servlet implementation class Sparql
 */
@WebServlet("/Sparql")
public class Sparql extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Map<String , ArrayList<String>> results  = new HashMap<>();

	ArrayList<String> errorList= new ArrayList<String>();
	PrintWriter out;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public Sparql() {
        super();
        // TODO Auto-generated constructor stub

    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		if (request.getParameterValues("query")!=null){
		ResultMap.path=getServletContext().getRealPath("/");
		results  = new HashMap<>();
		ResultMap.clean();
		String a[]= new String[220];
		
		ServletContext servletContext = getServletContext();
		String contextPath = servletContext.getRealPath(File.separator);
		String path = request.getContextPath();
    	a[0]=request.getParameterValues("query")[0];
    	if (!request.getHeader("accept").contains("application/sparql-results")){
    		a[1]=request.getParameterValues("format")[0];
    	} else {

        	a[1]=request.getHeader("accept");
    	}
    	if (request.getParameterValues("format")==null){
    	a[0]= cleanDamned(a[0]);
    	}
    	String prefixes = "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> \n"+
				"PREFIX lgdo: <http://linkedgeodata.org/ontology/> \n"+
				"PREFIX spatial: <http://jena.apache.org/spatial#> \n"+
				"PREFIX geo: <http://www.w3.org/2003/01/geo/wgs84_pos#> \n"+
				"PREFIX losm: <http://sisinflab.poliba.it/semanticweb/lod/losm/ontology/>\n";
    	//System.out.println(a[0]);
    	//System.out.println(searchpolygons(a[0]));
    	a[0]=searchpolygons(a[0]);
    	a[0]=prefixes+a[0];
    	Gate queryhandler = new Gate();
		results = queryhandler.ask(a);
		if (queryhandler.getNoErrors()!=0){
			
			errorList=queryhandler.getErrors();
			try {
				
		    	response.setContentType("text/html; charset=UTF-8");
		    	response.setCharacterEncoding("UTF-8");

		    	out = response.getWriter();
				printErrors();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}else
		{
		
			
			
					if (!ResultMap.totalResults.isEmpty()){
					if(a[1]!=null)	{
						if (a[1].contains("application/sparql-results+txt"))	{
								try {
			
							    	response.setContentType("text/html; charset=UTF-8");
							    	response.setCharacterEncoding("UTF-8");
			
							    	out = response.getWriter();
									printResults();
								} catch (IOException e) {
									e.printStackTrace();
								}
						}
								
						if (a[1].contains("application/sparql-results+xml"))	{
								try {
			
							    	response.setContentType("text/xml; charset=UTF-8");
							    	response.setCharacterEncoding("UTF-8");
			
							    	out = response.getWriter();
									printXml();
									
									
								} catch (IOException e) {
									e.printStackTrace();
								}
						}
						
						if (a[1].contains("application/sparql-results+json"))	{
			
					    		response.setContentType("application/json; charset=UTF-8");
					    		response.setCharacterEncoding("UTF-8");
			
					        	out = response.getWriter();
								printJson();
						}
						
						if (a[1].contains("application/sparql-results+html"))	{
			
					    		response.setContentType("text/html; charset=UTF-8");
					    		response.setCharacterEncoding("UTF-8");
			
					        	out = response.getWriter();
								printXhtml();
						}
							}else {
								
								try {
									
							    	response.setContentType("text/xml; charset=UTF-8");
							    	response.setCharacterEncoding("UTF-8");
			
							    	out = response.getWriter();
									printXml();
									
									
								} catch (IOException e) {
									e.printStackTrace();
								}
								
							}
					}else {
						//System.out.println("No results");
						// no results...
						if (a[1].contains("application/sparql-results+txt"))	{
							try {
		
						    	response.setContentType("text/html; charset=UTF-8");
						    	response.setCharacterEncoding("UTF-8");
		
						    	out = response.getWriter();
								printResults();
							} catch (IOException e) {
								e.printStackTrace();
							}
					}
							
					if (a[1].contains("application/sparql-results+xml"))	{
							try {
		
						    	response.setContentType("text/xml; charset=UTF-8");
						    	response.setCharacterEncoding("UTF-8");
		
						    	out = response.getWriter();
								printXml();
								
								
							} catch (IOException e) {
								e.printStackTrace();
							}
					}
					
					if (a[1].contains("application/sparql-results+json"))	{
		
				    		response.setContentType("application/json; charset=UTF-8");
				    		response.setCharacterEncoding("UTF-8");
		
				        	out = response.getWriter();
							printJson();
					}
					
					if (a[1].contains("application/sparql-results+html"))	{
		
				    		response.setContentType("text/html; charset=UTF-8");
				    		response.setCharacterEncoding("UTF-8");
		
				        	out = response.getWriter();
							printXhtml();
					}
					}
					
		}
		
		}
		
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
		
		
	}
	private void printErrors(){
		
		Iterator iterator2 = errorList.iterator();
		out.println("Parsing Errors:<br />");

		while (iterator2.hasNext()) {

			out.println("***********************************<br />");
		    String error = iterator2.next().toString();
		    out.println(error+"<br />");

			}
		

		out.println("***********************************<br />");
		out.println("Total errors:  "+errorList.size()+"<br />");
		out.println("***********************************<br />");
	}
	private static String cleanDamned(String request){
		boolean dot = false;
		String substitute = "<http://www.w3.org/2000/01/rdf-schema#";
		
		while(request.contains(substitute)){

			dot = true;
			int begin = request.indexOf(substitute);
			int end = request.indexOf(" ", begin);
			String temp = request.substring(begin, end);
			String temp2 = temp.replace(substitute, "");
			String def = temp2.substring(0, temp2.length()-1);
			def="rdfs:"+def;
			temp2 = request.replace(temp, def);
			request = temp2;
			System.out.println(request);
		}
		substitute = "<http://sisinflab.poliba.it/semanticweb/lod/losm/ontology/";
		
		while(request.contains(substitute)){

			dot = true;
			int begin = request.indexOf(substitute);
			int end = request.indexOf(" ", begin);
			String temp = request.substring(begin, end);
			String temp2 = temp.replace(substitute, "");
			String def = temp2.substring(0, temp2.length()-1);
			def="losm:"+def;
			temp2 = request.replace(temp, def);
			request = temp2;
			System.out.println(request);
		}
		substitute = "<http://linkedgeodata.org/ontology/";
		
		while(request.contains(substitute)){
			dot = true;
			int begin = request.indexOf(substitute);
			int end = request.indexOf(" ", begin);
			String temp = request.substring(begin, end);
			String temp2 = temp.replace(substitute, "");
			String def = temp2.substring(0, temp2.length()-1);
			def= "lgdo:"+def;
			temp2 = request.replace(temp, def);
			request = temp2;
			System.out.println(request);
		}
		substitute = "<http://jena.apache.org/spatial#";
		
		while(request.contains(substitute)){
			dot = true;
			int begin = request.indexOf(substitute);
			String ws =" ";
			String nil = "<http://www.w3.org/1999/02/22-rdf-syntax-ns#nil> .";
			int end = request.indexOf(ws, begin);
			String temp = request.substring(begin, end);
			String temp2 = temp.replace(substitute, "");
			String def = temp2.substring(0, temp2.length()-1);
			def="spatial:"+def;
			temp2 = request.replace(temp, def);
			//request = temp2;
			System.out.println(temp2);
			String first = "<http://www.w3.org/1999/02/22-rdf-syntax-ns#first>";
			String def2=def+"(";
			while (temp2.contains(first)){
				int newBegin = temp2.indexOf(first)+first.length();
				int newEnd = temp2.indexOf(" .", newBegin);
				String value = temp2.substring(newBegin, newEnd);
				String original = temp2.substring(0, newEnd);

				String original2 = original.replace(first, "");
				String temp3 = temp2.replace(original, original2);
				temp2 = temp3;

				def2=def2+value;
				
			}
			
			def2=def2+") . ";
			int defBegin = temp2.indexOf(def);
			int defEnd = temp2.indexOf(nil, defBegin)+nil.length();
			String toSubstitute = temp2.substring(defBegin, defEnd);
			String definitive = temp2.replace(toSubstitute, def2);
			request = definitive;
		}
		substitute = "<http://www.w3.org/2003/01/geo/wgs84_pos#";
		
		while(request.contains(substitute)){
			dot = true;
			int begin = request.indexOf(substitute);
			int end = request.indexOf(" ", begin);
			String temp = request.substring(begin, end);
			String temp2 = temp.replace(substitute, "");
			String def = temp2.substring(0, temp2.length()-1);
			def="geo:"+def;
			temp2 = request.replace(temp, def);
			request = temp2;
		}

		if (dot){
		String toPoint;
		int lastGraph = request.lastIndexOf("}");
		toPoint = request.substring(0, lastGraph-1);
		toPoint=toPoint+". }";
		request= toPoint;
		
		String withA = request.replace("<http://www.w3.org/1999/02/22-rdf-syntax-ns#type>", "a");
		request = withA;
		}
		return request;
	};
	/*
	private static String readFileAsString(String filePath) throws java.io.IOException {
		StringBuffer fileData = new StringBuffer(1000);
		BufferedReader reader = new BufferedReader(new FileReader(filePath));
		char[] buf = new char[1024];
		int numRead = 0;
		while ((numRead = reader.read(buf)) != -1) {
			String readData = String.valueOf(buf, 0, numRead);
			fileData.append(readData);
			buf = new char[1024];
		}
		reader.close();
		return fileData.toString();
	}
*/
	public void printResults() throws IOException{
		
		if (!ResultMap.totalResults.isEmpty()){
			Iterator iterator = results.keySet().iterator();
			 String firstKey = iterator.next().toString();
			for (int i = 0; i < results.get(firstKey).size(); i++) {
				Iterator iterator2 = results.keySet().iterator();
				out.println("<br />");
				out.println("***********************************");
	
				out.println("<br />");
			    out.println("Risultato numero: "+i);
	
				out.println("<br />");
				while (iterator2.hasNext()) {
				    String key = iterator2.next().toString();
				    ArrayList<String> ArrayValue = results.get(key);
				    out.println("*** "+key + " " + ArrayValue.get(i));
	
					out.println("<br />");
					}
	
				out.println("***********************************\n");
	
				out.println("<br />");
			}
		}else {
			out.println("No results");
			out.println(ResultMap.selectList);
		}
	}
	
	public void printXml() throws IOException {
		try {
			 
			DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
	 
			// root elements
			Document doc = docBuilder.newDocument();
			Element rootElement = doc.createElementNS("http://www.w3.org/2005/sparql-results#","sparql");
			rootElement.setAttributeNS("http://www.w3.org/2000/xmlns/","xmlns:xsi","http://www.w3.org/2001/XMLSchema-instance");
			rootElement.setAttribute("xsi:schemaLocation", "http://www.w3.org/2001/sw/DataAccess/rf1/result2.xsd");
			doc.appendChild(rootElement);
			
	 
			// staff elements
			Element head = doc.createElement("head");
			rootElement.appendChild(head);
			/*
			Iterator iterator2 = ResultMap.totalResults.keySet().iterator();
			while (iterator2.hasNext()) {
			    String key2 = iterator2.next().toString();
			    String key = key2.substring(1);
				Element variable = doc.createElement("variable");
				head.appendChild(variable);
				variable.setAttribute("name", key);;
				}
				*/
			Iterator iterator2 = ResultMap.selectList.iterator();
			while (iterator2.hasNext()) {
			    String key2 = iterator2.next().toString();
			    String key = key2.substring(1);
				Element variable = doc.createElement("variable");
				head.appendChild(variable);
				variable.setAttribute("name", key);;
				}
			// firstname elements
			Element results = doc.createElement("results");
			rootElement.appendChild(results);
			results.setAttribute("distinct", "true");
			results.setAttribute("ordered", "false");
			
			if (!ResultMap.totalResults.isEmpty()){
			
				Iterator iterator = ResultMap.totalResults.keySet().iterator();
				 String firstKey = iterator.next().toString();
				for (int i = 0; i < ResultMap.totalResults.get(firstKey).size(); i++) {
					Iterator iterator3 = ResultMap.totalResults.keySet().iterator();
					
					Element result = doc.createElement("result");
					results.appendChild(result);
					
					while (iterator3.hasNext()) {
					    String key2 = iterator3.next().toString();
					    ArrayList<String> ArrayValue = ResultMap.totalResults.get(key2);
	
						Element binding = doc.createElement("binding");
						result.appendChild(binding);
	
					    String key = key2.substring(1);
						binding.setAttribute("name", key);
						Element type = doc.createElement(ResultMap.varsType.get(key2));
						binding.appendChild(type);
						
						type.appendChild(doc.createTextNode(ArrayValue.get(i)));
						}
	
				}
			}
			
			// write the content into xml file
			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			Transformer transformer = transformerFactory.newTransformer();
			DOMSource source = new DOMSource(doc);
			StreamResult result = new StreamResult(out);
			transformer.transform(source, result);
	 
			System.out.println("File saved!");
	 
		  } catch (ParserConfigurationException pce) {
			pce.printStackTrace();
		  } catch (TransformerException tfe) {
			tfe.printStackTrace();
		  }
		
	}
	
	public void printJson(){
		

		JsonObject root = new JsonObject();
		JsonObject head = new JsonObject();
		JsonArray link = new JsonArray();
		head.put("link", link);
		JsonArray vars = new JsonArray();
		/*
		Iterator iterator2 = ResultMap.totalResults.keySet().iterator();
		while (iterator2.hasNext()) {
		    String key2 = iterator2.next().toString();
		    String key = key2.substring(1);
		    vars.add(key);
			}
		head.put("vars", vars);
		root.put("head", head);
		*/

		Iterator iterator2 = ResultMap.selectList.iterator();
		while (iterator2.hasNext()) {
		    String key2 = iterator2.next().toString();
		    String key = key2.substring(1);
		    vars.add(key);
			}
		head.put("vars", vars);
		root.put("head", head);
		
		JsonObject results = new JsonObject();
		results.put("distinct", "true");
		results.put("ordered", "false");
		
		JsonArray bindings = new JsonArray();
		
		if (!ResultMap.totalResults.isEmpty()){
		
			Iterator iterator = ResultMap.totalResults.keySet().iterator();
			 String firstKey = iterator.next().toString();
			for (int i = 0; i < ResultMap.totalResults.get(firstKey).size(); i++) {
				Iterator iterator3 = ResultMap.totalResults.keySet().iterator();
				JsonObject result = new JsonObject();
				
				while (iterator3.hasNext()) {
				    String key2 = iterator3.next().toString();
				    ArrayList<String> ArrayValue = ResultMap.totalResults.get(key2);
				    String key = key2.substring(1);
				    JsonObject var = new JsonObject();
				    var.put("type", ResultMap.varsType.get(key2));
				    var.put("value", ArrayValue.get(i));
				    result.put(key, var);
					}
				bindings.add(result);
			}
		}
		results.put("bindings", bindings);
		root.put("results",results);
		
		out.println(root.toString());
	}
	

	public void printXhtml(){
		
		String html = "<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Strict//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd\">\n";
		html+="<html>\n<head>\n</head>\n<body>\n";
		html+="<table class=\"sparql\" border=\"1\">\n";
		
		
		/*
		 * Iterator iterator2 = ResultMap.totalResults.keySet().iterator();
		html+="<tr>\n";
		while (iterator2.hasNext()) {
		    String key2 = iterator2.next().toString();
		    String key = key2.substring(1);
		    html+="<th>"+key+"</th>\n";
			}

		html+="</tr>\n";
		 */
		Iterator iterator2 = ResultMap.selectList.iterator();
		html+="<tr>\n";
		while (iterator2.hasNext()) {
		    String key2 = iterator2.next().toString();
		    String key = key2.substring(1);
		    html+="<th>"+key+"</th>\n";
			}

		html+="</tr>\n";
		
		if (!ResultMap.totalResults.isEmpty()){
		
			Iterator iterator = ResultMap.selectList.iterator();
			 String firstKey = iterator.next().toString();
			for (int i = 0; i < ResultMap.totalResults.get(firstKey).size(); i++) {
				Iterator iterator3 = ResultMap.selectList.iterator();
				
	
				html+="<tr>\n";
				
				while (iterator3.hasNext()) {
				    String key2 = iterator3.next().toString();
				    ArrayList<String> ArrayValue = ResultMap.totalResults.get(key2);
				    String key = key2.substring(1);
				    if (ResultMap.varsType.get(key2).equals("uri")){
				    	html+="<th><a href=\""+ArrayValue.get(i)+"\" target=_blank >"+ArrayValue.get(i)+"</a></th>\n";
				    } else
				    {			    
				    	html+="<th>"+ArrayValue.get(i)+"</th>\n";
				    }
				}
	
				html+="</tr>\n";
			}
		
		}
		html+="</table>\n";
		html+="</body>\n</html>";
		
		out.println(html);
	}
	
	 private Map<String, String> getHeadersInfo(HttpServletRequest request) {
		 
			Map<String, String> map = new HashMap<String, String>();
		 
			Enumeration headerNames = request.getHeaderNames();
			while (headerNames.hasMoreElements()) {
				String key = (String) headerNames.nextElement();
				String value = request.getHeader(key);
				map.put(key, value);
			}
		 
			return map;
		  }
	

	    public static String searchpolygons(String input) {
	    	
	    	int count = StringUtils.countMatches(input.toLowerCase(), "polygon");
	    	
	    	int beginsearch = 0;
	    	
	    	for (int i = 0; i<count;i++) {
	    		int beginThispolygon = input.toLowerCase().indexOf("polygon", beginsearch);
	    		
	            String polygonOverSet = input.substring(beginThispolygon);
	    		
	            int begin = polygonOverSet.indexOf("(")+1;
	            String newPolygon = "";
	            System.out.println("verifica "+input.substring(beginThispolygon+"polygon".length(), beginThispolygon+begin-1).trim().replaceAll(" +", " "));
	            if (input.substring(beginThispolygon+"polygon".length(), beginThispolygon+begin-1).trim().replaceAll(" +", " ").length()<2) {
		            
		            int open = 1;
		            char[] full =  polygonOverSet.substring(begin).toCharArray();
		            int step = 0;
		            while ((open!=0)&&(step!=full.length)) {
		            	if (full[step]=='(') {
		            		open++;
		            	}
		            	if (full[step]==')') {
		            		open--;
		            	}
		            	step++;
		            }
		            newPolygon = computepolygons(polygonOverSet.substring(0, begin+step));
		            input = input.substring(0,beginThispolygon)+
		            		newPolygon+
		            		polygonOverSet.substring(begin+step);
	            }else {
	            	newPolygon = "polygon";
	            }
	            
	            beginsearch =beginThispolygon+newPolygon.length();
	    	}
	    	
	    	
	    	return input;
	    }
	    
	    public static String computepolygons(String wkt) {
	    	

	        int totalpolygon = wkt.indexOf("(")+1;
	        int lastTotalpolygon = wkt.lastIndexOf(")");
	        String groupOfpolygon = wkt.substring(totalpolygon , lastTotalpolygon);
	        
	        int count = StringUtils.countMatches(groupOfpolygon, "(");
	        ArrayList<String> polygons = new ArrayList<String>();
	        boolean error =false;
	        for (int i = 0; i<count;i++) {
	        	
	        	int beginpolygon = groupOfpolygon.indexOf("(")+1;
	            int endpolygon = groupOfpolygon.indexOf(")",beginpolygon);
	        	String polygon = 
	        			groupOfpolygon.substring(beginpolygon, endpolygon);
	        	
	        	String[] points = polygon.split(",");
	        	points[0] = points[0].trim().replaceAll(" +", " ");
	    		points[0] = points[0].startsWith(" ") ? points[0].substring(1) : points[0];
	    		points[0] = points[0].endsWith(" ") ? points[0].substring(1) : points[0];
	        	String[] firstcoor = points[0].split(" ");
	        	if (firstcoor.length>=2) {
		        	polygon = firstcoor[1]+ " "+ firstcoor[0];
		        	for (int j = 1; j<points.length;j++) {
		        		points[j] = points[j].trim().replaceAll(" +", " ");
		        		points[j] = points[j].startsWith(" ") ? points[j].substring(1) : points[j];
		        		points[j] = points[j].endsWith(" ") ? points[j].substring(1) : points[j];
		        		String[] coor = points[j].split(" ");
		        		if (coor.length>=2) {
		        			polygon = polygon + ","+coor[1]+ " "+ coor[0];
		        		}else {error=true;}
		        	}
	        	}else {error=true;}
	        	
	        	
	        	polygons.add("("+polygon+")");
	        	groupOfpolygon = groupOfpolygon.substring(endpolygon+1);
	        }
	        String newpolygon = "POLYGON("+StringUtils.join(polygons, ',')+")";
	        if (error) newpolygon = wkt;
	        return newpolygon;
	    }
	
}
