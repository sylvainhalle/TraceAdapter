package ca.uqac.info.trace.conversion;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Set;
import java.util.Iterator;
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
public class XesTranslator extends Translator
{

  @Override
  public String translateTrace() {

	if (m_trace == null)
	  return null;
	
    StringBuffer out = new StringBuffer();
    String CRLF = System.getProperty("line.separator");
    Relation<String,String> domains = m_trace.getParameterDomain();
    Set<String> params = domains.keySet();

    out.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>").append(CRLF);

    out.append("<!-- Generated automatically by the BabelTrace translator, this XES file ").append(CRLF);
    out.append("     is only meant to be understood by the Prom6.2 LTLChecker plug-in.").append(CRLF);
    out.append("     As such, it does not respect the standard XES specifications").append(CRLF);
    out.append("     but is still derived from XES standard version: 1.0").append(CRLF);
    out.append("     OpenXES is available from http://www.openxes.org/ -->").append(CRLF);

    out.append("<log xes.version=\"1.0\" xes.features=\"nested-attributes\" openxes.version=\"1.0RC7\" xmlns=\"http://www.xes-standard.org/\">").append(CRLF);
    /*out.append("<!--extension name=\"Lifecycle\" prefix=\"lifecycle\" uri=\"http://www.xes-standard.org/lifecycle.xesext\"/>").append(CRLF);
    out.append("<extension name=\"Organizational\" prefix=\"org\" uri=\"http://www.xes-standard.org/org.xesext\"/>").append(CRLF);
    out.append("<extension name=\"Time\" prefix=\"time\" uri=\"http://www.xes-standard.org/time.xesext\"/>").append(CRLF);
    out.append("<extension name=\"Concept\" prefix=\"concept\" uri=\"http://www.xes-standard.org/concept.xesext\"/>").append(CRLF);
    out.append("<extension name=\"Semantic\" prefix=\"semantic\" uri=\"http://www.xes-standard.org/semantic.xesext\"/-->").append(CRLF);*/

    //Global/Default declaration
    out.append("\t<global scope=\"event\">").append(CRLF);
    Iterator<String> itr = params.iterator();
    while(itr.hasNext())
    {
      String tmp = itr.next();
      if (tmp.compareTo("Action") == 0 || tmp.compareTo("Name") == 0)
    	  out.append("\t\t<string key=\"concept:name\" value =\"NULL\"/>").append(CRLF);
      else
    	  out.append("\t\t<string key=\"" + tmp + "\" value =\"NULL\"/>").append(CRLF);
    }
    out.append("\t</global>").append(CRLF)
    	.append("\t<string key=\"concept:name\" value=\"current-trace.xes\"/>").append(CRLF);
    //Trace/Event listing
    out.append("\t<trace>").append(CRLF)
    	.append("\t\t<string key=\"concept:name\" value=\"current-trace\"/>").append(CRLF);
    for (Event e : m_trace)
    {
      if (!e.isFlat() || e.isMultiValued())
      {
        out.append("-- WARNING: this event is not flat or is multi-valued\n");
      }
      out.append(toXes(e));
    }

    out.append("\t</trace>").append(CRLF);
    out.append("</log>").append(CRLF);
    
    return out.toString();
  }

  @Override
  public boolean requiresPropositional() {
    return false;
  }

  private StringBuffer toXes(Event e)
  {
    String CRLF = System.getProperty("line.separator");

    StringBuffer out = new StringBuffer();
    Relation<String,String> domain = e.getParameterDomain();
    Set<String> params = domain.keySet();
    
    out.append("\t\t<event>").append(CRLF);
    for (String p : params)
    {
      Set<String> values = domain.get(p);
      String val="_UNDEFINED_";
      if (p.compareTo("Action") == 0 || p.compareTo("Name") == 0)
    	  p="concept:name";
      for (String v : values)
      {
        val = v ;
        break;
        //TODO: Throw Error "Same Key defined twice"
      }
      out.append("\t\t\t<string key=\"" + p + "\" value=\"" + val + "\"/>").append(CRLF);
    }
    out.append("\t\t</event>").append(CRLF);
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
	out.append("#############").append(CRLF)
		.append("# version\t\t: 0.1").append(CRLF)
		.append("# date\t\t\t: "+ sdf.format(now)).append(CRLF)
		.append("# author\t\t: BabelTrace").append(CRLF)
		.append("##").append(CRLF).append(CRLF)
		.append("##").append(CRLF)
		.append("# Standard attributes").append(CRLF)
		.append("##").append(CRLF).append(CRLF);
	
	Relation<String,String> domains = m_trace.getParameterDomain();
    Set<String> params = domains.keySet();
	Iterator<String> itr = params.iterator();
    while(itr.hasNext())
    {
      String tmp = itr.next();
      if (tmp.compareTo("Action") == 0 || tmp.compareTo("Name") == 0)
    	out.append("set ate.WorkflowModelElement;").append(CRLF);
      else
    	out.append("string ate." + tmp + ";").append(CRLF);
    }
    out.append("number ate.ReservedValueForTheTrueOrFalseStatement;").append(CRLF)
    	.append(CRLF)
    	.append("##").append(CRLF)
    	.append("# Standard renamings").append(CRLF)
    	.append("##").append(CRLF).append(CRLF);
    
	itr = params.iterator();
    while(itr.hasNext())
    {
      String tmp = itr.next();
      if (tmp.compareTo("Action") == 0 || tmp.compareTo("Name") == 0)
      	out.append("rename ate.WorkflowModelElement as " + tmp + ";").append(CRLF);
      else
        out.append("rename ate." + tmp + " as " + tmp + ";").append(CRLF);
    }
    out.append("rename ate.ReservedValueForTheTrueOrFalseStatement as ReservedValueForTheTrueOrFalseStatement;").append(CRLF)
    	.append(CRLF)
    	.append("subformula equals(a: ReservedValueForTheTrueOrFalseStatement, b: ReservedValueForTheTrueOrFalseStatement) :=").append(CRLF)
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
    
	PromLTLFormulaTranslator pft = new PromLTLFormulaTranslator();
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
  
  protected class PromLTLFormulaTranslator implements OperatorVisitor
  {
	Stack<StringBuffer> m_pieces;

	public PromLTLFormulaTranslator()
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
	  StringBuffer out = new StringBuffer().append(o_left).append(" == ").append(o_right);
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
      m_pieces.push(new StringBuffer("false()"));
    }
      
	@Override
	public void visit(OperatorTrue o)
	{
	  m_pieces.push(new StringBuffer("true()"));
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
