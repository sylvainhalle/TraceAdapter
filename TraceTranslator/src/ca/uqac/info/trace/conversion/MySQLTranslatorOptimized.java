package ca.uqac.info.trace.conversion;

import ca.uqac.info.ltl.*;

/**
 * Optimized version of the MySQL translator that uses fewer joins.
 * @author sylvain
 *
 */
public class MySQLTranslatorOptimized extends MySQLTranslator
{
  @Override
  protected StringBuilder translateFormula(OperatorAnd o, int level)
  {
    StringBuilder out = new StringBuilder();
    out.append("select ").append(m_eventId).append(" FROM ").append(m_tableName);
    out.append(" WHERE (").append(m_eventId).append(" IN (")
      .append(translateFormula(o.getLeft(), level + 1)).append(") AND ")
      .append(m_eventId).append(" IN ");
    out.append("(").append(translateFormula(o.getRight(), level + 1)).append("))");
    return out;
  }
  
  @Override
  protected StringBuilder translateFormula(OperatorOr o, int level)
  {
    StringBuilder out = new StringBuilder();
    out.append("select ").append(m_eventId).append(" FROM ").append(m_tableName);
    out.append(" WHERE (").append(m_eventId).append(" IN (")
      .append(translateFormula(o.getLeft(), level + 1)).append(") OR ")
      .append(m_eventId).append(" IN ");
    out.append("(").append(translateFormula(o.getRight(), level + 1)).append("))");
    return out;
  }
  
  @Override
  protected StringBuilder translateFormula(OperatorF o, int level)
  {
    StringBuilder out = new StringBuilder();
    out.append("select ").append(m_eventId);
    out.append(" FROM ").append(m_tableName).append(" WHERE ").append(m_eventId).append(" <= ");
    out.append("(SELECT MAX(").append(m_eventId).append(") FROM (").append(translateFormula(o.getOperand(), level + 1)).append(")");
    out.append(" AS ").append(m_tableName).append(level).append(")");
    return out;
  }
}
