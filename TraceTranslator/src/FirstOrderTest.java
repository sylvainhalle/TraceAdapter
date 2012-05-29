import java.io.File;

import ca.uqac.info.ltl.*;
import ca.uqac.info.trace.*;
import ca.uqac.info.trace.conversion.*;


public class FirstOrderTest
{

	/**
	 * @param args
	 */
	public static void main(String[] args)
	{
		// Formule originale, telle que saisie par l'utilisateur
		String formula = "∀i ∈ /a : (F (∃j ∈ /b : (i=j)))";
		Operator o = null;
		try
		{
			// On en fait le parsing
			o = Operator.parseFromString(formula);
		}
		catch (Operator.ParseException e)
		{
			e.printStackTrace();
		}
		// On lit une trace
		EventTrace t = new XmlTraceReader().parseEventTrace(new File("traces/trace1.xml"));
		
		// On vérifie si cette formule est du premier ordre (i.e. contient quantificateurs)
		if (FirstOrderDetector.isFirstOrder(o))
		{
			// Si oui et qu'on veut l'envoyer dans un outil qui ne supporte
			// pas les quantificateurs, on doit d'abord la réduire au cas
			// propositionnel...
			Translator bt = new PropositionalTranslator();
			bt.setTrace(t);
			formula = bt.translateFormula(o);
		}
		// Ensuite on fait le traitement normal
		Operator prop_formula = null;
		Translator smvt = new SmvTranslator();
		try
		{
			prop_formula = Operator.parseFromString(formula);
			
		}
		catch (Operator.ParseException e)
		{
			e.printStackTrace();
		}
		
		// On simplifie la formule propositionnelle résultante qui contient
		// beaucoup de constantes ou de contradictions du type "1=2"
		UnitPropagator up = new UnitPropagator();
		prop_formula.accept(up);
		prop_formula = up.getFormula();
		smvt.setFormula(prop_formula);
		// Résultat final: pas de quantificateurs dans la formule NuSMV
		String out = smvt.translateFormula();
		System.out.println(out);
		
		// Si on veut la traduire dans Maude, il faut en plus réduire
		// les égalités en symboles atomiques
		MaudeTranslator mt = new MaudeTranslator();
		mt.setFormula(prop_formula);
		mt.setTrace(t);
		out = mt.translateTrace();
		// Résultat final: on est passé d'une formule du premier ordre
		// à une expression que Maude peut traiter
		System.out.println(out);
	}

}
