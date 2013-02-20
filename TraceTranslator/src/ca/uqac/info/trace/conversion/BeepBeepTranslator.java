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

import java.io.File;
import java.util.Stack;

import ca.uqac.info.ltl.*;
import ca.uqac.info.trace.Event;
import ca.uqac.info.trace.EventTrace;
import ca.uqac.info.trace.XmlTraceReader;

/**
 * Translator for BeepBeep. This translator does essentially "nothing":
 * it simply re-outputs the formula in BB's syntax and re-outputs the
 * trace as is.
 * @author Sylvain Hallé
 *
 */
public class BeepBeepTranslator extends Translator
{
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

	/**
	 * Note: BeepBeep assumes there is not root element in the trace
	 */
	@Override
	public String translateTrace()
	{
		StringBuilder out = new StringBuilder();
		for (Event e : m_trace)
		{
			out.append(e.toString()).append("\n");
		}
		return out.toString();
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
      StringBuffer out = new StringBuffer("(").append(o_left).append(") = (").append(o_right).append(")");
      m_pieces.push(out);
    }

    @Override
    public void visit(Atom o)
    {
      StringBuffer out = new StringBuffer();
      if (o instanceof Constant)
    	out.append("{").append(o.toString()).append("}");
      else
    	out.append(o.toString());
      m_pieces.push(out);
    }

	@Override
	public void visit(OperatorEquiv o) {
		
	}

	@Override
	public void visit(OperatorU o)
	{
		
	}

	@Override
    public void visit(Exists o)
    {
	  StringBuffer operand = m_pieces.pop();
	  StringBuffer path = m_pieces.pop();
	  StringBuffer variable = m_pieces.pop();
	  StringBuffer out = new StringBuffer();
	  out.append("<").append(variable).append(" /").append(path).append("> (").append(operand).append(")");
	  m_pieces.push(out);
    }

	@Override
    public void visit(ForAll o)
    {
	  StringBuffer operand = m_pieces.pop();
	  StringBuffer path = m_pieces.pop();
	  StringBuffer variable = m_pieces.pop();
	  StringBuffer out = new StringBuffer();
	  out.append("[").append(variable).append(" /").append(path).append("] (").append(operand).append(")");
  	  m_pieces.push(out);
    }

	@Override
    public void visit(XPathAtom p)
    {
	  m_pieces.push(new StringBuffer(p.toString(true)));
    }

    @Override
    public void visit(OperatorTrue o)
    {
      m_pieces.push(new StringBuffer("TRUE"));
    }

	@Override
	public void visit(OperatorFalse o)
	{
	  m_pieces.push(new StringBuffer("FALSE"));
	}

  }
  
  public static void main(String[] args)
  {
		Operator o = null;
		File fic = new File("traces/trace2.xml");
		XmlTraceReader xtr = new XmlTraceReader();
		try
		{
			o = Operator.parseFromString("F (∃i ∈ /Event/p0 : (i=0))");
		}
		catch (Operator.ParseException e)
		{
			System.out.println("Parse exception");
		}
		BeepBeepTranslator bt = new BeepBeepTranslator();
	    EventTrace t = null;
	    try
	    {
	      t = xtr.parseEventTrace(new java.io.FileInputStream(fic));  
	    }
	    catch (java.io.FileNotFoundException ex)
	    {
	      ex.printStackTrace();
	      System.exit(1);
	    }
	    assert t != null;
		String f = bt.translateFormula(o);
		System.out.println(f);
		System.out.println(bt.translateTrace(t));
  }
  
  @Override
  public boolean requiresFlat()
  {
    return false;
  }
  
	@Override
	public boolean requiresPropositional() {
		return false;
	}

  @Override
  public boolean requiresAtomic()
  {
    return false;
  }
  
  @Override
  public String getSignature()
  {
    return "";
  }
}
