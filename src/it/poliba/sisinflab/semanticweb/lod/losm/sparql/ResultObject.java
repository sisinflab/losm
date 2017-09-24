package sparql;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

public class ResultObject {

	
	public Map<String , ArrayList<CoarseTriple>> sky = new HashMap<String , ArrayList<CoarseTriple>>();
	private Map<String , Boolean> solvedVars;
	public Map<String , String> values;
	private Map<String , ArrayList<String>> varsToEvaluate = new HashMap<>();
	private Map<String , String> varsType = new HashMap<String , String>();
	private ArrayList<ResultObject> childs = new ArrayList<ResultObject>();
    
	private String objectMinKey=new String();
	private int objectMinIndex;
	private String queryToExecute = new String();
	
	public ResultObject(Map<String , ArrayList<CoarseTriple>> skyInput) throws IOException, SAXException, ParserConfigurationException {
		// TODO Auto-generated constructor stub
		copyHashMap(skyInput,sky);
		this.solvedVars = new HashMap<String , Boolean>();
		this.values = new HashMap<String , String>();
		if (ResultMap.recursion){go();}

	}
	
	public ResultObject(Map<String , ArrayList<CoarseTriple>> skyInput,
			Map<String , Boolean> solvedVarsInput,
			Map<String , String> valuesOld,
			Map<String , String> valuesInput) throws IOException, SAXException, ParserConfigurationException {
		// TODO Auto-generated constructor stub
		copyHashMap(skyInput,sky);

		this.solvedVars = new HashMap<String , Boolean>(solvedVarsInput);
		this.values = new HashMap<String , String>(valuesOld);
		Iterator updateIte = valuesInput.keySet().iterator();
		while (updateIte.hasNext()) {
			   String key = updateIte.next().toString();
			   String value = valuesInput.get(key).toString();
			   solvedVars.put(key,true);
			   values.put(key,value);
		}

		if (ResultMap.recursion){go();}
		
	}
	
	public ArrayList<ResultObject> returninGo() throws IOException, SAXException, ParserConfigurationException{
		if (!(this.sky.isEmpty())){
			cleanUp();
			calculatePriority();
			ArrayList<CoarseTriple> triplesToMerge = new ArrayList<CoarseTriple>(this.sky.get(objectMinKey));
			oqueryBuilder(triplesToMerge,objectMinIndex);

			this.varsToEvaluate = executeQuery(queryToExecute,varsType);
			this.sky.remove(objectMinKey);
			setGlobalVarsType();
			generateChilds();
			
		}else{
			
			Iterator iterator2 = this.values.keySet().iterator();

			while (iterator2.hasNext()) {
			   String key = iterator2.next().toString();
			   String value = values.get(key).toString();
			   if (!(ResultMap.totalResults.containsKey(key))){

				   ResultMap.totalResults.put(key, new ArrayList<String>());
			   }
			   ResultMap.totalResults.get(key).add(value);
			}
		}
		
		return this.childs;
		
	}


	public void go() throws IOException, SAXException, ParserConfigurationException{
		if (!(this.sky.isEmpty())){
			cleanUp();
			System.out.println("Post cleanUp:  "+this.sky.toString());
			calculatePriority();
			ArrayList<CoarseTriple> triplesToMerge = new ArrayList<CoarseTriple>(this.sky.get(objectMinKey));
			oqueryBuilder(triplesToMerge,objectMinIndex);
			this.varsToEvaluate = executeQuery(queryToExecute,varsType);
			this.sky.remove(objectMinKey);
			setGlobalVarsType();
			generateChilds();
			
		}else{
			
			Iterator iterator2 = this.values.keySet().iterator();

			while (iterator2.hasNext()) {
			   String key = iterator2.next().toString();
			   String value = values.get(key).toString();
			   if (!(ResultMap.totalResults.containsKey(key))){

				   ResultMap.totalResults.put(key, new ArrayList<String>());
			   }
			   ResultMap.totalResults.get(key).add(value);
			}
		}
		
		
	}

	private void cleanUp(){
		
		Map<String , ArrayList<CoarseTriple>> exitvalue = this.sky;
		
		Iterator iterator = this.sky.keySet().iterator();

		while (iterator.hasNext()) {
			String key = iterator.next().toString();
			ArrayList<CoarseTriple> arrayCoarseTriple = exitvalue.get(key);

			for (CoarseTriple temptriple : arrayCoarseTriple) {
				//siamo nella tripla

				for (FineElement temp : (ArrayList<FineElement>)temptriple.getTriple()) {
					//siamo nell'elemento della tripla
					if ((temp.getLexicon().equals("VARIABLE"))&&(solvedVars.containsKey(temp.getContent()))){
						temp.setLexicon("WORD");
						String valueToAssign = values.get(temp.getContent());
						temp.setContent(valueToAssign);
					}
				}//for temp
	
			}//for temptriple
		  
		}
		this.sky=exitvalue;
	}
	

	private void calculatePriority(){
		


		Iterator iterator = this.sky.keySet().iterator();
		boolean lead = false;
		int minValue = -1;
		int minIndex = -1;
		String minKey = new String();
		int tempMinValue = -1;
		int tempMinIndex = -1;
		int groupMinValue = -1;
		String groupMinKey = new String();

			while (iterator.hasNext()) {
				

				   String key = iterator.next().toString();
				   String value = this.sky.get(key).toString();
					   int i=0;
					   Integer group_peso = 0;
					for (CoarseTriple temptriple : this.sky.get(key)) {
						//siamo nella tripla
						Integer peso = 0;
						
								for (FineElement temp : (ArrayList<FineElement>)temptriple.getTriple()) {
										//siamo nell'elemento della tripla
										if (temp.getLexicon().equals("VARIABLE")){
											peso+=100;
										}else{
											if (temp.getContent().contains("spatial")){
													lead=true;
											}
										} 
								}//for temp
								if (lead){
									peso*=100;
									//da valutare
									
								}
								group_peso+=peso;
								System.out.println(temptriple.toString() + " haValore " + peso+ " - il gruppo haValore " + group_peso);
								 lead=false;
								 if (tempMinValue==-1){
									 tempMinValue=peso;
									 tempMinIndex=i;
								 }else {
									 if (peso<minValue){

										 minValue=peso;
										 minIndex=i;
										 
									 }
									 
								 }
						 
								 i++;
					}//for temptriple
					
					 if (groupMinValue==-1){
						 groupMinValue=group_peso;
						 groupMinKey=key;
						 minIndex=tempMinIndex;
					 }else {
						 if (group_peso<groupMinValue){

							 groupMinValue=group_peso;
							 minIndex=tempMinIndex;
							 groupMinKey=key;
							 
						 }
						 
					 }
			  
			}
			this.objectMinKey=groupMinKey;
			this.objectMinIndex=minIndex;
		
	}
	
	private void oqueryBuilder(ArrayList<CoarseTriple> tripleList,int minIndex){
		
		String query="node";
		boolean isFirst = true;
		CoarseTriple first = new CoarseTriple(tripleList.get(minIndex));
		query= query+tripleTranslator(first,isFirst);
		tripleList.remove(minIndex);
		
		isFirst=false;
		while (!(tripleList.isEmpty())){
			minIndex  = tripleWeights(tripleList);
			first = new CoarseTriple(tripleList.get(minIndex));
			query+=tripleTranslator(first,isFirst);
			tripleList.remove(minIndex);
		}
		query+=";";
		query+="\nout body;";
		
		System.out.println(query);
		this.queryToExecute=query;
		
	}
	

	private void setGlobalVarsType(){
		
		if (!this.varsType.isEmpty()){
			Iterator test = this.varsType.keySet().iterator();
			String firstKey = test.next().toString();
			if(!ResultMap.varsType.containsKey(firstKey)){
				Iterator iterator = this.varsType.keySet().iterator();
				while (iterator.hasNext()) {
					String key = iterator.next().toString();
					String value = this.varsType.get(key).toString();
					if (value.equals("id")){
						ResultMap.varsType.put(key, "uri");
						}else if(value.equals("lat")){
							ResultMap.varsType.put(key, "literal");
							
						}else if(value.equals("lon")){
							ResultMap.varsType.put(key, "literal");
							
						}else if(value.equals("losmUri")){
							ResultMap.varsType.put(key, "uri");
							
						}else if(value.equals("wikipedia")){
							ResultMap.varsType.put(key, "uri");
							
						}else if(value.equals("dbpedia")){
							ResultMap.varsType.put(key, "uri");
							
						}else {
							ResultMap.varsType.put(key, "literal");
							
						}
				}
			}
		}
	}

	private int tripleWeights(ArrayList<CoarseTriple> tripleList){
		


		 ArrayList<Integer> weights = new ArrayList<Integer>();
		 
		int minValue = -1;
		int minIndex = -1;
		String minKey = new String();
		int i=0;
		for (CoarseTriple temptriple : tripleList) {
			//siamo nella tripla
			Integer peso = 0;
			boolean lead=false;
					for (FineElement temp : (ArrayList<FineElement>)temptriple.getTriple()) {
							//siamo nell'elemento della tripla
							if (temp.getLexicon().equals("VARIABLE")){
								peso+=100;
							}else{
								if (temp.getContent().contains("spatial")){
										lead=true;
								}
							} 
								
								
					}//for temp
					if (lead){
						peso/=100;
						
					}
	
					weights.add(peso);
					 
					 lead=false;
					 if (minValue==-1){
						 minValue=peso;
						 minIndex=i;
					 }else {
						 if (peso<minValue){
	
							 minValue=peso;
							 minIndex=i;
							 
						 }
						 
					 }
			 
					 i++;
		}//for temptriple
	return minIndex;
		
	}
	
	private String tripleTranslator(CoarseTriple first,boolean isFirst){
		String pref="\n";
		if (isFirst) pref="";
		String exitvalue="";
		if (first.getTriple().size()>2){
				if (first.getF(0).getLexicon().equals("VARIABLE")&&
						first.getF(1).getContent().startsWith("lgdo:")&&
						(first.getF(2).getContent().startsWith("lgdo:"))){
		
		
					varsType.put(first.getF(0).getContent(), "id");
					varsToEvaluate.put(first.getF(0).getContent(), new ArrayList<String>());
					String predicate = new String();
					predicate = first.getF(1).getContent().substring(first.getF(1).getContent().indexOf("lgdo:")+5);
					String object = new String();
					object = first.getF(2).getContent().substring(first.getF(2).getContent().indexOf("lgdo:")+5);
					
					try {
						
						exitvalue=pref+getOverpassTag(predicate,object);
						System.out.println(exitvalue);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				
				
				} else if (first.getF(0).getLexicon().equals("VARIABLE")&&
						first.getF(1).getContent().equals("rdfs:label")&&
						!(first.getF(2).getLexicon().equals("VARIABLE"))){
					
					varsType.put(first.getF(0).getContent(), "id");
					varsToEvaluate.put(first.getF(0).getContent(), new ArrayList<String>());
					
					exitvalue=pref+"[\"name\"="+first.getF(2).getContent()+"]";
				
				
				} else if (first.getF(0).getLexicon().equals("VARIABLE")&&
						first.getF(1).getContent().equals("a")){
					
					if (first.getF(2).getContent().startsWith("ok:")){
		
						varsType.put(first.getF(0).getContent(), "id");
						varsToEvaluate.put(first.getF(0).getContent(), new ArrayList<String>());
						
					
					
					}else if (first.getF(2).getContent().startsWith("lgdo:")){
		
						varsType.put(first.getF(0).getContent(), "id");
						varsToEvaluate.put(first.getF(0).getContent(), new ArrayList<String>());
		
						try {
							exitvalue=pref+getOverpassTag(first.getF(2).getContent().substring(first.getF(2).getContent().indexOf("lgdo:")+5));
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						
					}else {
		
						varsType.put(first.getF(0).getContent(), "id");
						varsToEvaluate.put(first.getF(0).getContent(), new ArrayList<String>());
						
					}
					
					
				} else if (first.getF(0).getLexicon().equals("VARIABLE")&&
						first.getF(1).getContent().startsWith("losm:")){
		
					varsType.put(first.getF(0).getContent(), "id");
					varsToEvaluate.put(first.getF(0).getContent(), new ArrayList<String>());
					
					if (first.getF(2).getLexicon().equals("VARIABLE")){
						
						String tagToUse = new String();
						tagToUse =  first.getF(1).getContent().substring(first.getF(1).getContent().indexOf("losm:")+5);
						varsType.put(first.getF(2).getContent(),tagToUse);
						varsToEvaluate.put(first.getF(2).getContent(), new ArrayList<String>());
						if (tagToUse.equals("dbpedia")){
							exitvalue=pref+"[\"wikipedia\"]";
							
						}else if (tagToUse.equals("losmUri")){
							
						}else {
							exitvalue=pref+"[\""+tagToUse+"\"]";
							
						}
						
					
					} else {
						
						String tagToUse1 = new String();
						tagToUse1 =  first.getF(1).getContent().substring(first.getF(1).getContent().indexOf("losm:")+5);
						String tagToUse2 = new String();
						tagToUse2 =  first.getF(2).getContent();
						
						if(tagToUse1.equals("dbpedia")){
							
							tagToUse1="wikipedia";
							String tagToUse3 = new String();
							String tagToUse4 = new String();
							int begin = tagToUse2.indexOf("resource/");
							
							if (begin==-1){
								tagToUse3 = tagToUse2;
								tagToUse4 = tagToUse3.substring(1, tagToUse3.length()-1);
							}else{
		
								int begDb = tagToUse2.indexOf("dbpedia");
								int begHt = tagToUse2.indexOf("http://");
								System.out.println("begDb "+begDb+" begHt "+begHt);
								if ((begDb==1)|((begHt+7)==begDb)){
								tagToUse3 = tagToUse2.substring(begin+9);
								tagToUse3 = "en:"+tagToUse3.substring(0, tagToUse3.length()-1);
								tagToUse4 = tagToUse3.replace("_", " ");
								}else if ((begDb==4)|(begDb-begHt)==10){
									if (begDb==4){
										String pre =  tagToUse2.substring(1,3);
										tagToUse3 = tagToUse2.substring(begin+9);
										tagToUse3 = pre+":"+tagToUse3.substring(0, tagToUse3.length()-1);
										tagToUse4 = tagToUse3.replace("_", " ");
									} else {
										String pre =  tagToUse2.substring((begHt+7),begDb-1);
										tagToUse3 = tagToUse2.substring(begin+9);
										tagToUse3 = pre+":"+tagToUse3.substring(0, tagToUse3.length()-1);
										tagToUse4 = tagToUse3.replace("_", " ");
									}
									
								}
							}
							tagToUse2 = tagToUse4;
						} else {
							
							tagToUse2 = tagToUse2.substring(1, tagToUse2.length()-1);
						}
						
		
						exitvalue=pref+"[\""+tagToUse1+"\"=\""+tagToUse2+"\"]";
					}
					
					
				} else if (first.getF(0).getLexicon().equals("VARIABLE")&&
						first.getF(1).getContent().startsWith("spatial:")){
		
					varsType.put(first.getF(0).getContent(), "id");
					varsToEvaluate.put(first.getF(0).getContent(), new ArrayList<String>());
					
					if (first.getF(1).getContent().startsWith("spatial:nearby")){
						Integer radius =Integer.parseInt(first.getF(4).getContent());
						if (first.getTriple().size()>5){
							if (first.getF(5).getContent().contains("km")||
									first.getF(5).getContent().contains("KM")||
									first.getF(5).getContent().contains("Km")||
									first.getF(5).getContent().contains("kM")){
								radius*=1000;
							} else if (first.getF(5).getContent().contains("mi")||
									first.getF(5).getContent().contains("MI")||
									first.getF(5).getContent().contains("Mi")||
									first.getF(5).getContent().contains("mI")){
								radius*=1609;
							}
						}
						exitvalue=pref+"(around:"+radius.toString()+","+first.getF(2).getContent()+
								","+first.getF(3).getContent()+")";
					} else if (first.getF(1).getContent().startsWith("spatial:withinCircle")) {
						Integer radius =Integer.parseInt(first.getF(4).getContent());
						if (first.getTriple().size()>5){
							if (first.getF(5).getContent().contains("km")||
									first.getF(5).getContent().contains("KM")||
									first.getF(5).getContent().contains("Km")||
									first.getF(5).getContent().contains("kM")){
								radius*=1000;
							} else if (first.getF(5).getContent().contains("mi")||
									first.getF(5).getContent().contains("MI")||
									first.getF(5).getContent().contains("Mi")||
									first.getF(5).getContent().contains("mI")){
								radius*=1609;
							}
						}
						exitvalue=pref+"(around:"+radius.toString()+","+first.getF(2).getContent()+
								","+first.getF(3).getContent()+")";
						
					} else if (first.getF(1).getContent().startsWith("spatial:withinBox")) {
						if (first.getTriple().size()>5){
		
							exitvalue=pref+"("+first.getF(2).getContent()+
									","+first.getF(3).getContent()+
									","+first.getF(4).getContent()+
									","+first.getF(5).getContent()+")";
							//Be careful 
							
						}else {
							exitvalue=pref;}
						
					} else if (first.getF(1).getContent().startsWith("spatial:within")) {
		
						if (first.getF(2).getContent().startsWith("\"POLYGON")){
							String polygonWkt = first.getF(2).getContent().substring(first.getF(2).getContent().indexOf("\"POLYGON:")+9);
							System.out.println(polygonWkt);
							String temp = new String();
							polygonWkt = polygonWkt.replace("\"", "");
							polygonWkt = polygonWkt.replace("(", "");
							polygonWkt = polygonWkt.replace(")", "");
							polygonWkt = polygonWkt.replace(",", " ");
							exitvalue=pref+"(poly:\""+polygonWkt+"\")";
						}else {
							exitvalue=pref;}
					}
					
				} else if (first.getF(0).getLexicon().equals("VARIABLE")&&
						(first.getF(1).getContent().equals("geo:lat"))){
		
					varsType.put(first.getF(0).getContent(), "id");
					varsToEvaluate.put(first.getF(0).getContent(), new ArrayList<String>());
		
					varsType.put(first.getF(2).getContent(), "lat");
					varsToEvaluate.put(first.getF(2).getContent(), new ArrayList<String>());
					
					
				} else if (first.getF(0).getLexicon().equals("VARIABLE")&&
						(first.getF(1).getContent().equals("geo:long"))){
		
					varsType.put(first.getF(0).getContent(), "id");
					varsToEvaluate.put(first.getF(0).getContent(), new ArrayList<String>());
		
					varsType.put(first.getF(2).getContent(), "lon");
					varsToEvaluate.put(first.getF(2).getContent(), new ArrayList<String>());
					
					
				} else if (first.getF(0).getLexicon().equals("VARIABLE")&&
						first.getF(1).getContent().equals("rdfs:label")&&
						(first.getF(2).getLexicon().equals("VARIABLE"))){
		
					varsType.put(first.getF(0).getContent(), "id");
					varsToEvaluate.put(first.getF(0).getContent(), new ArrayList<String>());
		
					varsType.put(first.getF(2).getContent(), "name");
					varsToEvaluate.put(first.getF(2).getContent(), new ArrayList<String>());
					
					exitvalue=pref+"[\"name\"]";
				
				} 
	}
		
		if (first.getTriple().size()==2){

			if (first.getF(0).getLexicon().equals("VARIABLE")&&
					first.getF(1).getContent().startsWith("lgdo:")){
	
	
				varsType.put(first.getF(0).getContent(), "id");
				varsToEvaluate.put(first.getF(0).getContent(), new ArrayList<String>());
				String predicate = new String();
				predicate = first.getF(1).getContent().substring(first.getF(1).getContent().indexOf("lgdo:")+5);
				
				try {
					
					exitvalue=pref+getOverpassTag(predicate);
					System.out.println(exitvalue);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			
			
			} else if (first.getF(0).getLexicon().equals("VARIABLE")&&
					first.getF(1).getContent().equals("rdfs:label")){
				
				varsType.put(first.getF(0).getContent(), "id");
				varsToEvaluate.put(first.getF(0).getContent(), new ArrayList<String>());
				
				exitvalue=pref+"[\"name\"]";
			
			
			} else if (first.getF(0).getLexicon().equals("VARIABLE")&&
					first.getF(1).getContent().startsWith("losm:")){
	
				varsType.put(first.getF(0).getContent(), "id");
				varsToEvaluate.put(first.getF(0).getContent(), new ArrayList<String>());
				
					String tagToUse = new String();
					tagToUse =  first.getF(1).getContent().substring(first.getF(1).getContent().indexOf("losm:")+5);
					
					if (tagToUse.equals("dbpedia")){
						exitvalue=pref+"[\"wikipedia\"]";
						
					}else if (tagToUse.equals("losmUri")){
						
					}else {
						exitvalue=pref+"[\""+tagToUse+"\"]";
						
					}
					
				
				
				
			} else if (first.getF(0).getLexicon().equals("VARIABLE")&&
					(first.getF(1).getContent().equals("geo:lat"))){
	
				varsType.put(first.getF(0).getContent(), "id");
				varsToEvaluate.put(first.getF(0).getContent(), new ArrayList<String>());
	
			} else if (first.getF(0).getLexicon().equals("VARIABLE")&&
					(first.getF(1).getContent().equals("geo:long"))){
	
				varsType.put(first.getF(0).getContent(), "id");
				varsToEvaluate.put(first.getF(0).getContent(), new ArrayList<String>());
	
			} 
			
		}
		return exitvalue;
	}
	
	private Map<String , ArrayList<String>>  executeQuery(String query, Map<String , String> varsType) throws IOException, SAXException, ParserConfigurationException{
		

	    Map<String , ArrayList<String>> exitvalue = new HashMap<>(OSMWrapperAPI.queryResolver(query, varsType));
		Iterator iterator = exitvalue.keySet().iterator();

		while (iterator.hasNext()) {
		   String key = iterator.next().toString();
		   String value = exitvalue.get(key).toString();
	}
		return exitvalue;
		
	}
	
	private String getOverpassTag(String input) throws IOException{
		String exitvalue= new String(RdfOverpass.searchDouble(input));
		return exitvalue;
	}

	private String getOverpassTag(String inputKey,String inputTag) throws IOException{
		String exitvalue= new String(RdfOverpass.searchDouble(inputKey,inputTag));
		return exitvalue;
	}
	
	private void generateChilds() throws IOException, SAXException, ParserConfigurationException{
		
		//creiamo l'iteratore sulle variabili dei valori ricevuti (63)
		Iterator iterator = varsToEvaluate.keySet().iterator();
		//recuperiamo la prima chiave
		String firstKey = iterator.next().toString();
		for (int i = 0; i < varsToEvaluate.get(firstKey).size(); i++) {
			//carichiamo il singolo set di variabili nuove da inserire nel nuovo oggetto
			
			Map<String , String> valuesToSend = new HashMap<String , String>();
			Iterator iterator2 = varsToEvaluate.keySet().iterator();

			while (iterator2.hasNext()) {

				   String varKey = iterator2.next().toString();
				   ArrayList<String> value = varsToEvaluate.get(varKey);
			       valuesToSend.put(varKey, value.get(i));
			}
			
			ResultObject toInsert = new ResultObject(this.sky,
					solvedVars,
					values,
					valuesToSend);
			this.childs.add(toInsert);
			
			
		}
	}
	
	//very important to avoid overwriting sky map
	private void copyHashMap(Map<String , ArrayList<CoarseTriple>> from,Map<String , ArrayList<CoarseTriple>> to){
		for (Map.Entry<String, ArrayList<CoarseTriple>>  entry : from.entrySet())
		{
			ArrayList<CoarseTriple> tempTripleArray = new ArrayList<CoarseTriple>();
			for (CoarseTriple coarse: entry.getValue()){
				
				CoarseTriple tempTriple = new CoarseTriple();
				ArrayList<FineElement> tempFineArray = new ArrayList<FineElement>();
				tempFineArray.addAll(coarse.getTriple());
				for(FineElement tempFine : tempFineArray){

					String content = new String(tempFine.getContent());
					String type = new String(tempFine.getLexicon());
					FineElement insert = new FineElement(content,type);
					tempTriple.addElement(insert);
				}
				tempTripleArray.add(tempTriple);
				
			}
			
			to.put(entry.getKey(), tempTripleArray);
		}
		
		
	}
	
}
