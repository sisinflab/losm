package sparql;
import java.util.Map;


public class OSMNode {
	
	private String id;
	
	private String lat;
	
	private String lon;
	
	private Map<String, String> tags;

	private String version;
	
	public OSMNode(String id, String latitude, String longitude, String version, Map<String, String> tags){
		this.id=id;
		this.lat = latitude;
		this.lon = longitude;
		this.version = version;
		this.tags = tags;
	}
	
	public String getId(){
		String value;
		value = this.id;
		return value;
	}
	

	public void setId(String value){
		this.id = value;
	}
	
	public String getLat(){
		String value;
		value = this.lat;
		return value;
	}
	

	public void setLat(String value){
		this.lat = value;
	}
	
	public String getLon(){
		String value;
		value = this.lon;
		return value;
	}
	

	public void setLon(String value){
		this.lon = value;
	}

	public String getVersion(){
		String value;
		value = this.version;
		return value;
	}

	public Map<String, String> getTags(){
		Map<String, String> value;
		value = this.tags;
		return value;
	}

}
