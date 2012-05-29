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

public abstract class UnaryOperator extends Operator
{
	protected Operator m_operand;
	protected String m_symbol;
	
	/*package*/ UnaryOperator()
	{
		m_operand = null;
		m_symbol = "*";
	}
	
	public UnaryOperator(Operator o)
	{
		this();
		m_operand = o;
	}
	
	public Operator getOperand()
	{
		return m_operand;
	}

	public void setOperand(Operator o)
	{
		m_operand = o;
	}
	
	@Override
	public String toString()
	{
		StringBuffer out = new StringBuffer();
		out.append(m_symbol);
		if (m_operand.isAtom())
			out.append(m_operand);
		else
			out.append("(").append(m_operand).append(")");
		return out.toString();
	}
	
	public boolean hasOperand(Operator o)
	{
		return m_operand.equals(o);
	}
	
	public Set<Operator> getSubformulas()
	{
		Set<Operator> out = new HashSet<Operator>();
		out.add(this);
		out.addAll(m_operand.getSubformulas());
		return out;
	}
	
	public final boolean isAtom()
	{
		return false;
	}
	
	public int getDepth()
	{
		return 1 + m_operand.getDepth();
	}
	
  public void accept(OperatorVisitor v)
  {
    m_operand.accept(v);
  }
}
