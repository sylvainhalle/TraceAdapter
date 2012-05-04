package ca.uqac.info.trace.execution;

import java.util.ArrayList;
import java.util.Vector;

public class Nusmv extends Execution {

	private String homeDir = "C:/Benchmark/";
	@SuppressWarnings("unused")
	private String command ="NuSMV ";
	
	@SuppressWarnings("unchecked")
	@Override
	public ArrayList<int[]> executeToTool(Vector<Object> inputLists) {
		
		ArrayList<int []> arrayResultat = new ArrayList<int []>();
		Vector<String> vectFiles ;
		
		if(!inputLists.isEmpty())
		{
			vectFiles = (Vector<String>) inputLists.get(0);
			
			for(int i = 0 ; i < vectFiles.size() ; i++)
			{
				String strCommand = homeDir + vectFiles.get(i) ;
				arrayResultat.add(this.timeAndMemoryExecution(strCommand));
			}
			
		}
		
		return arrayResultat;
	}

	@Override
	public int parseReturnValue(String strValue) {
		// TODO Auto-generated method stub
		return 0;
	}


}
