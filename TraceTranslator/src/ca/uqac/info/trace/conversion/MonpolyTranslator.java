package ca.uqac.info.trace.conversion;

import java.util.HashSet;
import java.util.Random;
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

public class MonpolyTranslator implements Translator {
	
	protected final String m_logname = "log";
	 
	  java.util.Random r=new java.util.Random( ) ;
	  int b ;
	  Random m_random = new Random();
    
	@Override
	/**
	 * 
	 */
	public String translateTrace(EventTrace m_trace)
	{
		StringBuffer out = new StringBuffer();
		int trace_length = m_trace.size();
		for (int i = 0; i < trace_length; i++) {
			Event e = m_trace.elementAt(i);
			out.append(" @ ").append("Event ");
			Node n = e.getDomNode();
			NodeList children = n.getChildNodes();
			Node child;
			String val = "";
			if (children.getLength() > 1) {
				NodeList level1 = children.item(1).getChildNodes();
				out.append(" ").append( children.item(1).getNodeName());
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
								out.append(toMonpoly(child, ""));
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
											out.append(toMonpoly(child, ""));
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
														out.append(toMonpoly(child, ""));
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
							out.append(toMonpoly(child, ""));
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
						out.append(toMonpoly(child, ""));
					}
					
				}
			}
			out.append("\n");
		}
		return out.toString();
	}
 
  private StringBuffer toMonpoly(Node n, String indent)
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
      out.append("\n\t").append(indent).append(n.getNodeName()).append(" ( ").append(val).append(" ) ");
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
      out.append(toMonpoly(child, ""));
          out.append("\n");
      }
      return out;
  }
	
	@Override
	/**
	 * 
	 */
	public String translateFormula(Operator o) {
		
		// TODO Auto-generated method stub
				StringBuffer out = new StringBuffer();
				MonpolyFormulaTranslator mft = new MonpolyFormulaTranslator();
				    o.accept(mft);
				    out.append(mft.getFormula());
			    return out.toString();
			}
			protected class MonpolyFormulaTranslator implements OperatorVisitor
			  {
			    Stack<StringBuffer> m_pieces;
			    
			    public MonpolyFormulaTranslator()
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
			      StringBuffer out = new StringBuffer("(").append(left).append(") And (").append(right).append(")");
			      m_pieces.push(out);
			    }

			    @Override
			    public void visit(OperatorOr o)
			    {
			      StringBuffer right = m_pieces.pop();
			      StringBuffer left = m_pieces.pop();
			      StringBuffer out = new StringBuffer("(").append(left).append(") Or (").append(right).append(")");
			      m_pieces.push(out);
			    }
			    
			    @Override
			    public void visit(OperatorImplies o)
			    {
			      StringBuffer right = m_pieces.pop();
			      StringBuffer left = m_pieces.pop();
			      StringBuffer op = m_pieces.pop();
			      b = m_random.nextInt(10);
			      StringBuffer out = new StringBuffer("(").append(left).append(") Implies[0,").append(b).append("]").append("(").append(right).append(op).append(")");
			      m_pieces.push(out);
			     
			      
			    }

			    @Override
			    public void visit(OperatorNot o)
			    {
			      StringBuffer op = m_pieces.pop();
			      StringBuffer out = new StringBuffer("Not (").append(op).append(")");
			      m_pieces.push(out);
			    }

			    @Override
			    public void visit(OperatorF o)
			    {
			      StringBuffer op = m_pieces.pop();
			      b = m_random.nextInt(10);
			      StringBuffer out = new StringBuffer("Eventually[0,").append(b).append("] (").append(op).append(")");
			      m_pieces.push(out);
			    }

			    @Override
			    public void visit(OperatorX o)
			    {
			      StringBuffer op = m_pieces.pop();
			      b = m_random.nextInt(10);
			      StringBuffer out = new StringBuffer("Next[0,").append(b).append("] (").append(op).append(")");
			      m_pieces.push(out);
			    }

			    @Override
			    public void visit(OperatorG o)
			    {
			      StringBuffer op = m_pieces.pop();
			      b = m_random.nextInt(10);
			      StringBuffer out = new StringBuffer("Always[0,").append(b).append("] (").append(op).append(")");
			      
			      m_pieces.push(out);
			    
			    }

			    @Override
			    public void visit(OperatorEquals o)
			    {
			      m_pieces.pop(); // Pop right-hand side
			      m_pieces.pop(); // Pop left-hand side
			      StringBuffer out = new StringBuffer(toMonpolyIdentifier(o));
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
				public void visit(OperatorU o)
				{
					StringBuffer op = m_pieces.pop();
					b = m_random.nextInt(10);
					StringBuffer out = new StringBuffer("Until[0,").append(b)
										.append("] (").append(op).append(")");

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
				public void visit(OperatorU o) {
					// TODO Auto-generated method stub
					
				}

				@Override
				public void visit(OperatorAnd o) {
					// TODO Auto-generated method stub
					
				}

				@Override
				public void visit(OperatorOr o) {
					// TODO Auto-generated method stub
					
				}

				@Override
				public void visit(OperatorNot o) {
					// TODO Auto-generated method stub
					
				}

				@Override
				public void visit(OperatorF o) {
					// TODO Auto-generated method stub
					
				}

				@Override
				public void visit(OperatorX o) {
					// TODO Auto-generated method stub
					
				}

				@Override
				public void visit(OperatorG o) {
					// TODO Auto-generated method stub
					
				}

				@Override
				public void visit(OperatorImplies o) {
					// TODO Auto-generated method stub
					
				}

				@Override
				public void visit(OperatorEquiv o) {
					// TODO Auto-generated method stub
					
				}

				@Override
				public void visit(Atom o) {
					// TODO Auto-generated method stub
					
				}

			
			  }
			  
			  protected static String toMonpolyIdentifier(OperatorEquals o)
			  {
			    String left = o.getLeft().toString();
			    String right = o.getRight().toString();
			    return new StringBuffer("eq").append(left).append("_").append(right).toString();
			  }
			@Override
			public String getSignature(EventTrace m_trace) {
				StringBuffer out = new StringBuffer();
				Vector<String> params = new Vector<String>() ;
				int trace_length = m_trace.size();
				for (int i = 0; i < trace_length; i++) {
					Event e = m_trace.elementAt(i);
			
					Node n = e.getDomNode();
					NodeList children = n.getChildNodes();
					Node child;
					if (children.getLength() > 1) {
						NodeList level1 = children.item(1).getChildNodes();
						
						if (level1.getLength() > 1) {

							NodeList level2 = level1.item(1).getChildNodes();
							out.append("\n");
							
							if (level2.getLength() > 1) {
								
								for (int index = 1; index < level2.getLength(); index++) {
									child = level2.item(index);									
									if (child.getChildNodes().getLength() == 1) {
										
										String strName = child.getNodeName() ; 
										if(!isExiste(strName, params))
										{
											params.add(strName);
										}
										
									} else if (child.getChildNodes().getLength() > 1) {
										NodeList level3 = level2.item(index).getChildNodes();
										if (level3.getLength() > 1) {
											for (int in = 1; in < level3.getLength(); in++) {
												child = level3.item(in);
																			
												if (child.getChildNodes().getLength() == 1) {
													if (!isExiste(child.getNodeName(),params)) 
													 {
														params.add(child.getNodeName());
													 }
													
												} else if (child.getChildNodes().getLength() > 1) {
													NodeList level4 = level3.item(in).getChildNodes();

													if (level4.getLength() > 1) {
														for (int y = 1; y < level4.getLength(); y++) {
															child = level4.item(y);
															
															if (child.getChildNodes().getLength() == 1) {
																
																if(!isExiste(child.getNodeName(), params))
																{
																	params.add(child.getNodeName());
																}
																
																
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
									if(!isExiste(child.getNodeName(), params))
									{
										params.add(child.getNodeName());
									 }
								}
								
								
							}
						} else {
							for (int j = 0; j < children.getLength(); j++) {
								child = children.item(j);
								
								if(!isExiste(child.getNodeName(), params))
								{
									params.add(child.getNodeName());
								}
								
							}
							
						}
					}
					
					
				}
				out.append(toMonpolySignature(params));
				out.append("\n");
				return out.toString();
			}

	/**
	 * Seek element in the list
	 * @param element
	 * @param list
	 * @return found
	 */
	private boolean isExiste(String element, Vector<String> list) 
	{
		boolean found = false;
		if (!list.isEmpty()) {
			for (int i = 0; i < list.size(); i++) {
				if (element.equalsIgnoreCase(list.get(i)))
				{
					found = true;
					break;
				}
			}
		}

		return found;
	}
	private StringBuffer toMonpolySignature(Vector<String> params)
	{
		StringBuffer out = new StringBuffer();
		for (String p : params)
		{
			String p_name = p ;
		  	out.append("\n\t").append(p_name).append(" ( ").append("x : String").append(" ) ");
		  	
		}
		System.out.println(out.toString());
	return out;
		    
	}
}
	

