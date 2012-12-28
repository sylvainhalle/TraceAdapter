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
import java.io.FileInputStream;
import java.util.*;

import ca.uqac.info.ltl.*;
import ca.uqac.info.trace.Event;
import ca.uqac.info.trace.EventTrace;
import ca.uqac.info.trace.XmlTraceReader;
import ca.uqac.info.util.Relation;

public class SqlTranslator extends Translator
{
  /**
   * Database name for storing the trace
   */
  protected final String m_databaseName = "TraceAdapter";
  
  /**
   * Table name for storing the trace
   */
  protected final String m_tableName = "trace";

  /**
   * Attribute name for message number
   */
  protected final String m_eventId = "msgno";
  
  /**
   * Whether to count time for query processing only, or also
   * for loading the trace into the database (this will change
   * to what file the trace data will be sent)
   */
  protected boolean m_queryOnly = true;
  
  @Override
  public String translateTrace(EventTrace t)
  {
    setTrace(t);
    return translateTrace();
  }

  public String translateTrace()
  {
    StringBuilder out = new StringBuilder();
    Relation<String,String> domains = m_trace.getParameterDomain();
    Set<String> params = domains.keySet();
    // We must convert the set into a list to force a fixed iteration order
    List<String> o_params = new LinkedList<String>();
    o_params.addAll(params);
    // Table contents
    out.append("USE ").append(m_databaseName).append(";\n");
    out.append("INSERT INTO ").append(m_tableName).append(" (`").append(m_eventId).append("`");
    for (String p_name : o_params)
    {
      out.append(",`").append(p_name).append("`");
    }
    out.append(")\nVALUES\n");
    for (int i = 0; i < m_trace.size(); i++)
    {
      Event e = m_trace.elementAt(i);
      if (!e.isFlat() || e.isMultiValued())
      {
        out.append("-- WARNING: this event is not flat or is multi-valued\n");
      }
      out.append(toSql(e, i, o_params));
      if (i < m_trace.size() - 1)
        out.append(",\n");
    }
    out.append(";");
    return out.toString();
  }

  private StringBuilder toSql(Event e, int event_no, List<String> all_params)
  {
    StringBuilder out = new StringBuilder();
    Relation<String,String> domain = e.getParameterDomain();
    Set<String> params = domain.keySet();
    out.append("(").append(event_no);
    for (String p : all_params)
    {
      out.append(", ");
      if (!params.contains(p))
        out.append("NULL");
      else
      {
        Set<String> values = domain.get(p);
        String val = "";
        // We get only the first value
        for (String v : values)
        {
          val = v;
          break;
        }
        out.append("\"").append(val).append("\"");
      }
    }
    out.append(")");
    return out;
  }

  public String translateFormula(Operator o)
  {
    setFormula(o);
    return translateFormula();
  }

  protected StringBuilder translateFormula(Operator o, int level)
  {
    StringBuilder out = new StringBuilder();
    if (o instanceof Exists)
      return translateFormula((Exists) o, level);
    else if (o instanceof ForAll)
      return translateFormula((ForAll) o, level);
    else if (o instanceof OperatorAnd)
      return translateFormula((OperatorAnd) o, level);
    else if (o instanceof OperatorOr)
      return translateFormula((OperatorOr) o, level);
    else if (o instanceof OperatorNot)
      return translateFormula((OperatorNot) o, level);
    else if (o instanceof OperatorEquals)
      return translateFormula((OperatorEquals) o, level);
    else if (o instanceof OperatorF)
      return translateFormula((OperatorF) o, level);
    else if (o instanceof OperatorG)
      return translateFormula((OperatorG) o, level);
    else if (o instanceof OperatorX)
      return translateFormula((OperatorX) o, level);
    else if (o instanceof OperatorFalse)
      return translateFormula((OperatorFalse) o, level);
    else if (o instanceof OperatorTrue)
      return translateFormula((OperatorTrue) o, level);
    else if (o instanceof Atom)
      return translateFormula((Atom) o, level);
    else if (o instanceof XPathAtom)
      return translateFormula((XPathAtom) o, level);
    return out;
  }

  protected StringBuilder translateFormula(Exists o, int level)
  {
    // Not supposed to happen!
    assert false;
    System.err.println("ERROR: quantifier in SQL input formula");
    return new StringBuilder("");
  }

  protected StringBuilder translateFormula(ForAll o, int level)
  {
    // Not supposed to happen!
    assert false;
    System.err.println("ERROR: quantifier in SQL input formula");
    return new StringBuilder("");
  }


  protected StringBuilder translateFormula(OperatorAnd o, int level)
  {
    StringBuilder out = new StringBuilder();
    out.append("(").append(translateFormula(o.getLeft(), level + 1)).append(")");
    out.append(" INTERSECT ");
    out.append("(").append(translateFormula(o.getRight(), level + 1)).append(")");
    return out;
  }

  protected StringBuilder translateFormula(OperatorOr o, int level)
  {
    StringBuilder out = new StringBuilder();
    out.append("(").append(translateFormula(o.getLeft(), level + 1)).append(")");
    out.append(" UNION ");
    out.append("(").append(translateFormula(o.getRight(), level + 1)).append(")");
    return out;
  }

  protected StringBuilder translateFormula(OperatorNot o, int level)
  {
    StringBuilder out = new StringBuilder();
    if (o.getOperand().getClass() == OperatorEquals.class)
    {
      // Optimization: if we negate an equality, we just write != instead
      return translateFormulaNegated((OperatorEquals) o.getOperand(), level + 1);
    }
    // Otherwise, the generic translation of the negation
    out.append("(SELECT ").append(m_eventId).append(" FROM ").append(m_tableName).append(" AS ").append(m_tableName).append(level).append(") MINUS (");
    return out;
  }

  protected StringBuilder translateFormula(OperatorG o, int level)
  {
    // We handle G phi through the equivalence !F (!phi)
    OperatorNot n1 = new OperatorNot();
    n1.setOperand(o.getOperand());
    OperatorF f = new OperatorF();
    f.setOperand(n1);
    OperatorNot n2 = new OperatorNot();
    n2.setOperand(f);
    return translateFormula(n2, level);
  }

  protected StringBuilder translateFormula(OperatorF o, int level)
  {
    StringBuilder out = new StringBuilder();
    String table_name_1 = m_tableName + "A" + level;
    String table_name_2 = m_tableName + "B" + level;
    String id_1 = table_name_1 + "." + m_eventId;
    String id_2 = table_name_2 + "." + m_eventId;
    out.append("SELECT DISTINCT ").append(id_1);
    out.append(" FROM ").append(m_tableName).append(" AS ").append(table_name_1).append(" JOIN ");
    out.append("(").append(translateFormula(o.getOperand(), level + 1)).append(") AS ").append(table_name_2);
    out.append(" WHERE ").append(id_1).append(" <= ").append(id_2);
    return out;
  }

  protected StringBuilder translateFormula(OperatorX o, int level)
  {
    StringBuilder out = new StringBuilder();
    out.append("SELECT ").append(m_eventId).append("-1 FROM ").append(" AS ").append(m_tableName).append(level);
    return out;
  }

  protected StringBuilder translateFormula(Atom o, int level)
  {
    //return o.getSymbol();
    StringBuilder out = new StringBuilder();
    if(o instanceof Constant)
      out.append("\"").append(o.getSymbol()).append("\"");
    else
      out.append(o.getSymbol());
    return out;

  }
  
  protected StringBuilder translateFormula(OperatorFalse o, int level)
  {
    StringBuilder out = new StringBuilder();
    out.append("SELECT ").append(m_eventId).append(" FROM ").append(m_tableName).append(" WHERE FALSE");
    return out;
  }
  
  protected StringBuilder translateFormula(OperatorTrue o, int level)
  {
    StringBuilder out = new StringBuilder();
    out.append("SELECT ").append(m_eventId).append(" FROM ").append(m_tableName).append(" WHERE TRUE");
    return out;
  }

  protected StringBuilder translateFormula(XPathAtom o, int level)
  {
    //return o.toString(false);
    // Flatten atom: keep only last part
    StringBuilder out = new StringBuilder();
    out.append("`").append(o.getFlatName()).append("`");
    return out;
  }

  protected StringBuilder translateFormula(OperatorEquals o, int level)
  {
    StringBuilder out = new StringBuilder();
    out.append("SELECT ").append(m_eventId).append(" FROM ").append(m_tableName).append(" AS ").append(m_tableName).append(level).append(" WHERE ");
    //out.append("`").append(translateFormula(o.getLeft(), level + 1));
    //out.append("` = '").append(translateFormula(o.getRight(), level + 1)).append("'");
    out.append(translateFormula(o.getLeft(), level + 1));
    out.append(" = ").append(translateFormula(o.getRight(), level + 1));
    return out;
  } 

  protected StringBuilder translateFormulaNegated(OperatorEquals o, int level)
  {
    StringBuilder out = new StringBuilder();
    out.append("SELECT ").append(m_eventId).append(" FROM ").append(m_tableName).append(" AS ").append(m_tableName).append(level).append(" WHERE ");
    out.append(translateFormula(o.getLeft(), level + 1)).append(" != ").append(translateFormula(o.getRight(), level + 1));
    return out;
  }

  @Override
  public String getSignature(EventTrace t)
  {
    return getSignature();
  }

  @Override
  public String translateFormula()
  {
    StringBuilder out = new StringBuilder();
    out.append("SELECT ").append(m_eventId).append(" FROM (");
    out.append(translateFormula(m_formula, 0));
    out.append(") WHERE `").append(m_eventId).append("` = 0;");
    return out.toString();
  }

  @Override
  public boolean requiresPropositional()
  {
    return true;
  }

  /*public static void main(String[] args)
{
	Operator o = null;
	File fic = new File("traces/traces_0.txt");
	XmlTraceReader xtr = new XmlTraceReader();
	try
	{
		o = Operator.parseFromString("F (/p0 = 0)");
	}
	catch (Operator.ParseException e)
	{
		System.out.println("Parse exception");
	}
	SqlTranslator bt = new SqlTranslator();
	EventTrace t = xtr.parseEventTrace(fic);
	String f = bt.translateFormula(o);
	System.out.println(f);
	System.out.println(bt.translateTrace(t));
}*/

  public static void main(String[] args)
  {
    // Definition of trace and property
    String filename = "traces/traces_0.txt";
    String formula = "# -----------------------------------\n# Control formula #1\n# Some event has p0=0\n# -----------------------------------\n";
    formula += "F (∃i ∈ /p0 : (i=0))";

    // Read trace
    File fic = new File(filename);
    XmlTraceReader xtr = new XmlTraceReader();
    EventTrace t = null;
    try
    {
      t = xtr.parseEventTrace(new FileInputStream(fic));
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

    // Convert to propositional LTL and reparse
    PropositionalTranslator pt = new PropositionalTranslator();
    pt.setFormula(o);
    pt.setTrace(t);
    String s = pt.translateFormula();
    try
    {
      o = Operator.parseFromString(s);
    }
    catch (Operator.ParseException e)
    {
      System.out.println("Parse exception");
    }

    // Convert property and trace to SQL
    Translator bt = new SqlTranslator();
    bt.setFormula(o);
    bt.setTrace(t);
    System.out.println(bt.translateTrace());
    String f = bt.translateFormula();
    System.out.println(f);
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

  @Override
  public String getSignature()
  {
    StringBuilder out = new StringBuilder();
    Relation<String,String> domains = m_trace.getParameterDomain();
    Set<String> params = domains.keySet();
    out.append("-- Trace file automatically generated by\n-- Event Trace Converter\n\n");
    out.append("USE ").append(m_databaseName).append(";\n");
    // Table structure
    out.append("DROP TABLE IF EXISTS ").append(m_tableName).append(";\n");
    out.append("CREATE TABLE ").append(m_tableName).append(" (\n");
    for (String p : params)
    {
      out.append("  `").append(p).append("` tinytext DEFAULT null,\n");
    }
    out.append("  msgno int(11) NOT NULL,\n");
    out.append("  PRIMARY KEY (`").append(m_eventId).append("`)\n) ENGINE=MyISAM AUTO_INCREMENT=0 DEFAULT CHARSET=utf8;\n\n");
    return out.toString();
  }

  @Override
  public String getTraceFile()
  {
    StringBuilder out = new StringBuilder();
    if (!m_queryOnly)
      out.append(m_outTrace).append("\n\n");
    else
      out.append("USE ").append(m_databaseName).append(";\n");
    out.append(m_outFormula);
    return out.toString();
  }

  @Override
  public String getFormulaFile()
  {
    return "";
  }

  @Override
  public String getSignatureFile()
  {
    StringBuilder out = new StringBuilder();
    out.append(m_outSignature);
    if (m_queryOnly)
      out.append(m_outTrace);
    return out.toString();
  }
}