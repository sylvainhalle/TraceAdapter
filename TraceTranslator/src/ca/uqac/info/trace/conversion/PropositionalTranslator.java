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

import java.io.File;
import java.util.*;
import org.w3c.dom.Node;

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
			assert path instanceof XPathAtom;
			Atom variable = (Atom) var;
			XPathAtom p = (XPathAtom) path;
			Set<Atom> values = m_domains.get(path);
			Operator out = null;
			if (values == null) // No value for the given path
				out = new OperatorFalse();
			else for (Atom v : values)
			{
				ReplaceVisitor rv = new ReplaceVisitor(variable, v);
				operand.accept(rv);
				Operator c = rv.getFormula();
				OperatorAnd oa = new OperatorAnd();
				OperatorEquals oeq = new OperatorEquals();
				oeq.setLeft(p); oeq.setRight(v);
				oa.setLeft(oeq);
				oa.setRight(c);
				out = Operator.disjunctTo(out, oa);
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
			assert path instanceof XPathAtom;
			Atom variable = (Atom) var;
			XPathAtom p = (XPathAtom) path;
			Set<Atom> values = m_domains.get(path);
			Operator out = null;
			if (values == null) // No value for the given path
				out = new OperatorTrue();
			else for (Atom v : values)
			{
				ReplaceVisitor rv = new ReplaceVisitor(variable, v);
				operand.accept(rv);
				Operator c = rv.getFormula();
				OperatorImplies oa = new OperatorImplies();
				OperatorEquals oeq = new OperatorEquals();
				oeq.setLeft(p); oeq.setRight(v);
				oa.setLeft(oeq);
				oa.setRight(c);
				out = Operator.conjunctTo(out, oa);
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
				Node n = e.getDomNode();
				Set<String> vals = x.getValues(n);
				for (String s : vals)
				{
					Atom a = new Atom(s);
					domains.add(x, a);
				}
			}
		}
		return domains;
	}
	
	public static void main(String[] args)
	{
		Operator o = null;
		try
		{
			//o = Operator.parseFromString("F ([i /a] (F ([j /b] (i=j))))");
			o = Operator.parseFromString("G (∀i ∈ /p1 : (F (∃j ∈ /p2 : (i=j))))");
		}
		catch (Operator.ParseException e)
		{
			System.out.println("Parse exception");
		}
		EventTrace t = new XmlTraceReader().parseEventTrace(new File("traces/trace2.xml"));
		Translator bt = new PropositionalTranslator();
		String c = bt.translateTrace(t);
		String f = bt.translateFormula(o);
		System.out.println(f);
	}

	@Override
	public boolean requiresPropositional() {
		return false;
	}
}
