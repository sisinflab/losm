package sparql;

public class FakeError extends Object  {

    private int lastErrorLine;
    private int lastErrorColumn;
    private String lastErrorWord;
    private int lastPos;
    private String lastBuffer;
    
	public FakeError (int inputLine,int inputColumn,String inputWord,int inputPos, char[] zzBuffer) {
		// TODO Auto-generated constructor stub
		lastErrorLine = inputLine;
		lastErrorColumn = inputColumn;
		lastErrorWord = inputWord;
		lastPos = inputPos;
		lastBuffer = new String(zzBuffer);
	}
	   public String getWord(){
	   	 return lastErrorWord;
	   }
	   
	   public int getLine()
	   {
	     return lastErrorLine;
	   }
	   
	   public int getColumn()
	   {
	     return lastErrorColumn;
	   }
	   public String getBuffer()
	   {
	     return lastBuffer;
	   }

	   public int getPos()
	   {
	     return lastPos;
	   }
}
