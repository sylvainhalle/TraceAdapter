/*
    LTL trace validation using MapReduce
    Copyright (C) 2012 Sylvain Hallé

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package ca.uqac.info.ltl;

public class XmlPath
{
	protected String[] m_parts;
	
	public XmlPath()
	{
		super();
	}
	
	public XmlPath(String p)
	{
		this();
		if (p == null)
			return;
		m_parts = p.split("/");
	}
	
	@Override
	public String toString()
	{
		if (m_parts == null)
			return "";
		StringBuilder out = new StringBuilder();
		for (int i = 0; i < m_parts.length; i++)
		{
			out.append(m_parts[i]);
			if (i < m_parts.length - 1)
				out.append("/");
		}
		return out.toString();
	}
	
  public void accept(OperatorVisitor v)
  {
    v.visit(this);
  }
}
