package ca.uqac.info.trace.execution;

import java.io.BufferedReader;
import java.io.IOException;
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
		long endTime, time, memory,startTime ;
		String  strReponse ="";
		String [] tab = command.split("#");
		String cmd;
		final String key;
		final String sep;
		try {
			 if(tab.length == 2)
			 {
				 cmd = tab[1] ;
				 key = tab[0] ;
				 sep = "" ;
			 }else {
				 key = tab[0] ;
				 sep =  tab[1] ;
				 cmd = tab[2] ;
				
			 }
			 //start counter
			 startTime = System.nanoTime();
			// startTime = mx.getCurrentThreadCpuTime();
			 Process process = Runtime.getRuntime().exec(cmd);
			
						
			 BufferedReader stdInput = new BufferedReader(new
					 						InputStreamReader(process.getInputStream()));
			 BufferedReader bufError = new BufferedReader(new
					 						InputStreamReader(process.getErrorStream()));
			 
			 String line;
						
			while (((line = stdInput.readLine()) != null)
					|| ((line = bufError.readLine()) != null)) 
			{
				if (line.contains(key.trim())) 
				{
					String[] tab2 = line.split(sep.trim());
				
					if (tab2.length == 1) {
						strReponse = tab2[0].trim();
					} else {
						strReponse = tab2[1].trim();
					}
					
					System.out.println(strReponse);
					break;
				}
			}

		    //end counter
			 endTime = System.nanoTime();
			//Execution time
			time = endTime - startTime;
			
			// Get the memory consumption
			Runtime runtime = Runtime.getRuntime();

			// run the garbage collactor
			runtime.gc();
			memory = runtime.totalMemory() - runtime.freeMemory();
			
			tabResultat[0] = Math.abs((int) time);
			tabResultat[1] = (int) memory;
			tabResultat[2] = parseReturnValue(strReponse);
			System.out.println( tabResultat[0] +" | " + tabResultat[1] +" | " +tabResultat[2] );
			
			// close
			process.destroy();
			stdInput.close();
			bufError.close();
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
