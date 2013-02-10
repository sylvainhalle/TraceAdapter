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
 * The GenericOperatorVisitor does nothing: it reproduces in output the same
 * formula as input. It is intended as a helper class, when one wants
 * to create a Visitor that only overrides a couple of signatures and
 * have some default behaviour for the rest.
 * @author Sylvain Hallé
 *
 */
public class GenericOperatorVisitor implements OperatorVisitor
{

  protected Stack<Operator> m_pieces;

  public GenericOperatorVisitor()
  {
    m_pieces = new Stack<Operator>();
  }

  @Override
  public void visit(OperatorAnd o)
  {
    Operator right = m_pieces.pop();
    Operator left = m_pieces.pop();
    OperatorAnd out = new OperatorAnd();
    out.setLeft(left);
    out.setRight(right);
    m_pieces.push(out);
  }

  @Override
  public void visit(OperatorOr o)
  {
    Operator right = m_pieces.pop();
    Operator left = m_pieces.pop();
    OperatorOr out = new OperatorOr();
    out.setLeft(left);
    out.setRight(right);
    m_pieces.push(out);
  }

  @Override
  public void visit(OperatorNot o)
  {
    Operator op  = m_pieces.pop();
    OperatorNot out = new OperatorNot();
    out.setOperand(op);
    m_pieces.push(out);
  }

  @Override
  public void visit(OperatorF o)
  {
    Operator op  = m_pieces.pop();
    OperatorF out = new OperatorF();
    out.setOperand(op);
    m_pieces.push(out);
  }

  @Override
  public void visit(OperatorX o)
  {
    Operator op  = m_pieces.pop();
    OperatorX out = new OperatorX();
    out.setOperand(op);
    m_pieces.push(out);
  }

  @Override
  public void visit(OperatorG o)
  {
    Operator op  = m_pieces.pop();
    OperatorG out = new OperatorG();
    out.setOperand(op);
    m_pieces.push(out);
  }

  @Override
  public void visit(OperatorEquals o)
  {
    Operator right = m_pieces.pop();
    Operator left = m_pieces.pop();
    OperatorEquals out = new OperatorEquals();
    out.setLeft(left);
    out.setRight(right);
    m_pieces.push(out);
  }

  @Override
  public void visit(OperatorImplies o)
  {
    Operator right = m_pieces.pop();
    Operator left = m_pieces.pop();
    OperatorImplies out = new OperatorImplies();
    out.setLeft(left);
    out.setRight(right);
    m_pieces.push(out);
  }

  @Override
  public void visit(OperatorEquiv o)
  {
    Operator right = m_pieces.pop();
    Operator left = m_pieces.pop();
    OperatorEquiv out = new OperatorEquiv();
    out.setLeft(left);
    out.setRight(right);
    m_pieces.push(out);
  }

  @Override
  public void visit(OperatorU o)
  {
    Operator right = m_pieces.pop();
    Operator left = m_pieces.pop();
    OperatorU out = new OperatorU();
    out.setLeft(left);
    out.setRight(right);
    m_pieces.push(out);
  }

  @Override
  public void visit(Exists o)
  {
    Operator operand = m_pieces.pop();
    Operator path = m_pieces.pop();
    Operator variable = m_pieces.pop();

    // These two assertions must hold or the formula is incorrectly parsed!
    assert variable instanceof Atom;
    assert path instanceof XPathAtom;

    Exists out = new Exists();
    out.setOperand(operand);
    out.setVariable((Atom) variable);
    out.setPath((XPathAtom) path);
    m_pieces.push(out);
  }

  @Override
  public void visit(ForAll o)
  {
    Operator operand = m_pieces.pop();
    Operator path = m_pieces.pop();
    Operator variable = m_pieces.pop();

    // These two assertions must hold or the formula is incorrectly parsed!
    assert variable instanceof Atom;
    assert path instanceof XPathAtom;

    ForAll out = new ForAll();
    out.setOperand(operand);
    out.setVariable((Atom) variable);
    out.setPath((XPathAtom) path);
    m_pieces.push(out);
  }

  @Override
  public void visit(Atom o)
  {
    m_pieces.push(o);
  }

  @Override
  public void visit(OperatorTrue o)
  {
    m_pieces.push(o);
  }

  @Override
  public void visit(OperatorFalse o)
  {
    m_pieces.push(o);
  }

  @Override
  public void visit(XPathAtom xPathAtom)
  {
    m_pieces.push(xPathAtom);
  }

}
