/******************************************************************************
  Execution of trace validation tools
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
package ca.uqac.info.trace.execution;

public class BeepBeepExecution extends Execution
{
	private final String m_jarLocation = "/usr/local/lib/BeepBeepValidator.jar";
	private final String m_key = "Outcome";
	private final String m_sep = ":";
	
	@Override
	protected String getCommandLine()
	{
		StringBuilder out = new StringBuilder();
		out.append("java -jar ").append(m_jarLocation)
			.append(" ").append(m_trace).append(" ").append(m_property);
		return out.toString();
	}
	
	@Override
	/* package */ int parseReturnValue(String strValue)
	{
		String s_val = "";
		int val = -99 ;
		String[] lines = strValue.split(CRLF);
		for (String line : lines)
		{
			if (line.contains(m_key))
			{
				String[] tab2 = line.split(m_sep);

				if (tab2.length == 1)
				{
					s_val = tab2[0].trim();
				}
				else
				{
					s_val = tab2[1].trim();
				}
				if (s_val.equalsIgnoreCase("True"))
				{
					val = 1 ;
				}
				else if (s_val.equalsIgnoreCase("False"))
				{
					val = 0 ;
				}
				else if (s_val.equalsIgnoreCase("Inconclusive"))
				{
					val = -1 ;
				}
				break;
			}
		}
		return val;
	}

	@Override
	public boolean isReady()
	{
		return !(m_trace.isEmpty() || m_property.isEmpty());
	}
	
}
