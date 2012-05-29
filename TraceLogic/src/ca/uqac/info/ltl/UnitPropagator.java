package ca.uqac.info.ltl;

import java.util.Stack;

public class UnitPropagator extends GenericOperatorVisitor
{
	
	protected Stack<Operator> m_pieces;
	
	public UnitPropagator()
	{
		super();
		m_pieces = new Stack<Operator>();
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
	  // TODO Auto-generated method stub
	  
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
  }

	@Override
  public void visit(OperatorImplies o)
  {
	  Operator right = m_pieces.pop();
	  Operator left = m_pieces.pop();
	  if (left instanceof OperatorFalse || right instanceof OperatorTrue)
	  	m_pieces.push(new OperatorTrue());
	  if (left instanceof OperatorTrue)
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
