package ca.uqac.info.trace.execution;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Vector;


public class MySQL extends Execution{
	
	//Attributes
	private String command = "mysql --user=root --password= < ";
	 //Attributes of class
    private Connection connexion ;
    private Statement statement;
    private ResultSet resultat;

    // connect to the database
    public void initConnection()
    {
        try
        {
            Class.forName("com.mysql.jdbc.Driver");
            connexion = DriverManager.getConnection("jdbc:mysql://localhost:3306/rfid","root","admin");
            statement = (Statement) connexion.createStatement();
         

         }catch(Exception e)
         {
                 System.out.println("Failed to get connection\n"+ e.getMessage());
         }
    }
    
    // Get the parameter list
    public void verifProp (String prop)
    {
    	 try
         {
    		 resultat = statement.executeQuery("select * from rfid.Traces  where "+prop);
    		 while(resultat.next())
             {
    			System.out.print("  " + resultat.getString(1));
    			System.out.println("  " + resultat.getString(2));
             }
    		 
         }catch (SQLException e) {
        	 System.out.println("Anomalie lors de l'execution de la requête sur la table ");
         }
    }
    /**
     * 
     */
    public void deconnection ()
    {
        try 
        {
            if ( resultat != null)
            {
                resultat.close();
            }
            if(statement!= null)
            {
                statement.close();
            }
            if( connexion != null)
            {
                connexion.close();
            }
        }catch ( Exception e)
        {
            System.out.println(" Echec de deconnexion de la base de donnée" + e.getMessage());
        }
    }
    
	@SuppressWarnings("unchecked")
	@Override
	public ArrayList<int[]> executeToTool(Vector<Object> inputLists)
	{
		ArrayList<int []> arrayResultat = new ArrayList<int []>();
		Vector<String> vectFiles ;
		
		if(!inputLists.isEmpty())
		{
			vectFiles = (Vector<String>) inputLists.get(0);
			
			for(int i = 0 ; i < vectFiles.size() ; i++)
			{
				String strCommand = command +  vectFiles.get(i)  ;
				arrayResultat.add(this.timeAndMemoryExecution(strCommand));
			}
			
		}
		
		return arrayResultat;
	}

	@Override
	public int parseReturnValue(String strValue) {

		int reponseLTL = -1;
		
		if(strValue.equalsIgnoreCase("value.trim().length() > 0"))
		{
			reponseLTL = 1;
		}
		else 
			reponseLTL = 0;
		
		return reponseLTL;
	}
	public static void main (String [] args)
    {
		MySQL ms = new MySQL();
		ms.initConnection() ;
		ms.verifProp("numero = '3' ");
		ms.deconnection() ;
    }

}
