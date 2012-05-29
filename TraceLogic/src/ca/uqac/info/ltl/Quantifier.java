package ca.uqac.info.ltl;

public abstract class Quantifier extends UnaryOperator
{
	/**
	 * The quantified variable in the expression 
	 */
	protected Atom m_variable;
	
	/**
	 * The path over which the variable takes its values
	 */
	protected XmlPath m_path;
	
	public void setVariable(Atom v)
	{
		m_variable = v;
	}
	
	public Atom getVariable()
	{
		return m_variable;
	}
	
	public void setPath(XmlPath p)
	{
		m_path = p;
	}
	
	public XmlPath getPath()
	{
		return m_path;
	}
	
  public void accept(OperatorVisitor v)
  {
  	m_variable.accept(v);
  	m_path.accept(v);
    m_operand.accept(v);
  }
}
