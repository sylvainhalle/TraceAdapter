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
