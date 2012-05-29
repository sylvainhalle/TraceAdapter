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

import java.util.Set;

/**
 * (Very) basic re-implementation of Boolean and LTL connectives.
 * The class Operator and its descendants implement only the bare
 * minimum to <em>represent</em> temporal logic formul&aelig;, but
 * do not perform any kind of computation on them. 
 * @author sylvain
 *
 */
public abstract class Operator
{
	public abstract boolean hasOperand(Operator o);
	
	public abstract Set<Operator> getSubformulas();
	
	public abstract boolean isAtom();
	
	public abstract int getDepth();
	
	public abstract void accept(OperatorVisitor v);
	
	public static Operator parseFromString(String s) throws ParseException
	{
		s = s.trim();
		String c = s.substring(0, 1);
		Operator out = null;
		if (isQuantifierStart(c))
		{
			Quantifier oq = null;
			if (c.compareTo(Exists.SYMBOL) == 0)
			{
				oq = new Exists();
			}
			if (c.compareTo(ForAll.SYMBOL) == 0)
			{
				oq = new ForAll();
			}
			// Quantifier: get end
			int end_quantif = s.indexOf(":");
			if (end_quantif == -1)
				throw new ParseException();
			// Process quantifier
			String inside_quantif = s.substring(1, end_quantif).trim();
			String[] parts = inside_quantif.split(Quantifier.ELEMENT_SYMBOL);
			if (parts.length != 2)
				throw new ParseException();
			Atom a = new Atom(parts[0].trim());
			oq.setVariable(a);
			XPathAtom p = new XPathAtom(parts[1].trim());
			oq.setPath(p);
			// Process operand
			s = s.substring(end_quantif + 1).trim();
			s = trimSurroundingPars(s);
			Operator in = parseFromString(s);
			oq.setOperand(in);
			out = oq;
		}
		else if (isUnaryOperator(c))
		{
			// Unary operator
			s = s.substring(1).trim();
			s = trimSurroundingPars(s);
			Operator in = parseFromString(s);
			UnaryOperator uo = null;
			if (c.compareTo("F") == 0)
				uo = new OperatorF();
			else if (c.compareTo("X") == 0)
				uo = new OperatorX();
			else if (c.compareTo("G") == 0)
				uo = new OperatorG();
			else if (c.compareTo(OperatorNot.SYMBOL) == 0)
				uo = new OperatorNot();
			if (uo == null)
				throw new ParseException();
			uo.setOperand(in);
			out = uo;
		}
		else if (containsBinaryOperator(s))
		{
			// Here, we know s contains either a binary operator or is
			// an atom. We discriminate by checking for the presence of
			// a binary operator
			String left = getLeft(s);
			String right = getRight(s);
			assert !left.isEmpty() && !right.isEmpty();
			int pars_left = s.indexOf(left);
			int pars_right = s.length() - s.lastIndexOf(right) - right.length();
			assert pars_left >= 0 && pars_right >= 0;
			String op = getOperator(s, left.length() + pars_left * 2, right.length() + pars_right * 2);
			Operator o_left = parseFromString(left);
			Operator o_right = parseFromString(right);
			if (o_left == null || o_right == null)
				throw new ParseException();
			BinaryOperator bo = null;
			if (op.compareTo(OperatorAnd.SYMBOL) == 0)
				bo = new OperatorAnd();
			else if (op.compareTo(OperatorOr.SYMBOL) == 0)
				bo = new OperatorOr();
			else if (op.compareTo(OperatorImplies.SYMBOL) == 0)
				bo = new OperatorImplies();
			else if (op.compareTo(OperatorEquals.SYMBOL) == 0)
				bo = new OperatorEquals();
			else if (op.compareTo(OperatorEquiv.SYMBOL) == 0)
				bo = new OperatorEquiv();
			else if (op.compareTo("U") == 0)
				bo  = new OperatorU();
			
			if (bo == null)
				throw new ParseException();
			bo.setLeft(o_left);
			bo.setRight(o_right);
			out = bo;
		}
		else
		{
			// Atom or XPathAtom, last remaining case
			if (s.startsWith("{"))
				out = new XPathAtom(s);
			else
				out = new Atom(s);
		}
		if (out == null)
			throw new ParseException();
		return out;
	}
	
	private static String trimSurroundingPars(String s)
	{
		if (s.startsWith(("(")))
		{
			// We trim surrounding parentheses, if any
			s = s.substring(1, s.length() - 1).trim();
		}
		return s;
	}
	
	private static boolean isQuantifierStart(String c)
	{
		return c.compareTo(Exists.SYMBOL) == 0 || c.compareTo(ForAll.SYMBOL) == 0;
	}
	
	private static boolean isUnaryOperator(String c)
	{
		return c.compareTo("F") == 0 || c.compareTo("G") == 0 || c.compareTo("X") == 0 || c.compareTo(OperatorNot.SYMBOL) == 0;
	}
	
	private static boolean containsBinaryOperator(String s)
	{
		return s.indexOf(OperatorAnd.SYMBOL) != -1 || s.indexOf(OperatorOr.SYMBOL) != -1 || s.indexOf(OperatorImplies.SYMBOL) != -1 || s.indexOf(OperatorEquals.SYMBOL) != -1;
	}
	
	private static String getLeft(String s)
	{
		if (s.startsWith("("))
		{
			// Find matching right parenthesis
			int paren_level = 1; 
			for (int i = 1; i < s.length(); i++)
			{
				String c = s.substring(i, i+1);
				if (c.compareTo("(") == 0)
					paren_level++;
				if (c.compareTo(")") == 0)
					paren_level--;
				if (paren_level == 0)
					return s.substring(1, i);
			}
		}
		else
		{
			// Loop until operator or space found
			for (int i = 1; i < s.length(); i++)
			{
				String c = s.substring(i, i+1);
				if (c.compareTo("(") == 0 || c.compareTo(")") == 0 || 
						c.compareTo("&") == 0  || c.compareTo("|") == 0 || 
						  c.compareTo("=") == 0)
					return s.substring(0, i);
				if (i < s.length() - 1 && s.substring(i, i+2).compareTo("->") == 0)
					return s.substring(0, i);
			}
		}
		return "";
	}
	
	private static String getRight(String s)
	{
		if (s.endsWith(")"))
		{
			// Find matching left parenthesis
			int paren_level = 1; 
			for (int i = s.length() - 1; i >= 0; i--)
			{
				String c = s.substring(i, i+1);
				if (c.compareTo(")") == 0)
					paren_level++;
				if (c.compareTo("(") == 0)
					paren_level--;
				if (paren_level == 1)
					return s.substring(i + 1, s.length() - 1);
			}
		}
		else
		{
			// Loop until operator or space found
			for (int i = s.length() - 1; i >= 0; i--)
			{
				String c = s.substring(i, i+1);
				if (c.compareTo("(") == 0 || c.compareTo(")") == 0 || 
						c.compareTo("&") == 0  || c.compareTo("|") == 0 || 
					  c.compareTo("=") == 0)
					return s.substring(i + 1);
				if (i < s.length() - 1 && s.substring(i, i+2).compareTo("->") == 0)
					return s.substring(i + 2);
			}
		}
		return "";
	}
	
	private static String getOperator(String s, int size_left, int size_right)
	{
		assert size_left + size_right < s.length();
		return s.substring(size_left, s.length() - size_right).trim();
	}
	
	public static class ParseException extends Exception
	{

		/**
		 * Default serial ID
		 */
		private static final long serialVersionUID = 1L;
		
	}
	
	public Operator getNegated(){
		return this; // QSH: un opérateur ne peut pas être sa propre négation
	}
	
	/**
	 * Helper method to build large conjunctions of operators
	 * incrementally
	 * @param existing Existing formula
	 * @param to_add New operator to add via a conjunction
	 * @return
	 */
	public static Operator conjunctTo(Operator existing, Operator to_add)
	{
		if (existing == null)
			return to_add;
		OperatorAnd o = new OperatorAnd();
		o.setLeft(existing);
		o.setRight(to_add);
		return o;
	}
	
	/**
	 * Helper method to build large conjunctions of operators
	 * incrementally
	 * @param existing Existing formula
	 * @param to_add New operator to add via a conjunction
	 * @return
	 */
	public static Operator disjunctTo(Operator existing, Operator to_add)
	{
		if (existing == null)
			return to_add;
		OperatorOr o = new OperatorOr();
		o.setLeft(existing);
		o.setRight(to_add);
		return o;
	}
}
