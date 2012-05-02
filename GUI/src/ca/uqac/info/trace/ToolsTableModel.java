package ca.uqac.info.trace;

import javax.swing.table.AbstractTableModel;

public class ToolsTableModel  extends AbstractTableModel{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	//attributes of class
	private  int colnum ;
	private  int rownum ;
	private String[]colNames ;
	private String [][] data ;
	
	public String[] getColNames() {
		return colNames;
	}
	public String[][] getData() {
		return data;
	}
	public void setData(String[][] data) {
		this.data = data;
	}
	public void setColNames(String[] colNames) {
		this.colNames = colNames;
	}
	public int getColnum() {
		return colnum;
	}
	public void setColnum(int colnum) {
		this.colnum = colnum;
	}
	public int getRownum() {
		return rownum;
	}
	public void setRownum(int rownum) {
		this.rownum = rownum;
	}
	public ToolsTableModel( String[] toolsSelected)
	{
		colNames = toolsSelected ;
	}
	@Override
	public int getColumnCount() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getRowCount() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public Object getValueAt(int arg0, int arg1) {
		// TODO Auto-generated method stub
		return null;
	}

}
