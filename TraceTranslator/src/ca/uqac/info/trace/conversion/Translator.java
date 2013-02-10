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

/**
 * Translates input traces and properties into various languages
 * and notations.
 * @author Sylvain Hallé
 *
 */
public abstract class Translator
{  
	protected Operator m_formula = null;
	
	protected EventTrace m_trace = null;
	
	protected String m_outTrace = "";
	protected String m_outFormula = "";
	protected String m_outSignature = "";
	
  /**
   * Translate an input trace into the output format
   * @deprecated This method shall be phased out, as many
   * translators need to process the trace and property at the
   * same time; please use {@link setTrace} followed by
   * {@link translateTrace} to have a similar effect.
   * @param t The event trace to translate
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
   * @deprecated This method shall be phased out, as many
   * translators need to process the trace and property at the
   * same time; please use {@link setFormula} followed by
   * {@link translateFormula} to have a similar effect.
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
  
  public final void translateAll()
  {
    m_outTrace = translateTrace();
    m_outFormula = translateFormula();
    m_outSignature = getSignature();
  }
  
  public String getTraceFile()
  {
    return m_outTrace;
  }

  public String getFormulaFile()
  {
    return m_outFormula;
  }

  public String getSignatureFile()
  {
    return m_outSignature;
  }
  
  /**
   * Sets the trace to be processed by the translator.
   * @param t The event trace
   */
  public void setTrace(EventTrace t)
  {
	  m_trace = t;
  }
  
  /**
   * Sets the formula to be processed by the translator
   * @param o The LTL formula
   */
  public void setFormula(Operator o)
  {
	  m_formula = o;
  }
  
  /**
   * Determines if the translator requires a flat message structure
   * @return
   */
  public abstract boolean requiresFlat();
  
  /**
   * Determines if the translator requires a propositional input formula
   * @return
   */
  public abstract boolean requiresPropositional();
  
  /**
   * Determines if the translator requires an atomic input formula
   * @return
   */
  public abstract boolean requiresAtomic();
  
  
  public abstract String getSignature();
}