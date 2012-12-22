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
import java.util.HashSet;
import java.util.Set;
import java.util.Stack;
import java.util.Vector;

import ca.uqac.info.ltl.*;
import ca.uqac.info.ltl.Operator.ParseException;
import ca.uqac.info.trace.EventTrace;
import ca.uqac.info.trace.XmlTraceReader;
import ca.uqac.info.util.Relation;


public class MaudeTranslator extends Translator {

  protected AtomicTranslator m_atomicTranslator;

  /**
   * Constructor
   */
  public MaudeTranslator() {
    super();
    m_atomicTranslator = new AtomicTranslator();
  }

  public MaudeTranslator(EventTrace t) {
    this();
    m_trace = t;
  }

  @Override
  public String translateTrace(EventTrace t)
  {
    setTrace(t);
    return translateTrace();
  }

  public String translateFormula(Operator o) {

    StringBuffer out = new StringBuffer();
    MaudeFormulaTranslator mft = new MaudeFormulaTranslator();
    o.accept(mft);
    out.append(mft.getFormula());

    return out.toString();
  }

  protected class MaudeFormulaTranslator implements OperatorVisitor
  {
    Stack<StringBuffer> m_pieces;

    public MaudeFormulaTranslator() {
      super();
      m_pieces = new Stack<StringBuffer>();
    }

    public String getFormula() {
      StringBuffer out = m_pieces.peek();
      return out.toString();
    }

    @Override
    public void visit(OperatorAnd o) {
      StringBuffer right = m_pieces.pop();
      StringBuffer left = m_pieces.pop();
      StringBuffer out = new StringBuffer("(").append(left).append(") ")
          .append("/\\").append(" (").append(right).append(")");
      m_pieces.push(out);
    }

    @Override
    public void visit(OperatorOr o) {
      StringBuffer right = m_pieces.pop();
      StringBuffer left = m_pieces.pop();
      StringBuffer out = new StringBuffer("(").append(left).append(") ")
          .append("\\/").append(" (").append(right).append(")");
      m_pieces.push(out);
    }

    @Override
    public void visit(OperatorImplies o) {
      StringBuffer right = m_pieces.pop();
      StringBuffer left = m_pieces.pop();
      StringBuffer out = new StringBuffer("(").append(left)
          .append(") -> (").append(right).append(")");
      m_pieces.push(out);
    }

    @Override
    public void visit(OperatorNot o) {
      StringBuffer op = m_pieces.pop();
      StringBuffer out = new StringBuffer("~(").append(op).append(")");
      m_pieces.push(out);
    }

    @Override
    public void visit(OperatorF o) {
      StringBuffer op = m_pieces.pop();

      StringBuffer out = new StringBuffer("<> (").append(op).append(")");
      m_pieces.push(out);

    }

    @Override
    public void visit(OperatorX o) {
      StringBuffer op = m_pieces.pop();
      StringBuffer out = new StringBuffer("o (").append(op).append(")");
      m_pieces.push(out);
    }

    @Override
    public void visit(OperatorG o) {
      StringBuffer op = m_pieces.pop();
      StringBuffer out = new StringBuffer("[] (").append(op).append(")");
      m_pieces.push(out);
    }

    @Override
    public void visit(OperatorEquals o) {
      StringBuffer right = m_pieces.pop(); // Pop right-hand side
      StringBuffer left = m_pieces.pop(); // Pop left-hand side
      StringBuffer out = new StringBuffer();
      out.append(left).append(" = ").append(right);
      m_pieces.push(out);
    }

    @Override
    public void visit(Atom o) {
      if (o instanceof OperatorFalse)
        m_pieces.push(new StringBuffer("false"));
      else if (o instanceof OperatorTrue)
        m_pieces.push(new StringBuffer("true"));
      else
        m_pieces.push(new StringBuffer(o.getSymbol()));
    }

    @Override
    public void visit(OperatorEquiv o) {
    }

    @Override
    public void visit(OperatorU o) {
    }

    @Override
    public void visit(Exists o)
    {
      // Not supposed to happen!
      System.err.println("Error: quantifier found in Maude translator");
      assert false;
    }

    @Override
    public void visit(ForAll o)
    {
      // Not supposed to happen!
      System.err.println("Error: quantifier found in Maude translator");
      assert false;
    }

    @Override
    public void visit(XPathAtom p)
    {
      // false because no leading slash
      m_pieces.push(new StringBuffer(p.toString(false)));
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

  }

  protected class MaudeEqualityGetter extends EmptyVisitor {

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
      // We only add equalities between an XPathAtom and something
      // Other equalities are between constants
      if (o.getLeft() instanceof XPathAtom || o.getRight() instanceof XPathAtom)
        m_equalities.add(o);
    }

  }

  @Override
  public String getSignature(EventTrace m_trace) {
    Relation<String, String> param_domains = m_trace.getParameterDomain();
    Set<String> params = param_domains.keySet();
    Vector<String> vectParams = new Vector<String>();
    vectParams.addAll(params);

    String strTemp = vectParams.toString().replaceAll(",", " ");
    strTemp = strTemp.replace("[", " ");
    strTemp = strTemp.replace("]", " ");
    return strTemp;
  }

  @Override
  public String translateFormula()
  {
    // We assume translateTrace has been called first!
    String prop = m_atomicTranslator.translateFormula(m_formula);
    Operator op = null;
    try
    {
      op = Operator.parseFromString(prop);
    }
    catch (ParseException e)
    {
      System.err.println("Parse error in MaudeTranslator");
      return "";
    }
    MaudeFormulaTranslator mft = new MaudeFormulaTranslator();
    op.accept(mft);
    return mft.getFormula();
  }

  @Override
  public String translateTrace()
  {
    String sat_operator = " |= ";
    StringBuffer out_Trace = new StringBuffer();
    Set<String> listParm = new HashSet<String>();
    StringBuffer maude_trace = new StringBuffer();
    String atomic_trace = m_atomicTranslator.translateTrace(m_trace);
    boolean first = true;
    for (String c : atomic_trace.split(" "))
    {
      if (!first)
        maude_trace.append(",");
      maude_trace.append(c);
      listParm.add(c);
      first = false;
    }
    StringBuffer operandes = new StringBuffer();
    for (String c : listParm)
      operandes.append(c).append(" ");
    String prop = translateFormula();
    maude_trace = maude_trace.append(sat_operator).append(prop);
    // Start writing the Java program
    out_Trace.append("in ltl.maude\n");
    out_Trace.append("fmod MY-TRACE is").append("\n");
    out_Trace.append("  extending LTL .").append("\n");
    out_Trace.append("  ops  ").append(operandes).append(" : -> Atom .")
    .append("\n");
    out_Trace.append("endfm\n");
    out_Trace.append("reduce").append(" ");
    out_Trace.append(maude_trace).append(".");
    out_Trace.append("\n quit");
    return out_Trace.toString();
  }

  public static void main (String [] args)
  {
    // File to read and property to verify
    String filename = "traces/traces_0.txt";
    String formula = "F (∃i ∈ /p0 : (i=0))";

    // Read trace
    File f = new File(filename);
    XmlTraceReader reader = new XmlTraceReader();
    EventTrace trace = null;
    try
    {
      trace = reader.parseEventTrace(new java.io.FileInputStream(f));  
    }
    catch (java.io.FileNotFoundException ex)
    {
      ex.printStackTrace();
      System.exit(1);
    }
    assert trace != null;

    // Parse property
    Operator o = null;
    try {
      o = Operator.parseFromString(formula);
    } catch (ParseException e) {
      e.printStackTrace();
    }

    // Convert to propositional LTL
    PropositionalTranslator pt = new PropositionalTranslator();
    pt.setFormula(o);
    pt.setTrace(trace);
    String s = pt.translateFormula();

    // Reparse and convert to atomic LTL
    try
    {
      o = Operator.parseFromString(s);
    }
    catch (Operator.ParseException e)
    {
      System.out.println("Parse exception");
    }
    ConstantConverter cc = new ConstantConverter();
    o.accept(cc);
    o = cc.getFormula();

    // Pass trace and property to Maude translator
    MaudeTranslator md = new MaudeTranslator() ;
    md.setTrace(trace);
    md.setFormula(o);

    // Output Maude file
    System.out.println(md.translateTrace(trace));

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
    return true;
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
    return "";
  }

  @Override
  public String getSignatureFile()
  {
    return "";
  }

}
