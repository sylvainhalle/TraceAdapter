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

public interface OperatorVisitor
{
  public void visit(OperatorAnd o);
  
  public void visit(OperatorOr o);
  
  public void visit(OperatorNot o);
  
  public void visit(OperatorF o);
  
  public void visit(OperatorX o);
  
  public void visit(OperatorG o);
  
  public void visit(OperatorEquals o);
  
  public void visit(OperatorImplies o);
  
  public void visit(OperatorEquiv o);
  
  public void visit(OperatorU o);
  
  public void visit(Exists o);
  
  public void visit(ForAll o);
  
  public void visit(Atom o);

	public void visit(XPathAtom xPathAtom);
}
