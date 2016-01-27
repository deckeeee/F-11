/*
 * $Header: /cvsroot/f-11/F-11/src/org/F11/scada/util/SingletonSortedMapTest.java,v 1.2.2.2 2004/12/28 05:53:07 frdm Exp $
 * $Revision: 1.2.2.2 $
 * $Date: 2004/12/28 05:53:07 $
 * 
 * =============================================================================
 * Projrct F-11 - Web SCADA for Java
 * Copyright (C) 2002 Freedom, Inc. All Rights Reserved.
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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;

import junit.framework.TestCase;

/**
 * SingletonSortedMapのテストケースです。
 * @author Hideaki Maekawa <frdm@users.sourceforge.jp>
 */
public class SingletonSortedMapTest extends TestCase {
	SortedMap map;

	/**
	 * Constructor for SingletonSortedMapTest.
	 * @param arg0
	 */
	public SingletonSortedMapTest(String arg0) {
		super(arg0);
	}

	/*
	 * @see TestCase#setUp()
	 */
	protected void setUp() throws Exception {
		map = new SingletonSortedMap();
		map.put("1", "1");
		map.put("2", "2");
	}

	/*
	 * Test for void SingletonSortedMap(Comparator)
	 */
	public void testUniqueValueSortedMap() {
		assertEquals("1", map.firstKey());
		assertEquals("2", map.lastKey());
	}

	/*
	 * Test for void SingletonSortedMap(Comparator)
	 */
	public void testUniqueValueSortedMapComparator() {
		SingletonSortedMap m = new SingletonSortedMap(Collections.reverseOrder());
		m.putAll(map);
		assertEquals("2", m.firstKey());
		assertEquals("1", m.lastKey());
	}

	/*
	 * Test for void SingletonSortedMap(Map)
	 */
	public void testUniqueValueSortedMapMap() {
		Map hm = new HashMap(map);
		SingletonSortedMap m = new SingletonSortedMap(hm);
		assertEquals("1", m.firstKey());
		assertEquals("2", m.lastKey());
	}

	/*
	 * Test for void SingletonSortedMap(SortedMap)
	 */
	public void testUniqueValueSortedMapSortedMap() {
		SingletonSortedMap rm = new SingletonSortedMap(Collections.reverseOrder());
		rm.putAll(map);
		SingletonSortedMap m = new SingletonSortedMap(rm);
		assertEquals("2", m.firstKey());
		assertEquals("1", m.lastKey());
	}

	public void testPut() {
		map.put("a", "A");
		assertEquals(3, map.size());
		assertEquals("A", map.get("a"));
		map.put("a", "B");
		assertEquals(3, map.size());
		assertEquals("B", map.get("a"));
		map.put("b", "B");
		assertEquals(3, map.size());
		assertEquals("B", map.get("b"));
		map.put("a", "A");
		assertEquals(4, map.size());
		assertEquals("A", map.get("a"));
		
		try {
			map.put(null, "NULL");
			fail();
		} catch (NullPointerException e) {}
		
		try {
			map.put("a", null);
			fail();
		} catch (NullPointerException e) {}
	}

	public void testPutAll() {
		Map m = new HashMap();
		m.put("a", "A");
		m.put("b", "B");
		m.put("c", "A");
		map.putAll(m);
		assertEquals(4, map.size());
		assertNull(map.get("a"));
		assertEquals("B", map.get("b"));
		assertEquals("A", map.get("c"));
	}

	public void testClear() {
		map.clear();
		assertEquals(0, map.size());
	}

	public void testEntrySet() {
		Set s = map.entrySet();
		try {
			s.remove("1");
			fail();
		} catch (UnsupportedOperationException e) {}
		try {
			s.removeAll(s);
			fail();
		} catch (UnsupportedOperationException e) {}
		try {
			s.retainAll(s);
			fail();
		} catch (UnsupportedOperationException e) {}
		try {
			s.clear();
			fail();
		} catch (UnsupportedOperationException e) {}

		Iterator i = s.iterator();
		try {
			i.remove();
			fail();
		} catch (UnsupportedOperationException e) {}
	}

	public void testKeySet() {
		Set s = map.keySet();
		try {
			s.remove("1");
			fail();
		} catch (UnsupportedOperationException e) {}
		try {
			s.removeAll(s);
			fail();
		} catch (UnsupportedOperationException e) {}
		try {
			s.retainAll(s);
			fail();
		} catch (UnsupportedOperationException e) {}
		try {
			s.clear();
			fail();
		} catch (UnsupportedOperationException e) {}

		Iterator i = s.iterator();
		try {
			i.remove();
			fail();
		} catch (UnsupportedOperationException e) {}
	}

	public void testRemove() {
		assertEquals("1", map.remove("1"));
		assertEquals(1, map.size());
		
		try {
			map.remove(null);
			fail();
		} catch (NullPointerException e) {}
	}

	public void testValues() {
		Collection c = map.values();
		try {
			c.remove("1");
			fail();
		} catch (UnsupportedOperationException e) {}
		try {
			c.removeAll(c);
			fail();
		} catch (UnsupportedOperationException e) {}
		try {
			c.retainAll(c);
			fail();
		} catch (UnsupportedOperationException e) {}
		try {
			c.clear();
			fail();
		} catch (UnsupportedOperationException e) {}

		Iterator i = c.iterator();
		try {
			i.remove();
			fail();
		} catch (UnsupportedOperationException e) {}
	}

	public void testHashCode() {
		SingletonSortedMap m = new SingletonSortedMap(map);
		assertEquals(m.hashCode(), map.hashCode());
	}

	public void testEquals() {
		SingletonSortedMap m = new SingletonSortedMap(map);
		assertEquals(m, map);
	}

	public void testSerialize() throws Exception {
		//デシリアライズのテスト。
		File temp = File.createTempFile("sertest", ".ser");
		temp.deleteOnExit();
		ObjectOutputStream outs =
			new ObjectOutputStream(new FileOutputStream(temp));
		outs.writeObject(map);
		outs.flush();
		outs.close();
		ObjectInputStream ins =
			new ObjectInputStream(new FileInputStream(temp));
		SingletonSortedMap d = (SingletonSortedMap) ins.readObject();
		ins.close();
		assertNotNull(d);
		assertEquals(d, map);
		temp.delete();
	}
	
	// 耐久テスト
/*
	public void testEndurance() {
		SingletonSortedMap m = new SingletonSortedMap();

		long MAX = 100000;
		for (int i = 0; i < MAX; i++) {
			double d = Math.random() * 10;
			m.put(new Timestamp(i), new Long(Math.round(d)));
		}
	}
*/
}
