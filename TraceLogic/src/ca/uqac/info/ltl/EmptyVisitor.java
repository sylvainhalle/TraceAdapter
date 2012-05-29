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

/**
 * The EmptyVisitor does absolutely nothing.
 * It is intended as a helper class, when one wants
 * to create a Visitor that only overrides a couple of signatures and
 * have no behaviour for the rest.
 * @author sylvain
 *
 */

public class EmptyVisitor implements OperatorVisitor
{

	@Override
  public void visit(OperatorAnd o)
  {
	  
  }

	@Override
  public void visit(OperatorOr o)
  {
	  
  }

	@Override
  public void visit(OperatorNot o)
  {
	  
  }

	@Override
  public void visit(OperatorF o)
  {
	  
  }

	@Override
  public void visit(OperatorX o)
  {
	  
  }

	@Override
  public void visit(OperatorG o)
  {
	  
  }

	@Override
  public void visit(OperatorEquals o)
  {
	  
  }

	@Override
  public void visit(OperatorImplies o)
  {
	  
  }

	@Override
  public void visit(OperatorEquiv o)
  {
	  
  }

	@Override
  public void visit(OperatorU o)
  {
	  
  }

	@Override
  public void visit(Exists o)
  {
	  
  }

	@Override
  public void visit(ForAll o)
  {
	  
  }

	@Override
  public void visit(Atom o)
  {
	  
  }

	@Override
  public void visit(XPathAtom p)
  {
	  
  }

}
