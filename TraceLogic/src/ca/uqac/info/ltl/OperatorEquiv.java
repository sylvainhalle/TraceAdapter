package ca.uqac.info.ltl;

public class OperatorEquiv extends BinaryOperator
{
	public static final String SYMBOL = "\u2194"; //"<->";

	public OperatorEquiv()
	{
		super();
		m_symbol = SYMBOL;
		m_commutes = true;
	}

	public OperatorEquiv(Operator left, Operator right)
	{
		super(left, right);
		m_symbol = SYMBOL;
		m_commutes = true;
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
