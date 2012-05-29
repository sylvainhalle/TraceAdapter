package ca.uqac.info.ltl;

public abstract class Quantifier extends UnaryOperator
{
	/**
	 * The quantified variable in the expression 
	 */
	protected Atom m_variable;
	
	public static final String ELEMENT_SYMBOL = "\u2208";
	
	/**
	 * The path over which the variable takes its values
	 */
	protected XPathAtom m_path;
	
	public void setVariable(Atom v)
	{
		m_variable = v;
	}
	
	public Atom getVariable()
	{
		return m_variable;
	}
	
	public void setPath(XPathAtom p)
	{
		m_path = p;
	}
	
	public XPathAtom getPath()
	{
		return m_path;
	}
	
  public void accept(OperatorVisitor v)
  {
  	m_variable.accept(v);
  	m_path.accept(v);
    m_operand.accept(v);
  }
  
  @Override
  public String toString()
  {
  	StringBuilder out = new StringBuilder();
  	out.append(m_symbol).append(m_variable.toString()).append(" ").append(ELEMENT_SYMBOL).append(" ").append(m_path.toString()).append(" : (").append(m_operand.toString()).append(")");
  	return out.toString();
  }
}
