package ca.uqac.info.trace;

import java.util.ArrayList;
import java.util.Vector;

import ca.uqac.info.trace.execution.Execution;

public class ExecutionThread extends Thread{

	// attribute ArrayList
	private ArrayList<int []> listResultat ;
	protected Execution clsExec ;
	protected Vector<Object> listParams ;
	private int numero ;
	
	// construct
	public ExecutionThread (int num , Execution exec , Vector<Object> fileTrace)
	{
		this.clsExec = exec ;
		this.listParams = fileTrace ;
		this.numero = num ;
		listResultat = new ArrayList<int[]>();
	}

	public ArrayList<int []> getListResultat() {
		return listResultat;
	}

	public void setListResultat(ArrayList<int []> listResultat) {
		this.listResultat = listResultat;
	}
	
	@Override
	public void run ()
	{ 
		this.setListResultat(clsExec.executeToTool(listParams));
		
//		try {
//			this.sleep(2000);
//		} catch (InterruptedException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
	}
	
	/**
	 * Return the number of thread
	 * @return
	 */
	
	public int getNumber ()
	{
	  return this.numero ;
	}
	
}
