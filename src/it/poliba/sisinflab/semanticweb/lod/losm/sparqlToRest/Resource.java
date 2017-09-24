package sparqlToRest;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import com.sun.org.apache.xml.internal.utils.URI;

import sparql.OSMWrapperAPI;

/**
 * Servlet implementation class Resource
 */
@WebServlet({ "/resource/*" })
public class Resource extends HttpServlet {
	private static final long serialVersionUID = 1L;

	PrintWriter out;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public Resource() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    	response.setContentType("text/html; charset=UTF-8");
    	response.setCharacterEncoding("UTF-8");

    	out = response.getWriter();
		URI uri = new URI("http://dot.dot"+request.getRequestURI());
		String[] segments = uri.getPath().split("/");
		String id = segments[getValue(segments)+1];
		Map<String , ArrayList<String>> varsTaken = new HashMap<>();
		try {
			varsTaken = OSMWrapperAPI.getResourceById(id);
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		}

		out.println(buildXhtml(id,varsTaken));
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}
	private String buildXhtml(String id,Map<String , ArrayList<String>> inputData){
		String html = new String();
		String home = "/semanticweb/lod/losm";
		
		html="<?xml version=\"1.0\" encoding=\"UTF-8\" ?>\n"+
				"<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML+RDFa 1.0//EN\" \"http://www.w3.org/MarkUp/DTD/xhtml-rdfa-1.dtd\">\n"+
				"<!--<html xmlns=\"http://www.w3.org/1999/xhtml\" \n"+
				"    xmlns:dbpprop=\"http://dbpedia.org/property/\" \n"+
				"    xmlns:foaf=\"http://xmlns.com/foaf/0.1/\" \n"+
				"    version=\"XHTML+RDFa 1.0\" xml:lang=\"en\">-->\n"+
				"<html  \n"+
				"    version=\"XHTML+RDFa 1.0\" xml:lang=\"en\">\n"+
				"\n";
		html+="<!-- header -->\n"+
				"<head >\n"+
				"    <title>"+id+"</title>\n";
		html+=			"    <link rel=\"stylesheet\" type=\"text/css\" href=\""+home+"/css/describerStyle.css\" />\n"+
						"    <link href=\""+home+"/css/describerHighlighter.css\" type=\"text/css\" rel=\"stylesheet\" charset=\"utf-8\" />\n"+
						"    <link rel=\"foaf:primarytopic\" href=\"http://sisinflab.poliba.it/semanticweb/lod/losm/resource/"+id+"\"/>\n"+
						"    <link rev=\"describedby\" href=\"http://sisinflab.poliba.it/semanticweb/lod/losm/resource/"+id+"\"/>\n";
		html+="</head>\n"+
				"<body >\n"+
				"    <div id=\"header\">\n"+
				"      <div id=\"hd_l\">\n"+
				"	  <h1 id=\"title\">About: <a href=\"http://sisinflab.poliba.it/semanticweb/lod/losm/resource/"+id.toString()+"\">"+id.toString()+"</a></h1>\n"+
				"        <div id=\"homelink\">\n"+
				"          <!--?vsp if (white_page = 0) http (txt); ?-->\n"+
				"        </div>\n"+
				"	<div class=\"page-resource-uri\">\n"+
				"	    An Entity of Type : <a href=\"#\">Node</a>, \n"+
				"	    from Named Graph : <a href=\"http://sisinflab.poliba.it/semanticweb/lod/losm\">/losm</a>, \n"+
				"	    within Data Space : <a href=\"http://sisinflab.poliba.it/semanticweb/lod/losm\">/losm</a>\n"+
				"        </div>\n"+
				"      </div> <!-- hd_l -->\n"+
				"      <div id=\"hd_r\">\n"+
				"	  <a href=\"http://sisinflab.poliba.it\" title=\"About Sisinf Lab\">\n"+
				"	      <img src=\""+home+"/images/logosisinf.png\" height=\"64\" alt=\"About Sisinf Lab\"/>\n"+
				"        </a>\n"+
				"      </div> <!-- hd_r -->\n"+
				"    </div> <!-- header -->\n"+
				"    <div id=\"content\">\n"+
				"\n"+
				"      <p>Description: </p>\n"+
				"<!-- proptable -->\n"+
				"      <table class=\"description\"><tr><th>Property</th><th>Value</th></tr>\n"+
				"\n";
		boolean parity = true;
		String parityString = new String();
		SortedSet<String> keys = new TreeSet<String>(inputData.keySet());
		for (String key : keys) { 
		   String value = inputData.get(key).get(0);
		   
		   if (parity){
			   parityString = "odd";
			   parity=false;
		   }else{
			   parityString = "even";
			   parity=true;
		   }
		   
		  html+= "<tr class=\""+parityString+"\">"+
				  "<td class=\"property\">"+
				  "<a class=\"uri\" href=\"#\" style=\"color:#0000ee;\">"+
				  "<small>losm:</small>"+key+"</a>\n"+
				  "</td><td><ul>\n"+
				  "	<li><span class=\"literal\">"+
				  "<span property=\"#\" xml:lang=\"en\">"+value+"</span>"+
				  "</span></li>\n"+
				  "</ul></td></tr>";
		   
		   
		}
		html+="      </table>\n"+
				"    </div> <!--  #content -->\n"+
				"<!-- footer -->\n"+
				"    <div id=\"footer\">\n"+
				"      <div id=\"ft_t\">\n"+
				" <!--       Browse using:\n"+
				"	<a href=\"http://linkeddata.uriburner.com/ode/?uri=http%3A%2F%2Fdbpedia.org%2Fresource%2F50_Berkeley_Square\">OpenLink Data Explorer</a> |\n"+
				"	<a href=\"/describe/?uri=http%3A%2F%2Fdbpedia.org%2Fresource%2F50_Berkeley_Square\">OpenLink Faceted Browser</a>\n"+
				"        &nbsp; &nbsp; Raw Data in:\n"+
				"	\n"+
				"        <a href=\"http://dbpedia.org/sparql?default-graph-uri=http%3A%2F%2Fdbpedia.org&amp;query=DESCRIBE+&lt;http://dbpedia.org/resource/50_Berkeley_Square&gt;&amp;format=text%2Fcsv\">CSV</a> | RDF (\n"+
				"        <a href=\"http://dbpedia.org/data/50_Berkeley_Square.ntriples\">N-Triples</a> \n"+
				"        <a href=\"http://dbpedia.org/data/50_Berkeley_Square.n3\">N3/Turtle</a> \n"+
				"	<a href=\"http://dbpedia.org/data/50_Berkeley_Square.json\">JSON</a> \n"+
				"        <a href=\"http://dbpedia.org/data/50_Berkeley_Square.rdf\">XML</a> ) | OData (\n"+
				"	<a href=\"http://dbpedia.org/data/50_Berkeley_Square.atom\">Atom</a> \n"+
				"	<a href=\"http://dbpedia.org/data/50_Berkeley_Square.jsod\">JSON</a> )| Microdata (\n"+
				"	<a href=\"http://dbpedia.org/sparql?default-graph-uri=http%3A%2F%2Fdbpedia.org&amp;query=DESCRIBE+&lt;http://dbpedia.org/resource/50_Berkeley_Square&gt;&amp;output=application%2Fmicrodata%2Bjson\">JSON</a>\n"+
				"        <a href=\"http://dbpedia.org/sparql?default-graph-uri=http%3A%2F%2Fdbpedia.org&amp;query=DESCRIBE+&lt;http://dbpedia.org/resource/50_Berkeley_Square&gt;&amp;output=text%2Fhtml\">HTML</a>) |  \n"+
				"        <a href=\"http://dbpedia.org/sparql?default-graph-uri=http%3A%2F%2Fdbpedia.org&amp;query=DESCRIBE+&lt;http://dbpedia.org/resource/50_Berkeley_Square&gt;&amp;output=application%2Fld%2Bjson\">JSON-LD</a> \n"+
				"\n"+
				"        &nbsp; &nbsp;<a href=\"http://wiki.dbpedia.org/about/dbpedia-community/imprint\">About</a>&nbsp; &nbsp;\n"+
				"     -->  </div> <!-- #ft_t -->\n"+
				"      <div id=\"ft_b\">\n"+
				"   <!--     <a href=\"http://virtuoso.openlinksw.com\" title=\"OpenLink Virtuoso\"><img class=\"powered_by\" src=\"/statics/virt_power_no_border.png\" alt=\"Powered by OpenLink Virtuoso\"/></a>\n"+
				"        <a href=\"http://linkeddata.org/\"><img alt=\"This material is Open Knowledge\" src=\"/statics/LoDLogo.gif\"/></a> &nbsp;\n"+
				"        <a href=\"http://dbpedia.org/sparql\"><img alt=\"W3C Semantic Web Technology\" src=\"/statics/sw-sparql-blue.png\"/></a> &nbsp;  &nbsp;\n"+
				"        <a href=\"http://www.opendefinition.org/\"><img alt=\"This material is Open Knowledge\" src=\"/statics/od_80x15_red_green.png\"/></a>\n"+
				"	<span about=\"\"\n"+
				"	resource=\"http://www.w3.org/TR/rdfa-syntax\"\n"+
				"	rel=\"dc:conformsTo\" xmlns:dc=\"http://purl.org/dc/terms/\">\n"+
				"	<a href=\"http://validator.w3.org/check?uri=referer\"><img\n"+
				"	    src=\"http://www.w3.org/Icons/valid-xhtml-rdfa\"\n"+
				"	    alt=\"Valid XHTML + RDFa\" height=\"27\" /></a>\n"+
				"	</span>\n"+
				"   -->  </div> <!-- #ft_b -->\n"+
				"      <div id=\"ft_ccbysa\">\n"+
				"	This content was extracted from <a href=\"http://openstreetmap.org/node/"+id+"\">OpenStreetMap</a> and is licensed under the <a href=\"http://creativecommons.org/licenses/by-sa/3.0/\">Creative Commons Attribution-ShareAlike 3.0 Unported License</a>\n"+
				"\n"+
				"      </div> <!-- #ft_ccbysa -->\n"+
				"    </div> <!-- #footer -->\n"+
				"    <!--script type=\"text/javascript\">\n"+
				"     Place any Javascript code e.g. Google Analytics scripts \n"+
				"    </script-->\n"+
				"    \n"+
				" </body>\n"+
				"</html>\n"+
				"\n";
		
		return html;
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
	 
		private static int getValue(String[] segments){
			int exitvalue=-1;
			 for (int i = 0; i < segments.length; i++)
			    {
			        if (segments[i].equals("resource")){
			        	exitvalue=i;
			        }
			    }
			
			return exitvalue;
		}
}
