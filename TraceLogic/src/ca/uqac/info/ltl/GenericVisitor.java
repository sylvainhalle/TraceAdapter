package ca.uqac.info.ltl;

import java.util.Stack;

/**
 * The GenericVisitor does nothing: it reproduces in output the same
 * formula as input. It is intended as a helper class, when one wants
 * to create a Visitor that only overrides a couple of signatures and
 * have some default behaviour for the rest.
 * @author sylvain
 *
 */
public class GenericVisitor implements OperatorVisitor
{
	protected Stack<StringBuffer> m_pieces;

  public GenericVisitor()
  {
    super();
    m_pieces = new Stack<StringBuffer>();
  }
	
	@Override
  public void visit(OperatorAnd o)
  {
    StringBuffer right = m_pieces.pop();
    StringBuffer left = m_pieces.pop();
    StringBuffer out = new StringBuffer("(").append(left).append(") & (").append(right).append(")");
    m_pieces.push(out);
  }

	@Override
  public void visit(OperatorOr o)
  {
    StringBuffer right = m_pieces.pop();
    StringBuffer left = m_pieces.pop();
    StringBuffer out = new StringBuffer("(").append(left).append(") | (").append(right).append(")");
    m_pieces.push(out);
  }

  @Override
  public void visit(OperatorNot o)
  {
    StringBuffer op = m_pieces.pop();
    StringBuffer out = new StringBuffer("! (").append(op).append(")");
    m_pieces.push(out);
  }

  @Override
  public void visit(OperatorF o)
  {
    StringBuffer op = m_pieces.pop();
    StringBuffer out = new StringBuffer("F (").append(op).append(")");
    m_pieces.push(out);
  }

  @Override
  public void visit(OperatorX o)
  {
    StringBuffer op = m_pieces.pop();
    StringBuffer out = new StringBuffer("X (").append(op).append(")");
    m_pieces.push(out);
  }

  @Override
  public void visit(OperatorG o)
  {
    StringBuffer op = m_pieces.pop();
    StringBuffer out = new StringBuffer("G (").append(op).append(")");
    m_pieces.push(out);
  }

	@Override
  public void visit(OperatorEquals o)
  {
    StringBuffer right = m_pieces.pop();
    StringBuffer left = m_pieces.pop();
    StringBuffer out = new StringBuffer("").append(left).append(" = ").append(right).append("");
    m_pieces.push(out);
  }

	@Override
  public void visit(OperatorImplies o)
  {
    StringBuffer right = m_pieces.pop();
    StringBuffer left = m_pieces.pop();
    StringBuffer out = new StringBuffer("(").append(left).append(") -> (").append(right).append(")");
    m_pieces.push(out);
  }

	@Override
  public void visit(OperatorEquiv o)
  {
    StringBuffer right = m_pieces.pop();
    StringBuffer left = m_pieces.pop();
    StringBuffer out = new StringBuffer("(").append(left).append(") <-> (").append(right).append(")");
    m_pieces.push(out);
  }

	@Override
  public void visit(OperatorU o)
  {
    // TODO Auto-generated method stub
    
  }

	@Override
  public void visit(Atom o)
  {
    m_pieces.push(new StringBuffer(o.toString()));
    
  }
}
