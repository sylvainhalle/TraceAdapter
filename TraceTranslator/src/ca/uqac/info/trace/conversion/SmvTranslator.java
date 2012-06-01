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
import ca.uqac.info.util.*;
import java.util.*;

/**
 * Translates an event trace into a NuSMV finite-state machine.
 * @author sylvain
 */
public class SmvTranslator extends Translator
{
	/**
	 * Symbol standing for undefined
	 */
	private final String m_undefinedSymbol = "UNDEF";

	public String translateTrace()
	{
		StringBuilder out = new StringBuilder();
		Relation<String,String> domains = m_trace.getParameterDomain();
		Set<String> params = domains.keySet();
		// State variable definitions
		out.append("-- Trace file automatically generated by\n-- Event Trace Converter\n\n");
		out.append("MODULE main\n\n");
		out.append("VAR\n");
		out.append("  m_num : 0..").append(m_trace.size()).append(";\n");
		for (String p : params)
		{
			Set<String> dom = domains.get(p);
			out.append("  ").append(toSmvToken(p)).append(" : ").append(formatSet(dom)).append(";\n");
		}
		// Initial state definition
		out.append("\nINIT\n");
		out.append("m_num = 0 & (\n  ");
		out.append(toSmv(m_trace.firstElement(), false, params));
		out.append(")\n");
		// Transition relation
		out.append("\nTRANS\n");
		out.append("(next(m_num) = m_num + 1 | (m_num = ").append(m_trace.size()).append(" & next(m_num) = m_num)) & (\n");
		for (int i = 0; i < m_trace.size(); i++)
		{
			if (i > 0)
				out.append("\n&\n");
			out.append("(next(m_num) = ").append(i).append(" ->\n  (");
			Event e = m_trace.elementAt(i);
			if (!e.isFlat() || e.isMultiValued())
			{
				out.append("-- WARNING: this event is not flat or is multi-valued\n");
			}
			out.append(toSmv(e, true, params));
			out.append(")");
			out.append(")");
		}

		out.append("& (next(m_num) = ").append(m_trace.size()).append(" ->\n  ("); ///ajouter &
		out.append(toSmv(true, params)).append("))");
		out.append(")");

		return out.toString();
	}

	private StringBuilder toSmv(Event e, boolean is_next, Set<String> all_params)
	{
		StringBuilder out = new StringBuilder();
		Relation<String,String> domain = e.getParameterDomain();
		Set<String> params = domain.keySet();
		boolean first = true;
		for (String p : all_params)
		{
			if (!first)
				out.append(" & ");
			first = false;
			String p_name = p;
			if (is_next)
				p_name = "next(" + p + ")";
			if (!params.contains(p))
				out.append(p_name).append(" = ").append(m_undefinedSymbol);
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
				out.append(toSmvToken(p_name)).append(" = ").append(toSmvToken(val));
			}
		}
		return out;
	}

	private StringBuilder toSmv(boolean is_next, Set<String> all_params)
	{
		StringBuilder out = new StringBuilder();
		boolean first = true;
		for (String p : all_params)
		{
			if (!first)
				out.append(" & ");
			first = false;
			String p_name = p;
			if (is_next)
			{
				p_name = "next(" + p + ")";
			}
			out.append(p_name).append(" = ").append(m_undefinedSymbol);
		}
		return out;
	}

	/**
	 * Formats a set as a set of NuSMV tokens
	 * @param s
	 * @return
	 */
	private StringBuffer formatSet(Set<String> s)
	{
		StringBuffer out = new StringBuffer();
		out.append("{");
		for (String v : s)
		{
			out.append(toSmvToken(v));
			out.append(",");
		}
		out.append(m_undefinedSymbol);
		out.append("}");
		return out;
	}

	/**
	 * Converts a value into a token acceptable by NuSMV
	 * @param s
	 */
	private static String toSmvToken(String s)
	{
		String out = new String(s);
		out.replace("_", "__");
		out.replace(".", "_d");
		out.replace(",", "_c");
		out.replace(";", "_l");
		out.replace(":", "_s");
		return out;
	}

	@Override
	public String translateFormula(Operator o)
	{
		setFormula(o);
		return translateFormula();
	}

	protected class SmvFormulaTranslator implements OperatorVisitor
	{
		Stack<StringBuffer> m_pieces;

		public SmvFormulaTranslator()
		{
			super();
			m_pieces = new Stack<StringBuffer>();
		}

		public String getFormula()
		{
			StringBuffer out = m_pieces.peek();
			return out.toString();
		}

		@Override
		public void visit(OperatorAnd o)
		{
			StringBuffer right = m_pieces.pop();
			StringBuffer left = m_pieces.pop();
			StringBuffer out = new StringBuffer("(").append(left).append(") & (").append(right).append(")");
			m_pieces.push(out);
		}

		@Override
		public void visit(OperatorOr o)
		{
			StringBuffer right = m_pieces.pop();
			StringBuffer left = m_pieces.pop();
			StringBuffer out = new StringBuffer("(").append(left).append(") | (").append(right).append(")");
			m_pieces.push(out);
		}

		@Override
		public void visit(OperatorImplies o)
		{
			StringBuffer right = m_pieces.pop();
			StringBuffer left = m_pieces.pop();
			StringBuffer out = new StringBuffer("(").append(left).append(") -> (").append(right).append(")");
			m_pieces.push(out);
		}

		@Override
		public void visit(OperatorNot o)
		{
			StringBuffer op = m_pieces.pop();
			StringBuffer out = new StringBuffer("! (").append(op).append(")");
			m_pieces.push(out);
		}

		@Override
		public void visit(OperatorF o)
		{
			StringBuffer op = m_pieces.pop();
			StringBuffer out = new StringBuffer("F (").append(op).append(")");
			m_pieces.push(out);
		}

		@Override
		public void visit(OperatorX o)
		{
			StringBuffer op = m_pieces.pop();
			StringBuffer out = new StringBuffer("X (").append(op).append(")");
			m_pieces.push(out);
		}

		@Override
		public void visit(OperatorG o)
		{
			StringBuffer op = m_pieces.pop();
			StringBuffer out = new StringBuffer("G (").append(op).append(")");
			m_pieces.push(out);
		}

		@Override
		public void visit(OperatorEquals o)
		{
			StringBuffer o_right = m_pieces.pop(); // Pop right-hand side
			StringBuffer o_left = m_pieces.pop(); // Pop left-hand side
			StringBuffer out = new StringBuffer().append(o_left).append(" = ").append(o_right);
			m_pieces.push(out);
		}

		@Override
		public void visit(Atom o)
		{
			m_pieces.push(new StringBuffer(toSmvToken(o.getSymbol())));
		}

		@Override
		public void visit(OperatorEquiv o) {
			// TODO Auto-generated method stub

		}

		@Override
		public void visit(OperatorU o) {
			// TODO Auto-generated method stub

		}

		@Override
		public void visit(Exists o)
		{
			// Not supposed to happen!
			System.err.println("Error: quantifier found in NuSMV translator");
			assert false;
		}

		@Override
		public void visit(ForAll o)
		{
			// Not supposed to happen!
			System.err.println("Error: quantifier found in NuSMV translator");
			assert false;
		}

		@Override
		public void visit(XPathAtom p)
		{
			// false because no leading slash
			m_pieces.push(new StringBuffer(toSmvToken(p.toString(false))));
		}

	}

	@Override
	public String getSignature(EventTrace t)
	{
		// No signature in NuSMV
		return "";
	}

	@Override
	public String translateFormula()
	{
		StringBuffer out = new StringBuffer("LTLSPEC\n");
		SmvFormulaTranslator sft = new SmvFormulaTranslator();
		m_formula.accept(sft);
		out.append(sft.getFormula());
		return out.toString();

	}

	@Override
	public String translateTrace(EventTrace m_trace) {
		setTrace(m_trace);
		return translateTrace();
	}
	
	@Override
	public boolean requiresPropositional() {
		return true;
	}
}