package ca.uqac.info.trace.conversion;

import java.util.HashSet;
import java.util.Set;
import java.util.Stack;
import java.util.Vector;

import ca.uqac.info.ltl.Atom;
import ca.uqac.info.ltl.Operator;
import ca.uqac.info.ltl.OperatorAnd;
import ca.uqac.info.ltl.OperatorEquals;
import ca.uqac.info.ltl.OperatorEquiv;
import ca.uqac.info.ltl.OperatorF;
import ca.uqac.info.ltl.OperatorG;
import ca.uqac.info.ltl.OperatorImplies;
import ca.uqac.info.ltl.OperatorNot;
import ca.uqac.info.ltl.OperatorOr;
import ca.uqac.info.ltl.OperatorU;
import ca.uqac.info.ltl.OperatorVisitor;
import ca.uqac.info.ltl.OperatorX;
import ca.uqac.info.trace.Event;
import ca.uqac.info.trace.EventTrace;
import ca.uqac.info.util.Relation;

import org.w3c.dom.NodeList;

public class MaudeTranslator extends Translator
{

	Vector<String> o_params;
	  
	  /**
	   * Constructor
	   */
	  public MaudeTranslator()
	  {
	    super();
	    o_params = new Vector<String>();
	  }

	@Override
	public String translateTrace(EventTrace t) {
		StringBuffer out = new StringBuffer();
		StringBuffer out_Trace = new StringBuffer();
		EventTrace maTrace = t ;
		for (Event e : t) {
			NodeList listnode = e.getDomNode().getChildNodes();
			out.append(listnode.item(1).getTextContent());
			out.append(",");
		}
		out.append("  ").append("|=");
		
		// Start writing the Java program
		String params = this.getSignature(maTrace);
		out_Trace.append("in ltl.maude");
		out_Trace.append("\n \n \n");
		out_Trace.append("fmod MY-TRACE is").append("\t");
		out_Trace.append("\n ");
		out_Trace.append("extending LTL .").append("\t");
		out_Trace.append("\n \n \n");
		out_Trace.append("ops").append(params).append(" : -> Atom .").append("\t");
		out_Trace.append("\n \n \n");
		out_Trace.append("endfm \n ");
		out_Trace.append("");
		out_Trace.append("reduce").append(" ");
		out_Trace.append(out);
		return out_Trace.toString();
	}

	public String translateFormula(Operator o) {
		StringBuffer out = new StringBuffer();
	    MaudeFormulaTranslator f_trans = new MaudeFormulaTranslator();
	    o.accept(f_trans);
	    out.append(f_trans.getFormula());
	    return out.toString();
	}
	protected class MaudeFormulaTranslator implements OperatorVisitor
	  {
		    Stack<StringBuffer> m_pieces;
		    
		    public MaudeFormulaTranslator()
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
		    public void visit(OperatorAnd o) {
		    	StringBuffer right = m_pieces.pop();
				StringBuffer left = m_pieces.pop();
				StringBuffer out = new StringBuffer("(").append(left).append(") ")
					.append("/\\").append("(").append(right).append(")");
				m_pieces.push(out);
		    }

		    @Override
		    public void visit(OperatorOr o) {
		    	StringBuffer right = m_pieces.pop();
		    	StringBuffer left = m_pieces.pop();
		    	StringBuffer out = new StringBuffer("(").append(left).append(")")
					.append("\\/").append("(").append(right)
					.append(")");
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
		      StringBuffer out = new StringBuffer("~ (").append(op).append(")");
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

		    @Override
		    public void visit(OperatorEquals o)
		    {
		      m_pieces.pop(); // Pop right-hand side
		      m_pieces.pop(); // Pop left-hand side
		      StringBuffer out = new StringBuffer(toMaudeIdentifier(o));
		      m_pieces.push(out);
		    }

		    @Override
		    public void visit(Atom o)
		    {
		      m_pieces.push(new StringBuffer(o.getSymbol()));
		    }

			@Override
			public void visit(OperatorEquiv o) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void visit(OperatorU o) {
				// TODO Auto-generated method stub
				
			}

			
		  }
		  
		  protected class MaudeEqualityGetter implements OperatorVisitor
		  {

		    Set<OperatorEquals> m_equalities; 
		    
		    public MaudeEqualityGetter()
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

		    @Override
		    public void visit(Atom o) {}

			@Override
			public void visit(OperatorEquiv o) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void visit(OperatorU o) {
				// TODO Auto-generated method stub
				
			}

			
		  }
		  
		  protected static String toMaudeIdentifier(OperatorEquals o)
		  {
		    String left = o.getLeft().toString();
		    String right = o.getRight().toString();
		    return new StringBuffer("eq").append(left).append("_").append(right).toString();
		  }

		@Override
		public String getSignature(EventTrace m_trace) {
			  Relation<String,String> param_domains = m_trace.getParameterDomain();
			  Set<String> params = param_domains.keySet();
		      Vector<String> vectParams = new Vector<String>();
		      vectParams.addAll(params);
			  
		      String strTemp = vectParams.toString().replaceAll(",", " ") ;
		      strTemp =  strTemp.replace("[", " ");
		      strTemp =  strTemp.replace("]", " ");
			return strTemp;
		}

		@Override
		public String translateFormula() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public String translateTrace() {
			// TODO Auto-generated method stub
			return null;
		}

	}


