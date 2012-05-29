/*
    LTL trace validation using MapReduce
    Copyright (C) 2012 Sylvain Hallé

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package ca.uqac.info.ltl;

public class OperatorOr extends BinaryOperator
{
	public static final String SYMBOL = "\u2228"; //"|";

	public OperatorOr()
	{
		super();
		m_symbol = SYMBOL;
		m_commutes = true;
	}

	public OperatorOr(Operator left, Operator right)
	{
		super(left, right);
		m_symbol = SYMBOL;
		m_commutes = true;
	}
	
	@Override
	public boolean equals(Object o)
	{
		if (o == null)
			return false;
		if (o.getClass() != this.getClass())
			return false;
		return super.equals((BinaryOperator) o);
	}
	
  @Override
  public void accept(OperatorVisitor v)
  {
    super.accept(v);
    v.visit(this);
  }
}
