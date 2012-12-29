/******************************************************************************
  Event trace translator
  Copyright (C) 2012 Sylvain Halle

  This program is free software; you can redistribute it and/or modify
  it under the terms of the GNU Lesser General Public License as published by
  the Free Software Foundation; either version 3 of the License, or
  (at your option) any later version.

  This program is distributed in the hope that it will be useful,
  but WITHOUT ANY WARRANTY; without even the implied warranty of
  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
  GNU General Public License for more details.

  You should have received a copy of the GNU Lesser General Public License along
  with this program; if not, write to the Free Software Foundation, Inc.,
  51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 ******************************************************************************/
package ca.uqac.info.ltl;

/**
 * Implementation of the
 * <a href="http://en.wikipedia.org/wiki/Visitor_pattern">visitor
 * pattern</a> to traverse LTL syntax trees. Usage for a visitor:
 * <ol>
 * <li>Instantiate a visitor <tt>v</tt></li>
 * <li>Feed it to some operator <tt>op</tt> by calling
 *  <tt>op.accept(v)</tt>
 * <li>Internally, the operator will first call <tt>accept</tt>
 * on its operand(s), and then call <tt>v.visit(this)</tt></li>
 * </ol>
 * The end result is that the visitor's <tt>accept</tt> method
 * will be called exactly once for each element of the syntax tree,
 * in a sequence corresponding to a postfix traversal.
 * 
 * @author Sylvain Hallé
 *
 */
public interface OperatorVisitor
{
  /**
   * Visits an operator
   * @param o The operator
   */
  public void visit(OperatorAnd o);

  /**
   * Visits an operator
   * @param o The operator
   */
  public void visit(OperatorOr o);
  
  /**
   * Visits an operator
   * @param o The operator
   */
  public void visit(OperatorNot o);
  
  /**
   * Visits an operator
   * @param o The operator
   */
  public void visit(OperatorF o);
  
  /**
   * Visits an operator
   * @param o The operator
   */
  public void visit(OperatorX o);
  
  /**
   * Visits an operator
   * @param o The operator
   */
  public void visit(OperatorG o);
  
  /**
   * Visits an operator
   * @param o The operator
   */
  public void visit(OperatorEquals o);
  
  /**
   * Visits an operator
   * @param o The operator
   */
  public void visit(OperatorImplies o);
  
  /**
   * Visits an operator
   * @param o The operator
   */
  public void visit(OperatorEquiv o);
  
  /**
   * Visits an operator
   * @param o The operator
   */
  public void visit(OperatorU o);
  
  /**
   * Visits an operator
   * @param o The operator
   */
  public void visit(Exists o);
  
  /**
   * Visits an operator
   * @param o The operator
   */
  public void visit(ForAll o);
  
  /**
   * Visits an operator
   * @param o The operator
   */
  public void visit(Atom o);
  
  /**
   * Visits an operator
   * @param o The operator
   */
  public void visit(OperatorTrue o);
  
  /**
   * Visits an operator
   * @param o The operator
   */
  public void visit(OperatorFalse o);

  /**
   * Visits an operator
   * @param o The operator
   */
  public void visit(XPathAtom o);
}
