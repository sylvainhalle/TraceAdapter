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
 * The GenericVisitor does nothing: it reproduces in output the same
 * formula as input. It is intended as a helper class, when one wants
 * to create a Visitor that only overrides a couple of signatures and
 * have some default behaviour for the rest.
 * @author Sylvain Hallé
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
    StringBuffer out = new StringBuffer("(").append(left).append(") ").append(OperatorAnd.SYMBOL).append(" (").append(right).append(")");
    m_pieces.push(out);
  }

  @Override
  public void visit(OperatorOr o)
  {
    StringBuffer right = m_pieces.pop();
    StringBuffer left = m_pieces.pop();
    StringBuffer out = new StringBuffer("(").append(left).append(") ").append(OperatorOr.SYMBOL).append(" (").append(right).append(")");
    m_pieces.push(out);
  }

  @Override
  public void visit(OperatorNot o)
  {
    StringBuffer op = m_pieces.pop();
    StringBuffer out = new StringBuffer(OperatorNot.SYMBOL).append("(").append(op).append(")");
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
    StringBuffer out = new StringBuffer("").append(left).append(") ").append(OperatorEquals.SYMBOL).append(" (").append(right).append("");
    m_pieces.push(out);
  }

  @Override
  public void visit(OperatorImplies o)
  {
    StringBuffer right = m_pieces.pop();
    StringBuffer left = m_pieces.pop();
    StringBuffer out = new StringBuffer("(").append(left).append(") ").append(OperatorImplies.SYMBOL).append(" (").append(right).append(")");
    m_pieces.push(out);
  }

  @Override
  public void visit(OperatorEquiv o)
  {
    StringBuffer right = m_pieces.pop();
    StringBuffer left = m_pieces.pop();
    StringBuffer out = new StringBuffer("(").append(left).append(") ").append(OperatorEquiv.SYMBOL).append(" (").append(right).append(")");
    m_pieces.push(out);
  }

  @Override
  public void visit(OperatorU o)
  {
    StringBuffer right = m_pieces.pop();
    StringBuffer left = m_pieces.pop();
    StringBuffer out = new StringBuffer("(").append(left).append(") ").append(OperatorU.SYMBOL).append(" (").append(right).append(")");
    m_pieces.push(out);
  }

  @Override
  public void visit(ForAll o)
  {
    StringBuffer operand = m_pieces.pop();
    StringBuffer path = m_pieces.pop();
    StringBuffer var = m_pieces.pop();
    StringBuffer out = new StringBuffer(ForAll.SYMBOL).append(var).append(" ").append(Quantifier.ELEMENT_SYMBOL).append(" ").append(path).append(" : (").append(operand).append(")");
    m_pieces.push(out);
  }

  @Override
  public void visit(Exists o)
  {
    StringBuffer operand = m_pieces.pop();
    StringBuffer path = m_pieces.pop();
    StringBuffer var = m_pieces.pop();
    StringBuffer out = new StringBuffer(Exists.SYMBOL).append(var).append(" ").append(Quantifier.ELEMENT_SYMBOL).append(" ").append(path).append(" : (").append(operand).append(")");
    m_pieces.push(out);
  }

  @Override
  public void visit(Atom o)
  {
    m_pieces.push(new StringBuffer(o.toString()));

  }

  @Override
  public void visit(OperatorTrue o)
  {
    m_pieces.push(new StringBuffer(o.toString()));
  }

  @Override
  public void visit(OperatorFalse o)
  {
    m_pieces.push(new StringBuffer(o.toString()));
  }

  @Override
  public void visit(XPathAtom p)
  {
    m_pieces.push(new StringBuffer(p.toString()));

  }
}
