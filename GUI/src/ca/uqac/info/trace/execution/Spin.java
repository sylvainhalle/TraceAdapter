package ca.uqac.info.trace.execution;

import java.util.ArrayList;
import java.util.Vector;

public class Spin extends Execution {

	private String homeDir = "/home/aouatef/Tools/Spin/";
	private String command ="go ";
	private String strResult = "errors #:#"; // key word # separator
	
	@SuppressWarnings("unchecked")
	@Override
	public ArrayList<int[]> executeToTool(Vector<Object> inputLists) {
		ArrayList<int []> arrayResultat = new ArrayList<int []>();
		Vector<String> vectProp;
		Vector<String> vectFiles ;
		
		
		
		if(!inputLists.isEmpty())
		{
			vectProp = (Vector<String>) inputLists.get(1);
			vectFiles = (Vector<String>) inputLists.get(0);
			
			for(int i = 0 ; i < vectFiles.size() ; i++)
			{
				String strCommand =  homeDir + command + vectFiles.get(i) +" " + vectProp.get(0) ;
				arrayResultat.add(this.timeAndMemoryExecution(strResult.concat(strCommand)));
			}
			
		}
		
		return arrayResultat;
	}

	@Override
	public int parseReturnValue(String strValue) {


		int reponseLTL = -99;
		
		if(strValue.equalsIgnoreCase("1"))
		{
			reponseLTL = 1;
		}else if(strValue.equalsIgnoreCase("0")){
			reponseLTL = 0;
		}
		
		return reponseLTL;
	}

}
