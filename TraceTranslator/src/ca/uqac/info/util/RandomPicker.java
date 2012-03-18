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
	 * Randomly picks an element from the vector
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
}
