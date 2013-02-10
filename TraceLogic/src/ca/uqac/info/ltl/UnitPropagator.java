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


/**
 * The UnitPropagator propagates upwards any constants that can be
 * used to simplify the formula.
 * @author Sylvain Hallé
 *
 */
public class UnitPropagator extends GenericOperatorVisitor
{
	public UnitPropagator()
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
  public void visit(OperatorAnd o)
  {
	  Operator right = m_pieces.pop();
	  Operator left = m_pieces.pop();
	  if (left instanceof OperatorFalse || right instanceof OperatorFalse)
	  	m_pieces.push(new OperatorFalse());
	  if (left instanceof OperatorTrue)
	  	m_pieces.push(right);
	  else if (right instanceof OperatorTrue)
	  	m_pieces.push(left);
	  else
	  {
	  	OperatorAnd op = new OperatorAnd();
	  	op.setLeft(left);
	  	op.setRight(right);
	  	m_pieces.push(op);
	  }
  }

	@Override
  public void visit(OperatorOr o)
  {
	  Operator right = m_pieces.pop();
	  Operator left = m_pieces.pop();
	  if (left instanceof OperatorTrue || right instanceof OperatorTrue)
	  	m_pieces.push(new OperatorTrue());
	  if (left instanceof OperatorFalse)
	  	m_pieces.push(right);
	  else if (right instanceof OperatorFalse)
	  	m_pieces.push(left);
	  else
	  {
	  	OperatorOr op = new OperatorOr();
	  	op.setLeft(left);
	  	op.setRight(right);
	  	m_pieces.push(op);
	  }
  }

	@Override
  public void visit(OperatorNot o)
  {
		Operator operand = m_pieces.pop();
		if (operand instanceof OperatorTrue)
			m_pieces.push(new OperatorFalse());
		else if (operand instanceof OperatorFalse)
			m_pieces.push(new OperatorTrue());
		else
		{
			OperatorNot op = new OperatorNot();
			op.setOperand(operand);
			m_pieces.push(op);
		}
  }

	@Override
  public void visit(OperatorF o)
  {
		Operator operand = m_pieces.pop();
		if (operand instanceof OperatorTrue)
			m_pieces.push(new OperatorTrue());
		else
		{
			OperatorF op = new OperatorF();
			op.setOperand(operand);
			m_pieces.push(op);
		}
  }

	@Override
  public void visit(OperatorX o)
  {
		Operator operand = m_pieces.pop();
		if (operand instanceof OperatorFalse)
			m_pieces.push(new OperatorFalse());
		else
		{
			OperatorX op = new OperatorX();
			op.setOperand(operand);
			m_pieces.push(op);
		}
  }

	@Override
  public void visit(OperatorG o)
  {
		Operator operand = m_pieces.pop();
		if (operand instanceof OperatorFalse)
			m_pieces.push(new OperatorFalse());
		else
		{
			OperatorG op = new OperatorG();
			op.setOperand(operand);
			m_pieces.push(op);
		}
  }

	@Override
  public void visit(OperatorImplies o)
  {
	  Operator right = m_pieces.pop();
	  Operator left = m_pieces.pop();
	  if (left instanceof OperatorFalse || right instanceof OperatorTrue)
	  	m_pieces.push(new OperatorTrue());
	  else if (left instanceof OperatorTrue)
	  	m_pieces.push(right);
	  else if (right instanceof OperatorFalse)
	  {
	  	if (left instanceof OperatorFalse)
	  		m_pieces.push(new OperatorTrue());
	  	else if (left instanceof OperatorTrue)
	  		m_pieces.push(new OperatorFalse());
	  	else
	  		m_pieces.push(new OperatorNot(left));
	  }
	  else
	  {
	  	OperatorImplies op = new OperatorImplies();
	  	op.setLeft(left);
	  	op.setRight(right);
	  	m_pieces.push(op);
	  }
  }

	@Override
  public void visit(OperatorEquiv o)
  {
	  Operator right = m_pieces.pop();
	  Operator left = m_pieces.pop();
	  if (left instanceof OperatorTrue)
	  	m_pieces.push(right);
	  else if (left instanceof OperatorFalse)
	  {
	  	if (right instanceof OperatorFalse)
	  		m_pieces.push(new OperatorTrue());
	  	else if (right instanceof OperatorTrue)
	  		m_pieces.push(new OperatorFalse());
	  	else
	  		m_pieces.push(new OperatorNot(right));
	  }
	  else if (right instanceof OperatorTrue)
	  	m_pieces.push(left);
	  else if (right instanceof OperatorFalse)
	  {	  	
	  	if (left instanceof OperatorFalse)
	  		m_pieces.push(new OperatorTrue());
	  	else if (left instanceof OperatorTrue)
	  		m_pieces.push(new OperatorFalse());
	  	else
	  		m_pieces.push(new OperatorNot(left));
	  }
	  else
	  {
	  	OperatorEquiv op = new OperatorEquiv();
	  	op.setLeft(left);
	  	op.setRight(right);
	  	m_pieces.push(op);
	  }
  }

	@Override
  public void visit(Exists o)
  {
		Operator operand = m_pieces.pop();
		Operator path = m_pieces.pop();
		Operator var = m_pieces.pop();
		
		// These two assertions must hold or the formula is incorrectly parsed!
		assert var instanceof Atom;
		assert path instanceof XPathAtom;
		Atom variable = (Atom) var;
		XPathAtom p = (XPathAtom) path;
		
		if (operand instanceof OperatorFalse)
			m_pieces.push(new OperatorFalse());
		else
		{
			ForAll op = new ForAll();
			op.setOperand(operand);
			op.setVariable(variable);
			op.setPath(p);
			m_pieces.push(op);
		}
  }

	@Override
  public void visit(ForAll o)
  {
		Operator operand = m_pieces.pop();
		Operator path = m_pieces.pop();
		Operator var = m_pieces.pop();
		
		// These two assertions must hold or the formula is incorrectly parsed!
		assert var instanceof Atom;
		assert path instanceof XPathAtom;
		Atom variable = (Atom) var;
		XPathAtom p = (XPathAtom) path;
		
		if (operand instanceof OperatorTrue)
			m_pieces.push(new OperatorTrue());
		else
		{
			ForAll op = new ForAll();
			op.setOperand(operand);
			op.setVariable(variable);
			op.setPath(p);
			m_pieces.push(op);
		}
  }
}
