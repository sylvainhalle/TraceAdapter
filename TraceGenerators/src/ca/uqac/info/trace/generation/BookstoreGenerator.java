/******************************************************************************
  Event trace generator
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
package ca.uqac.info.trace.generation;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Options;

/**
 * Generate a random trace of requests and responses for the Amazon Bookstore
 * example. The control flow followed by the sequence of messages is detailed in:
 * <blockquote>
 * W.M.P. van der Aalst, M. Pesic. (2007). Specifying and Monitoring Service
 * Flows: Making Web Services Process-Aware. In
 * <cite>Test and Analysis of Web Services</cite>, Springer, 11-55. DOI:
 * 10.1007/978-3-540-72912-9_2.
 * </blockquote>
 * The Bookstore generator follows a fixed Petri net defined
 * internally. As such, it can be considered a specific preset of
 * {@link PetriNetGenerator}.
 * @author sylvain
 *
 */
public class BookstoreGenerator extends PetriNetGenerator
{

	/**
	 * The predefined finite-state machine
	 * @author sylvain
	 *
	 */
	public final String m_petriNetDefinition;
	
	
	/**
	 * Initializes the bookstore generator
	 */
	public BookstoreGenerator()
	{
		StringBuilder petri_net = new StringBuilder();
		petri_net.append("");
		m_petriNetDefinition = petri_net.toString();
	}
	
		
	@Override
	public String getAppName()
	{
		return "Bookstore Generator";
	}

	@Override
	public Options getCommandLineOptions()
	{
		Options options = super.getCommandLineOptions();
		return options;
	}

	@Override
	public void initialize(CommandLine c_line)
	{
		super.initialize(c_line);
		super.parseFromString(m_petriNetDefinition);
	}

	/**
	 * @param args
	 */
	public static void main(String[] args)
	{
		process(args, new BookstoreGenerator());
	}

}
