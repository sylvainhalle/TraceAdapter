package ca.uqac.info.trace.generation;

import java.util.Random;
import java.util.Vector;

import org.jdom.output.DOMOutputter;
import org.llrp.ltk.generated.enumerations.*;
import org.llrp.ltk.generated.messages.*;
import org.llrp.ltk.generated.parameters.*;
import org.llrp.ltk.types.*;
import org.w3c.dom.Node;

import ca.uqac.info.trace.Event;
import ca.uqac.info.trace.EventTrace;
import ca.uqac.info.util.RandomPicker;

public class LlrpGenerator extends TraceGenerator {

	protected int m_minMessages = 3;
	protected int m_maxMessages = 5;
	/**
	 * The random number generator
	 */
	protected Random m_random;

	/**
	 * Creates a trace generator with default settings
	 */
	public LlrpGenerator() {
		m_random = new Random();
	}

	/**
	 * Actions one can take on the cart
	 * 
	 */
	protected enum Action {
		ROSPEC_DELETE, ROSPEC_START, ROSPEC_STOP, ROSPEC_ENABLE, ROSPEC_DISABLE
	};

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		process(args, new LlrpGenerator());

	}

	@Override
	public EventTrace generate() {

		EventTrace trace = new EventTrace();

		try {
			// We chose the number of message to produce
			int n_message = m_random.nextInt(m_maxMessages + 1 - m_minMessages)
					+ m_minMessages;
			Vector<ROSpec> listMessage = new Vector<ROSpec>();

			for (int i = 0; i < n_message; i++) {
				addROSpec(new UnsignedInteger(i), trace);
				listMessage.add(createROSpec(new UnsignedInteger(i)));
			}

			while (listMessage.size() > 0) {
				// Step 1: pick a message
				RandomPicker<ROSpec> message_picker = new RandomPicker<ROSpec>(
						m_random);
				ROSpec message = message_picker.pick(listMessage);

				// step 2 : pick an operation
				RandomPicker<Action> action_picker = new RandomPicker<Action>(
						m_random);
				Vector<Action> available_operations = this.getAction(message);
				Action operation = action_picker.pick(available_operations);
				ROSpec tempo = null;
				switch (operation) {
				case ROSPEC_ENABLE:
					enable_ROSpec(message.getROSpecID(), trace);
					message.setCurrentState(new ROSpecState(
							ROSpecState.Inactive));
					tempo = message;
					break;
				case ROSPEC_DELETE:
					delete_ROSpe(message.getROSpecID(), trace);
					listMessage.remove(message);
					break;
				case ROSPEC_START:
					start_ROSpec(message.getROSpecID(), trace);
					message.setCurrentState(new ROSpecState(ROSpecState.Active));
					tempo = message;
					break;
				case ROSPEC_STOP:
					stop_ROSpec(message.getROSpecID(), trace);
					message.setCurrentState(new ROSpecState(
							ROSpecState.Inactive));
					tempo = message;
					break;
				case ROSPEC_DISABLE:
					disable_ROSpec(message.getROSpecID(), trace);
					message.setCurrentState(new ROSpecState(
							ROSpecState.Disabled));
					tempo = message;
					break;
				}
				if (!operation.equals(Action.ROSPEC_DELETE)) {
					listMessage.add(tempo);
					listMessage.remove(message);

				}

			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return trace;
	}

	/**
	 * return the sets of operations available for the message status
	 * 
	 * @param message
	 * @return
	 */
	public Vector<Action> getAction(ROSpec message) {
		Vector<Action> available_operations = new Vector<Action>();
		ROSpecState etat = message.getCurrentState();
		String strEtat = etat.toString();

		int cpt = 9;
		if (strEtat.equalsIgnoreCase("Disabled")) {
			cpt = 0;
		} else if (strEtat.equalsIgnoreCase("Inactive")) {
			cpt = 1;
		} else if (strEtat.equalsIgnoreCase("Active")) {
			cpt = 2;
		}

		switch (cpt) {
		case 0:
			available_operations.add(Action.ROSPEC_ENABLE);
			available_operations.add(Action.ROSPEC_DELETE);
			break;
		case 1:
			available_operations.add(Action.ROSPEC_DISABLE);
			available_operations.add(Action.ROSPEC_START);
			available_operations.add(Action.ROSPEC_DELETE);
			break;
		case 2:
			available_operations.add(Action.ROSPEC_STOP);
			available_operations.add(Action.ROSPEC_DELETE);
			break;
		default:
			break;
		}

		return available_operations;

	}

	@Override
	public String getAppName() {
		// TODO Auto-generated method stub
		return "Generator LLRP ";
	}

	/**
	 * Allow to create a new specification with an ID
	 * 
	 * @param unsignedInteger
	 * @return
	 */
	public ROSpec createROSpec(UnsignedInteger unsignedInteger) {

		ROSpec roSpec = new ROSpec();
		roSpec.setPriority(new LLRPInteger(0));
		roSpec.setCurrentState(new ROSpecState(ROSpecState.Disabled));
		roSpec.setROSpecID(unsignedInteger);

		// set up ROBoundary (start and stop triggers)
		ROBoundarySpec roBoundarySpec = new ROBoundarySpec();
		ROSpecStartTrigger startTrig = new ROSpecStartTrigger();

		startTrig.setROSpecStartTriggerType(new ROSpecStartTriggerType(
				ROSpecStartTriggerType.Null));
		roBoundarySpec.setROSpecStartTrigger(startTrig);

		ROSpecStopTrigger stopTrig = new ROSpecStopTrigger();
		stopTrig.setDurationTriggerValue(new UnsignedInteger(0));
		stopTrig.setROSpecStopTriggerType(new ROSpecStopTriggerType(
				ROSpecStopTriggerType.Null));
		roBoundarySpec.setROSpecStopTrigger(stopTrig);

		roSpec.setROBoundarySpec(roBoundarySpec);

		// Add an AISpec
		AISpec aispec = new AISpec();

		// set AI Stop trigger to null
		// Operation 'll be triggered by the start_rospec
		AISpecStopTrigger aiStopTrigger = new AISpecStopTrigger();
		aiStopTrigger.setAISpecStopTriggerType(new AISpecStopTriggerType(
				AISpecStopTriggerType.Null));
		aiStopTrigger.setDurationTrigger(new UnsignedInteger(0));
		aispec.setAISpecStopTrigger(aiStopTrigger);

		// this AISpec will utilize all the antennas of the Reader.
		UnsignedShortArray antennaIDs = new UnsignedShortArray();
		antennaIDs.add(new UnsignedShort(0));
		aispec.setAntennaIDs(antennaIDs);

		InventoryParameterSpec inventoryParam = new InventoryParameterSpec();
		inventoryParam.setProtocolID(new AirProtocols(
				AirProtocols.EPCGlobalClass1Gen2));
		inventoryParam.setInventoryParameterSpecID(new UnsignedShort(1));
		aispec.addToInventoryParameterSpecList(inventoryParam);

		roSpec.addToSpecParameterList(aispec);

		return roSpec;
	}

	/**
	 * CREATE an ADD_ROSPEC Message and send it to the reader
	 * 
	 * @param unsignedInteger
	 */

	public void addROSpec(UnsignedInteger unsignedInteger, EventTrace trace) {
		try {
			{ // create addROSpec
				ADD_ROSPEC addROSpec = new ADD_ROSPEC();
				addROSpec.setROSpec(createROSpec(unsignedInteger));

				Node n = trace.getNode();
				n.appendChild(trace.importNode(ajouterElement(addROSpec), true));
				trace.add(new Event(n));
			}
			{// create ADD_ROSPEC_RESPONSE
				ADD_ROSPEC_RESPONSE response = new ADD_ROSPEC_RESPONSE();
				LLRPStatus status = new LLRPStatus();
				status.setStatusCode(new StatusCode(0));
				status.setErrorDescription(new UTF8String_UTF_8(
						"ROSPec successfully added"));
				response.setLLRPStatus(status);

				Node n2 = trace.getNode();
				n2.appendChild(trace.importNode(ajouterElement(response), true));
				trace.add(new Event(ajouterElement(response)));
			}

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * Create a DELTE_ROSPEC message and send it to the reader
	 * 
	 * @param id_ROSpec
	 */
	public void delete_ROSpe(UnsignedInteger id_ROSpec, EventTrace trace) {

		try {
			{ // create DELETE_ROSPE
				DELETE_ROSPEC deleteROSpec = new DELETE_ROSPEC();
				deleteROSpec.setROSpecID(id_ROSpec);

				Node n = trace.getNode();
				n.appendChild(trace.importNode(ajouterElement(deleteROSpec),
						true));
				trace.add(new Event(n));

			}

			{// create DELETE_ROSPEC_RESPONSE
				DELETE_ROSPEC_RESPONSE response = new DELETE_ROSPEC_RESPONSE();
				LLRPStatus status = new LLRPStatus();
				status.setStatusCode(new StatusCode(0));
				status.setErrorDescription(new UTF8String_UTF_8(
						"ROSPec successfully deleted"));
				response.setLLRPStatus(status);

				Node n2 = trace.getNode();
				n2.appendChild(trace.importNode(ajouterElement(response), true));
				trace.add(new Event(n2));
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
	}

	/**
	 * Create a DISABLE_ROSPEC message and send it to the reader
	 * 
	 * @param id_ROSpec
	 */
	public void disable_ROSpec(UnsignedInteger id_ROSpec, EventTrace trace) {

		try {
			{ // create DISABLE_ROSPEC
				DISABLE_ROSPEC disableROSpec = new DISABLE_ROSPEC();
				disableROSpec.setROSpecID(id_ROSpec);

				Node n = trace.getNode();
				n.appendChild(trace.importNode(ajouterElement(disableROSpec),
						true));
				trace.add(new Event(n));

			}

			{// create DISABLE_ROSPEC_RESPONSE
				DISABLE_ROSPEC_RESPONSE response = new DISABLE_ROSPEC_RESPONSE();
				LLRPStatus status = new LLRPStatus();
				status.setStatusCode(new StatusCode(0));
				status.setErrorDescription(new UTF8String_UTF_8(
						"ROSPec successfully disabled"));

				response.setLLRPStatus(status);
				Node n2 = trace.getNode();
				n2.appendChild(trace.importNode(ajouterElement(response), true));
				trace.add(new Event(n2));
			}

		} catch (Exception e) {
			// TODO: handle exception
		}

	}

	/**
	 * Create an ENABLE_ROSPEC message and send it to the reader
	 * 
	 * @param id_ROSpec
	 */
	public void enable_ROSpec(UnsignedInteger id_ROSpec, EventTrace trace) {

		try {

			{// create ENABLE_ROSPEC
				ENABLE_ROSPEC enableROSpec = new ENABLE_ROSPEC();
				enableROSpec.setROSpecID(id_ROSpec);

				Node n = trace.getNode();
				n.appendChild(trace.importNode(ajouterElement(enableROSpec),
						true));
				trace.add(new Event(n));
			}

			{// create ENABLE_ROSPEC_RESPONSE
				ENABLE_ROSPEC_RESPONSE response = new ENABLE_ROSPEC_RESPONSE();
				LLRPStatus status = new LLRPStatus();
				status.setStatusCode(new StatusCode(0));
				status.setErrorDescription(new UTF8String_UTF_8(
						"ROSPec successfully enabled"));
				response.setLLRPStatus(status);

				Node n2 = trace.getNode();
				n2.appendChild(trace.importNode(ajouterElement(response), true));
				trace.add(new Event(n2));

			}
		} catch (Exception e) {
			// TODO: handle exception
		}
	}

	/**
	 * Create a START_ROSPEC message and send it to the reader
	 * 
	 * @param id_ROSpec
	 */
	public void start_ROSpec(UnsignedInteger id_ROSpec, EventTrace trace) {

		try {
			{// Create START_ROSPEC
				START_ROSPEC startROSpec = new START_ROSPEC();
				startROSpec.setROSpecID(id_ROSpec);

				Node n = trace.getNode();
				n.appendChild(trace.importNode(ajouterElement(startROSpec),
						true));
				trace.add(new Event(n));
			}

			{// create START_ROSPEC_RESPONSE
				START_ROSPEC_RESPONSE response = new START_ROSPEC_RESPONSE();
				LLRPStatus status = new LLRPStatus();
				status.setStatusCode(new StatusCode(0));
				status.setErrorDescription(new UTF8String_UTF_8(
						"ROSPec successfully started"));
				response.setLLRPStatus(status);

				Node n2 = trace.getNode();
				n2.appendChild(trace.importNode(ajouterElement(response), true));
				trace.add(new Event(n2));
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
	}

	/**
	 * 
	 * @param id_ROSpec
	 * @param trace
	 */
	public void stop_ROSpec(UnsignedInteger id_ROSpec, EventTrace trace) {

		try {
			{ // create STOP_ROSPEC
				STOP_ROSPEC stopROSpec = new STOP_ROSPEC();
				stopROSpec.setROSpecID(id_ROSpec);

				Node n = trace.getNode();
				n.appendChild(trace
						.importNode(ajouterElement(stopROSpec), true));
				trace.add(new Event(n));
			}

			{// create STOP_ROSPEC_RESPONSE
				STOP_ROSPEC_RESPONSE response = new STOP_ROSPEC_RESPONSE();
				LLRPStatus status = new LLRPStatus();
				status.setStatusCode(new StatusCode(0));
				status.setErrorDescription(new UTF8String_UTF_8(
						"ROSPec successfully stopped"));
				response.setLLRPStatus(status);

				Node n2 = trace.getNode();
				n2.appendChild(trace.importNode(ajouterElement(response), true));
				trace.add(new Event(n2));
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
	}

	/**
	 * add new event in the trace
	 * 
	 * @param message
	 * @return
	 */
	public org.w3c.dom.Element ajouterElement(LLRPMessage message) {
		org.w3c.dom.Element root = null;
		try {
			DOMOutputter domOutputter = new DOMOutputter();
			org.w3c.dom.Document documentDOM = domOutputter.output(message
					.encodeXML());
			root = documentDOM.getDocumentElement();

		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		return root;
	}
}