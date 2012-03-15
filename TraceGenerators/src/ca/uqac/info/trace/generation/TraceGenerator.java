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


import ca.uqac.info.trace.EventTrace;
import ca.uqac.info.trace.conversion.Translator;
import ca.uqac.info.trace.conversion.XmlTranslator;
import org.apache.commons.cli.*;

/**
 * The TraceGenerator defines the basic methods one can use to produce
 * event traces of various kinds. It also provides functionality to
 * read command-line arguments and produce an output trace.
 * @author sylvain
 *
 */
public abstract class TraceGenerator
{  
    /**
     * Generate an event trace. There are no basic assumptions on that
     * trace: it may be the same on every call to generate, or different.
     * It is up to each trace generator to determine the appropriate
     * behaviour for this method.
     * @return The EventTrace produced by the generator
     */
    public abstract EventTrace generate();
        
    /**
     * Initializes the generator, taking its input parameters from the
     * parsed command line
     * @param c_line An instance of the parsed CommandLine, which can
     * be queried for values gathered from the user 
     */
    public abstract void initialize(CommandLine c_line);
    
    /**
     * Returns an identifying string used by the command-line application
     * to designate the trace generator
     * @return The name
     */
    public abstract String getAppName();
        
    /**
     * Creates the proper command line options to gather parameters
     * relevant to the configuration of the trace generator. This
     * method must be implemented by all trace generators, in order
     * for them to declare their command line switches.
     * @return The Options object containing the options required by
     * this generator
     */
    public abstract Options getCommandLineOptions();
    
    /**
     * Main processing loop for generating traces.
     * @param args
     * @param t_gen
     */
    public static final void process(String[] args, TraceGenerator t_gen)
    {    
      Options options = t_gen.getCommandLineOptions();
      options.addOption("h", "help", false, "Show help");
      CommandLineParser parser = new GnuParser();
      CommandLine c_line = null;
      try
      {
          // parse the command line arguments
          c_line = parser.parse(options, args);
      }
      catch (ParseException exp)
      {
          // oops, something went wrong
          System.err.println("ERROR: " + exp.getMessage() + "\n");
          HelpFormatter hf = new HelpFormatter();
          hf.printHelp(t_gen.getAppName() + " [options]", options);
          System.exit(1);
      }
      assert c_line != null;
      if (c_line.hasOption("h"))
      {
        HelpFormatter hf = new HelpFormatter();
        hf.printHelp(t_gen.getAppName() + " [options]", options);
        System.exit(1);
      }
      t_gen.initialize(c_line);
      EventTrace trace = t_gen.generate();
      Translator trans = new XmlTranslator();
      System.out.println(trans.translateTrace(trace));
    }
}
