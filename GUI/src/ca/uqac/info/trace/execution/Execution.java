package ca.uqac.info.trace.execution;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Vector;

public abstract class Execution {

	/**
	 * Run the traces with their LTL properties
	 * @return The execution time and memory used  
	 */
		
	public abstract ArrayList<int []> executeToTool (Vector<Object> inputLists);
	
	/**
	 * Calculate the time execution and used memory for command
	 * @param command
	 * @return  a table that contains time execution and used memory
	 */
	public int[] timeAndMemoryExecution(String command) {
		
		int[] tabResultat = new int[3];
		long endTime, time, memory ;
		String line = null, strReponse ="";
		int reponseLTL = -1 ;
		
		long startTime = System.currentTimeMillis();
		try {
			  Process process = Runtime.getRuntime().exec(command);
			  endTime = System.currentTimeMillis();
			BufferedReader buf = new BufferedReader(new InputStreamReader(process.getErrorStream()));
			
			while ((line = buf.readLine()) != null) {
            	if(line.contains("Outcome"))
            	{
            		String[] tab = line.split(":");
            		strReponse = tab[1].trim();
            		System.out.println(strReponse);
            	}
            }
			buf.close();
			// String retour = sb.toString();
			time = endTime - startTime;
			
			// Get the memory consumption
			Runtime runtime = Runtime.getRuntime();

			// run the garbage collactor
			runtime.gc();
			memory = runtime.totalMemory() - runtime.freeMemory();
			
			if(strReponse.equalsIgnoreCase("true"))
			{
				reponseLTL = 1;
			}else if(strReponse.equalsIgnoreCase("false")){
				reponseLTL = 0;
			}
			tabResultat[0] = (int) time;
			tabResultat[1] = (int) memory;
			tabResultat[2] = reponseLTL;
			

		} catch (Exception e) {
			e.printStackTrace();
		}
		return tabResultat;

	}
	/** 
	 * @param strNameTools
	 * @param value
	 * @return result of property
	 */

	public abstract int parseReturnValue( String strValue) ;
	
}
