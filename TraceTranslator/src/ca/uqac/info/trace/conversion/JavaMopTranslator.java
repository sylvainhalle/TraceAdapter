package ca.uqac.info.trace.conversion;

import ca.uqac.info.ltl.*;
import ca.uqac.info.trace.*;
import ca.uqac.info.util.*;
import java.util.*;

/**
 * Translates an event trace into a JavaMOP file.
 * 
 * @author sylvain
 */
public class JavaMopTranslator implements Translator
{
  /**
   * The class name used by default in the output program
   */
  protected String m_className = "MOPProgram";
  
  /**
   * Since parameters have to be in the proper order, we freeze a parameter ordering
   */
  Vector<String> o_params;
  
  /**
   * Constructor
   */
  public JavaMopTranslator()
  {
    super();
    o_params = new Vector<String>();
  }

  @Override
  public String translateFormula(Operator o)
  {
    StringBuffer out = new StringBuffer();
    out.append("package mop;\n\n");
    out.append("MyProperty (").append(m_className).append(" my_").append(m_className).append(")\n");
    out.append("{\n");
    // Step 1: define the events as AspectJ pointcuts
    JavaMopEqualityGetter f_eq= new JavaMopEqualityGetter();
    o.accept(f_eq);
    Set<OperatorEquals> equalities = f_eq.getEqualities();
    for (OperatorEquals eq : equalities)
    {
      out.append("  event ").append(toJavaMopIdentifier(eq)); /*.append("(");
      for (int i = 0; i < o_params.size(); i++)
      {
        if (i > 0)
          out.append(",");
        String p_name = o_params.elementAt(i);
        out.append("int ").append(p_name);
      }
      out.append(")");*/
      out.append(" before :\n");
      out.append("    call(void my_event(");
      StringBuffer args_string = new StringBuffer();
      for (int i = 0; i < o_params.size(); i++)
      {
        String p_name = o_params.elementAt(i);
        if (i > 0)
        {
          out.append(",");
          args_string.append(",");
        }
        out.append("String ").append(p_name);
        args_string.append(p_name);
      }
      out.append(")) && args(").append(args_string).append(")");
      Operator left = eq.getLeft();
      Operator right = eq.getRight();
      // TODO: we assume left-hand is a parameter and right-hand is a value
      out.append(" && ").append(left.toString()).append(" == \"").append(right.toString()).append("\"");
      out.append(" { }\n");
    }
    out.append("\n");
    
    // Step 2: append translated formula on those events 
    JavaMopFormulaTranslator f_trans = new JavaMopFormulaTranslator();
    o.accept(f_trans);
    out.append("  ltl: ").append(f_trans.getFormula()).append("\n");
    out.append("\n  @violation : { System.out.println(\"Property false\"); }\n");
    out.append("}");
    return out.toString();
  }

  @Override
  public String translateTrace(EventTrace t)
  {
    StringBuffer out = new StringBuffer();
    Relation<String,String> param_domains = t.getParameterDomain();
    Set<String> params = param_domains.keySet();
    //Vector<String> o_params = new Vector<String>();
    // We dump params in a Vector to forced ordered iteration every time
    for (String p : params)
      o_params.add(p);
    // Start writing the Java program
    out.append("/**\n  Auto-generated program for trace analysis through Java\n */\n");
    out.append("public class ").append(m_className).append("\n");
    out.append("{\n");
    out.append("  public static void main(String[] args)\n");
    out.append("  {\n");
    for (Event e : t)
    {
      Relation<String,String> e_dom = e.getParameterDomain();
      out.append("    my_event(");
      for (int i = 0; i < o_params.size(); i++)
      {
        String p_name = o_params.elementAt(i);
        Set<String> p_values = e_dom.get(p_name);
        if (p_values == null || p_values.isEmpty())
          out.append("null");
        else
        {
          String p_val = "";
          for (String v : p_values)
          {
            p_val = v;
            break;
          }
          out.append(p_val);
          if (p_values.size() > 1)
          {
            // TODO: trop de valeurs
          }
        }
        if (i < o_params.size() - 1)
          out.append(",");
      }
      out.append(");\n");
    }
    out.append("  }\n");
    out.append("\n");
    out.append("  public static void my_event(");
    for (int i = 0; i < o_params.size(); i++)
    {
      String p = o_params.elementAt(i);
      out.append("String ").append(p);
      if (i < o_params.size() - 1)
        out.append(",");
    }
    out.append(")\n");
    out.append("  {\n");
    out.append("    // This function is a mere placeholder to be caught\n");
    out.append("  }\n");
    out.append("}\n");
    return out.toString();
  }
  
  protected class JavaMopFormulaTranslator implements OperatorVisitor
  {
    Stack<StringBuffer> m_pieces;
    
    public JavaMopFormulaTranslator()
    {
      super();
      m_pieces = new Stack<StringBuffer>();
    }
    
    public String getFormula()
    {
      StringBuffer out = m_pieces.peek();
      return out.toString();
    }

    @Override
    public void visit(OperatorAnd o)
    {
      StringBuffer right = m_pieces.pop();
      StringBuffer left = m_pieces.pop();
      StringBuffer out = new StringBuffer("(").append(left).append(") and (").append(right).append(")");
      m_pieces.push(out);
    }

    @Override
    public void visit(OperatorOr o)
    {
      StringBuffer right = m_pieces.pop();
      StringBuffer left = m_pieces.pop();
      StringBuffer out = new StringBuffer("(").append(left).append(") or (").append(right).append(")");
      m_pieces.push(out);
    }
    
    @Override
    public void visit(OperatorImplies o)
    {
      StringBuffer right = m_pieces.pop();
      StringBuffer left = m_pieces.pop();
      StringBuffer out = new StringBuffer("(").append(left).append(") implies (").append(right).append(")");
      m_pieces.push(out);
    }

    @Override
    public void visit(OperatorNot o)
    {
      StringBuffer op = m_pieces.pop();
      StringBuffer out = new StringBuffer("not (").append(op).append(")");
      m_pieces.push(out);
    }

    @Override
    public void visit(OperatorF o)
    {
      StringBuffer op = m_pieces.pop();
      StringBuffer out = new StringBuffer("<> (").append(op).append(")");
      m_pieces.push(out);
    }

    @Override
    public void visit(OperatorX o)
    {
      StringBuffer op = m_pieces.pop();
      StringBuffer out = new StringBuffer("o (").append(op).append(")");
      m_pieces.push(out);
    }

    @Override
    public void visit(OperatorG o)
    {
      StringBuffer op = m_pieces.pop();
      StringBuffer out = new StringBuffer("[] (").append(op).append(")");
      m_pieces.push(out);
    }

    @Override
    public void visit(OperatorEquals o)
    {
      m_pieces.pop(); // Pop right-hand side
      m_pieces.pop(); // Pop left-hand side
      StringBuffer out = new StringBuffer(toJavaMopIdentifier(o));
      //StringBuffer out = new StringBuffer("eq").append(left).append("_").append(right);
      m_pieces.push(out);
    }

    @Override
    public void visit(Atom o)
    {
      m_pieces.push(new StringBuffer(o.getSymbol()));
    }
  }
  
  protected class JavaMopEqualityGetter implements OperatorVisitor
  {

    Set<OperatorEquals> m_equalities; 
    
    public JavaMopEqualityGetter()
    {
      super();
      m_equalities = new HashSet<OperatorEquals>();
    }
    
    public Set<OperatorEquals> getEqualities()
    {
      return m_equalities;
    }
    
    @Override
    public void visit(OperatorEquals o)
    {
      m_equalities.add(o);
    }
    
    @Override
    public void visit(OperatorAnd o) {}

    @Override
    public void visit(OperatorOr o) {}
    
    @Override
    public void visit(OperatorImplies o) {}

    @Override
    public void visit(OperatorNot o) {}

    @Override
    public void visit(OperatorF o) {}

    @Override
    public void visit(OperatorX o) {}

    @Override
    public void visit(OperatorG o) {}

    @Override
    public void visit(Atom o) {}
  }
  
  protected static String toJavaMopIdentifier(OperatorEquals o)
  {
    String left = o.getLeft().toString();
    String right = o.getRight().toString();
    return new StringBuffer("eq").append(left).append("_").append(right).toString();
  }
}
