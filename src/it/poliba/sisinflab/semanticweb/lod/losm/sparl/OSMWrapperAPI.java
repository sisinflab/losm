package it.poliba.sisinflab.semanticweb.lod.losm.sparl;
/**
 * (c) Jens Kübler
 * This software is public domain
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL <COPYRIGHT HOLDER> BE LIABLE FOR ANY
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 * 
 */
//package org.osm.lights.extraction.osm;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringWriter;
import java.net.Authenticator;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

//import OSMNode;
//import org.osm.lights.upload.BasicAuthenticator;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
//standard wrapper for overpass API some functions have been modified

public class OSMWrapperAPI {

	private static final String OVERPASS_API = "http://www.overpass-api.de/api/interpreter";
	//private static final String OVERPASS_API = "http://overpass.osm.rambler.ru/cgi/interpreter";
	private static final String OPENSTREETMAP_API_06 = "http://www.openstreetmap.org/api/0.6/";
	private static final String OPENSTREETMAP_URI_BASE = "http://www.openstreetmap.org/node/";
	private static final String LOSM_URI_BASE = "http://sisinflab.poliba.it/semanticweb/lod/losm/resource/";

	public static OSMNode getNode(String nodeId) throws IOException, ParserConfigurationException, SAXException {
		String string = "http://www.openstreetmap.org/api/0.6/node/" + nodeId;
		URL osm = new URL(string);
		HttpURLConnection connection = (HttpURLConnection) osm.openConnection();

		DocumentBuilderFactory dbfac = DocumentBuilderFactory.newInstance();
		DocumentBuilder docBuilder = dbfac.newDocumentBuilder();
		Document document = docBuilder.parse(connection.getInputStream());
		List<OSMNode> nodes = getNodes(document);
		if (!nodes.isEmpty()) {
			return nodes.iterator().next();
		}
		return null;
	}

	/**
	 * 
	 * @param lon the longitude
	 * @param lat the latitude
	 * @param vicinityRange bounding box in this range
	 * @return the xml document containing the queries nodes
	 * @throws IOException
	 * @throws SAXException
	 * @throws ParserConfigurationException
	 */
	@SuppressWarnings("nls")
	private static Document getXML(double lon, double lat, double vicinityRange) throws IOException, SAXException,
			ParserConfigurationException {

		DecimalFormat format = new DecimalFormat("##0.0000000", DecimalFormatSymbols.getInstance(Locale.ENGLISH)); //$NON-NLS-1$
		String left = format.format(lat - vicinityRange);
		String bottom = format.format(lon - vicinityRange);
		String right = format.format(lat + vicinityRange);
		String top = format.format(lon + vicinityRange);

		String string = OPENSTREETMAP_API_06 + "map?bbox=" + left + "," + bottom + "," + right + ","
				+ top;
		URL osm = new URL(string);
		HttpURLConnection connection = (HttpURLConnection) osm.openConnection();

		DocumentBuilderFactory dbfac = DocumentBuilderFactory.newInstance();
		DocumentBuilder docBuilder = dbfac.newDocumentBuilder();
		return docBuilder.parse(connection.getInputStream());
	}

	public static Document getXMLFile(String location) throws ParserConfigurationException, SAXException, IOException {
		DocumentBuilderFactory dbfac = DocumentBuilderFactory.newInstance();
		DocumentBuilder docBuilder = dbfac.newDocumentBuilder();
		return docBuilder.parse(location);
	}

	/**
	 * 
	 * @param xmlDocument 
	 * @return a list of openseamap nodes extracted from xml
	 */
	@SuppressWarnings("nls")
	public static List<OSMNode> getNodes(Document xmlDocument) {
		List<OSMNode> osmNodes = new ArrayList<OSMNode>();
		Node osmRoot = xmlDocument.getFirstChild();
		NodeList osmXMLNodes = osmRoot.getChildNodes();
		for (int i = 1; i < osmXMLNodes.getLength(); i++) {
			Node item = osmXMLNodes.item(i);
			if (item.getNodeName().equals("node")) {
				NamedNodeMap attributes = item.getAttributes();
				NodeList tagXMLNodes = item.getChildNodes();
				Map<String, String> tags = new HashMap<String, String>();
				for (int j = 1; j < tagXMLNodes.getLength(); j++) {
					Node tagItem = tagXMLNodes.item(j);
					NamedNodeMap tagAttributes = tagItem.getAttributes();
					if (tagAttributes != null) {
						tags.put(tagAttributes.getNamedItem("k").getNodeValue(), tagAttributes.getNamedItem("v")
								.getNodeValue());
					}
				}
				Node namedItemID = attributes.getNamedItem("id");
				Node namedItemLat = attributes.getNamedItem("lat");
				Node namedItemLon = attributes.getNamedItem("lon");
				Node namedItemVersion = attributes.getNamedItem("version");

				String id = namedItemID.getNodeValue();
				String latitude = namedItemLat.getNodeValue();
				String longitude = namedItemLon.getNodeValue();
				String version = "0";
				if (namedItemVersion != null) {
					version = namedItemVersion.getNodeValue();
				}

				osmNodes.add(new OSMNode(id, latitude, longitude, version, tags));
			}

		}
		return osmNodes;
	}

	public static List<OSMNode> getOSMNodesInVicinity(double lat, double lon, double vicinityRange) throws IOException,
			SAXException, ParserConfigurationException {
		return OSMWrapperAPI.getNodes(getXML(lon, lat, vicinityRange));
	}


	public static List<OSMNode> getOSMNodesByOverpassQL(String query) throws IOException,
			SAXException, ParserConfigurationException {
		return OSMWrapperAPI.getNodes(getNodesViaOverpass(query));
	}
	
	public static List<OSMNode> getOSMNodesByOverpassQLOnTheFly(String query) throws IOException,
	SAXException, ParserConfigurationException {
		//System.out.println(query);
		return OSMWrapperAPI.getNodes(getNodesViaOverpassString(query));
	}	
	
	/**
	 * 
	 * @param query the overpass query
	 * @return the nodes in the formulated query
	 * @throws IOException
	 * @throws ParserConfigurationException
	 * @throws SAXException
	 */
	public static Document getNodesViaOverpass(String query) throws IOException, ParserConfigurationException, SAXException {
		String hostname = OVERPASS_API;
		String queryString = readFileAsString(query);

		URL osm = new URL(hostname);
		HttpURLConnection connection = (HttpURLConnection) osm.openConnection();
		connection.setDoInput(true);
		connection.setDoOutput(true);
		connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

		DataOutputStream printout = new DataOutputStream(connection.getOutputStream());
		printout.writeBytes("data=" + URLEncoder.encode(queryString, "utf-8"));
		printout.flush();
		printout.close();

		DocumentBuilderFactory dbfac = DocumentBuilderFactory.newInstance();
		DocumentBuilder docBuilder = dbfac.newDocumentBuilder();
		return docBuilder.parse(connection.getInputStream());
	}
	
	public static Document getNodesViaOverpassString(String queryString) throws IOException, ParserConfigurationException, SAXException {
		String hostname = OVERPASS_API;

		URL osm = new URL(hostname);
		HttpURLConnection connection = (HttpURLConnection) osm.openConnection();
		connection.setDoInput(true);
		connection.setDoOutput(true);
		connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

		DataOutputStream printout = new DataOutputStream(connection.getOutputStream());
		printout.writeBytes("data=" + URLEncoder.encode(queryString, "utf-8"));
		printout.flush();
		printout.close();

		DocumentBuilderFactory dbfac = DocumentBuilderFactory.newInstance();
		DocumentBuilder docBuilder = dbfac.newDocumentBuilder();
		return docBuilder.parse(connection.getInputStream());
	}

	/**
	 * 
	 * @param filePath
	 * @return
	 * @throws java.io.IOException
	 */
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

	/**
	 * main method that simply reads some nodes
	 * 
	 * @param args
	 * @throws IOException
	 * @throws SAXException
	 * @throws ParserConfigurationException
	 */
	public static void main(String[] args) throws IOException, SAXException, ParserConfigurationException {
		Authenticator.setDefault(new CustomAuthenticator("vitowalteranelli@gmail.com", "provaopenstreetmap"));
		String path = new File(".").getCanonicalPath();
		
		List<OSMNode> osmNodesInVicinity = getOSMNodesByOverpassQL(path+"/src/queryoverpass");
		for (OSMNode osmNode : osmNodesInVicinity) {
			System.out.println(osmNode.getId() + ":" + osmNode.getLat() + ":" + osmNode.getLon()+ ":" + osmNode.getTags().toString());
		}
		
		Document doc = getNodesViaOverpass(path+"/src/queryoverpass");
		TransformerFactory tf = TransformerFactory.newInstance();
		Transformer transformer;
		try {
			transformer = tf.newTransformer();

			transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
			StringWriter writer = new StringWriter();
			transformer.transform(new DOMSource(doc), new StreamResult(writer));
			String output = writer.getBuffer().toString();
			
			System.out.println(output);
			
			
			
		} catch (TransformerConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (TransformerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	

	public static Map<String , ArrayList<String>> queryResolver(String queryArrived, Map<String , String> varsType) throws IOException, SAXException, ParserConfigurationException {

		Authenticator.setDefault(new CustomAuthenticator("vitowalteranelli@gmail.com", "provaopenstreetmap"));
		Map<String , ArrayList<String>> varsToReturn = new HashMap<>();
		
		Iterator iterator = varsType.keySet().iterator();

		while (iterator.hasNext()) {
		   String key = iterator.next().toString();
		   String value = varsType.get(key).toString();

			varsToReturn.put(key, new ArrayList<String>());
		}
		
		Authenticator.setDefault(new CustomAuthenticator("vitowalteranelli@gmail.com", "provaopenstreetmap"));
		List<OSMNode> osmNodes = getOSMNodesByOverpassQLOnTheFly(queryArrived);
		for (OSMNode osmNode : osmNodes) {
			Iterator iterator2 = varsType.keySet().iterator();
			while (iterator2.hasNext()) {
				   String key = iterator2.next().toString();
				   String value = varsType.get(key).toString();
				   ArrayList<String> temp = new ArrayList<String>();
				   temp = varsToReturn.get(key);
				   if (value.equals("id")){
					   temp.add(OPENSTREETMAP_URI_BASE+osmNode.getId());
					}else if(value.equals("losmUri")){
						   temp.add(LOSM_URI_BASE+osmNode.getId());
						
					}else if(value.equals("lat")){
						   temp.add(osmNode.getLat());
						
					}else if(value.equals("lon")){
						
						String val = osmNode.getLon();
						byte byting[] = val.getBytes("UTF-8");
					    String defval =  new String(byting, "UTF-8");
						temp.add(osmNode.getLon());
						
						
					}else if(value.equals("wikipedia")){
						
						String val = osmNode.getTags().get(value);
						byte byting[] = val.getBytes();
					    String defval =  new String(byting, "UTF-8");
						if (defval.startsWith("http:")){

							
							   temp.add(defval);
							
						}else {
						    String pre = defval.substring(0, 2);
							String defEncoded = URLEncoder.encode(defval.substring(3), "UTF-8");
							String url = "http://"+pre+".wikipedia.org/wiki/"+pre+":"+defEncoded.replace("+", "%20");
						    temp.add(url);
						}
						
						
					}else if(value.equals("dbpedia")){
						String val = osmNode.getTags().get("wikipedia");
						byte byting[] = val.getBytes();
					    String defval =  new String(byting, "UTF-8");
						if (defval.startsWith("http:")){


								int begin = defval.indexOf("wiki/")+5;
								int end =  defval.indexOf("?uselang");
								String tagToUse3 = new String();
								if (end==-1){
									tagToUse3 = defval.substring(begin);
								}else{
									tagToUse3 = defval.substring(begin, end);
								}
								System.out.println("dbpedia affair: "+defval+"  "+tagToUse3);
							    String url = "http://dbpedia.org/resource/"+tagToUse3;
							   temp.add(url);
							
						}else {
							System.out.println("dbpedia affair nohttp: "+defval+"  ");
							String defReduced = defval.substring(3);
						    String url = "http://dbpedia.org/resource/"+defReduced.replace(" ", "_");
						    temp.add(url);
						}
						
					}else {			
						String val = osmNode.getTags().get(value);
						byte byting[] = val.getBytes();
					    String defval =  new String(byting, "UTF-8");
						   temp.add(defval);
						
					}
				   varsToReturn.put(key, temp);
					
				   
				}
			
		}
		
		
		return varsToReturn;
		
		
		
	}
	
	
	public static Map<String , ArrayList<String>> getResourceById(String inputId) throws IOException, SAXException, ParserConfigurationException {

		
		String queryArrived = "node("+inputId+");\nout body;";
		Authenticator.setDefault(new CustomAuthenticator("vitowalteranelli@gmail.com", "provaopenstreetmap"));
		Map<String , ArrayList<String>> varsToReturn = new HashMap<>();
		
		Authenticator.setDefault(new CustomAuthenticator("vitowalteranelli@gmail.com", "provaopenstreetmap"));

		List<OSMNode> osmNodes = getOSMNodesByOverpassQLOnTheFly(queryArrived);
		for (OSMNode osmNode : osmNodes) {
			
			ArrayList<String> temp = new ArrayList<String>();
			temp.add(OPENSTREETMAP_URI_BASE+osmNode.getId());
			varsToReturn.put("losm:openStreetMapNode", temp);
			
			temp = new ArrayList<String>();
			temp.add(osmNode.getId());
			varsToReturn.put("losm:overpassId", temp);
			
			temp = new ArrayList<String>();
			temp.add(osmNode.getLat());
			varsToReturn.put("geo:lat", temp);
			
			temp = new ArrayList<String>();
			temp.add(osmNode.getLon());
			varsToReturn.put("geo:long", temp);
			
			Iterator iterator2 = osmNode.getTags().keySet().iterator();
			while (iterator2.hasNext()) {
				String key = iterator2.next().toString();
				temp = new ArrayList<String>();
				String val = osmNode.getTags().get(key);
				byte byting[] = val.getBytes();
			    String defval =  new String(byting, "UTF-8");
				temp.add(defval);
				varsToReturn.put(key, temp);
				
				
			}
			
	
		}
		
		
		return varsToReturn;
		
		
	}
	
}
