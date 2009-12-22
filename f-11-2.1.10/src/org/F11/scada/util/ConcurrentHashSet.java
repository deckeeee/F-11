/*
 * =============================================================================
 * Projrct F-11 - Web SCADA for Java
 * Copyright (C) 2002-2006 Freedom, Inc. All Rights Reserved.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 *
 */

package org.F11.scada.util;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.AbstractSet;
import java.util.Collection;
import java.util.Iterator;
import java.util.Set;

import java.util.concurrent.ConcurrentHashMap;

public class ConcurrentHashSet extends AbstractSet implements Set, Serializable {
	private static final long serialVersionUID = 2344164766774501667L;
	private static final Object DUMMY_VALUE = new Object();
	private final ConcurrentHashMap map;
	private transient Set keySet;

	public ConcurrentHashSet() {
		map = new ConcurrentHashMap();
		keySet = map.keySet();
	}

	public ConcurrentHashSet(int initialCapacity) {
		map = new ConcurrentHashMap(initialCapacity);
		keySet = map.keySet();
	}

	public ConcurrentHashSet(
			int initialCapacity,
			float loadFactor,
			int concurrencyLevel) {
		map = new ConcurrentHashMap(initialCapacity, loadFactor, concurrencyLevel);
		keySet = map.keySet();
	}

	public int size() {
		return map.size();
	}

	public boolean isEmpty() {
		return map.isEmpty();
	}

	public boolean contains(Object o) {
		return map.containsKey(o);
	}

	public Iterator iterator() {
		return keySet.iterator();
	}

	public Object[] toArray() {
		return keySet.toArray();
	}

	public Object[] toArray(Object[] a) {
		return keySet.toArray(a);
	}

	public boolean add(Object e) {
		return map.put(e, DUMMY_VALUE) == null;
	}

	public boolean remove(Object o) {
		return map.remove(o) != null;
	}

	public boolean removeAll(Collection c) {
		return keySet.removeAll(c);
	}

	public boolean retainAll(Collection c) {
		return keySet.retainAll(c);
	}

	public void clear() {
		map.clear();
	}

	public boolean equals(Object o) {
		return keySet.equals(o);
	}

	public int hashCode() {
		return keySet.hashCode();
	}

	private void readObject(ObjectInputStream s) throws IOException,
			ClassNotFoundException {
		s.defaultReadObject();
		keySet = map.keySet();
	}
}