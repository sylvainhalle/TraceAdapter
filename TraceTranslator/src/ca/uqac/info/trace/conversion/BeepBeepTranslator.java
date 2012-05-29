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
package ca.uqac.info.trace.conversion;

import java.util.Stack;

import ca.uqac.info.ltl.*;
import ca.uqac.info.trace.EventTrace;

/**
 * Translator for BeepBeep. This translator does essentially "nothing":
 * it simply re-outputs the formula in BB's syntax and re-outputs the
 * trace as is.
 * @author sylvain
 *
 */
public class BeepBeepTranslator extends Translator
{

	@Override
	public String getSignature(EventTrace t)
	{
		// No signature necessary for BeepBeep
		return "";
	}

	@Override
	public String translateFormula(Operator o)
	{
		setFormula(o);
		return translateFormula();
	}

	@Override
	public String translateFormula()
	{
    BeepBeepFormulaTranslator sft = new BeepBeepFormulaTranslator();
    m_formula.accept(sft);
    return sft.getFormula();
	}

	@Override
	public String translateTrace(EventTrace t)
	{
		setTrace(t);
		return translateTrace();
	}

	@Override
	public String translateTrace()
	{
		return m_trace.toString();
	}
	
  protected class BeepBeepFormulaTranslator implements OperatorVisitor
  {
    Stack<StringBuffer> m_pieces;
    
    public BeepBeepFormulaTranslator()
    {
      super();
      m_pieces = new Stack<StringBuffer>();
    }
    
    public String getFormula()
    {
      StringBuffer out = m_pieces.peek();
      return out.toString();
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
    public void visit(OperatorImplies o)
    {
      StringBuffer right = m_pieces.pop();
      StringBuffer left = m_pieces.pop();
      StringBuffer out = new StringBuffer("(").append(left).append(") -> (").append(right).append(")");
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
      StringBuffer o_right = m_pieces.pop(); // Pop right-hand side
      StringBuffer o_left = m_pieces.pop(); // Pop left-hand side
      StringBuffer out = new StringBuffer().append(o_left).append(" = ").append(o_right);
      m_pieces.push(out);
    }

    @Override
    public void visit(Atom o)
    {
      m_pieces.push(new StringBuffer(o.toString()));
    }

	@Override
	public void visit(OperatorEquiv o) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void visit(OperatorU o)
	{
		// TODO Auto-generated method stub

	}

	@Override
  public void visit(Exists o)
  {
		StringBuffer operand = m_pieces.pop();
		StringBuffer path = m_pieces.pop();
		StringBuffer variable = m_pieces.pop();
		StringBuffer out = new StringBuffer();
		out.append("<").append(variable).append(" ").append(path).append("> (").append(operand).append(")");
		m_pieces.push(out);
  }

	@Override
  public void visit(ForAll o)
  {
		StringBuffer operand = m_pieces.pop();
		StringBuffer path = m_pieces.pop();
		StringBuffer variable = m_pieces.pop();
		StringBuffer out = new StringBuffer();
		out.append("[").append(variable).append(" ").append(path).append("] (").append(operand).append(")");
		m_pieces.push(out);
  }

	@Override
  public void visit(XPathAtom p)
  {
		m_pieces.push(new StringBuffer(p.toString()));
  }

  }

}
