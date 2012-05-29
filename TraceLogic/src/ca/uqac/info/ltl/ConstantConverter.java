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

import java.util.Stack;

public class ConstantConverter extends GenericOperatorVisitor
{
	public ConstantConverter()
	{
		super();
		m_pieces = new Stack<Operator>();
	}
	
	/**
	 * Returns the simplified formula after the processing of the
	 * unit propagations.
	 * @return
	 */
	public Operator getFormula()
	{
		return m_pieces.peek();
	}
	
	@Override
  public void visit(OperatorEquals o)
  {
	  Operator right = m_pieces.pop();
	  Operator left = m_pieces.pop();
	  if (left instanceof Atom && right instanceof Atom)
	  {
	  	if (left.equals(right))
	  		m_pieces.push(new OperatorTrue());
	  	else
	  		m_pieces.push(new OperatorFalse());
	  }
	  else
	  {
	  	OperatorEquals op = new OperatorEquals();
	  	op.setLeft(left);
	  	op.setRight(right);
	  	m_pieces.push(op);
	  }
  }
}
