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

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

import ca.uqac.info.ltl.*;
import ca.uqac.info.trace.*;
import ca.uqac.info.util.Relation;

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
		PathVisitor pav = new PathVisitor();
		m_formula.accept(pav);
		Set<XPathAtom> paths = pav.getPaths();
		Relation<XPathAtom,Atom> domains = getDomains(paths);
		PropositionalVisitor pv = new PropositionalVisitor();
		pv.setDomains(domains);
		m_formula.accept(pv);
		Operator out = pv.getFormula(); 
		return out.toString();
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
	
	protected class PropositionalVisitor extends GenericOperatorVisitor
	{
		Operator m_formula;
		Relation<XPathAtom,Atom> m_domains;
		
		public PropositionalVisitor()
		{
			m_pieces = new Stack<Operator>();
		}
		
		public void setDomains(Relation<XPathAtom,Atom> domains)
		{
			m_domains = domains;
		}
		
		public Operator getFormula()
		{
			return m_pieces.pop();
		}

		@Override
    public void visit(Exists o)
    {
			Operator operand = m_pieces.pop();
			Operator path = m_pieces.pop();
			Operator var = m_pieces.pop();
			assert var instanceof Atom;
			Atom variable = (Atom) var;
			Set<Atom> values = m_domains.get(path);
			Operator out = null;
			for (Atom v : values)
			{
				ReplaceVisitor rv = new ReplaceVisitor(variable, v);
				operand.accept(rv);
				Operator c = rv.getFormula();
				Operator.disjunctTo(out, c);
			}
	    m_pieces.push(out);
    }

		@Override
    public void visit(ForAll o)
    {
			Operator operand = m_pieces.pop();
			Operator path = m_pieces.pop();
			Operator var = m_pieces.pop();
			assert var instanceof Atom;
			Atom variable = (Atom) var;
			Set<Atom> values = m_domains.get(path);
			Operator out = null;
			for (Atom v : values)
			{
				ReplaceVisitor rv = new ReplaceVisitor(variable, v);
				operand.accept(rv);
				Operator c = rv.getFormula();
				Operator.conjunctTo(out, c);
			}
	    m_pieces.push(out);
    }
		
	}
	
	protected class ReplaceVisitor extends GenericOperatorVisitor
	{
		protected final Atom m_search;
		protected final Atom m_replace;
		
		public ReplaceVisitor(Atom search, Atom replace)
		{
			super();
			m_search = search;
			m_replace = replace;
		}
		
		public Operator getFormula()
		{
			return m_pieces.pop();
		}
		
		public void visit(Atom a)
		{
			if (a.equals(m_search))
				m_pieces.push(m_replace);
			else
				m_pieces.push(a);
		}
	}
	
	/**
	 * Visitor that fetches the set of all paths appearing in
	 * the quantifier of the visited formula.
	 * @author sylvain
	 *
	 */
	protected class PathVisitor extends EmptyVisitor
	{
		
		protected Set<XPathAtom> m_paths;
		
		public PathVisitor()
		{
			super();
			m_paths = new HashSet<XPathAtom>();
		}
		
		public Set<XPathAtom> getPaths()
		{
			return m_paths;
		}
		
		@Override
    public void visit(Exists o)
    {
			m_paths.add(o.getPath());
    }

		@Override
    public void visit(ForAll o)
    {
			m_paths.add(o.getPath());
    }
	}
	
	/**
	 * Computes the domain for each path expression in the current
	 * trace
	 * @param paths The path expressions occurring in the current formula
	 * @return
	 */
	protected Relation<XPathAtom,Atom> getDomains(Set<XPathAtom> paths)
	{
		Relation<XPathAtom,Atom> domains = new Relation<XPathAtom,Atom>();
		for (Event e : m_trace)
		{
			for (XPathAtom x : paths)
			{
				Set<String> vals = x.getValues(e.getDomNode());
				for (String s : vals)
				{
					Atom a = new Atom(s);
					domains.add(x, a);
				}
			}
		}
		return domains;
	}

}
