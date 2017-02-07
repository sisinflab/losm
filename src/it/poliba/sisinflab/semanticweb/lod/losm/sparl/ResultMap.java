package it.poliba.sisinflab.semanticweb.lod.losm.sparl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

public class ResultMap {


	public static Map<String , ArrayList<String>> totalResults = new HashMap<>();
	public static Map<String , String> varsType = new HashMap<String , String>();

	public static LinkedList<ResultObject> childhood = new LinkedList<ResultObject>();
	public static ArrayList<FineElement> selectList = new ArrayList<FineElement>();
    public static String path=new String();
	public static boolean recursion = true;
	public ResultMap() {
		// TODO Auto-generated constructor stub
	}
	
	public static void clean() {
		// TODO Auto-generated constructor stub
		totalResults = new HashMap<>();
		varsType = new HashMap<String , String>();
		childhood = new LinkedList<ResultObject>();
		selectList = new ArrayList<FineElement>();
	}
	

}
