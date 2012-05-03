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

	@Override
	public String parseReturnValue(String value) {

		String out = "";
		
		if (value.contains(" is monitorable"))
			out = "TRUE";
		else if (value.contains("is not monitorable"))
			out = "FALSE";
		
		return out;
	}

}
