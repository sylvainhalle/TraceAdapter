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

import java.util.Set;

import ca.uqac.info.ltl.*;
import ca.uqac.info.trace.*;
import ca.uqac.info.util.Relation;

/**
 * Translates a multi-valued trace and formula into a single-valued
 * one.
 * @author Sylvain Hallé
 *
 */
public class FlatTranslator extends Translator
{
  
  /**
   * Determines if the equivalence has been preserved during
   * the translation.
   */
  protected boolean m_equivalencePreserved = true;

  /**
   * Determines if the equivalence has been preserved during
   * the translation. The equivalence is <em>not</em> preserved
   * when there is more than one occurrence of a parameter inside
   * an event, as only one of these values will survive in the
   * translated trace.
   * @return True if the equivalence was preserved, false
   * otherwise
   */
  public boolean isEquivalencePreserved()
  {
    return m_equivalencePreserved;
  }

  @Override
  public String translateTrace(EventTrace t)
  {
    m_trace = t;
    return translateTrace();
  }

  @Override
  public String translateTrace()
  {
    StringBuilder out = new StringBuilder();
    out.append("<trace>\n");
    for (Event e : m_trace)
    {
      // If event is multi-valued, then we will lose information
      // by flattening it
      if (e.isMultiValued())
        m_equivalencePreserved = false;
      out.append("<message>\n");
      Relation<String,String> domain = e.getParameterDomain();
      Set<String> all_params = domain.keySet();
      for (String p : all_params)
      {
        if (!all_params.contains(p))
          continue;
        Set<String> values = domain.get(p);
        String val = "";
        // We get only the first value
        for (String v : values)
        {
          val = v;
          break;
        }
        out.append("<").append(p).append(">").append(val).append("</").append(p).append(">\n");
      }
      out.append("</message>\n");
    }
    out.append("</trace>");
    return out.toString();
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
    FlatVisitor fv = new FlatVisitor();
    m_formula.accept(fv);
    Operator op = fv.getFormula();
    return op.toString();
  }
  
  @Override
  public boolean requiresFlat()
  {
    return false;
  }

  @Override
  public boolean requiresPropositional()
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
  
  protected class FlatVisitor extends GenericOperatorVisitor
  {
    public Operator getFormula()
    {
      Operator op = m_pieces.peek();
      return op;
    }
    
    @Override
    public void visit(Exists o)
    {
      flattenQuantifier(new Exists());
    }
    
    @Override
    public void visit(ForAll o)
    {
      flattenQuantifier(new ForAll());
    }
    
    @Override
    public void visit(XPathAtom o)
    {
      XPathAtom new_x_path = new XPathAtom(o.getFlatName());
      m_pieces.push(new_x_path);
    }
    
    private void flattenQuantifier(Quantifier out)
    {
      Operator operand = m_pieces.pop();
      Operator path = m_pieces.pop();
      Operator variable = m_pieces.pop();
      
      // These two assertions must hold or the formula is incorrectly parsed!
      assert variable instanceof Atom;
      assert path instanceof XPathAtom;
      XPathAtom x_path = ((XPathAtom) path); // Can safely cast
      
      // Flatten the path in the quantifier
      XPathAtom new_x_path = new XPathAtom(x_path.getFlatName());
      out.setOperand(operand);
      out.setVariable((Atom) variable);
      out.setPath(new_x_path);
      m_pieces.push(out);      
    }
  }
}
