package ca.uqac.info.ltl;

public class OperatorTest
{

	/**
	 * @param args
	 */
	public static void main(String[] args)
	{
		Operator o = null;
		try
		{
			o = Operator.parseFromString("<i /message/p1> (i=0)");
		}
		catch (Operator.ParseException e)
		{
			System.out.println("Parse exception");
		}
		System.out.println(o);
	}

}
