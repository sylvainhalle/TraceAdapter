package ca.uqac.info.trace.execution;


import java.util.ArrayList;
import java.util.Vector;

public class Saxon extends Execution{

	private String homeDir = "C:/Benchmark/";
	private String command ="java -jar ";
	private String pointJar = "beepbeep/XQueryValidator.jar -m saxon -i " ;
	
	
	@SuppressWarnings("unchecked")
	@Override
	public ArrayList<int[]> executeToTool(Vector<Object> inputLists) {
		ArrayList<int []> arrayResultat = new ArrayList<int []>();
		Vector<String> vectProp;
		Vector<String> vectFiles ;
		
		System.out.println(" \n SAXON \n");
		
		if(!inputLists.isEmpty())
		{
			vectProp = (Vector<String>) inputLists.get(0);
			vectFiles = (Vector<String>) inputLists.get(1);
			
			for(int i = 0 ; i < vectFiles.size() ; i++)
			{
				String strCommand = command + homeDir + pointJar + vectFiles.get(i) +" -f " + vectProp.get(0) ;
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
	@Override
	public int parseReturnValue(String strValue) {

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
