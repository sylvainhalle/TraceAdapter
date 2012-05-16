package ca.uqac.info.trace.execution;

import java.util.ArrayList;
import java.util.Vector;

public class Beepbeep extends Execution{

	private String homeDir = "/home/aouatef/Tools/beepbeep/";//C:/Benchmark/";
	private String command ="java -jar ";
	private String pointJar = "BeepBeepValidator.jar " ;
	private String strResult = "Outcome #:#";

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
			vectProp = (Vector<String>) inputLists.get(1);
			vectFiles = (Vector<String>) inputLists.get(0);
			
			for(int i = 0 ; i < vectFiles.size() ; i++)
			{
				String strCommand = command + homeDir + pointJar +" "+ vectFiles.get(i) +"  " + vectProp.get(0) ;
				arrayResultat.add(this.timeAndMemoryExecution(strResult.concat(strCommand)));
			}
			
		}
		return arrayResultat;
	}
	/** 
	 * @param strNameTools
	 * @param value
	 * @return result of property
	 */
	@Override
	public int parseReturnValue(String strValue) {

		int val = -99 ;
		if (strValue.equalsIgnoreCase("True"))
		{
			val = 1 ;
		}else if (strValue.equalsIgnoreCase("False"))
		{
			val = 0 ;
		}else if (strValue.equalsIgnoreCase("Inconclusive"))
		{
			val = -1 ;
		}
		return val;
	}

	
}
