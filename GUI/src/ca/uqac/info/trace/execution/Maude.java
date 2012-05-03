package ca.uqac.info.trace.execution;

import java.util.ArrayList;
import java.util.Vector;

public class Maude extends Execution {

	private String homeDir = "C:/Benchmark/";
	private String command ="maude.exe in ";
	
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

	/** 
	 * @param strNameTools
	 * @param value
	 * @return result of property
	 */
	public int parseReturnValue( String value) {
		
		int out = -1 ;
		
		if (value.contains("Result: Property is TRUE"))
			out = 1;
		else if (value.contains("Result: Property is FALSE"))
			out = 0;
		
		return out;

	}

	@Override
	public int[] timeAndMemoryExecution(String command) {
		// TODO Auto-generated method stub
		return null;
	}

}
