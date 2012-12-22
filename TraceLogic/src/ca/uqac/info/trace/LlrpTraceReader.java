package ca.uqac.info.trace;

import java.io.InputStream;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class LlrpTraceReader extends XmlTraceReader {
	
	
	public EventTrace parseEventTrace(InputStream f) {
		
		// new trace
		EventTrace traceLlrp = new EventTrace();
		// old trace
		EventTrace trace;
		trace = super.parseEventTrace(f);
		
		for (Event e : trace) {
			// recover list of child
			NodeList listnode = e.getDomNode().getChildNodes();
			if (listnode.getLength() > 0) {
				
				Node n = listnode.item(1);
				// create new element Action
				Node nm = traceLlrp.createElement("Action");
				nm.setTextContent(n.getNodeName());
				
				//Add new element action  in the LLrp trace
				Node newNode = traceLlrp.getNode();
				newNode.appendChild(nm);

				NodeList listChild = listnode.item(1).getChildNodes();
				if (listChild.getLength() > 0) {
					
					// add set of child in the LLRP trace
					for (int i = 0; i < listChild.getLength(); i++) {
						Node xy = listChild.item(i);
						newNode.appendChild(traceLlrp.importNode(xy, true));
					}
					traceLlrp.add(new Event(newNode));

				}

			}
		}

		return traceLlrp;
	}

}
