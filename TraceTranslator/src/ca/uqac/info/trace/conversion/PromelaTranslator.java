package ca.uqac.info.trace.conversion;

import java.util.HashSet;
import java.util.Set;
import java.util.Stack;
import java.util.Vector;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

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

public class PromelaTranslator implements Translator {
	
	protected final String m_logname = "log";
	Vector<String> o_params;
	
	public String translateTrace(EventTrace m_trace)
	{
		StringBuffer out = new StringBuffer();
		int trace_length = m_trace.size();
		for (int i = 0; i < trace_length; i++) {
			Event e = m_trace.elementAt(i);
			out.append(" typedef  ");
			Node n = e.getDomNode();
			NodeList children = n.getChildNodes();
			Node child;
			String val = "";
			if (children.getLength() > 1) {
				NodeList level1 = children.item(1).getChildNodes();
				out.append(" ").append( children.item(1).getNodeName()).append(" ").append("{ ");
				out.append("\n");
				
				if (level1.getLength() > 1) {

					NodeList level2 = level1.item(1).getChildNodes();
					out.append("\n");
					
					if (level2.getLength() > 1) {
					
						for (int index = 1; index < level2.getLength(); index++) {
							child = level2.item(index);
							
							if (child.getChildNodes().getLength() == 1) {
								val = child.getTextContent();
								val = val.trim();
								if (val.isEmpty())
									continue;
								out.append("\n");
								out.append(toPromela(child, ""));
							} else if (child.getChildNodes().getLength() > 1) {
								NodeList level3 = level2.item(index)
										.getChildNodes();

								if (level3.getLength() > 1) {
									for (int in = 1; in < level3.getLength(); in++) {
										child = level3.item(in);
																		
										if (child.getChildNodes().getLength() == 1) {
											val = child.getTextContent();
											val = val.trim();
											if (val.isEmpty())
												continue;
											out.append("\n");
											out.append(toPromela(child, ""));
										} else if (child.getChildNodes()
												.getLength() > 1) {
											NodeList level4 = level3.item(in)
													.getChildNodes();

											if (level4.getLength() > 1) {
												for (int y = 1; y < level4
														.getLength(); y++) {
													child = level4.item(y);
													if (child.getChildNodes()
															.getLength() == 1) {
														val = child
																.getTextContent();
														val = val.trim();
														if (val.isEmpty())
															continue;
														out.append("\n");
														out.append(toPromela(child, ""));
													}
												}
											}
										}
									}

								}

							}
						}
					}else{
						for (int j = 0; j < level1.getLength(); j++) {
							child = level1.item(j);
							val = child.getTextContent();
							val = val.trim();
							if (val.isEmpty())
								continue;
							out.append("\n");
							out.append(toPromela(child, ""));
						}
						
						
					}
				} else {
					for (int j = 0; j < children.getLength(); j++) {
						child = children.item(j);
						val = child.getTextContent();
						val = val.trim();
						if (val.isEmpty())
							continue;
						out.append("\n");
						out.append(toPromela(child, ""));
					}
					
				}
			}
			out.append("\n");
		      out.append(" } ");
		      out.append("\n");
		      out.append("\n");
		}
		return out.toString();
	}
 
  private StringBuffer toPromela(Node n, String indent)
  {
    StringBuffer out = new StringBuffer();
    NodeList children = n.getChildNodes();
    int num_children = children.getLength();
    if (num_children == 1 && children.item(0).getNodeType() == Node.TEXT_NODE)
    {
      Node child = children.item(0);
      String val = child.getNodeValue();
      val = val.trim();
      if (val.isEmpty())
      {
        out = new StringBuffer();
        return out;
      }
      out.append("\n\t").append(indent).append(n.getNodeName()).append(" = ").append(val).append(" ; ");

      return out;
    }
    for (int i = 0; i < num_children; i++)
    {
      Node child = children.item(i);
      if (child.getNodeType() == Node.TEXT_NODE)
      {
         String val = child.getNodeValue();
         val = val.trim();
         if (val.isEmpty()) // We ignore whitespace
           continue;
      }
      out.append(toPromela(child, ""));
          out.append("\n");
      }
   
      return out;
  }
	  
	public String translateFormula(Operator o) {
		// TODO Auto-generated method stub
		StringBuffer out = new StringBuffer();
	    
	    // Step 1: define the events as AspectJ pointcuts
	    PromelaEqualityGetter f_eq= new PromelaEqualityGetter();
	    o.accept(f_eq);
	   
	    
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

		@Override
		public void visit(OperatorEquiv o) {
			StringBuffer right = m_pieces.pop();
			StringBuffer left = m_pieces.pop();
			StringBuffer out = new StringBuffer("(").append(left).append(") <-> (").append(right).append(")");
			m_pieces.push(out);
			
		}

		@Override
		public void visit(OperatorU o) {
			StringBuffer op = m_pieces.pop();
		      StringBuffer out = new StringBuffer("until (").append(op).append(")");
		      m_pieces.push(out);
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

		@Override
		public void visit(OperatorEquiv o) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void visit(OperatorU o) {
			// TODO Auto-generated method stub
			
		}

		
	  }
	  
	  protected static String toPromelaIdentifier(OperatorEquals o)
	  {
	    String left = o.getLeft().toString();
	    String right = o.getRight().toString();
	    return new StringBuffer("eq").append(left).append("_").append(right).toString();
	  }

	@Override
	public String getSignature(EventTrace t) {
		// TODO Auto-generated method stub
		return null;
	}
}
