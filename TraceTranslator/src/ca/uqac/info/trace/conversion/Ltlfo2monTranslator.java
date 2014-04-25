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

import java.util.*;

import ca.uqac.info.ltl.*;
import ca.uqac.info.trace.*;
import ca.uqac.info.util.Relation;

/**
 * Translates an input trace and a formula into the LTL<sup>FO</sup>
 * language for ltlfo2mon, a monitoring tool that checks
 * compliance of log files with respect to policies. 
 * <p>
 * For more information about ltlfo2mon, please refer to its
 * <a href="https://github.com/jckuester/ltlfo2mon">web site</a>
 * or the following technical report:
 * <ul>
 * <li>Andreas Bauer, Jan-Christoph Küster, and Gil Vegliach.
 * (2013). <a href="http://arxiv.org/abs/1303.3645"><cite>From propositional
 * to first-order monitoring.</cite></a>
 * Computing Research Repository (CoRR) abs/1303.3645. Association
 * for Computing Machinery (ACM), March 2013.</li> 
 * </ul>
 * @author Sylvain Hallé
 *
 */
public class Ltlfo2monTranslator extends Translator
{
  @Override
  public String translateTrace(EventTrace m_trace)
  {
    setTrace(m_trace);
    return translateTrace();
  }

  @Override
  public String translateTrace()
  {
    StringBuilder out = new StringBuilder();
    boolean first_event = true;
    for (Event e : m_trace)
    {
      if (first_event)
        first_event = false;
      else
        out.append(",");
      out.append("{");
      Relation<String,String> domain = e.getParameterDomain();
      Set<String> all_params = domain.keySet();
      boolean first_tuple = true;
      for (String p : all_params)
      {
        if (!all_params.contains(p))
          continue;
        Set<String> values = domain.get(p);
        // We get only the first value
        for (String v : values)
        {
          if (first_tuple)
            first_tuple = false;
          else
            out.append(",");
          out.append(p).append("(").append(v).append(")");
        }
      }
      out.append("}");
    }
    return out.toString();
    
  }

  @Override
  public String translateFormula()
  {
    StringBuilder out = new StringBuilder();
    Ltlfo2monFormulaTranslator mft = new Ltlfo2monFormulaTranslator();
    m_formula.accept(mft);
    out.append(mft.getFormula());
    return out.toString();
  }

  protected class Ltlfo2monFormulaTranslator implements OperatorVisitor
  {
    Stack<StringBuilder> m_pieces;

    public Ltlfo2monFormulaTranslator()
    {
      super();
      m_pieces = new Stack<StringBuilder>();
    }

    public String getFormula()
    {
      StringBuilder out = m_pieces.peek();
      return out.toString();
    }

    @Override
    public void visit(OperatorAnd o)
    {
      StringBuilder right = m_pieces.pop();
      StringBuilder left = m_pieces.pop();
      StringBuilder out = new StringBuilder("(").append(left).append(") && (")
          .append(right).append(")");
      m_pieces.push(out);
    }

    @Override
    public void visit(OperatorOr o)
    {
      StringBuilder right = m_pieces.pop();
      StringBuilder left = m_pieces.pop();
      StringBuilder out = new StringBuilder("(").append(left).append(") || (")
          .append(right).append(")");
      m_pieces.push(out);
    }

    @Override
    public void visit(OperatorImplies o)
    {
      StringBuilder right = m_pieces.pop();
      StringBuilder left = m_pieces.pop();

      StringBuilder out = new StringBuilder("(").append(left).append(") -> ")
          .append("(").append(right).append(")");
      m_pieces.push(out);

    }

    @Override
    public void visit(OperatorNot o)
    {
      StringBuilder op = m_pieces.pop();
      StringBuilder out = new StringBuilder("! (").append(op).append(")");
      m_pieces.push(out);
    }

    @Override
    public void visit(OperatorF o)
    {
      StringBuilder op = m_pieces.pop();
      StringBuilder out = new StringBuilder("F (").append(op).append(")");
      m_pieces.push(out);
    }

    @Override
    public void visit(OperatorX o)
    {
      StringBuilder op = m_pieces.pop();
      StringBuilder out = new StringBuilder("X (").append(op).append(")");
      m_pieces.push(out);
    }

    @Override
    public void visit(OperatorG o)
    {
      StringBuilder op = m_pieces.pop();
      StringBuilder out = new StringBuilder("G (").append(op).append(")");
      m_pieces.push(out);
    }
    
    @Override
    public void visit(OperatorTrue o)
    {
      m_pieces.push(new StringBuilder("true"));
      
    }

    @Override
    public void visit(OperatorFalse o)
    {
      m_pieces.push(new StringBuilder("false"));
    }

    @Override
    public void visit(OperatorEquals o)
    {
      StringBuilder right = m_pieces.pop(); // Pop right-hand side
      StringBuilder left = m_pieces.pop(); // Pop left-hand side
      StringBuilder out = new StringBuilder();
      Operator o_left = o.getLeft();
      if (o_left instanceof XPathAtom)
      {
        // If we equal a path to a term, we must write it as a predicate
        // NOTE: not supported by ltlfo2mon!
        System.err.println("WARNING: Comparing a path to a variable directly is not supported by ltlfo2mon");
        out.append(left).append("(").append(right).append(")");
      }
      else
      {
        // Otherwise, we write the equality with the eq predicate
        if (o.getLeft() instanceof Constant || o.getRight() instanceof Constant)
        {
          // NOTE: not supported by ltlfo2mon!
          System.err.println("WARNING: Comparing constants is not supported by ltlfo2mon");
        }
        out.append("eq(").append(left).append(",").append(right).append(")");
      }
      m_pieces.push(out);
    }

    @Override
    public void visit(Atom o)
    {
      if (o instanceof Constant)
        m_pieces.push(new StringBuilder("\"").append(o.getSymbol()).append("\""));
      else
        m_pieces.push(new StringBuilder(o.getSymbol()));
    }

    @Override
    public void visit(OperatorEquiv o)
    {
      StringBuilder right = m_pieces.pop();
      StringBuilder left = m_pieces.pop();
      StringBuilder out = new StringBuilder("(").append(left).append(") <-> (")
          .append(right).append(")");
      m_pieces.push(out);
    }

    @Override
    public void visit(OperatorU o)
    {
      StringBuilder right = m_pieces.pop();  // Pop right-hand side
      StringBuilder left = m_pieces.pop();  // Pop left-hand side
      StringBuilder out = new StringBuilder("(").append(left).append(") U (")
          .append(right).append(")");
      m_pieces.push(out);
    }

    @Override
    public void visit(Exists o)
    {
      StringBuilder operand = m_pieces.pop(); // Operand
      StringBuilder variable = m_pieces.pop(); // Variable
      StringBuilder var = m_pieces.pop(); // Path
      StringBuilder out = new StringBuilder();
      out.append("E ").append(var).append(":").append(variable)
          .append(".(").append(operand).append(")");
      m_pieces.push(out);
    }

    @Override
    public void visit(ForAll o)
    {
      StringBuilder operand = m_pieces.pop(); // Operand
      StringBuilder variable = m_pieces.pop(); // Variable
      StringBuilder var = m_pieces.pop(); // Path
      StringBuilder out = new StringBuilder();
      out.append("A ").append(var).append(":").append(variable)
          .append(".(").append(operand).append(")");
      m_pieces.push(out);
    }

    @Override
    public void visit(XPathAtom p)
    {
      // Not supposed to happen!
      // System.err.println("Error: XML Path found in ltlfo2mon translator");
      // assert false;
      m_pieces.push(new StringBuilder(p.toString(false)));
    }
  }

  protected class Ltlfo2monEqualityGetter implements OperatorVisitor
  {

    Set<OperatorEquals> m_equalities;

    public Ltlfo2monEqualityGetter()
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


  public String getSignature(EventTrace trace)
  {
    StringBuilder out = new StringBuilder();
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

  private StringBuilder toMonpolySignature(Vector<String> params)
  {
    StringBuilder out = new StringBuilder();
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
      java.io.File fic = new java.io.File(filename);
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
      String sig = bt.getSignature();
      System.out.println(sig);
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
}
