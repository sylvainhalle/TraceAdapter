package ca.uqac.info.trace.conversion;

import java.io.File;
import java.util.HashSet;
import java.util.Set;
import java.util.Stack;
import java.util.Vector;

import ca.uqac.info.ltl.*;
import ca.uqac.info.ltl.Operator.ParseException;
/*import ca.uqac.info.ltl.Operator;

import ca.uqac.info.ltl.BinaryOperator;
import ca.uqac.info.ltl.Exists;
import ca.uqac.info.ltl.ForAll;
import ca.uqac.info.ltl.OperatorAnd;
import ca.uqac.info.ltl.OperatorEquals;
import ca.uqac.info.ltl.OperatorEquiv;
import ca.uqac.info.ltl.OperatorF;
import ca.uqac.info.ltl.OperatorG;
import ca.uqac.info.ltl.OperatorImplies;
import ca.uqac.info.ltl.OperatorNot;
import ca.uqac.info.ltl.OperatorOr;
import ca.uqac.info.ltl.OperatorU;
import ca.uqac.info.ltl.OperatorVisitor;
import ca.uqac.info.ltl.OperatorX;
import ca.uqac.info.ltl.UnaryOperator;
import ca.uqac.info.ltl.XPathAtom;*/
import ca.uqac.info.trace.EventTrace;
import ca.uqac.info.trace.XmlTraceReader;
import ca.uqac.info.util.Relation;


public class MaudeTranslator extends Translator {

	protected AtomicTranslator m_atomicTranslator;

	/**
	 * Constructor
	 */
	public MaudeTranslator() {
		super();
		m_atomicTranslator = new AtomicTranslator();
	}

	public MaudeTranslator(EventTrace t) {
		this();
		m_trace = t;
	}

	@Override
	public String translateTrace(EventTrace t)
	{
		setTrace(t);
		return translateTrace();
	}
	
	public String translateFormula(Operator o) {
		
		StringBuffer out = new StringBuffer();
		MaudeFormulaTranslator mft = new MaudeFormulaTranslator();
		o.accept(mft);
		out.append(mft.getFormula());
		
		return out.toString();
	}

	protected class MaudeFormulaTranslator implements OperatorVisitor {
		Stack<StringBuffer> m_pieces;

		public MaudeFormulaTranslator() {
			super();
			m_pieces = new Stack<StringBuffer>();
		}

		public String getFormula() {
			StringBuffer out = m_pieces.peek();
			return out.toString();
		}

		@Override
		public void visit(OperatorAnd o) {
			StringBuffer right = m_pieces.pop();
			StringBuffer left = m_pieces.pop();
			StringBuffer out = new StringBuffer("(").append(left).append(") ")
					.append("/\\").append("(").append(right).append(")");
			m_pieces.push(out);
		}

		@Override
		public void visit(OperatorOr o) {
			StringBuffer right = m_pieces.pop();
			StringBuffer left = m_pieces.pop();
			StringBuffer out = new StringBuffer("(").append(left).append(")")
					.append("\\/").append("(").append(right).append(")");
			m_pieces.push(out);
		}

		@Override
		public void visit(OperatorImplies o) {
			StringBuffer right = m_pieces.pop();
			StringBuffer left = m_pieces.pop();
			StringBuffer out = new StringBuffer("(").append(left)
					.append(") -> (").append(right).append(")");
			m_pieces.push(out);
		}

		@Override
		public void visit(OperatorNot o) {
			StringBuffer op = m_pieces.pop();
			StringBuffer out = new StringBuffer("~ (").append(op).append(")");
			m_pieces.push(out);
		}

		@Override
		public void visit(OperatorF o) {
			StringBuffer op = m_pieces.pop();

			StringBuffer out = new StringBuffer("<> ( ").append(op).append(" )");
			m_pieces.push(out);

		}

		@Override
		public void visit(OperatorX o) {
			StringBuffer op = m_pieces.pop();
			StringBuffer out = new StringBuffer("o (").append(op).append(")");
			m_pieces.push(out);
		}

		@Override
		public void visit(OperatorG o) {
			StringBuffer op = m_pieces.pop();
			StringBuffer out = new StringBuffer("[] (").append(op).append(")");
			m_pieces.push(out);
		}

		@Override
		public void visit(OperatorEquals o) {
			m_pieces.pop(); // Pop right-hand side
			m_pieces.pop(); // Pop left-hand side
			StringBuffer out = new StringBuffer(toMaudeIdentifier(o));
			m_pieces.push(out);
		}

		@Override
		public void visit(Atom o) {
			m_pieces.push(new StringBuffer(o.getSymbol()));
		}

		@Override
		public void visit(OperatorEquiv o) {
		}

		@Override
		public void visit(OperatorU o) {
		}

		@Override
    public void visit(Exists o)
    {
			// Not supposed to happen!
			System.err.println("Error: quantifier found in Maude translator");
	    assert false;
    }

		@Override
    public void visit(ForAll o)
    {
			// Not supposed to happen!
			System.err.println("Error: quantifier found in Maude translator");
	    assert false;
    }

		@Override
		public void visit(XPathAtom p)
    {
			// Not supposed to happen!
			System.err.println("Error: XML path found in Maude translator");
	    assert false;
    }

	}

	protected class MaudeEqualityGetter implements OperatorVisitor {

		Set<OperatorEquals> m_equalities;

		public MaudeEqualityGetter() {
			super();
			m_equalities = new HashSet<OperatorEquals>();
		}

		public Set<OperatorEquals> getEqualities() {
			return m_equalities;
		}

		@Override
		public void visit(OperatorEquals o) {
			m_equalities.add(o);
		}

		@Override
		public void visit(OperatorAnd o) {
		}

		@Override
		public void visit(OperatorOr o) {
		}

		@Override
		public void visit(OperatorImplies o) {
		}

		@Override
		public void visit(OperatorNot o) {
		}

		@Override
		public void visit(OperatorF o) {
		}

		@Override
		public void visit(OperatorX o) {
		}

		@Override
		public void visit(OperatorG o) {
		}

		@Override
		public void visit(Atom o) {
		}

		@Override
		public void visit(OperatorEquiv o) {
		}

		@Override
		public void visit(OperatorU o) {

		}

		@Override
    public void visit(Exists o)
    {
	    
    }

		@Override
    public void visit(ForAll o)
    {
	    
    }

		@Override
		public void visit(XPathAtom p)
    {
	    
    }

	}

	protected static String toMaudeIdentifier(OperatorEquals o) {
		String left = o.getLeft().toString();
		String right = o.getRight().toString();
		return new StringBuffer(left).append(" = ").append(right).toString();
	}

	@Override
	public String getSignature(EventTrace m_trace) {
		Relation<String, String> param_domains = m_trace.getParameterDomain();
		Set<String> params = param_domains.keySet();
		Vector<String> vectParams = new Vector<String>();
		vectParams.addAll(params);

		String strTemp = vectParams.toString().replaceAll(",", " ");
		strTemp = strTemp.replace("[", " ");
		strTemp = strTemp.replace("]", " ");
		return strTemp;
	}

	@Override
	public String translateFormula()
	{
		 
		//at.setParameters(o_params);
		m_atomicTranslator.translateTrace(m_trace);
		String prop = m_atomicTranslator.translateFormula(m_formula);
		return generateFormula(prop);
	}

	protected String generateFormula(String chaine)
	{
		String res = "";
		String [] tab = chaine.split(" ");
		for(String c : tab)
		{
			if(c.equalsIgnoreCase("G"))
			{
				res = res.concat("[] ");
			}else if (c.equalsIgnoreCase("F"))
			{
				res = res.concat("<> ");
			}else if (c.equalsIgnoreCase("X"))
			{
				res = res.concat("o ");
			}else if (c.equalsIgnoreCase("|"))
			{
				res = res.concat("\\/ ");
			}else if (c.equalsIgnoreCase("&"))
			{
				res = res.concat("/\\ ");
			}else if (c.equalsIgnoreCase("!"))
			{
				res = res.concat("~ ");
			}else{
				
				res = res.concat(c).concat(" ");
			}
			
		}
		res = res.concat(".");
		return res ;
	}
	
	@Override
	public String translateTrace()
	{
		String out = "", operandes = "", chaine = "",prop="";
		StringBuffer out_Trace = new StringBuffer();
		Vector<String> listParm = new Vector<String>();
		out = m_atomicTranslator.translateTrace(m_trace);
		prop = generateFormula(m_atomicTranslator.translateFormula(m_formula));

		String[] tab = out.split(" ");
		for (int j = 0; j < tab.length; j++) {
			String c = tab[j];
			if (j == 0) {
				chaine = chaine.concat(c);
			} else {
				chaine = chaine.concat(" , " + c);
			}
			if (!listParm.contains(c)) {
				listParm.add(c);
				operandes = operandes.concat(c + " ");
			}
		}

		chaine = chaine.concat("    |= ").concat(prop);
		// Start writing the Java program
		out_Trace.append("in ltl.maude");
		out_Trace.append("\n \n \n");
		out_Trace.append("fmod MY-TRACE is").append("\t");
		out_Trace.append("\n ");
		out_Trace.append("extending LTL .").append("\t");
		out_Trace.append("ops  ").append(operandes).append(" : -> Atom .")
		.append("\t");
		out_Trace.append("\n \n \n");
		out_Trace.append("endfm \n ");
		out_Trace.append("");
		out_Trace.append("reduce").append(" ");
		out_Trace.append(chaine);
		out_Trace.append("\n quit");
		return out_Trace.toString();
	}
	
	public static void main (String [] args)
	{
		
		MaudeTranslator md = new MaudeTranslator() ;
		File f = new File("traces/trace2.xml");
		XmlTraceReader reader = new XmlTraceReader();
		EventTrace trace = reader.parseEventTrace(f);
		String sop = "G (p0 = 4) ";
		Operator op;
		try {
			op = Operator.parseFromString(sop);
			System.out.println(md.translateFormula(op));
			md.setFormula(op);
			System.out.println(md.translateTrace(trace));
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

}
