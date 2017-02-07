package it.poliba.sisinflab.semanticweb.lod.losm.sparl;

import java.util.ArrayList;

public class CoarseTriple {
	private ArrayList<FineElement> triple = new ArrayList<FineElement>();
	

	public CoarseTriple(ArrayList<FineElement> tripleInput){
		this.triple = new ArrayList<FineElement>();
		for (FineElement e : tripleInput){
			String content = new String(e.getContent());
			String type = new String(e.getLexicon());
			FineElement insert = new FineElement(content,type);
			this.triple.add(insert);
		}
	}
	

	public CoarseTriple(){
		this.triple = new ArrayList<FineElement>();
	}
	

	public CoarseTriple(CoarseTriple input){
		this.triple = new ArrayList<FineElement>();
		for (FineElement e : input.triple){
			String content = new String(e.getContent());
			String type = new String(e.getLexicon());
			FineElement insert = new FineElement(content,type);
			this.triple.add(insert);
		}
	}
	
	public ArrayList<FineElement> getTriple(){
		return this.triple;
		
	}
	public void setTriple(ArrayList<FineElement> tripleInput){
		this.triple = new ArrayList<FineElement>();
		for (FineElement e : tripleInput){
			String content = new String(e.getContent());
			String type = new String(e.getLexicon());
			FineElement insert = new FineElement(content,type);
			this.triple.add(insert);
		}
		
	}
	public void addElement(FineElement element){
		String content = new String(element.getContent());
		String type = new String(element.getLexicon());
		FineElement insert = new FineElement(content,type);
		this.triple.add(insert);
		
	}
	
	public void addElement(CoarseTriple input){
		for (FineElement e : input.triple){
			String content = new String(e.getContent());
			String type = new String(e.getLexicon());
			FineElement insert = new FineElement(content,type);
			this.triple.add(insert);
		}
		
	}
	
	public String toString(){
		String value ="";
		for (FineElement temp : this.triple) {
			value+=temp.getContent()+" ";
		}
		return value;
		
	}

	public String get(int i){
		FineElement value = new FineElement(this.triple.get(i));
		return value.getContent();
	}
	
	public void set(int i,FineElement input){

		String content = new String(input.getContent());
		String type = new String(input.getLexicon());
		FineElement insert = new FineElement(content,type);
		this.triple.set(i,insert);
	}
	

	public FineElement getF(int i){
		return this.triple.get(i);
	}
}
