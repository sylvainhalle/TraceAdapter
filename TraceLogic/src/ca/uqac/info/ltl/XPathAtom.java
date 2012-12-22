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

import java.util.HashSet;
import java.util.Set;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class XPathAtom extends Operator
{
	String[] m_parts;
	
	public XPathAtom(String s)
	{
		super();
		if (s.startsWith("{"))
		{
			// Trim s from braces
			s = s.substring(1, s.length() - 1);
		}
		// Trim leading slash if any
		if (s.startsWith("/"))
			s = s.substring(1);
		m_parts = s.split("/");
	}
	
	public boolean isPresent(Node n)
	{
		return isPresent(n, 0);
	}
	
	protected boolean isPresent(Node n, int p)
	{
		NodeList nl = n.getChildNodes();
		int length = nl.getLength();
		for (int i = 0; i < length; i++)
		{
			Node child_n = nl.item(i);
			if (p == m_parts.length - 1)
			{
				if (child_n.getNodeType() != Node.TEXT_NODE)
					continue;
				if (m_parts[p].compareTo(child_n.getTextContent()) == 0)
					return true;
			}
			else if (m_parts[p].compareTo(child_n.getNodeName()) == 0)
			{
				if (isPresent(child_n, p + 1))
					return true;
			}
		}
		return false;
	}
	
	/**
	 * For the given node, returns the set of values appearing at
	 * the end of the present path.
	 * @param n
	 * @return
	 */
	public Set<String> getValues(Node n)
	{
	  if (n.getNodeName().compareTo(m_parts[0]) == 0)
		return getValues(n, 1, true);
	  return new HashSet<String>();
	}
	
	public Set<String> getValues(Node n, boolean trim_whitespace)
	{
		return getValues(n, 0, trim_whitespace);
	}
	
	protected Set<String> getValues(Node n, int p, boolean trim_whitespace)
	{
		Set<String> out = new HashSet<String>();
		NodeList nl = n.getChildNodes();
		int length = nl.getLength();
		if (p < m_parts.length)
		{
			for (int i = 0; i < length; i++)
			{
				Node child_n = nl.item(i);
				if (m_parts[p].compareTo(child_n.getNodeName()) == 0)
				{
					out.addAll(getValues(child_n, p + 1, trim_whitespace));
				}
			}
		}
		else
		{
			for (int i = 0; i < length; i++)
			{
				Node child_n = nl.item(i);
				if (child_n.getNodeType() == Node.TEXT_NODE)
				{
					String v = child_n.getTextContent();
					if (trim_whitespace)
						v = v.trim();
					out.add(v);
				}
			}
		}
		return out;
	}
	
  @Override
  public void accept(OperatorVisitor v)
  {
    v.visit(this);
  }

	public final boolean isAtom()
	{
		return true;
	}

	@Override
	public Set<Operator> getSubformulas()
	{
		Set<Operator> out = new HashSet<Operator>();
		out.add(this);
		return out;
	}

	@Override
	public boolean hasOperand(Operator o)
	{
		return false;
	}
	
	@Override
	public String toString()
	{
		return toString(true);
	}
	
	public String toString(boolean leadingSlash)
	{
		StringBuilder out = new StringBuilder();
		if (leadingSlash)
			out.append("/");
		for (int i = 0; i < m_parts.length; i++)
		{
			out.append(m_parts[i]);
			if (i < m_parts.length - 1)
				out.append("/");
		}
		return out.toString();
	}
	
	public int getDepth()
	{
		return 1;
	}
	
	@Override
	public boolean equals(Object o)
	{
		if (o == null)
			return false;
		if (!(o instanceof XPathAtom))
			return false;
		return equals((XPathAtom) o);
	}
	
	/**
	 * Two paths are equal if they have the same length their respective
	 * parts are identical
	 * @param x
	 * @return
	 */
	public boolean equals(XPathAtom x)
	{
		if (x == null)
			return false;
		assert x != null;
		if (m_parts.length != x.m_parts.length)
			return false;
		for (int i = 0; i < m_parts.length; i++)
		{
			if (m_parts[i].compareTo(x.m_parts[i]) != 0)
				return false;
		}
		return true;
	}
	
	@Override
	public int hashCode()
	{
		return m_parts[m_parts.length - 1].hashCode();
	}
	
	/**
	 * Gets the "flattened" name of the XPath atom --that is,
	 * the last part of the path.
	 * @return The flat name
	 */
	public String getFlatName()
	{
	  return m_parts[m_parts.length - 1];
	}
}
