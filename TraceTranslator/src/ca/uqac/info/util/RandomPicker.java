package ca.uqac.info.util;

import java.util.*;

public class RandomPicker<T>
{
	private Random m_random;
	
	public RandomPicker(Random r)
	{
		super();
		assert r != null;
		m_random = r;
	}
	
	/**
	 * Randomly picks an element from a vector
	 * @param v Input vector
	 * @return Chosen element
	 */
	public T pick(Vector<T> v)
	{
		assert v.size() > 0;
		int index = m_random.nextInt(v.size());
		assert index >= 0 && index < v.size();
		return v.elementAt(index);
	}
	
	/**
	 * Randomly picks an element from a set
	 * @param v Input set
	 * @return Chosen element
	 */
	public T pick(Set<T> s)
	{
		Vector<T> out = new Vector<T>(s);
		return pick(out);
	}
}
