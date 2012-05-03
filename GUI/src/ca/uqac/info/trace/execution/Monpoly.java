package ca.uqac.info.trace.execution;

import java.util.ArrayList;
import java.util.Vector;

public class Monpoly extends Execution {

	private String homeDir = "C:/Benchmark/";
	private String command ="/Monpoly/monpoly ";
	
	@SuppressWarnings("unchecked")
	@Override
	public ArrayList<int[]> executeToTool(Vector<Object> inputLists) {
		ArrayList<int []> arrayResultat = new ArrayList<int []>();
		Vector<String> vectProp;
		Vector<String> vectFiles ;
		Vector<String> vectsignatures ;
		if(!inputLists.isEmpty())
		{
			vectProp = (Vector<String>) inputLists.get(0);
			vectFiles = (Vector<String>) inputLists.get(1);
			vectsignatures = (Vector<String>) inputLists.get(2);
			
			for(int i = 0 ; i < vectFiles.size() ; i++)
			{
				String strCommand = homeDir + command + "-sig"+vectsignatures.get(i)  +"-formula " + vectProp.get(0) + "-log" + vectFiles.get(i)+"-check";
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
