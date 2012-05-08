package ca.uqac.info.trace.execution;

import java.util.ArrayList;
import java.util.Vector;


public class Maude extends Execution {

	private String homeDir = "/home/aouatef/";
	private String command ="/Maude/maude/maude.linux  ";
	
	@SuppressWarnings("unchecked")
	@Override
	public ArrayList<int[]> executeToTool(Vector<Object> inputLists) {
		ArrayList<int []> arrayResultat = new ArrayList<int []>();
		Vector<String> vectFiles ;
		
		if(!inputLists.isEmpty())
		{
			vectFiles = (Vector<String>) inputLists.get(1);
			
			for(int i = 0 ; i < vectFiles.size() ; i++)
			{
				String strCommand =  homeDir + command +vectFiles.get(i) ;
				arrayResultat.add(this.timeAndMemoryExecution(strCommand));
			}
			
		}
		
		return arrayResultat;
	}

	@Override
	public int parseReturnValue(String strValue) 
	{/*

		int reponseLTL = -1;
		
		if(strValue.equalsIgnoreCase("result Bool: (true).Bool"))
		{
			reponseLTL = 1;
		}else if(strValue.equalsIgnoreCase("result Bool: (false).Bool")){
			reponseLTL = 0;
		}*/
		
		return 0;
		
	}


}