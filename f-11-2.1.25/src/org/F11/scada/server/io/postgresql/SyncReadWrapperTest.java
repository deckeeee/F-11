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

package org.F11.scada.server.io.postgresql;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Map;

import junit.framework.TestCase;

import org.F11.scada.WifeException;
import org.F11.scada.server.communicater.Communicater;
import org.F11.scada.server.event.WifeCommand;
import org.F11.scada.server.event.WifeEventListener;

public class SyncReadWrapperTest extends TestCase {
	public void testSyncRead() throws Exception {
		ArrayList commands = new ArrayList();
		commands.add(new WifeCommand("P1", 0, 1, 0, 0, 1));
		SyncReadWrapper wrapper = new SyncReadWrapper("P1");
		Map map = wrapper.syncRead(new TestCommunicater(), commands);
		byte[] data = (byte[]) map.get(commands.iterator().next());
		assertTrue(Arrays.equals(new byte[] { 0x00, 0x00 }, data));
	}

	class TestCommunicater implements Communicater {
		public void addReadCommand(Collection commands) {
		}

		public void removeReadCommand(Collection commands) {
		}

		public void addWifeEventListener(WifeEventListener l) {
		}

		public void close() throws InterruptedException {
		}

		public void removeWifeEventListener(WifeEventListener l) {
		}

		public Map syncRead(Collection commands, boolean sameDataBalk)
				throws InterruptedException,
				IOException,
				WifeException {
			throw new WifeException();
		}

		public Map syncRead(Collection commands)
				throws InterruptedException,
				IOException,
				WifeException {
			return null;
		}

		public void syncWrite(Map commands)
				throws InterruptedException,
				IOException,
				WifeException {
		}
	}
}
