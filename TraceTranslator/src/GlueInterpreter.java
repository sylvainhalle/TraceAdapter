/******************************************************************************
  Event trace translator
  Copyright (C) 2012 Sylvain Halle

  This program is free software; you can redistribute it and/or modify
  it under the terms of the GNU Lesser General Public License as published by
  the Free Software Foundation; either version 3 of the License, or
  (at your option) any later version.

  This program is distributed in the hope that it will be useful,
  but WITHOUT ANY WARRANTY; without even the implied warranty of
  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
  GNU General Public License for more details.

  You should have received a copy of the GNU Lesser General Public License along
  with this program; if not, write to the Free Software Foundation, Inc.,
  51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 ******************************************************************************/

import java.io.*;
import java.util.*;

public class GlueInterpreter
{

	/**
	 * @param args
	 */
	public static void main(String[] args)
	{
		// Extract graph from file
		int return_code = 0;
		if (args.length == 0)
			System.exit(1); // Missing filename
		String file_name = args[0];
		List<PipeNode> nodes = getGraphFromFile(file_name);
		if (nodes == null)
			System.exit(1); // Nothing could be made of file contents
		
		// Execute graph
		Map<String,Object> m_results = new HashMap<String,Object>();
		while (!nodes.isEmpty())
		{
			for (PipeNode pn : nodes)
			{
				if (pn.m_inputs.isEmpty())
				{
					// Leaf node
					
				}
			}
		}
		
	    System.out.println(nodes);
		System.exit(return_code);
	}
	
	private static List<PipeNode> getGraphFromFile(String filename)
	{
		List<PipeNode> nodes = new LinkedList<PipeNode>();
		File f = new File(filename);
	    Scanner scanner = null;
	    try
	    {
	    	scanner = new Scanner(f);
	        while(scanner.hasNextLine())
	        {        
	        	String line = scanner.nextLine().trim();
	        	if (line.isEmpty())
	        		continue;
	        	if (line.startsWith("#"))
	        		continue;
	        	String[] parts = line.split("->");
	        	if (parts.length != 2)  // Syntax error
	        	{
	        		scanner.close();
	        		System.exit(1);
	        	}
	        	PipeNode p_from = new PipeNode(parts[0].trim());
	        	PipeNode p_to = new PipeNode(parts[1].trim());
	        	if (!nodes.contains(p_from))
	        		nodes.add(p_from);
	        	if (!nodes.contains(p_from))
	        		nodes.add(p_from);
	        	int p_from_index = nodes.indexOf(p_from);
	        	p_from = nodes.get(p_from_index);
	        	p_from.m_inputs.add(p_to.m_name);
	        	nodes.set(p_from_index, p_from);
	        }
	    }
	    catch (FileNotFoundException e)
	    {
	    	e.printStackTrace();
	    }
	    finally
	    {
	    	if (scanner != null)
	    		scanner.close();
	    }
	    return nodes;
	}
		
	public static class PipeNode
	{
		final String m_name;
		Set<String> m_inputs;
		
		public PipeNode(String name)
		{
			m_name = name;
			m_inputs = new HashSet<String>();
		}
		
		public boolean equals(Object o)
		{
			if (o == null)
				return false;
			if (!(o instanceof PipeNode))
				return false;
			return equals((PipeNode)o);
		}
		
		public boolean equals(PipeNode n)
		{
			if (n == null || n.m_name == null)
				return false;
			return m_name.compareTo(n.m_name) == 0;
		}
		
		public String toString()
		{
			return m_name + m_inputs.toString();
		}
	}

}
