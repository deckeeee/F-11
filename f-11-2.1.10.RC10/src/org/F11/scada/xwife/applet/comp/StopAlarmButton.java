/*
 * Project F-11 - Web SCADA for Java
 * Copyright (C) 2002-2008 Freedom, Inc. All Rights Reserved.
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

package org.F11.scada.xwife.applet.comp;

import java.awt.event.ActionEvent;
import java.util.Date;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.KeyStroke;

import jp.gr.javacons.jim.DataHolder;
import jp.gr.javacons.jim.DataReferencer;
import jp.gr.javacons.jim.DataReferencerOwner;
import jp.gr.javacons.jim.DataValueChangeEvent;
import jp.gr.javacons.jim.DataValueChangeListener;
import jp.gr.javacons.jim.Manager;

import org.F11.scada.applet.symbol.GraphicManager;
import org.F11.scada.applet.symbol.ReferencerOwnerSymbol;
import org.F11.scada.data.WifeData;
import org.F11.scada.data.WifeDataDigital;
import org.F11.scada.data.WifeQualityFlag;
import org.F11.scada.xwife.applet.AbstractWifeApplet;
import org.apache.log4j.Logger;

public class StopAlarmButton extends JButton {
	private static final long serialVersionUID = -7423103742279766042L;
	private final Logger logger = Logger.getLogger(StopAlarmButton.class);
	private static byte[] TRUE_DATA = { (byte) 0xFF, (byte) 0xFF };
	private String writeProvider;
	private String writeHolder;

	public StopAlarmButton(final AbstractWifeApplet wifeApplet) {
		super(GraphicManager.get("/images/sndstop.png"));
		setToolTipText("警報音停止");
		getWrtiteHolder(wifeApplet);
		initKeyEvent(wifeApplet);
		createStopAlarmButtonListener(wifeApplet);
	}

	private void initKeyEvent(final AbstractWifeApplet wifeApplet) {
		String stopKey =
			wifeApplet.getConfiguration().getString(
				"xwife.applet.Applet.alarmStopKey",
				"F12");
		Action key = new AbstractAction(stopKey) {
			private static final long serialVersionUID = -2668128777789593884L;

			public void actionPerformed(ActionEvent e) {
				wifeApplet.stopAlarm();
				writeAlarmButton(wifeApplet);
				logger.info("警報音停止");
			}
		};

		// associate action with key
		InputMap imap = getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
		imap.put(KeyStroke.getKeyStroke(stopKey), key.getValue(Action.NAME));
		ActionMap amap = getActionMap();
		amap.put(key.getValue(Action.NAME), key);
		addActionListener(key);
	}

	private void getWrtiteHolder(final AbstractWifeApplet wifeApplet) {
		String value =
			wifeApplet.getConfiguration().getString(
				"xwife.applet.Applet.alarmStopKey.write",
				"");
		if (!"".equals(value)) {
			int p = value.indexOf('_');
			if (0 < p) {
				writeProvider = value.substring(0, p);
				writeHolder = value.substring(p + 1);
				logger.info("警報音停止ボタン書き込みホルダを "
					+ writeProvider
					+ "_"
					+ writeHolder
					+ " に割り付けました");
			}
		}
	}

	private void writeAlarmButton(AbstractWifeApplet wifeApplet) {
		DataHolder dh =
			Manager.getInstance().findDataHolder(writeProvider, writeHolder);
		if (dh != null) {
			WifeData wd = (WifeData) dh.getValue();
			if (wd instanceof WifeDataDigital) {
				writeDigital(dh, wd);
			} else {
				logger.error("デジタル以外のデータが指定されています。 : "
					+ writeProvider
					+ "_"
					+ writeHolder);
			}
		} else {
			if (null != writeProvider && null != writeHolder) {
				logger.warn(writeProvider + "_" + writeHolder + " が登録されていません");
			}
		}
	}

	private void writeDigital(DataHolder dh, WifeData wd) {
		WifeDataDigital dd = (WifeDataDigital) wd;
		dh.setValue(
			(WifeData) dd.valueOf(TRUE_DATA),
			new Date(),
			WifeQualityFlag.GOOD);
		try {
			dh.syncWrite();
		} catch (Exception e) {
			logger.error("デジタルデータ書き込みエラー", e);
		}
	}

	private void createStopAlarmButtonListener(AbstractWifeApplet wifeApplet) {
		String value =
			wifeApplet.getConfiguration().getString(
				"xwife.applet.Applet.alarmStopKey.event",
				"");
		if (!"".equals(value)) {
			new StopAlarmButtonListener(value, this);
		}
	}

	private static class StopAlarmButtonListener implements
			DataValueChangeListener, DataReferencerOwner, ReferencerOwnerSymbol {
		private final Logger logger =
			Logger.getLogger(StopAlarmButtonListener.class);
		private final JButton button;
		private DataReferencer referencer;

		StopAlarmButtonListener(String value, JButton button) {
			this.button = button;
			connectReferencer(value);
		}

		private void connectReferencer(String value) {
			int p = value.indexOf('_');
			if (0 < p) {
				String provider = value.substring(0, p);
				String holder = value.substring(p + 1);
				referencer = new DataReferencer(provider, holder);
				referencer.connect(this);
				logger.info("警報音停止ボタンを "
					+ provider
					+ "_"
					+ holder
					+ " に割り付けました");
			}
		}

		public void disConnect() {
			referencer.disconnect(this);
		}

		public void dataValueChanged(DataValueChangeEvent evt) {
			Object o = evt.getSource();
			if (o instanceof DataHolder) {
				DataHolder dh = (DataHolder) o;
				Object value = dh.getValue();
				if (value instanceof WifeDataDigital) {
					WifeDataDigital dd = (WifeDataDigital) value;
					if (dd.isOnOff(true)) {
						button.doClick();
					}
				}
			}
		}

		public Class[][] getReferableDataHolderTypeInfo(DataReferencer dr) {
			return new Class[][] { { DataHolder.class, WifeData.class } };
		}
	}
}
