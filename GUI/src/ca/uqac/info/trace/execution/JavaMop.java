package ca.uqac.info.trace.execution;

import java.util.ArrayList;
import java.util.Vector;

public class JavaMop extends Execution {

	@Override
	public ArrayList<int[]> executeToTool(Vector<Object> inputLists) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
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
