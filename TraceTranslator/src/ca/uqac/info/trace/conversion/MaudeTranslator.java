package ca.uqac.info.trace.conversion;

import java.util.Vector;

import ca.uqac.info.ltl.Operator;
import ca.uqac.info.trace.Event;
import ca.uqac.info.trace.EventTrace;
import org.w3c.dom.NodeList;

public class MaudeTranslator implements Translator
{

	Vector<String> o_params;
	  
	  /**
	   * Constructor
	   */
	  public MaudeTranslator()
	  {
	    super();
	    o_params = new Vector<String>();
	  }

	@Override
	public String translateTrace(EventTrace t) {
		StringBuffer out = new StringBuffer();
		int NbEvent = t.size();
		// Start writing the Java program

		for (Event e : t) {
			NodeList listnode = e.getDomNode().getChildNodes();

			if (listnode.getLength() > 0) {
				out.append(listnode.item(1).getNodeName()).append("_");

				NodeList list2 = listnode.item(1).getChildNodes();

				if (list2.getLength() > 1) {
					NodeList list3 = list2.item(1).getChildNodes();

					if (list3.getLength() > 1) {
						out.append(list3.item(1).getNodeName()).append("_")
								.append(list3.item(1).getTextContent());
					} else {
						out.append(list2.item(1).getNodeName()).append("_")
								.append(list2.item(1).getTextContent());
					}

				} else {

					out.append(listnode.item(1).getTextContent());
				}

			}
			NbEvent--;
			if (NbEvent != 0) {
				out.append(" ,\n ");
			}

		}

		return out.toString();

	}

	public String translateFormula(Operator o) {
		// TODO Auto-generated method stub
		return null;
	}

}
