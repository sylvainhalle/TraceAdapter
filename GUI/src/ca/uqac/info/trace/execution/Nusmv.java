package ca.uqac.info.trace.execution;

import java.util.ArrayList;
import java.util.Vector;

public class Nusmv extends Execution {

	@Override
	public ArrayList<int[]> executeToTool(Vector<Object> inputLists) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String parseReturnValue(String value) {

		String out = "";
		
		if (value.contains("Result: Property is TRUE"))
			out = "TRUE";
		else if (value.contains("Result: Property is FALSE"))
			out = "FALSE";
		
		return out;
	}

}
