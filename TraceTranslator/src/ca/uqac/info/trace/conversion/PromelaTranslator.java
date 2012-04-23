package ca.uqac.info.trace.conversion;

import java.util.HashSet;
import java.util.Set;
import java.util.Stack;
import java.util.Vector;

import ca.uqac.info.ltl.Atom;
import ca.uqac.info.ltl.Operator;
import ca.uqac.info.ltl.OperatorAnd;
import ca.uqac.info.ltl.OperatorEquals;
import ca.uqac.info.ltl.OperatorF;
import ca.uqac.info.ltl.OperatorG;
import ca.uqac.info.ltl.OperatorImplies;
import ca.uqac.info.ltl.OperatorNot;
import ca.uqac.info.ltl.OperatorOr;
import ca.uqac.info.ltl.OperatorVisitor;
import ca.uqac.info.ltl.OperatorX;
import ca.uqac.info.trace.Event;
import ca.uqac.info.trace.EventTrace;
import ca.uqac.info.trace.conversion.JavaMopTranslator.JavaMopEqualityGetter;
import ca.uqac.info.trace.conversion.JavaMopTranslator.JavaMopFormulaTranslator;
import ca.uqac.info.util.Relation;

public class PromelaTranslator implements Translator {
	
	protected final String m_logname = "log";
	Vector<String> o_params;
	
	public String translateTrace(EventTrace m_trace)
	{
		StringBuffer out = new StringBuffer();
	    Relation<String,String> domains = m_trace.getParameterDomain();
	    Set<String> params = domains.keySet();
	    out.append("-- Trace file automatically generated by\n-- Event Trace Converter\n\n");
	    
	    // Table contents
	    for (int i = 0; i < m_trace.size(); i++)
	    {
	      Event e = m_trace.elementAt(i);
	      if (!e.isFlat() || e.isMultiValued())
	      {
	        out.append("-- WARNING: this event is not flat or is multi-valued\n");
	      }
	      out.append(" typedef message ").append(i).append("{ ");
	      out.append(toPromela(e, params));
	      out.append("\n");
	      
	    }
	    return out.toString();
	}

	private StringBuffer toPromela(Event e, Set<String> all_params)
	  {
	    StringBuffer out = new StringBuffer();
	    Relation<String,String> domain = e.getParameterDomain();
	    Set<String> params = domain.keySet();
	    
	    for (String p : all_params)
	    {
	    	String p_name = p ;
	     
	     
	      if (!params.contains(p))
	      {
	    	  out.append("\n\t").append(p_name);
	      }
	      else
	      {
	        Set<String> values = domain.get(p);
	        String val = "";
	        
	        for (String v : values)
	        {
	          val = v;
	          break;
	        }
	        
	        out.append("\n\t").append(p_name).append(" = ").append(val).append(" ; ");

	      }
	    }
	    out.append(" } ");
	    
	    return out;
	  	 
	  }
	public String translateFormula(Operator o) {
		// TODO Auto-generated method stub
		StringBuffer out = new StringBuffer();
	    
	    // Step 1: define the events as AspectJ pointcuts
	    PromelaEqualityGetter f_eq= new PromelaEqualityGetter();
	    o.accept(f_eq);
	    Set<OperatorEquals> equalities = f_eq.getEqualities();
	    for (OperatorEquals eq : equalities)
	    {
	      out.append("  event ").append(toPromelaIdentifier(eq)); /*.append("(");
	      for (int i = 0; i < o_params.size(); i++)
	      {
	        if (i > 0)
	          out.append(",");
	        String p_name = o_params.elementAt(i);
	        out.append("int ").append(p_name);
	      }
	      out.append(")");*/
	      out.append(" before :\n");
	      out.append("    call(void my_event(");
	      StringBuffer args_string = new StringBuffer();
	      for (int i = 0; i < o_params.size(); i++)
	      {
	        String p_name = o_params.elementAt(i);
	        if (i > 0)
	        {
	          out.append(",");
	          args_string.append(",");
	        }
	        out.append("String ").append(p_name);
	        args_string.append(p_name);
	      }
	      out.append(")) && args(").append(args_string).append(")");
	      Operator left = eq.getLeft();
	      Operator right = eq.getRight();
	      // TODO: we assume left-hand is a parameter and right-hand is a value
	      out.append(" && ").append(left.toString()).append(" == \"").append(right.toString()).append("\"");
	      out.append(" { }\n");
	    }
	    out.append("\n");
	    
	    // Step 2: append translated formula on those events 
	    PromelaFormulaTranslator f_trans = new PromelaFormulaTranslator();
	    o.accept(f_trans);
	    out.append("  ltl ").append("p1").append("{ ").append(f_trans.getFormula()).append("}").append("\n");
	    
	    return out.toString();
	}
	protected class PromelaFormulaTranslator implements OperatorVisitor
	  {
	    Stack<StringBuffer> m_pieces;
	    
	    public PromelaFormulaTranslator()
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
	      StringBuffer out = new StringBuffer("(").append(left).append(") && (").append(right).append(")");
	      m_pieces.push(out);
	    }

	    @Override
	    public void visit(OperatorOr o)
	    {
	      StringBuffer right = m_pieces.pop();
	      StringBuffer left = m_pieces.pop();
	      StringBuffer out = new StringBuffer("(").append(left).append(") || (").append(right).append(")");
	      m_pieces.push(out);
	    }
	    
	    @Override
	    public void visit(OperatorImplies o)
	    {
	      StringBuffer right = m_pieces.pop();
	      StringBuffer left = m_pieces.pop();
	      StringBuffer out = new StringBuffer("(").append(left).append(") <-> (").append(right).append(")");
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
	      StringBuffer out = new StringBuffer("<> (").append(op).append(")");
	      m_pieces.push(out);
	    }

	    @Override
	    public void visit(OperatorX o)
	    {
	      StringBuffer op = m_pieces.pop();
	      StringBuffer out = new StringBuffer("o (").append(op).append(")");
	      m_pieces.push(out);
	    }

	    @Override
	    public void visit(OperatorG o)
	    {
	      StringBuffer op = m_pieces.pop();
	      StringBuffer out = new StringBuffer("[] (").append(op).append(")");
	      m_pieces.push(out);
	    }
//	    public void visit(OperatorEquivalences o)
//	    {
//	    	StringBuffer right = m_pieces.pop();
//		      StringBuffer left = m_pieces.pop();
//		      StringBuffer out = new StringBuffer("(").append(left).append(") <-> (").append(right).append(")");
//		      m_pieces.push(out);
//	    	
//	    	
//	    }

	    @Override
	    public void visit(OperatorEquals o)
	    {
	      m_pieces.pop(); // Pop right-hand side
	      m_pieces.pop(); // Pop left-hand side
	      StringBuffer out = new StringBuffer(toPromelaIdentifier(o));
	      //StringBuffer out = new StringBuffer("eq").append(left).append("_").append(right);
	      m_pieces.push(out);
	    }

	    @Override
	    public void visit(Atom o)
	    {
	      m_pieces.push(new StringBuffer(o.getSymbol()));
	    }
	  }
	  
	  protected class PromelaEqualityGetter implements OperatorVisitor
	  {

	    Set<OperatorEquals> m_equalities; 
	    
	    public PromelaEqualityGetter()
	    {
	      super();
	      m_equalities = new HashSet<OperatorEquals>();
	    }
	    
	    public Set<OperatorEquals> getEqualities()
	    {
	      return m_equalities;
	    }
	    
	    @Override
	    public void visit(OperatorEquals o)
	    {
	      m_equalities.add(o);
	    }
	    
	    @Override
	    public void visit(OperatorAnd o) {}

	    @Override
	    public void visit(OperatorOr o) {}
	    
	    @Override
	    public void visit(OperatorImplies o) {}

	    @Override
	    public void visit(OperatorNot o) {}

	    @Override
	    public void visit(OperatorF o) {}

	    @Override
	    public void visit(OperatorX o) {}

	    @Override
	    public void visit(OperatorG o) {}
//	    public void visit(OperatorEquivalences o){
//	    	
//	    }

	    @Override
	    public void visit(Atom o) {}
	  }
	  
	  protected static String toPromelaIdentifier(OperatorEquals o)
	  {
	    String left = o.getLeft().toString();
	    String right = o.getRight().toString();
	    return new StringBuffer("eq").append(left).append("_").append(right).toString();
	  }
}
