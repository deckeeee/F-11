/*
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
package org.F11.scada.security;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import junit.framework.TestCase;

import org.F11.scada.WifeUtilities;
import org.F11.scada.security.auth.Subject;
import org.apache.log4j.Logger;

/**
 * 
 * @auther Hideaki Maekawa <frdm@users.sourceforge.jp>
 */
public class WifePolicyTest extends TestCase {
	WifePolicy policy;
	static Logger logger = Logger.getLogger(WifePolicyTest.class);

	/**
	 * Constructor for WifePolicyTest.
	 * @param arg0
	 */
	public WifePolicyTest(String arg0) {
		super(arg0);
	}
	protected void setUp() throws Exception {
		Class.forName(WifeUtilities.getJdbcDriver());
		policy = (WifePolicy) WifePolicy.getPolicy();
	}

	public void testImplies() {

		Set pri = new HashSet();
		pri.add(new WifePrincipal("user1"));
		Subject sub = Subject.createSubject(pri, "user1");

		assertTrue(policy.implies(sub, new WifePermission("P1_D_500_BcdSingle", "write")));
		assertTrue(!policy.implies(sub, new WifePermission("P1_D_500_BcdSingle", "read")));
		assertTrue(!policy.implies(sub, new WifePermission("P1_D_500_BcdSingle", "delete")));
		assertTrue(!policy.implies(sub, new WifePermission("P1_D_500_BcdSingle", "execute")));
		assertTrue(!policy.implies(sub, new WifePermission("P1_D_500_BcdSingle", "write,read,delete,execute")));

		assertTrue(policy.implies(sub, new WifePermission("P1_D_501_BcdSingle", "write")));
		assertTrue(policy.implies(sub, new WifePermission("P1_D_501_BcdSingle", "read")));
		assertTrue(policy.implies(sub, new WifePermission("P1_D_501_BcdSingle", "delete")));
		assertTrue(policy.implies(sub, new WifePermission("P1_D_501_BcdSingle", "execute")));
		assertTrue(policy.implies(sub, new WifePermission("P1_D_501_BcdSingle", "write,read,delete,execute")));

		assertTrue(!policy.implies(sub, new WifePermission("h003", "write")));
	}
	
	public void testStress() throws Exception {
		Set pri = new HashSet();
		pri.add(new WifePrincipal("user1"));
		Subject sub = Subject.createSubject(pri, "user1");

		int i = 0;
		logger.info("開始時刻:" + new Date() + " c:" + i);
		for (; i < 100000; i++) {
			policy.implies(sub, new WifePermission("P1_D_500_BcdSingle", "write"));
		}
		logger.info("開始時刻:" + new Date() + " c:" + i);
	}

}
