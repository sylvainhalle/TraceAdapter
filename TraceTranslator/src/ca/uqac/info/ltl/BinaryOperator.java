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

public abstract class BinaryOperator extends Operator
{
	protected Operator m_left;
	protected Operator m_right;
	protected String m_symbol;
	protected boolean m_commutes;
	
	/*package*/ BinaryOperator()
	{
		m_left = null;
		m_right = null;
		m_symbol = "*";
		m_commutes = false;
	}
	
	public BinaryOperator(Operator left, Operator right)
	{
		this();
		m_left = left;
		m_right = right;
	}
	
	public Operator getLeft()
	{
		return m_left;
	}
	
	public Operator getRight()
	{
		return m_right;
	}
	
	public void setLeft(Operator o)
	{
		m_left = o;
	}
	
	public void setRight(Operator o)
	{
		m_right = o;
	}
	
	@Override
	public String toString()
	{
		StringBuffer out = new StringBuffer();
		out.append(m_left).append(m_symbol).append(m_right);
		return out.toString();
	}
	
	public int hashCode()
	{
		return m_left.hashCode() + m_right.hashCode();
	}
	
	public boolean equals(BinaryOperator o)
	{
		if (m_left.equals(o.m_left) && m_right.equals(o.m_right))
			return true;
		if (m_commutes && m_left.equals(o.m_right) && m_right.equals(o.m_left))
			return true;
		return false;
	}
	
	public boolean hasOperand(Operator o)
	{
		return m_left.equals(o) || m_right.equals(o);
	}
	
	public Set<Operator> getSubformulas()
	{
		Set<Operator> out = new HashSet<Operator>();
		out.add(this);
		out.addAll(m_left.getSubformulas());
		out.addAll(m_right.getSubformulas());
		return out;
	}
	
	public final boolean isAtom()
	{
		return false;
	}
	
	public int getDepth()
	{
		return 1 + Math.max(m_left.getDepth(), m_right.getDepth());
	}
	
	public void accept(OperatorVisitor v)
	{
	  m_left.accept(v);
	  m_right.accept(v);
	}
}
