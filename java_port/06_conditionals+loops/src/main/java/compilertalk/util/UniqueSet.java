package compilertalk.util;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 * Unique set.
 * <ul>
 * <li>You can ask this class for the id of any element. Elements will be
 * numbered from 1..n, the number of distinct elements that has been seens by
 * this class.</li>
 * <li>This can be used to make sure that only unique references to constants or
 * strings or tables are stored.</li>
 * </ul>
 */
public class UniqueSet<T> {

	private final Map<T, Short> values = new HashMap<T, Short>();
	private short count = 0;

	public short getIDZeroBased(T key) {
		Short id = values.get(key);
		if (id == null) {
			id = count;
			count++;
			values.put(key, id);
		}
		return id;
	}

	/**
	 * @see List#toArray(Object[])
	 */
	public T[] toArray(T[] a) {
		if (a.length < values.size())
			a = Arrays.copyOf(a, values.size());
		for (Entry<T, Short> i_v : values.entrySet()) {
			T i = i_v.getKey();
			Short v = i_v.getValue();
			a[v] = i;
		}
		if (a.length > values.size())
			a[values.size()] = null;
		return a;
	}
}
