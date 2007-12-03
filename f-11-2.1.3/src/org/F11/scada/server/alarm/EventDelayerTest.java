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
 */
package org.F11.scada.server.alarm;

import java.util.Date;

import jp.gr.javacons.jim.DataHolder;
import jp.gr.javacons.jim.DataProvider;
import jp.gr.javacons.jim.DataValueChangeEvent;
import jp.gr.javacons.jim.Manager;
import junit.framework.TestCase;

import org.F11.scada.data.WifeDataDigital;
import org.F11.scada.data.WifeQualityFlag;
import org.F11.scada.test.util.TestUtil;
import org.F11.scada.xwife.server.WifeDataProvider;

/**
 * @author Hideaki Maekawa
 */
public class EventDelayerTest extends TestCase {
	private EventDelayer delayer;
	private TestDelayDataValueChangeListener listener;
	private DataProvider dp;

	/**
	 * Constructor for EventDelayerTest.
	 * @param name
	 */
	public EventDelayerTest(String name) {
		super(name);
	}

	protected void setUp() throws Exception {
		listener = new TestDelayDataValueChangeListener();
		delayer = new EventDelayer(listener);
		dp = TestUtil.createDP();
		Manager.getInstance().addDataProvider(dp);
	}

	protected void tearDown() throws Exception {
		TestUtil.crearJIM();
	}

	public void testFireDelaydDataValueChange() throws Exception {
		DataHolder dh = TestUtil.createDigitalHolder("H1", true);
		// ディレイタイマーを10秒に設定
		dh.setParameter(WifeDataProvider.PARA_NAME_OFFDELAY, new Integer(10));
		dp.addDataHolder(dh);

		fireEvent(dh);
		assertTrue(listener.isDataChanged());
		clearFlag();
		dh.setValue(WifeDataDigital.valueOfFalse(0), new Date(), WifeQualityFlag.GOOD);
		// タイマー開始
		fireEvent(dh);
		assertFalse(listener.isDataChanged());
		clearFlag();

		// チャタリングの間はデータ変更イベントを無視
		dh.setValue(WifeDataDigital.valueOfTrue(0), new Date(), WifeQualityFlag.GOOD);
		fireEvent(dh);
		assertFalse(listener.isDataChanged());
		dh.setValue(WifeDataDigital.valueOfFalse(0), new Date(), WifeQualityFlag.GOOD);
		fireEvent(dh);
		assertFalse(listener.isDataChanged());
		dh.setValue(WifeDataDigital.valueOfTrue(0), new Date(), WifeQualityFlag.GOOD);
		fireEvent(dh);
		assertFalse(listener.isDataChanged());
		dh.setValue(WifeDataDigital.valueOfFalse(0), new Date(), WifeQualityFlag.GOOD);
		fireEvent(dh);
		assertFalse(listener.isDataChanged());

		// 約9秒後も無視
		TestUtil.sleep(9000L);
		dh.setValue(WifeDataDigital.valueOfTrue(0), new Date(), WifeQualityFlag.GOOD);
		fireEvent(dh);
		assertFalse(listener.isDataChanged());
		dh.setValue(WifeDataDigital.valueOfFalse(0), new Date(), WifeQualityFlag.GOOD);
		fireEvent(dh);
		assertFalse(listener.isDataChanged());

		// その後約11秒後はイベントを処理
		TestUtil.sleep(11000L);
		dh.setValue(WifeDataDigital.valueOfTrue(0), new Date(), WifeQualityFlag.GOOD);
		fireEvent(dh);
		assertTrue(listener.isDataChanged());
	}

	private void clearFlag() {
		listener.setDataChanged(false);
	}

	private void fireEvent(DataHolder dh) {
		DataValueChangeEvent evt =
			new DataValueChangeEvent(dh, dh.getValue(), new Date(), WifeQualityFlag.GOOD);
		delayer.fireDelayedDataValueChange(evt);
	}


	private static class TestDelayDataValueChangeListener implements DelayDataValueChangeListener {
		private boolean isDataChanged;

		public void delayedDataValueChanged(DataValueChangeEvent evt) {
			setDataChanged(true);
		}

		public boolean isDataChanged() {
			return isDataChanged;
		}

		public void setDataChanged(boolean isDataChanged) {
			this.isDataChanged = isDataChanged;
		}
	}
}
