package ca.uqac.info.trace.execution;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Vector;

public class Beepbeep extends Execution{

	private String homeDir = "/home/aouatef/beepbeep/";
	private String command ="java -jar ";
	private String pointJar = "BeepBeepValidator.jar " ;
	

	/**
	 * Run the traces with their LTL properties
	 * @return The execution time and memory used  
	 */
	@SuppressWarnings("unchecked")
	@Override
	public ArrayList<int[]> executeToTool(Vector<Object> inputLists) {
		
		ArrayList<int []> arrayResultat = new ArrayList<int []>();
		Vector<String> vectProp;
		Vector<String> vectFiles ;
		
		
		
		if(!inputLists.isEmpty())
		{
			vectProp = (Vector<String>) inputLists.get(0);
			vectFiles = (Vector<String>) inputLists.get(1);
			
			for(int i = 0 ; i < vectFiles.size() ; i++)
			{
				String strCommand = command + homeDir + pointJar +" "+ vectFiles.get(i) +"  " + vectProp.get(0) ;
				arrayResultat.add(this.timeAndMemoryExecution(strCommand));
			}
			
		}
		return arrayResultat;
	}
	/** 
	 * @param strNameTools
	 * @param value
	 * @return result of property
	 */
	public int parseReturnValue( String strValue) {
		

		int reponseLTL = -1;
		
		if(strValue.equalsIgnoreCase("true"))
		{
			reponseLTL = 1;
		}else if(strValue.equalsIgnoreCase("false")){
			reponseLTL = 0;
		}
		
		return reponseLTL;

	}

	
}
