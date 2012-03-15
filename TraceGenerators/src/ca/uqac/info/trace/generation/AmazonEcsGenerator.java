package ca.uqac.info.trace.generation;

import java.util.Random;

import org.apache.commons.cli.*;

import ca.uqac.info.trace.EventTrace;

public class AmazonEcsGenerator extends TraceGenerator
{

  /**
   * The seed used to initialize the generator
   */
  protected long m_seed = 0;
  
  /**
   * The random number generator
   */
  protected Random m_random;
  
  /**
   * Creates a trace generator with default settings
   */
  public AmazonEcsGenerator()
  {
    m_random = new Random();
  }
  
  /**
   * Sets the seed used by the internal random number generator.
   * Starting from the same seed and the same input parameters, successive
   * calls to generate will always generate the same sequence of
   * output traces.
   * @param s
   */
  public void setSeed(long s)
  {
    m_seed = s;
    m_random.setSeed(s);
  }
  
  @Override
  public EventTrace generate()
  {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public String getAppName()
  {
    return "Amazon ECS Shopping Cart Generator";
  }

  @SuppressWarnings("static-access")
  @Override
  public Options getCommandLineOptions()
  {
    Options options = new Options();
    Option opt;
    opt = OptionBuilder.withArgName("x").hasArg().withDescription("Set random seed").create("s");
    options.addOption(opt);
    return options;
  }

  @Override
  public void initialize(CommandLine c_line)
  {
    if (c_line.hasOption("s"))
      setSeed(new Integer(c_line.getOptionValue("s")).intValue());
  }

  /**
   * @param args
   */
  public static void main(String[] args)
  {
    process(args, new AmazonEcsGenerator());
  }

}
