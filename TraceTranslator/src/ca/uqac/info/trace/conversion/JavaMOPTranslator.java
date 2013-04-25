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

import java.text.SimpleDateFormat;
import java.util.AbstractSet;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.Stack;

import ca.uqac.info.ltl.Atom;
import ca.uqac.info.ltl.Constant;
import ca.uqac.info.ltl.Exists;
import ca.uqac.info.ltl.ForAll;
import ca.uqac.info.ltl.Operator;
import ca.uqac.info.ltl.OperatorAnd;
import ca.uqac.info.ltl.OperatorEquals;
import ca.uqac.info.ltl.OperatorEquiv;
import ca.uqac.info.ltl.OperatorF;
import ca.uqac.info.ltl.OperatorFalse;
import ca.uqac.info.ltl.OperatorG;
import ca.uqac.info.ltl.OperatorImplies;
import ca.uqac.info.ltl.OperatorNot;
import ca.uqac.info.ltl.OperatorOr;
import ca.uqac.info.ltl.OperatorTrue;
import ca.uqac.info.ltl.OperatorU;
import ca.uqac.info.ltl.OperatorVisitor;
import ca.uqac.info.ltl.OperatorX;
import ca.uqac.info.ltl.XPathAtom;
import ca.uqac.info.trace.Event;
import ca.uqac.info.trace.EventTrace;
import ca.uqac.info.util.Relation;

public class JavaMOPTranslator extends Translator {
  String className;
  HashSet<String> methods = new HashSet<String>();
	  
  @Override
  public String translateTrace() {
	className = "Test";
	if (m_trace == null)
	  return null;
	
    StringBuffer out_header = new StringBuffer();
    StringBuffer out = new StringBuffer();
    String CRLF = System.getProperty("line.separator");
    Relation<String,String> domains = m_trace.getParameterDomain();
    Set<String> params = domains.keySet();
    //methods = (AbstractSet<String>) domains.keySet();
    //methods.clear();

    //Main construction/Event listing
    out.append("\t//Main construction/Event listing").append(CRLF);
    out.append("\tpublic static void main(String[] args) {").append(CRLF);
    	
    for (Event e : m_trace)
    {
      if (!e.isFlat() || e.isMultiValued())
      {
        out.append("//-- WARNING: this event is not flat or is multi-valued --\n");
      }
      out.append(eventType(e));
    }
    out.append("\t}").append(CRLF);
    out.append("}").append(CRLF).append(CRLF);
    out_header.append("public class " + className + " {").append(CRLF).append(CRLF);
    
    //Method declaration
    Iterator<String> itr = methods.iterator();
    out_header.append("\t//Method declaration").append(CRLF);
    while(itr.hasNext()) {
      String tmp = itr.next();
      out_header.append("\tpublic static void " + tmp + "() {}").append(CRLF);
    }
    out_header.append(CRLF);
    out_header.append(out);
    return out_header.toString();
  }

  @Override
  public boolean requiresPropositional() {
    return true;
  }

  private StringBuffer eventType(Event e)
  {
	String CRLF = System.getProperty("line.separator");
  
	StringBuffer out = new StringBuffer();
	Relation<String,String> domain = e.getParameterDomain();
	Set<String> params = domain.keySet();
	    
	for (String p : params)
	{
	  Set<String> values = domain.get(p);
	  String val="_UNDEFINED_";
	  
      for (String v : values)
      {
        val = v;
        break;
        //TODO: Throw Error "Same Key defined twice"
      }
      if (p.compareTo("Action") == 0 || p.compareTo("Name") == 0 ||
    		  p.compareTo("name") == 0 || p.compareTo("p0") == 0) {
    	methods.add(val);
    	out.append("\t\tthis." + val + "();").append(CRLF); 
      }
    }
    	
    return out;
  }
  
  @Override
  public String translateFormula(Operator o)
  {
    m_formula = o;
    return translateFormula();
  }

  @Override
  public String translateFormula()
  {
	String CRLF = System.getProperty("line.separator");
	Date now = new Date();
	SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
	StringBuffer out = new StringBuffer();
	//out.append("// *************").append(CRLF)
	//	.append("// * Automatically generated JavaMop File").append(CRLF)
	//	.append("// * date\t\t\t: "+ sdf.format(now)).append(CRLF)
	//	.append("// * author\t\t: BabelTrace").append(CRLF)
	//	.append("// *************").append(CRLF).append(CRLF);
	out.append("package mop;").append(CRLF).append(CRLF)
		.append("import java.io.*;").append(CRLF)
		.append("import java.util.*;").append(CRLF).append(CRLF);
	
	out.append(className + "(" + className + " obj) {").append(CRLF).append(CRLF);
	
	Iterator<String> itr = methods.iterator();
    while(itr.hasNext())
    {
      String tmp = itr.next();
      out.append("\tevent " + tmp + " before (" + className + " obj) :").append(CRLF)
      	.append("\t\tcall(* " + className + "." + tmp + "())").append(CRLF)
        .append("\t\t&& target(obj) {}").append(CRLF).append(CRLF);
    }
        
	JavaMopFormulaTranslator jft = new JavaMopFormulaTranslator();
	m_formula.accept(jft);
	out.append("\tltl: ").append(jft.getFormula()).append(CRLF).append(CRLF);
	
	out.append("\t@violation { System.out.println(\"ltl violated\"); }").append(CRLF);
	out.append("}").append(CRLF);
    return out.toString();
  }

  @Override
  public String translateTrace(EventTrace trace)
  {
	this.m_trace = trace;
	translateTrace();
    return null;
  }
  
  @Override
  public boolean requiresFlat()
  {
    return true;
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
  
  protected class JavaMopFormulaTranslator implements OperatorVisitor
  {
	Stack<StringBuffer> m_pieces;

	public JavaMopFormulaTranslator()
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
	  StringBuffer out = new StringBuffer("(").append(left).append(" /\\ ").append(right).append(")");
	  System.out.println(out);
	  m_pieces.push(out);
	}

	@Override
	public void visit(OperatorOr o)
	{
	  StringBuffer right = m_pieces.pop();
      StringBuffer left = m_pieces.pop();
	  StringBuffer out = new StringBuffer("(").append(left).append(" \\/ ").append(right).append(")");
	  m_pieces.push(out);
	}

	@Override
	public void visit(OperatorImplies o)
	{
	  StringBuffer right = m_pieces.pop();
	  StringBuffer left = m_pieces.pop();
	  StringBuffer out = new StringBuffer("(").append(left).append(" -> ").append(right).append(")");
	  m_pieces.push(out);
	}

	@Override
	public void visit(OperatorNot o)
	{
	  StringBuffer op = m_pieces.pop();
	  StringBuffer out = new StringBuffer("!(").append(op).append(")");
	  m_pieces.push(out);
	}

	@Override
	public void visit(OperatorF o)
	{
	  StringBuffer op = m_pieces.pop();
	  StringBuffer out = new StringBuffer("<>(").append(op).append(")");
	  m_pieces.push(out);
	}

	@Override
	public void visit(OperatorX o)
	{
	  StringBuffer op = m_pieces.pop();
	  StringBuffer out = new StringBuffer("o(").append(op).append(")");
	  m_pieces.push(out);
	}

	@Override
	public void visit(OperatorG o)
	{
	  StringBuffer op = m_pieces.pop();
	  StringBuffer out = new StringBuffer("[](").append(op).append(")");
	  m_pieces.push(out);
	}

	@Override
	public void visit(OperatorEquals o)
	{
	  StringBuffer o_right = m_pieces.pop(); // Pop right-hand side
	  StringBuffer o_left = m_pieces.pop(); // Pop left-hand side
	  StringBuffer out = new StringBuffer().append(o_left).append(" == ").append(o_right);
	  m_pieces.push(out);
	}

	@Override
    public void visit(Atom o)
    {
      if (o instanceof Constant)
        m_pieces.push(new StringBuffer("").append(o.getSymbol()).append(""));
      else
        m_pieces.push(new StringBuffer("").append(o.getSymbol()));
	}

    @Override
    public void visit(OperatorFalse o)
    {
      m_pieces.push(new StringBuffer("false"));
    }
      
	@Override
	public void visit(OperatorTrue o)
	{
	  m_pieces.push(new StringBuffer("true"));
	}
      
	@Override
	public void visit(OperatorEquiv o)
	{
	  StringBuffer o_right = m_pieces.pop(); // Pop right-hand side
	  StringBuffer o_left = m_pieces.pop(); // Pop left-hand side
	  StringBuffer out = new StringBuffer().append("(").append(o_left).append(" <-> ").append(o_right).append(")");
	  m_pieces.push(out);
	}

	@Override
	public void visit(OperatorU o)
	{
	  StringBuffer o_right = m_pieces.pop(); // Pop right-hand side
	  StringBuffer o_left = m_pieces.pop(); // Pop left-hand side
	  StringBuffer out = new StringBuffer().append("(").append(o_left).append(" U ").append(o_right).append(")");
      m_pieces.push(out);	
	}
		
    public void visit(Exists o)
    {
    	//System.err.println("Unexpected behaviour, Exists can not be used with JavaMop"); 
      StringBuffer operand = m_pieces.pop();
      StringBuffer variable = m_pieces.pop();
      StringBuffer var = m_pieces.pop();
      StringBuffer out = new StringBuffer();
      //out.append("exists [ ").append(var).append(": ").append(variable)
      //  .append(" | ").append("").append(operand).append("").append(" ]");
      m_pieces.push(out);
    }

    @Override
    public void visit(ForAll o)
    {
    	//System.err.println("Unexpected behaviour, ForAll can not be used with JavaMop");
      StringBuffer operand = m_pieces.pop();
      StringBuffer variable = m_pieces.pop();
      StringBuffer var = m_pieces.pop();
      StringBuffer out = new StringBuffer();
      //out.append("forall [ ").append(var).append(": ").append(variable)
      //  .append(" | ").append("").append(operand).append("").append(" ]");
      m_pieces.push(out);
    }

	@Override
	public void visit(XPathAtom p)
    {
      // Not supposed to happen!
      // System.err.println("Error: XML Path found in MonPoly translator");
      // assert false;
      m_pieces.push(new StringBuffer(p.toString(false)));
    }
  }
}
