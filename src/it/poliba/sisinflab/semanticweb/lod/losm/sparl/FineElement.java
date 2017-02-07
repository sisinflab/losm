package it.poliba.sisinflab.semanticweb.lod.losm.sparl;

import java.util.Map;

public class FineElement {
	
	private String content;	
	private enum SortOfLexeme {
	    WORD,VARIABLE
	}
	SortOfLexeme lexicon;
	
	public FineElement(String content, String type){

		if (type.equals("WORD"))
		{
			this.lexicon = SortOfLexeme.WORD;
		}else{
			this.lexicon = SortOfLexeme.VARIABLE;
		}
		this.content=content;
	}

	public FineElement(FineElement input){
		
		if (input.getLexicon().equals("WORD"))
		{
			this.lexicon = SortOfLexeme.WORD;
		}else{
			this.lexicon = SortOfLexeme.VARIABLE;
		}
		this.content=input.getContent();
	}
	
	public String getContent(){
		String value;
		value = this.content;
		return value;
	}
	

	public void setContent(String value){
		this.content = value;
	}
	
	public String getLexicon(){

		String value;
		switch (this.lexicon) {
        case WORD:
        	value = "WORD";
            break;
                
        case VARIABLE:
        	value = "VARIABLE";
            break;
                     
        default:
        	value = "WORD";
            break;
    }
		return value;
	}
	

	public void setLexicon(String value){
		if (value.equals("WORD"))
		{
			this.lexicon = SortOfLexeme.WORD;
		}else{
			this.lexicon = SortOfLexeme.VARIABLE;
		}
	}
	
	public String toString(){
		return this.content;
	}
	
}
