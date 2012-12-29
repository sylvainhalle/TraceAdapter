/******************************************************************************
  Event trace generator
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
package ca.uqac.info.trace.generation;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Options;

/**
 * Generate a random trace of requests and responses for the Amazon Bookstore
 * example. The control flow followed by the sequence of messages is exactly
 * the Petri net in Figure 2.3 from:
 * <blockquote>
 * W.M.P. van der Aalst, M. Pesic. (2007). Specifying and Monitoring Service
 * Flows: Making Web Services Process-Aware. In
 * <cite>Test and Analysis of Web Services</cite>, Springer, 11-55. DOI:
 * 10.1007/978-3-540-72912-9_2.
 * </blockquote>
 * The Bookstore generator follows a fixed Petri net defined
 * internally. As such, it can be considered a specific preset of
 * {@link PetriNetGenerator}.
 * @author Sylvain Hallé
 *
 */
public class BookstoreGenerator extends PetriNetGenerator
{

	/**
	 * The predefined finite-state machine. I know it's horrible to put
	 * it as one long string, but this is it ;-)
	 * @author Sylvain Hallé
	 *
	 */
	protected static final String m_petriNetDefinition = "P start place_c_order T place_c_order c_order P c_order handle_c_order T place_c_order p0 P p0 rec_decl P p0 rec_acc T rec_acc p1 T rec_acc p2 T rec_decl end P c_decline rec_decl P c_confirm rec_acc P p1 rec_book P p2 rec_bill T rec_book p4 P book_to_c rec_book T rec_bill p5 P bill rec_bill P p4 pay P p5 pay T pay payment P payment handle_payment T handle_payment end T handle_c_order p10 T alt_publ p10 P p10 place_b_order T place_b_order b_order T place_b_order p11 P p11 c_accept P p11 decide T c_accept c_confirm T c_accept p18 P b_confirm c_accept P p12 alt_publ P p12 c_reject P b_order eval_b_order T c_reject c_decline P b_decline decide T decide p12 P p18 req_shipment T req_shipment p8 P p8 inform_publ P p8 alt_shipper T alt_shipper p18 T req_shipment s_request P s_decline alt_shipper P s_confirm inform_publ T inform_publ p3 T inform_publ ship_info P ship_info prepare_b P p3 send_bill T send_bill bill T send_bill p6 P notification send_bill P p6 handle_payment T eval_b_order p9 P p9 b_reject T b_reject b_decline P p9 b_accept T b_accept b_confirm T b_accept p13 P p13 prepare_b T prepare_b p7 P p7 send_book T send_book book_to_s P book_to_s prepare_s T ship book_to_c P s_request eval_s_req T eval_s_req p14 P p14 s_reject P p14 s_accept T s_reject s_decline T s_accept p15 T s_accept s_confirm P p15 prepare_s T prepare_s p16 P p16 ship T ship p17 P p17 notify T notify notification M start 1";
	
	
	/**
	 * Initializes the bookstore generator
	 */
	public BookstoreGenerator()
	{
		super();
	}
	
		
	@Override
	public String getAppName()
	{
		return "Bookstore Generator";
	}

	@Override
	public Options getCommandLineOptions()
	{
		Options options = super.getCommandLineOptions();
		return options;
	}

	@Override
	public void initialize(CommandLine c_line)
	{
		super.initialize(c_line);
		try
		{
			super.parseFromString(m_petriNetDefinition);
		}
		catch (java.text.ParseException e)
		{
			e.printStackTrace(System.err);
		}
	}

	/**
	 * @param args
	 */
	public static void main(String[] args)
	{
		process(args, new BookstoreGenerator());
	}

}
