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
import ca.uqac.info.trace.EventTrace;

public abstract class Translator
{  
	protected Operator m_formula = null;
	
	protected EventTrace m_trace = null;
	
  /**
   * Translate an input trace into the output format
   * @return A String representing the trace, into the target
   * format 
   */
  public abstract String translateTrace(EventTrace t);
  
  /**
   * Translate an input trace into the output format
   * @return A String representing the trace, into the target
   * format 
   */
  public abstract String translateTrace();
  
  /**
   * Translates an LTL formula into an equivalent expression of the
   * target language.
   * @param o The LTL formula
   * @return A String rendition of the target formula
   */
  public abstract String translateFormula(Operator o);

  /**
   * Translates an LTL formula into an equivalent expression of the
   * target language.
   * @return A String rendition of the target formula
   */
  public abstract String translateFormula();
  
  
  
  public void setTrace(EventTrace t)
  {
	  m_trace = t;
  }
  
  public void setFormula(Operator o)
  {
	  m_formula = o;
  }
  
  /**
   * Determines if the translator requires a propositional input formula
   * @return
   */
  public abstract boolean requiresPropositional();
  
  /**
   * get an input signature for Monpoly
   * @param t
   * @return a String representing the signature for Monpoly tool
   */
  public abstract String getSignature(EventTrace t);
}