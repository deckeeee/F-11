/*
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

import jp.gr.javacons.jim.AbstractDataProvider;
import jp.gr.javacons.jim.DataHolder;
import jp.gr.javacons.jim.DataProviderDoesNotSupportException;
import jp.gr.javacons.jim.Manager;
import junit.framework.TestCase;

import org.F11.scada.data.WifeData;
import org.F11.scada.test.util.TestUtil;

public class SystemTimeUtilTest extends TestCase {
	TestDataProvider dp;

	public SystemTimeUtilTest(String arg0) {
		super(arg0);
	}

	protected void setUp() throws Exception {
		dp = new TestDataProvider();
		DataHolder dh = TestUtil.createTimestampHolder("TimeWrite");
		dp.addDataHolder(dh);
		Manager.getInstance().addDataProvider(dp);
	}

	protected void tearDown() throws Exception {
		TestUtil.crearJIM();
	}

	public void testSetPlcTime() throws Exception {
		SystemTimeUtil util = new SystemTimeUtil();
		util.setPlcTime("P1_TimeWrite");
		assertTrue(dp.isWrite);
	}


	class TestDataProvider extends AbstractDataProvider {
		private static final long serialVersionUID = 3797591758942543877L;
		boolean isWrite;
		TestDataProvider() {
			setDataProviderName("P1");
		}
		public Class[][] getProvidableDataHolderTypeInfo() {
			Class[][] c = {
				{DataHolder.class, WifeData.class}
			};
			return c;
		}
		public void syncWrite(DataHolder dh) throws DataProviderDoesNotSupportException {
			isWrite = true;
		    System.out.println("write data : " + dh);
		}
		public void asyncWrite(DataHolder dh) throws DataProviderDoesNotSupportException {
			throw new DataProviderDoesNotSupportException();
		}
		public void asyncRead(DataHolder dh) throws DataProviderDoesNotSupportException {
			throw new DataProviderDoesNotSupportException();
		}
		public void asyncRead(DataHolder[] dhs) throws DataProviderDoesNotSupportException {
			throw new DataProviderDoesNotSupportException();
		}
		public void asyncWrite(DataHolder[] dhs) throws DataProviderDoesNotSupportException {
			throw new DataProviderDoesNotSupportException();
		}
		public void syncRead(DataHolder dh) throws DataProviderDoesNotSupportException {
			throw new DataProviderDoesNotSupportException();
		}
		public void syncRead(DataHolder[] dhs) throws DataProviderDoesNotSupportException {
			throw new DataProviderDoesNotSupportException();
		}
		public void syncWrite(DataHolder[] dhs) throws DataProviderDoesNotSupportException {
			throw new DataProviderDoesNotSupportException();
		}
	}
}
