package ca.uqac.info.ltl;

public class OperatorU extends BinaryOperator
{
	private static String symbol = "U";
	public OperatorU()
	{
		super();
		m_symbol = OperatorU.symbol;
	}
	public OperatorU(Operator left, Operator right)
	{
		super(left, right);
		m_symbol = OperatorU.symbol;
	}
	@Override
	public boolean equals(Object o)
	{
		if (o == null)
			return false;
		if (o.getClass() != this.getClass())
			return false;
		return super.equals((BinaryOperator) o);
	}

}
