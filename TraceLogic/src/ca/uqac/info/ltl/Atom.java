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

import java.util.Set;
import java.util.HashSet;

public class Atom extends Operator
{
	protected String m_symbol;
	
	public Atom()
	{
		super();
		m_symbol = "";
	}
	
	public Atom(String s)
	{
		m_symbol = s;
	}
	
	public String getSymbol()
	{
		return m_symbol;
	}
	
	public void setSymbol(String s)
	{
		m_symbol = s;
	}
	
	@Override
	public int hashCode()
	{
		if (m_symbol == null)
			return 0;
		return m_symbol.hashCode();
	}
	
	@Override
	public boolean equals(Object o)
	{
	  if (o == null)
	    return false;
		if (o.getClass() != this.getClass())
			return false;
		return equals((Atom) o);
	}
	
	public boolean equals(Atom a)
	{
		if (a == null)
			return false;
		return m_symbol.compareTo(a.m_symbol) == 0;
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
		return m_symbol;
	}
	
	public int getDepth()
	{
		return 1;
	}
	
  @Override
  public void accept(OperatorVisitor v)
  {
    v.visit(this);
  }
}
