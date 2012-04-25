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
import org.w3c.dom.NodeList;

public class MaudeTranslator implements Translator
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
		int NbEvent = t.size();
		// Start writing the Java program

		for (Event e : t) {
			NodeList listnode = e.getDomNode().getChildNodes();

			if (listnode.getLength() > 0) {
				out.append(listnode.item(1).getNodeName()).append("_");

				NodeList list2 = listnode.item(1).getChildNodes();

				if (list2.getLength() > 1) {
					NodeList list3 = list2.item(1).getChildNodes();

					if (list3.getLength() > 1) {
						out.append(list3.item(1).getNodeName()).append("_")
								.append(list3.item(1).getTextContent());
					} else {
						out.append(list2.item(1).getNodeName()).append("_")
								.append(list2.item(1).getTextContent());
					}

				} else {

					out.append(listnode.item(1).getTextContent());
				}

			}
			NbEvent--;
			if (NbEvent != 0) {
				out.append(" ,\n ");
			}

		}

		return out.toString();

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
		      StringBuffer out = new StringBuffer(toJavaMopIdentifier(o));
		      //StringBuffer out = new StringBuffer("eq").append(left).append("_").append(right);
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
		  
		  protected class JavaMopEqualityGetter implements OperatorVisitor
		  {

		    Set<OperatorEquals> m_equalities; 
		    
		    public JavaMopEqualityGetter()
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
		  
		  protected static String toJavaMopIdentifier(OperatorEquals o)
		  {
		    String left = o.getLeft().toString();
		    String right = o.getRight().toString();
		    return new StringBuffer("eq").append(left).append("_").append(right).toString();
		  }
	}


