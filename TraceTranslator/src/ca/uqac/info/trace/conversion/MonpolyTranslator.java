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
import java.util.*;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import ca.uqac.info.ltl.*;
import ca.uqac.info.trace.*;
import ca.uqac.info.util.Relation;

public class MonpolyTranslator extends Translator
{
  protected Random m_random = new Random();
  protected final int m_leftBound = 0;
  protected int m_rightBound = 1000;

  @Override
  public String translateTrace(EventTrace m_trace)
  {
    setTrace(m_trace);
    return translateTrace();
  }

  /**
   * 
   */
  @Override
  public String translateTrace()
  {
    StringBuffer out = new StringBuffer();
    int trace_length = m_trace.size();
    for (int i = 0; i < trace_length; i++)
    {
      Event e = m_trace.elementAt(i);
      out.append("@").append(i);
      Node n = e.getDomNode();
      NodeList children = n.getChildNodes();
      Node child;
      String val = "";
      /* TODO: hard-coded nested loops is not a good way to do trace flattening! */
      if (children.getLength() > 1)
      {
        NodeList level1 = children.item(1).getChildNodes();
        // out.append(" ").append( children.item(1).getNodeName());
        if (level1.getLength() > 1)
        {

          NodeList level2 = level1.item(1).getChildNodes();
          if (level2.getLength() > 1)
          {

            for (int index = 1; index < level2.getLength(); index++)
            {
              child = level2.item(index);

              if (child.getChildNodes().getLength() == 1)
              {
                val = child.getTextContent();
                val = val.trim();
                if (val.isEmpty())
                  continue;
                out.append(toMonpoly(child, ""));
              } else if (child.getChildNodes().getLength() > 1)
              {
                NodeList level3 = level2.item(index).getChildNodes();

                if (level3.getLength() > 1)
                {
                  for (int in = 1; in < level3.getLength(); in++)
                  {
                    child = level3.item(in);

                    if (child.getChildNodes().getLength() == 1)
                    {
                      val = child.getTextContent();
                      val = val.trim();
                      if (val.isEmpty())
                        continue;
                      out.append(toMonpoly(child, ""));
                    } else if (child.getChildNodes().getLength() > 1)
                    {
                      NodeList level4 = level3.item(in).getChildNodes();

                      if (level4.getLength() > 1)
                      {
                        for (int y = 1; y < level4.getLength(); y++)
                        {
                          child = level4.item(y);
                          if (child.getChildNodes().getLength() == 1)
                          {
                            val = child.getTextContent();
                            val = val.trim();
                            if (val.isEmpty())
                              continue;
                            out.append(toMonpoly(child, ""));
                          }
                        }
                      }
                    }
                  }

                }

              }
            }
          } else
          {
            for (int j = 0; j < level1.getLength(); j++)
            {
              child = level1.item(j);
              val = child.getTextContent();
              val = val.trim();
              if (val.isEmpty())
                continue;
              out.append(toMonpoly(child, ""));
            }

          }
        } else
        {
          for (int j = 0; j < children.getLength(); j++)
          {
            child = children.item(j);
            val = child.getTextContent();
            val = val.trim();
            if (val.isEmpty())
              continue;
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
      out.append("\n\t").append(indent).append(n.getNodeName()).append(" ( ")
          .append(val).append(" ) ");
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
  public String translateFormula()
  {
    // Since MFOTL operators are bounded, we set the upper bound of the
    // interval to the length of the trace
    m_rightBound = m_trace.size();
    StringBuffer out = new StringBuffer();
    MonpolyFormulaTranslator mft = new MonpolyFormulaTranslator();
    m_formula.accept(mft);
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
      StringBuffer out = new StringBuffer("(").append(left).append(") AND (")
          .append(right).append(")");
      m_pieces.push(out);
    }

    @Override
    public void visit(OperatorOr o)
    {
      StringBuffer right = m_pieces.pop();
      StringBuffer left = m_pieces.pop();
      StringBuffer out = new StringBuffer("(").append(left).append(") OR (")
          .append(right).append(")");
      m_pieces.push(out);
    }

    @Override
    public void visit(OperatorImplies o)
    {
      StringBuffer right = m_pieces.pop();
      StringBuffer left = m_pieces.pop();
      StringBuffer op = m_pieces.pop();

      StringBuffer out = new StringBuffer("(").append(left).append(") IMPLIES")
          .append("(").append(right).append(op).append(")");
      m_pieces.push(out);

    }

    @Override
    public void visit(OperatorNot o)
    {
      StringBuffer op = m_pieces.pop();
      StringBuffer out = new StringBuffer("NOT (").append(op).append(")");
      m_pieces.push(out);
    }

    @Override
    public void visit(OperatorF o)
    {
      StringBuffer op = m_pieces.pop();
      StringBuffer out = new StringBuffer("EVENTUALLY [")
          .append(m_leftBound).append(",").append(m_rightBound)
          .append("] ( ").append(op).append(" )");
      m_pieces.push(out);
    }

    @Override
    public void visit(OperatorX o)
    {
      StringBuffer op = m_pieces.pop();
      StringBuffer out = new StringBuffer("NEXT [0,1] ( ")
        .append(op).append(" )");
      m_pieces.push(out);
    }

    @Override
    public void visit(OperatorG o)
    {
      StringBuffer op = m_pieces.pop();
      StringBuffer out = new StringBuffer("ALWAYS [")
      .append(m_leftBound).append(",").append(m_rightBound)
      .append("] ( ").append(op).append(" )");
      m_pieces.push(out);

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

    @Override
    public void visit(OperatorEquals o)
    {
      StringBuffer right = m_pieces.pop(); // Pop right-hand side
      StringBuffer left = m_pieces.pop(); // Pop left-hand side
      StringBuffer out = new StringBuffer();
      Operator o_left = o.getLeft();
      if (o_left instanceof XPathAtom)
      {
        // If we equal a path to a term, we must write it as a predicate
        out.append(left).append("(").append(right).append(")");
      }
      else
      {
        // Otherwise, we write the equality as an equality
        out.append("(").append(left).append(" = ").append(right).append(")");
      }
      m_pieces.push(out);
    }

    @Override
    public void visit(Atom o)
    {
      if (o instanceof Constant)
        m_pieces.push(new StringBuffer("\"").append(o.getSymbol()).append("\""));
      else
        m_pieces.push(new StringBuffer("?").append(o.getSymbol()));
    }

    @Override
    public void visit(OperatorEquiv o)
    {
      StringBuffer right = m_pieces.pop();
      StringBuffer left = m_pieces.pop();
      StringBuffer out = new StringBuffer("(").append(left).append(") <-> (")
          .append(right).append(")");
      m_pieces.push(out);
    }

    @Override
    public void visit(OperatorU o)
    {
      StringBuffer right = m_pieces.pop();  // Pop right-hand side
      StringBuffer left = m_pieces.pop();  // Pop left-hand side
      StringBuffer out = new StringBuffer("(").append(left).append(" UNTIL [")
      .append(m_leftBound).append(",").append(m_rightBound)
      .append("] ( ").append(right).append(" )");
      m_pieces.push(out);
    }

    @Override
    public void visit(Exists o)
    {
      StringBuffer operand = m_pieces.pop();
      StringBuffer variable = m_pieces.pop();
      StringBuffer var = m_pieces.peek();
      StringBuffer out = new StringBuffer();
      out.append("EXISTS ").append(var).append(". ").append("(")
          .append(variable).append("(").append(var).append(")")
          .append(" AND ").append(operand).append(")");
      m_pieces.push(out);
    }

    @Override
    public void visit(ForAll o)
    {
      StringBuffer operand = m_pieces.pop();
      StringBuffer variable = m_pieces.pop();
      StringBuffer var = m_pieces.peek();
      StringBuffer out = new StringBuffer();
      out.append("FORALL ").append(var).append(". ").append("(")
          .append(variable).append("(").append(var).append(")")
          .append(" AND ").append(operand).append(")");
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

  protected class MonpolyEqualityGetter implements OperatorVisitor
  {

    Set<OperatorEquals> m_equalities;

    public MonpolyEqualityGetter()
    {
      super();
      m_equalities = new HashSet<OperatorEquals>();
    }

    public Set<OperatorEquals> getEqualities()
    {
      return m_equalities;
    }
    
    @Override
    public void visit(OperatorTrue o)
    {
      // Nothing to be done here
    }

    @Override
    public void visit(OperatorFalse o)
    {
      // Nothing to be done here
    }

    @Override
    public void visit(OperatorEquals o)
    {
      m_equalities.add(o);
    }

    @Override
    public void visit(OperatorU o)
    {
      // Nothing to be done here
    }

    @Override
    public void visit(OperatorAnd o)
    {
      // Nothing to be done here
    }

    @Override
    public void visit(OperatorOr o)
    {
      // Nothing to be done here
    }

    @Override
    public void visit(OperatorNot o)
    {
      // Nothing to be done here
    }

    @Override
    public void visit(OperatorF o)
    {
      // Nothing to be done here
    }

    @Override
    public void visit(OperatorX o)
    {
      // Nothing to be done here
    }

    @Override
    public void visit(OperatorG o)
    {
      // Nothing to be done here
    }

    @Override
    public void visit(OperatorImplies o)
    {
      // Nothing to be done here
    }

    @Override
    public void visit(OperatorEquiv o)
    {
      // Nothing to be done here
    }

    @Override
    public void visit(Atom o)
    {
      // Nothing to be done here
    }

    @Override
    public void visit(Exists o)
    {
      // Nothing to be done here
    }

    @Override
    public void visit(ForAll o)
    {
      // Nothing to be done here
    }

    @Override
    public void visit(XPathAtom p)
    {

    }

  }
  
  @Override
  public String getSignature()
  {
    return getSignature(m_trace);
  }

  @Override
  public String getSignature(EventTrace trace)
  {
    StringBuffer out = new StringBuffer();
    Relation<String, String> param_domains = trace.getParameterDomain();
    Set<String> params = param_domains.keySet();
    Vector<String> vectParams = new Vector<String>();
    vectParams.addAll(params);

    out = this.toMonpolySignature(vectParams);
    return out.toString();
  }

  /**
   * 
   * @param params
   * @return
   */

  private StringBuffer toMonpolySignature(Vector<String> params)
  {
    StringBuffer out = new StringBuffer();
    for (String p : params)
    {
      out.append(p).append(" (string)\n");
    }
    return out;

  }

  @Override
  public String translateFormula(Operator o)
  {
    setFormula(o);
    return translateFormula();
  }

  @Override
  public boolean requiresPropositional()
  {
    return false;
  }
  
  public static void main(String[] args)
  {
      // Definition of trace and property
      String filename = "traces/traces_0.txt";
      String formula = "F (∃i ∈ /p0 : (i=0))";
      
      // Read trace
      File fic = new File(filename);
      XmlTraceReader xtr = new XmlTraceReader();
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
      
      // Parse property
      Operator o = null;
      try
      {
          o = Operator.parseFromString(formula);
      }
      catch (Operator.ParseException e)
      {
          System.out.println("Parse exception");
      }
      
      // Convert property and trace to SQL
      Translator bt = new MonpolyTranslator();
      bt.setFormula(o);
      bt.setTrace(t);
      System.out.println(bt.translateTrace());
      String f = bt.translateFormula();
      System.out.println(f);
      String sig = bt.getSignature(t);
      System.out.println(sig);
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
}
