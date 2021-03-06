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

import ca.uqac.info.ltl.*;

/**
 * Helper class that simply checks whether a given formula is first-order
 * or not. This is to be used to determine if an input formula needs to
 * be passed into the {@link PropositionalTranslator} before sending to a
 * given tool.
 * @author Sylvain Hallé
 *
 */
public class FirstOrderDetector
{
  /**
   * Checks if a formula is first-order. A formula is considered
   * first-order as soon as it contains a quantifier &forall; or
   * &exist;.
   * @param o The formula to check
   * @return True if the formula is first-order, false otherwise
   */
	public static boolean isFirstOrder(Operator o)
	{
		if (o == null)
			return false;
		FirstOrderVisitor fov = new FirstOrderVisitor();
		o.accept(fov);
		return fov.isQuantifierFound();
	}
	
	/**
	 * Visitor that simply updates a boolean flag when a
	 * first-order quantifier is encountered.
	 * @author Sylvain Hallé
	 *
	 */
	protected static class FirstOrderVisitor extends EmptyVisitor
	{
		protected boolean m_quantifierFound;
		
		public FirstOrderVisitor()
		{
			super();
			m_quantifierFound = false;
		}
		
		@Override
		public void visit(Exists o)
		{
			m_quantifierFound = true;
		}
		
		@Override
		public void visit(ForAll o)
		{
			m_quantifierFound = true;
		}
		
		public boolean isQuantifierFound()
		{
		  return m_quantifierFound;
		}
	}	

}
