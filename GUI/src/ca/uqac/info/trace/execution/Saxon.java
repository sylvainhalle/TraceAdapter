package ca.uqac.info.trace.execution;


import java.util.ArrayList;
import java.util.Vector;

public class Saxon extends Execution{

	private String homeDir = "C:/Benchmark/";
	private String command ="java -jar ";
	private String pointJar = "XQueryValidator.jar -m symbolic -i " ;
	private String strResult = "Result#";
	
	
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
				String strCommand = command + homeDir + pointJar + vectFiles.get(i) +" -f " + vectProp.get(1) ;
				arrayResultat.add(this.timeAndMemoryExecution(strResult.concat(strCommand)));
			}
			
		}
		
		return arrayResultat;
	}


	@Override
	public int parseReturnValue(String strValue) {

		int reponseLTL = -1;
		String [] strTab = strValue.split(" ");
		strValue = strTab[strTab.length - 1] ;
		
		if(strValue.equalsIgnoreCase("true"))
		{
			reponseLTL = 1;
		}else if(strValue.equalsIgnoreCase("false")){
			reponseLTL = 0;
		}if(strValue.equalsIgnoreCase("INCONCLUSIVE")){
			reponseLTL = -1;
		}
		return reponseLTL;
	}

	/** 
	 * @param strNameTools
	 * @param value
	 * @return result of property
	 */

}
