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

import ca.uqac.info.ltl.Operator;
import ca.uqac.info.trace.EventTrace;

public class NewBeepBeepTranslator extends Translator
{
  @Override
  public String translateTrace()
  {
    return m_trace.toString();
  }

  @Override
  public String translateFormula()
  {
    return m_formula.toString();
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
    return null;
  }

  @Override
  public String translateTrace(EventTrace t)
  {
    return t.toString();
  }

  @Override
  public String translateFormula(Operator o)
  {
    return o.toString();
  }

}
