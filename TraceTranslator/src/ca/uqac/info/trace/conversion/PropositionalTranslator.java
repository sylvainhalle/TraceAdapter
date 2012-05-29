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
import ca.uqac.info.trace.*;

/**
 * Converts an arbitrary trace and formula to a propositional
 * problem. This will only change the formula, not the trace.
 * @author sylvain
 *
 */
public class PropositionalTranslator extends Translator
{

	@Override
	public String getSignature(EventTrace t)
	{
		// No signature for this translator
		return "";
	}

	@Override
	public String translateFormula(Operator o)
	{
		setFormula(o);
		return translateFormula();
	}

	@Override
	public String translateFormula()
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String translateTrace(EventTrace t)
	{
		setTrace(t);
		return translateTrace();
	}

	@Override
	public String translateTrace()
	{
		return m_trace.toString();
	}

}
