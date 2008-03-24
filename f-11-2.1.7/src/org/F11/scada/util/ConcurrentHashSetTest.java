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

import java.util.Arrays;
import java.util.List;
import java.util.Set;

import junit.framework.TestCase;

public class ConcurrentHashSetTest extends TestCase {
	public void testSet() throws Exception {
		Object o = new Object();
		Set set = new ConcurrentHashSet();
		assertEquals(true, set.add(o));
		assertEquals(false, set.add(o));
		assertEquals(1, set.size());
		List o2 = Arrays.asList(new Object[]{new Object(), new Object()});
		assertEquals(true, set.addAll(o2));
		assertEquals(3, set.size());
		assertTrue(set.contains(o));
		assertTrue(set.containsAll(o2));
		assertTrue(set.equals(set));
		Object o3 = new Object();
		assertEquals(true, set.add(o3));
		assertEquals(true, set.remove(o));
		assertEquals(3, set.size());
		assertEquals(true, set.removeAll(o2));
		assertEquals(1, set.size());
		set.clear();
		assertEquals(0, set.size());
		assertTrue(set.isEmpty());
	}
}
