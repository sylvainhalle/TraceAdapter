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
		String [] tab = command.split("#");
		
		long startTime = System.currentTimeMillis();
		try {
			  Process process = Runtime.getRuntime().exec(tab[2]);
			  endTime = System.currentTimeMillis();
			BufferedReader buf = new BufferedReader(new InputStreamReader(process.getErrorStream()));
			BufferedReader stdInput = new BufferedReader(new InputStreamReader(process.getInputStream()));

		    BufferedReader stdError = new BufferedReader(new InputStreamReader(process.getErrorStream()));
			
		    while ((line = stdInput.readLine()) != null) {
		    	if(line.contains(tab[0].trim()))
            	{
            		String[] tab2 = line.split(tab[1].trim());
            		strReponse = tab2[1].trim();
            		System.out.println(strReponse);
            		break;
            	}
            }
			
		    while ((line = stdError.readLine()) != null) {
		    	if(line.contains(tab[0].trim()))
            	{
            		String[] tab2 = line.split(tab[1].trim());
            		strReponse = tab2[1].trim();
            		System.out.println(strReponse);
            		break;
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
			
			
			tabResultat[0] = (int) time;
			tabResultat[1] = (int) memory;
			tabResultat[2] = parseReturnValue(strReponse);
			System.out.println( tabResultat[0] +" | " + tabResultat[1] +" | " +tabResultat[2] );
			

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
