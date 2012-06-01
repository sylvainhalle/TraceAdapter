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

import ca.uqac.info.ltl.Atom;
import ca.uqac.info.ltl.Operator;
import ca.uqac.info.ltl.OperatorAnd;
import ca.uqac.info.ltl.OperatorEquals;
import ca.uqac.info.ltl.OperatorF;
import ca.uqac.info.ltl.OperatorG;
import ca.uqac.info.ltl.OperatorNot;
import ca.uqac.info.ltl.OperatorOr;
import ca.uqac.info.ltl.OperatorX;
import ca.uqac.info.trace.Event;
import ca.uqac.info.trace.EventTrace;
import ca.uqac.info.util.Relation;

public class SqlTranslator extends Translator
{
  /**
   * Table name for storing the trace
   */
  protected final String m_tableName = "trace";
  
  /**
   * Attribute name for message number
   */
  protected final String m_eventId = "msgno";
  @Override
	public String translateTrace(EventTrace t)
	{
		setTrace(t);
		return translateTrace();
	}

  public String translateTrace()
  {
    StringBuffer out = new StringBuffer();
    Relation<String,String> domains = m_trace.getParameterDomain();
    Set<String> params = domains.keySet();
    out.append("-- Trace file automatically generated by\n-- Event Trace Converter\n\n");
    out.append("Use TraceAdaptater;").append("\n");
    // Table structure
    out.append("DROP TABLE IF EXISTS ").append(m_tableName).append(";\n");
    out.append("CREATE TABLE ").append(m_tableName).append(" (\n");
    for (String p : params)
    {
      out.append("  `").append(p).append("` tinytext DEFAULT null,\n");
    }
    out.append("  msgno int(11) NOT NULL,\n");
    out.append("  PRIMARY KEY (`").append(m_eventId).append("`)\n) ENGINE=MyISAM AUTO_INCREMENT=0 DEFAULT CHARSET=utf8;\n\n");
    // Table contents
    for (int i = 0; i < m_trace.size(); i++)
    {
      Event e = m_trace.elementAt(i);
      if (!e.isFlat() || e.isMultiValued())
      {
        out.append("-- WARNING: this event is not flat or is multi-valued\n");
      }
      out.append("INSERT INTO ").append(m_tableName).append(" SET ").append(m_eventId).append(" = ").append(i).append(", ");
      out.append(toSql(e, params));
      out.append(";\n");
      
    }
    return out.toString();
  }
  
  private StringBuffer toSql(Event e, Set<String> all_params)
  {
    StringBuffer out = new StringBuffer();
    Relation<String,String> domain = e.getParameterDomain();
    Set<String> params = domain.keySet();
    boolean first = true;
    for (String p : all_params)
    {
      if (!first)
        out.append(", ");
      first = false;
      String p_name = "`" + p + "`";
      if (!params.contains(p))
        out.append(p_name).append(" = NULL");
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
        out.append(p_name).append(" = \"").append(val).append("\"");
      }
    }
    return out;
  }
  
  public String translateFormula(Operator o)
  {
    return translateFormula(o, 0);
  }
  
  protected String translateFormula(Operator o, int level)
  {
    String out = "";
    if (o.getClass() == OperatorAnd.class)
      return translateFormula((OperatorAnd) o, level);
    if (o.getClass() == OperatorOr.class)
      return translateFormula((OperatorOr) o, level);
    if (o.getClass() == OperatorNot.class)
      return translateFormula((OperatorNot) o, level);
    if (o.getClass() == OperatorEquals.class)
      return translateFormula((OperatorEquals) o, level);
    if (o.getClass() == OperatorF.class)
      return translateFormula((OperatorF) o, level);
    if (o.getClass() == OperatorG.class)
      return translateFormula((OperatorG) o, level);
    if (o.getClass() == OperatorX.class)
      return translateFormula((OperatorX) o, level);
    if (o.getClass() == Atom.class)
      return translateFormula((Atom) o, level);
    return out;
  }

  protected String translateFormula(OperatorAnd o, int level)
  {
    StringBuffer out = new StringBuffer();
    out.append("(").append(translateFormula(o.getLeft(), level + 1)).append(")");
    out.append(" INTERSECT ");
    out.append("(").append(translateFormula(o.getRight(), level + 1)).append(")");
    return out.toString();
  }

  protected String translateFormula(OperatorOr o, int level)
  {
    StringBuffer out = new StringBuffer();
    out.append("(").append(translateFormula(o.getLeft(), level + 1)).append(")");
    out.append(" UNION ");
    out.append("(").append(translateFormula(o.getRight(), level + 1)).append(")");
    return out.toString();
  }

  protected String translateFormula(OperatorNot o, int level)
  {
    StringBuffer out = new StringBuffer();
    if (o.getOperand().getClass() == OperatorEquals.class)
    {
      // Optimization: if we negate an equality, we just write != instead
      return translateFormulaNegated((OperatorEquals) o.getOperand(), level + 1);
    }
    // Otherwise, the generic translation of the negation
    out.append("SELECT ").append(m_eventId).append(" FROM ").append(" AS ").append(m_tableName).append(level).append("MINUS (");
    out.append(translateFormula(o.getOperand(), level + 1));
    out.append(")");
    return out.toString();
  }

  protected String translateFormula(OperatorG o, int level)
  {
    // We handle G phi through the equivalence !F (!phi)
    OperatorNot n1 = new OperatorNot();
    n1.setOperand(o.getOperand());
    OperatorF f = new OperatorF();
    f.setOperand(n1);
    OperatorNot n2 = new OperatorNot();
    n2.setOperand(f);
    return translateFormula(n2);
  }

  protected String translateFormula(OperatorF o, int level)
  {
    StringBuffer out = new StringBuffer();
    String table_name_1 = m_tableName + "A" + level;
    String table_name_2 = m_tableName + "B" + level;
    String id_1 = table_name_1 + "." + m_eventId;
    String id_2 = table_name_2 + "." + m_eventId;
    out.append("SELECT DISTINCT ").append(id_1);
    out.append(" FROM ").append(m_tableName).append(" AS ").append(table_name_1).append(" JOIN ");
    out.append("(").append(translateFormula(o.getOperand(), level + 1)).append(") AS ").append(table_name_2);
    out.append(" WHERE ").append(id_1).append(" <= ").append(id_2);
    return out.toString();
  }

  protected String translateFormula(OperatorX o, int level)
  {
    StringBuffer out = new StringBuffer();
    out.append("SELECT ").append(m_eventId).append("-1 FROM ").append(" AS ").append(m_tableName).append(level);
    return out.toString();
  }
  
  protected String translateFormula(Atom o, int level)
  {
    return o.getSymbol();
  }
  
  protected String translateFormula(OperatorEquals o, int level)
  {
    StringBuffer out = new StringBuffer();
    out.append("SELECT ").append(m_eventId).append(" FROM ").append(m_tableName).append(" AS ").append(m_tableName).append(level).append(" WHERE ");
    out.append("`").append(translateFormula(o.getLeft(), level + 1)).append("` = '").append(translateFormula(o.getRight(), level + 1)).append("'");
    return out.toString();
  } 
  
  protected String translateFormulaNegated(OperatorEquals o, int level)
  {
    StringBuffer out = new StringBuffer();
    out.append("SELECT ").append(m_eventId).append(" FROM ").append(m_tableName).append(" AS ").append(m_tableName).append(level).append(" WHERE ");
    out.append("`").append(translateFormula(o.getLeft(), level + 1)).append("` != '").append(translateFormula(o.getRight(), level + 1)).append("'");
    return out.toString();
  }

@Override
public String getSignature(EventTrace t) {
	// TODO Auto-generated method stub
	return null;
}

@Override
public String translateFormula() {
	// TODO Auto-generated method stub
	return null;
}

@Override
public boolean requiresPropositional() {
	return true;
}

}