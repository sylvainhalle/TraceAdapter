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

import ca.uqac.info.ltl.XPathAtom;

/**
 * The translation for BeepBeep's Saxon-based monitor if slightly different
 * from BeepBeep's syntax. This translator only codes the differences with
 * respect to the {@link BeepBeepTranslator}.
 * @author Sylvain Hallé
 *
 */
public class SaxonTranslator extends BeepBeepTranslator
{
  /**
   * Saxon overrides BeepBeep's translation by adding a root element
   * to the trace.
   */
  @Override
  public String translateTrace()
  {
    StringBuilder out = new StringBuilder();
    out.append("<Trace>\n");
    out.append(super.translateTrace());
    out.append("</Trace>\n");
    return out.toString();
  }
  
  protected class SaxonFormulaTranslator extends BeepBeepFormulaTranslator
  {
    /**
     * Saxon overrides BeepBeep's notation for paths: we remove the
     * prepending "/message/..." to the expressions
     */
    @Override
    public void visit(XPathAtom p)
    {
      String s = p.toString();
      String[] parts = s.split("/");
      StringBuffer s_out = new StringBuffer();
      for (int i = 2; i < parts.length; i++)
        // We start at i=2, since s has a leading slash
        s_out.append("/").append(parts[i]);
      m_pieces.push(s_out);
    }
  }
  
  @Override
  public String translateFormula()
  {
    SaxonFormulaTranslator sft = new SaxonFormulaTranslator();
    m_formula.accept(sft);
    return sft.getFormula();
  }
}
