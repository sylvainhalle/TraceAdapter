package ca.uqac.info.trace.execution;

import java.util.ArrayList;
import java.util.Vector;


public class Maude extends Execution {

	private String homeDir = "/home/aouatef/Tools/";
	private String command ="Maude/maude/maude.linux  ";
	private String strResult = "result #:#";
	@SuppressWarnings("unchecked")
	@Override
	public ArrayList<int[]> executeToTool(Vector<Object> inputLists) {
		ArrayList<int []> arrayResultat = new ArrayList<int []>();
		Vector<String> vectFiles = new Vector<String>()  ;
		
		if(!inputLists.isEmpty())
		{
			vectFiles = (Vector<String>) inputLists.get(0);
				
		 
			
			for(int i = 0 ; i < vectFiles.size() ; i++)
			{
				String strCommand =  homeDir + command +vectFiles.get(i) ;
				arrayResultat.add(this.timeAndMemoryExecution(strResult.concat(strCommand)));
			}
			
		}
		
		return arrayResultat;
	}

	@Override
	public int parseReturnValue(String strValue) 
	{

		int reponseLTL = -99;
		
		if(strValue.equalsIgnoreCase("(true).Bool"))
		{
			reponseLTL = 1;
		}else if(strValue.equalsIgnoreCase("(false).Bool")){
			reponseLTL = 0;
		}
		
		return reponseLTL;
		
	}


}