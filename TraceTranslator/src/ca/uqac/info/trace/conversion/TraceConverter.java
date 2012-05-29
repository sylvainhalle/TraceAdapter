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
import java.io.*;
import org.apache.commons.cli.*;

import ca.uqac.info.ltl.Operator;
import ca.uqac.info.trace.*;

public class TraceConverter
{
  private static final String app_version = "1.1";
  private static final String app_string = "Event trace converter\n(C) 2012 Sylvain Hallé " + app_version + "\n";
  private static final String app_name = "myappname [options]";
  
  
  /**
   * Main program loop
   * @param args Command-line arguments
   */
  @SuppressWarnings("static-access")
  public static void main(String[] args)
  {
    // Default values
    String input_format = "xml", output_format = "smv";
    String input_filename = "trace.xml", output_filename = "";
    String event_tag_name = "Event";
    
    // Define and process command line arguments
    Options options = new Options();
    HelpFormatter help_formatter = new HelpFormatter();
    Option opt;
    options.addOption("h", "help", false, "Show help");
    opt = OptionBuilder.withArgName("format").hasArg().withDescription("Input format for trace. Accepted values are csv, xml.").create("i");
    opt.setRequired(false);
    options.addOption(opt);
    opt = OptionBuilder.withArgName("format").hasArg().withDescription("Output format for trace. Accepted values are javamop, json, monpoly, smv, sql, xml. Default: smv").create("t");
    opt.setRequired(false);
    options.addOption(opt);
    opt = OptionBuilder.withArgName("filename").hasArg().withDescription("Input filename. Default: trace.xml").create("f");
    opt.setRequired(false);
    options.addOption(opt);
    opt = OptionBuilder.withArgName("filename").hasArg().withDescription("Output filename").create("o");
    opt.setRequired(false);
    options.addOption(opt);
    opt = OptionBuilder.withArgName("name").hasArg().withDescription("Event tag name. Default: Event").create("e");
    opt.setRequired(false);
    options.addOption(opt);
    opt = OptionBuilder.withArgName("formula").hasArg().withDescription("Formula to translate").create("s");
    opt.setRequired(false);
    options.addOption(opt);
    CommandLine c_line = parseCommandLine(options, args);
    if (c_line.hasOption("h"))
    {
      help_formatter.printHelp(app_name, options);
      System.exit(0);
    }
    input_filename = c_line.getOptionValue("f");
    if (c_line.hasOption("o"))
      output_filename = c_line.getOptionValue("o");
    if (c_line.hasOption("e"))
      event_tag_name = c_line.getOptionValue("e");
    
    // Determine the input format
    if (!c_line.hasOption("i"))
    {
      // Guess output format by filename extension
      input_format = getExtension(input_filename);
    }
    if (c_line.hasOption("i"))
    {
      // The "t" parameter overrides the filename extension
      input_format = c_line.getOptionValue("i");
    }
    
    // Determine which trace reader to initialize
    TraceReader reader = initializeReader(input_format);
    if (reader== null)
    {
      System.err.println("ERROR: Unrecognized input format");
      System.exit(1);
    }
    
    // Instantiate the proper trace reader and checks that the trace exists
    //reader.setEventTagName(event_tag_name);
    File in_f = new File(input_filename);
    if (!in_f.exists())
    {
      System.err.println("ERROR: Input file not found");
      System.exit(1);
    }
    if (!in_f.canRead())
    {
      System.err.println("ERROR: Input file is not readable");
      System.exit(1);
    }
        
    // Determine the output format
    if (!c_line.hasOption("o") && !c_line.hasOption("t"))
    {
      System.err.println("ERROR: At least one of output filename and output format must be given");
      System.exit(1);
    }
    if (c_line.hasOption("o"))
    {
      // Guess output format by filename extension
      output_filename = c_line.getOptionValue("o");
      output_format = getExtension(output_filename);
    }
    if (c_line.hasOption("t"))
    {
      // The "t" parameter overrides the filename extension
      output_format = c_line.getOptionValue("t");
    }
    
    // Determine which translator to initialize
    Translator trans = initializeTranslator(output_format);
    if (trans == null)
    {
      System.err.println("ERROR: Unrecognized output format");
      System.exit(1);
    }

    // Translate the trace into the output format
    EventTrace trace = reader.parseEventTrace(in_f);
    String out_trace = trans.translateTrace(trace);
    if (output_filename.isEmpty())
      System.out.println(out_trace);
    else
      writeToFile(output_filename, out_trace);
    
    // Check if there is a formula to translate
    if (c_line.hasOption("s"))
    {
      String formula = c_line.getOptionValue("s");
      try
      {
        Operator o = Operator.parseFromString(formula);
        System.out.println(trans.translateFormula(o));
      }
      catch (Operator.ParseException e)
      {
        System.err.println("ERROR: parsing input formula");
        System.exit(1);
      }
    }
  }
  
  /**
   * Parses the command-line array
   * passed as argument
   * @param args
   * @return The parsed command line
   */
  private static CommandLine parseCommandLine(Options options, String[] args)
  { 
    // Parse arguments
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
        System.err.println(app_string);
        System.err.println("ERROR: " + exp.getMessage() + "\n");
        HelpFormatter hf = new HelpFormatter();
        hf.printHelp(app_name, options);
        System.exit(1);
    }
    return c_line;
  }
  
  /**
   * Writes some string to a file
   * @param filename The filename to write to
   * @param contents The file's contents
   */
  private static void writeToFile(String filename, String contents)
  {
    try
    {
      // Create file 
      FileWriter fstream = new FileWriter(filename);
      BufferedWriter out = new BufferedWriter(fstream);
      out.write(contents);
      //Close the output stream
      out.close();
    }
    catch (Exception e)
    {
      // Catch exception if any
      System.err.println("ERROR: " + e.getMessage());
    }
  }
  
  /**
   * Return the filename extension (rightmost substring after last
   * period)
   * @return The extension
   */
  private static String getExtension(String filename)
  {
    int p = filename.lastIndexOf(".");
    if (p == -1)
      return filename;
    return filename.substring(p + 1);
  }
  
  /**
   * Initialize the translator, based on the string passed from the
   * command-line
   * @param output_format The translator name
   * @return An instance of Translator
   */
  private static Translator initializeTranslator(String output_format)
  {
    Translator trans = null;
    if (output_format.compareToIgnoreCase("smv") == 0)
    {
        trans = new SmvTranslator();
    }
    else if (output_format.compareToIgnoreCase("sql") == 0)
    {
        trans = new SqlTranslator();
    }
    /*else if (output_format.compareToIgnoreCase("javamop") == 0)
    {
        trans = new JavaMopTranslator();
    }*/
    else if (output_format.compareToIgnoreCase("json") == 0)
    {
        trans = new JsonTranslator();
    }
    else if (output_format.compareToIgnoreCase("xml") == 0)
    {
        trans = new XmlTranslator();
    }
    else if (output_format.compareToIgnoreCase("promela") == 0)
    {
        trans = new PromelaTranslator();
    }
    else if (output_format.compareToIgnoreCase("maude") == 0)
    {
        trans = new MaudeTranslator();
    }
    return trans;
  }
  
  /**
   * Initialize the trace reader, based on the string passed from the
   * command-line
   * @param input_format The reader name
   * @return An instance of TraceFactory
   */
  private static TraceReader initializeReader(String input_format)
  {
    TraceReader tf = null;
    if (input_format.compareToIgnoreCase("xml") == 0)
    {
        tf = new XmlTraceReader();
    }
    else if (input_format.compareToIgnoreCase("sql") == 0)
    {
        tf = new SqlTraceReader();
    }
    else if (input_format.compareToIgnoreCase("csv") == 0)
    {
        tf = new CsvTraceReader();
    }
    return tf;
  }

}
