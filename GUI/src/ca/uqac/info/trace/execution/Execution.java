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
		StringBuilder sb = new StringBuilder();
		StringBuffer out = new StringBuffer();
		int[] result = new int[2];

		long startTime = System.currentTimeMillis();
		try {
			final Process process = Runtime.getRuntime().exec(command);
			long endTime = System.currentTimeMillis();
			BufferedReader buf = new BufferedReader(new InputStreamReader(
					process.getInputStream()));
			String line;
			while ((line = buf.readLine()) != null) {
				sb.append(line);
				sb.append("\n");

			}
			buf.close();
			// String retour = sb.toString();
			long time = endTime - startTime;
			out.append("Total elapsed time in execution of runcommand() is :")
					.append(time).append("\n");

			// Get the memory consumption
			Runtime runtime = Runtime.getRuntime();

			// run the garbage collactor
			runtime.gc();
			long memory = runtime.totalMemory() - runtime.freeMemory();
			out.append("Used memory is bytes : ").append(memory).append("\n");
			// System.out.print(out);
			// System.out.println(parseReturnValue( retour));

			result[0] = (int) time;
			result[1] = (int) memory;

		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;

	}
	/** 
	 * @param strNameTools
	 * @param value
	 * @return result of property
	 */
	public abstract String parseReturnValue( String value) ;
	
}
