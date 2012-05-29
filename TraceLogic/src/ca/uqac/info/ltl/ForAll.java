package ca.uqac.info.ltl;

public class ForAll extends Quantifier
{
	@Override
	public void accept(OperatorVisitor v)
	{
    super.accept(v);
    v.visit(this);
	}
}
