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

import ca.uqac.info.ltl.OperatorAnd;
import ca.uqac.info.ltl.OperatorEquals;
import ca.uqac.info.ltl.OperatorNot;
import ca.uqac.info.ltl.OperatorOr;

/**
 * Translates traces and events to a set of SQL queries for processing
 * by a relational database engine. The MySQLTranslator is almost identical
 * to the {@link SQLTranslator}; the only differences are that
 * <ol>
 * <li>The <tt>MINUS</tt> and <tt>INTERSECTION</tt> operators, defined
 * in the SQL standard, are not supported by MySQL. Therefore they must
 * be simulated using other constructs (self-join in the case of
 * intersection, and selection using
 * <tt>NOT IN</tt> for set difference).</li>
 * <li>The <tt>UNION</tt> operator, although supported, creates a syntax
 * error if surrounded by extraneous sets of parentheses (as per this
 * <a href="http://stackoverflow.com/questions/5728947/mysql-query-with-union-is-failing">post</a>).
 * To avoid the problem (since the translation recursively surrounds nested
 * expressions by parentheses), an extra <tt>SELECT</tt> statement is
 * added around the union.</li>
 * </ol>
 * This class only overrides the translation
 * methods relevant to these operators.
 * @author Sylvain Hallé
 */
public class MySQLTranslator extends SqlTranslator
{

  @Override
  protected StringBuilder translateFormula(OperatorAnd o, int level)
  {
    StringBuilder out = new StringBuilder();
    StringBuilder table_left = new StringBuilder().append("trace").append(level).append("L");
    StringBuilder table_right = new StringBuilder().append("trace").append(level).append("R");
    out.append("SELECT ").append(table_left).append(".* FROM (");
    out.append("(").append(translateFormula(o.getLeft(), level + 1)).append(") AS ").append(table_left);
    out.append(" INNER JOIN ");
    out.append("(").append(translateFormula(o.getRight(), level + 1)).append(") AS ").append(table_right);
    out.append(" ON ").append(table_left).append(".msgno = ").append(table_right).append(".msgno");
    out.append(")");
    return out;
  }
  
  @Override
  protected StringBuilder translateFormula(OperatorOr o, int level)
  {
    StringBuilder out = new StringBuilder();
    out.append("SELECT ").append(m_eventId).append(" FROM (");
    out.append("(").append(translateFormula(o.getLeft(), level + 1)).append(")");
    out.append(" UNION ");
    out.append("(").append(translateFormula(o.getRight(), level + 1)).append(")");
    out.append(") AS ").append(m_tableName).append(level);
    return out;
  }
  
  @Override
  protected StringBuilder translateFormula(OperatorNot o, int level)
  {
    StringBuilder out = new StringBuilder();
    if (o.getOperand().getClass() == OperatorEquals.class)
    {
      // Optimization: if we negate an equality, we just write != instead
      return translateFormulaNegated((OperatorEquals) o.getOperand(), level + 1);
    }
    out.append("SELECT ").append(m_eventId).append(" FROM ").append(m_tableName).append(" AS ").append(m_tableName).append(level).append(" WHERE ").append(m_eventId).append(" NOT IN (");
    out.append(translateFormula(o.getOperand(), level + 1));
    out.append(")");
    return out;
  }
}
