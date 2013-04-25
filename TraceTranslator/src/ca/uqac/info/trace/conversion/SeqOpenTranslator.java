package ca.uqac.info.trace.conversion;

import java.text.SimpleDateFormat;
import java.util.Date;
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

/**
 * Translates an event trace into an Prom-Xes compliant file.
 * @author Jason Vallet
 */
public class SeqOpenTranslator extends Translator
{

  @Override
  public String translateTrace() {

	if (m_trace == null)
	  return null;
	
    StringBuffer out = new StringBuffer();
    String CRLF = System.getProperty("line.separator");

    out.append("# This file is generated automatically by the BabelTrace translator").append(CRLF);
    out.append("# It must be ").append(CRLF);
    out.append("# As such, it does not respect the standard XES specifications").append(CRLF);
    out.append("# but is still derived from XES standard version: 1.0").append(CRLF);

    //Trace/Event listing
    for (Event e : m_trace)
    {
      out.append(toSeq(e));
    }
    
    return out.toString();
  }

  @Override
  public boolean requiresPropositional() {
    return true;
  }

  private StringBuffer toSeq(Event e)
  {
    String CRLF = System.getProperty("line.separator");

    StringBuffer out = new StringBuffer();
    Relation<String,String> domain = e.getParameterDomain();
    Set<String> params = domain.keySet();
    
    out.append(CRLF).append("\"message");
    for (String p : params)
    {
      Set<String> values = domain.get(p);
      String val="_UNDEFINED_";
      for (String v : values)
      {
        val = v ;
        out.append(" " + p + "=" + val);
      }
    }
    out.append("\"").append(CRLF);
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
	out.append("(*************").append(CRLF)
		.append(" * version\t\t: 0.1").append(CRLF)
		.append(" * date\t\t\t: "+ sdf.format(now)).append(CRLF)
		.append(" * author\t\t: BabelTrace").append(CRLF)
		.append(" *************)").append(CRLF).append(CRLF);
	
    out.append("subformula equals(a: ReservedValueForTheTrueOrFalseStatement, b: ReservedValueForTheTrueOrFalseStatement) :=").append(CRLF)
    	.append("{}").append(CRLF)
    	.append("  a == b;").append(CRLF).append(CRLF)
    	.append("subformula true() :=").append(CRLF)
    	.append("{}").append(CRLF)
    	.append("  equals(0, 0);").append(CRLF).append(CRLF)
    	.append("subformula false() :=").append(CRLF)
    	.append("{}").append(CRLF)
    	.append("  equals(0, 1);").append(CRLF).append(CRLF)
    	.append("formula current_formula() :=").append(CRLF)
    	.append("{}").append(CRLF);
    
    SeqOpenFormulaTranslator pft = new SeqOpenFormulaTranslator();
	m_formula.accept(pft);
	out.append(pft.getFormula());
	
	out.append(";").append(CRLF);
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
  
  protected class SeqOpenFormulaTranslator implements OperatorVisitor
  {
	Stack<StringBuffer> m_pieces;

	public SeqOpenFormulaTranslator()
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
	  StringBuffer out = new StringBuffer("(").append(left).append(" and ").append(right).append(")");
	  System.out.println(out);
	  m_pieces.push(out);
	}

	@Override
	public void visit(OperatorOr o)
	{
	  StringBuffer right = m_pieces.pop();
      StringBuffer left = m_pieces.pop();
	  StringBuffer out = new StringBuffer("(").append(left).append(" or ").append(right).append(")");
	  m_pieces.push(out);
	}

	@Override
	public void visit(OperatorImplies o)
	{
	  StringBuffer right = m_pieces.pop();
	  StringBuffer left = m_pieces.pop();
	  StringBuffer out = new StringBuffer("(").append(left).append(" implies ").append(right).append(")");
	  m_pieces.push(out);
	}

	@Override
	public void visit(OperatorNot o)
	{
	  StringBuffer op = m_pieces.pop();
	  StringBuffer out = new StringBuffer("not (").append(op).append(")");
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
	  StringBuffer out = new StringBuffer("_O(").append(op).append(")");
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
	  StringBuffer out = new StringBuffer("'.*").append(o_left).append("=").append(o_right).append(".*'");
	  m_pieces.push(out);
	}

	@Override
    public void visit(Atom o)
    {
      if (o instanceof Constant)
        m_pieces.push(new StringBuffer("\"").append(o.getSymbol()).append("\""));
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
	  StringBuffer out = new StringBuffer().append("(").append(o_left).append(" equ ").append(o_right).append(")");
	  m_pieces.push(out);
	}

	@Override
	public void visit(OperatorU o)
	{
	  StringBuffer o_right = m_pieces.pop(); // Pop right-hand side
	  StringBuffer o_left = m_pieces.pop(); // Pop left-hand side
	  StringBuffer out = new StringBuffer().append("(").append(o_left).append(" _U ").append(o_right).append(")");
      m_pieces.push(out);	
	}
		
    public void visit(Exists o)
    {
      StringBuffer operand = m_pieces.pop();
      StringBuffer variable = m_pieces.pop();
      StringBuffer var = m_pieces.pop();
      StringBuffer out = new StringBuffer();
      out.append("exists [ ").append(var).append(": ").append(variable)
        .append(" | ").append("").append(operand).append("").append(" ]");
      m_pieces.push(out);
    }

    @Override
    public void visit(ForAll o)
    {
      StringBuffer operand = m_pieces.pop();
      StringBuffer variable = m_pieces.pop();
      StringBuffer var = m_pieces.pop();
      StringBuffer out = new StringBuffer();
      out.append("forall [ ").append(var).append(": ").append(variable)
      .append(" | ").append("").append(operand).append("").append(" ]");
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
