package ca.uqac.info.trace.execution;

import java.util.ArrayList;
import java.util.Vector;


public class Monpoly extends Execution {

	private String homeDir = "/home/aouatef/Tools/MonPoly/";//"C:/Benchmark/";
	private String command ="monpoly ";
	private String strResult = "monitorable # is #";
	
	@SuppressWarnings("unchecked")
	@Override
	public ArrayList<int[]> executeToTool(Vector<Object> inputLists) {
		ArrayList<int []> arrayResultat = new ArrayList<int []>();
		Vector<String> vectProp;
		Vector<String> vectFiles ;
		Vector<String> vectsignatures ;
		if(!inputLists.isEmpty())
		{
			vectProp = (Vector<String>) inputLists.get(1);
			vectFiles = (Vector<String>) inputLists.get(0);
			vectsignatures = (Vector<String>) inputLists.get(2);
			
			for(int i = 0 ; i < vectFiles.size() ; i++)
			{
				String strCommand = homeDir + command + " -sig  "+vectsignatures.get(i)  +" -formula  " + vectProp.get(i) + " -log  " + vectFiles.get(i)+"  -check";
				arrayResultat.add(this.timeAndMemoryExecution(strResult.concat(strCommand)));
			}
			
		}
		
		return arrayResultat;
	}

	@Override
	public int parseReturnValue(String strValue) {

		int reponseLTL = -99;
		
		if(strValue.equalsIgnoreCase("monitorable."))
		{
			reponseLTL = 1;
		}else if(strValue.equalsIgnoreCase("not monitorable.")){
			reponseLTL = 0;
		}
		
		return reponseLTL;
	}

}