package ca.uqac.info.ltl;

public class OperatorU extends BinaryOperator
{
	public static String SYMBOL = "U";
	
	public OperatorU()
	{
		super();
		m_symbol = OperatorU.SYMBOL;
		m_commutes = false;
	}

	public OperatorU(Operator left, Operator right)
	{
		super(left, right);
		m_symbol = OperatorU.SYMBOL;
		m_commutes = false;
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

    @Override
	public void accept(OperatorVisitor v)
	{
	  super.accept(v);
	  v.visit(this);
	}
}
