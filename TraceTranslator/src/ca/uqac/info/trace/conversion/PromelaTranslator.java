package ca.uqac.info.trace.conversion;


import java.util.HashSet;
import java.util.Set;
import java.util.Stack;
import java.util.Vector;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import ca.uqac.info.ltl.Atom;
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
import ca.uqac.info.ltl.XPathAtom;

import ca.uqac.info.ltl.OperatorVisitor;
import ca.uqac.info.ltl.OperatorX;
import ca.uqac.info.trace.Event;
import ca.uqac.info.trace.EventTrace;

import ca.uqac.info.util.Relation;

public class PromelaTranslator extends Translator {

  protected final String m_logname = "log";
  Vector<String> o_params;

  public String translateTrace()
  {
    StringBuffer out = new StringBuffer();
    Relation<String,String> domains = m_trace.getParameterDomain();
    Set<String> params = domains.keySet();
    boolean found = false ; int k = params.size()- 1 ;
    out.append("int  ");
    for (String p : params)
    {
      String p_name = p;
      if(k == 0)
        found = true ;
      out.append("  ").append(p_name) ;
      if(!found)
      {
        out.append(",");
      }
      k-- ;
    }
    out.append(";\n");
    out.append("int msgno = 0;\n");
    out.append("active proctype A(){\n");
    out.append("do\n");
    for(int i = 0; i<m_trace.size(); i++)
    {
      out.append("::");
      out.append("(msgno  == ").append(i).append(")").append(" -> \n");
      Event e = m_trace.elementAt(i);
      out.append("atomic {\n");
      out.append(" msgno = msgno + 1;");
      Node n = e.getDomNode();
      NodeList children = n.getChildNodes();
      Node child;
      String val = "";
      if (children.getLength() > 1) {
        NodeList level1 = children.item(1).getChildNodes();
        if (level1.getLength() > 1) {

          NodeList level2 = level1.item(1).getChildNodes();
          if (level2.getLength() > 1) {

            for (int index = 1; index < level2.getLength(); index++) {
              child = level2.item(index);

              if (child.getChildNodes().getLength() == 1) {
                val = child.getTextContent();
                val = val.trim();
                if (val.isEmpty())
                  continue;
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
            out.append(toPromela(child, ""));
          }

        }
      }

      out.append("\n");
      out.append("}\n");


    }
    out.append("od;\n");
    out.append("}");
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

    }

    return out;
  }

  public String translateFormula()
  {
    PromelaEqualityGetter f_eq= new PromelaEqualityGetter();
    m_formula.accept(f_eq);
    PromelaFormulaTranslator f_trans = new PromelaFormulaTranslator();
    m_formula.accept(f_trans);
    return "! (" + f_trans.getFormula() + ")";
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
    public void visit(OperatorTrue o)
    {
      m_pieces.push(new StringBuffer("true"));

    }

    @Override
    public void visit(OperatorFalse o)
    {
      m_pieces.push(new StringBuffer("false"));
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
      StringBuffer right = m_pieces.pop(); 
      StringBuffer left = m_pieces.pop(); 
      StringBuffer out = new StringBuffer();
      out.append(left).append("==").append(right);
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

    @Override
    public void visit(Exists o)
    {
      // Not supposed to happen!
      System.err.println("Error: quantifier found in Promela translator");
      assert false;
    }

    @Override
    public void visit(ForAll o)
    {
      // Not supposed to happen!
      System.err.println("Error: quantifier found in Promela translator");
      assert false;
    }

    @Override
    public void visit(XPathAtom p)
    {
      // false because no leading slash
      m_pieces.push(new StringBuffer(p.toString(false)));
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

    @Override
    public void visit(OperatorTrue o) {}

    @Override
    public void visit(OperatorFalse o) {}

    @Override
    public void visit(Atom o) {}

    @Override
    public void visit(OperatorEquiv o) {

    }

    @Override
    public void visit(OperatorU o) {

    }

    @Override
    public void visit(Exists o)
    {
      //out.append(" ").append( children.item(1).getNodeName());
    }

    @Override
    public void visit(ForAll o)
    {

    }

    @Override
    public void visit(XPathAtom p)
    {

    }
  }

  @Override
  public String translateFormula(Operator o)
  {
    setFormula(o);
    return translateFormula();
  }
  @Override
  public String translateTrace(EventTrace t)
  {
    setTrace(t);
    return translateTrace();
  }

  @Override
  public boolean requiresFlat()
  {
    return true;
  }

  @Override
  public boolean requiresPropositional() {
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

  @Override
  public String getTraceFile()
  {
    return m_outTrace;
  }

  @Override
  public String getFormulaFile()
  {
    return m_outFormula;
  }

  @Override
  public String getSignatureFile()
  {
    return "";
  }

 
}


