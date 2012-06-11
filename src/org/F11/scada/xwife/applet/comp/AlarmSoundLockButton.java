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
import java.awt.event.ActionListener;
import java.util.Date;

import javax.swing.JButton;

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

public class AlarmSoundLockButton extends JButton {
	private static final long serialVersionUID = 8692460921260246988L;
	private final Logger logger = Logger.getLogger(AlarmSoundLockButton.class);
	private String soundOnHolderID;

	public AlarmSoundLockButton(final AbstractWifeApplet wifeApplet) {
		super(GraphicManager.get("/images/sdlock.png"));
		setToolTipText("åxïÒâπã÷é~");
		soundOnHolderID = getSoundOnHolder(wifeApplet);

		if ("".equals(soundOnHolderID)) {
			addActionListener(new NomalActionListener(wifeApplet, this));
		} else {
			addActionListener(new HolderActionListener(
				wifeApplet,
				this,
				soundOnHolderID));
		}
		init(wifeApplet);
	}

	private String getSoundOnHolder(final AbstractWifeApplet wifeApplet) {
		return wifeApplet.getConfiguration().getString(
			"xwife.applet.Applet.soundOnHolder",
			"");
	}

	private void init(AbstractWifeApplet wifeApplet) {
		if (!"".equals(soundOnHolderID)) {
			DataHolder dh =
				Manager.getInstance().findDataHolder(soundOnHolderID);
			if (dh != null) {
				WifeData wd = (WifeData) dh.getValue();
				if (wd instanceof WifeDataDigital) {
					new StopSoundButtonListener(
						soundOnHolderID,
						this,
						wifeApplet);
				} else {
					logger.error("ÉfÉWÉ^Éãà»äOÇÃÉfÅ[É^Ç™éwíËÇ≥ÇÍÇƒÇ¢Ç‹Ç∑ÅB : " + soundOnHolderID);
				}
			} else {
				if (null != soundOnHolderID) {
					logger.warn(soundOnHolderID + " Ç™ìoò^Ç≥ÇÍÇƒÇ¢Ç‹ÇπÇÒ");
				}
			}
		}
	}

	private static class NomalActionListener implements ActionListener {
		private final Logger logger = Logger
			.getLogger(NomalActionListener.class);
		private final AbstractWifeApplet wifeApplet;
		private final JButton button;

		public NomalActionListener(AbstractWifeApplet wifeApplet, JButton button) {
			this.wifeApplet = wifeApplet;
			this.button = button;
		}

		public void actionPerformed(ActionEvent evt) {
			if (wifeApplet.isAlarmSoundLock()) {
				wifeApplet.setAlarmSoundLock(false);
				button.setToolTipText("åxïÒâπã÷é~");
				button.setIcon(GraphicManager.get("/images/sdlock.png"));
				logger.info("åxïÒâπã÷é~");
			} else {
				wifeApplet.setAlarmSoundLock(true);
				button.setToolTipText("åxïÒâπã÷é~âèú");
				button.setIcon(GraphicManager.get("/images/sdunlock.png"));
				logger.info("åxïÒâπã÷é~âèú");
			}
		}
	}

	private static class HolderActionListener implements ActionListener {
		private static byte[] TRUE_DATA = { (byte) 0xFF, (byte) 0xFF };
		private static byte[] FALSE_DATA = { (byte) 0x00, (byte) 0x00 };
		private final Logger logger = Logger
			.getLogger(HolderActionListener.class);
		private final AbstractWifeApplet wifeApplet;
		private final JButton button;
		private final String soundOnHolderID;

		public HolderActionListener(AbstractWifeApplet wifeApplet,
				JButton button,
				String soundOnHolderID) {
			this.wifeApplet = wifeApplet;
			this.button = button;
			this.soundOnHolderID = soundOnHolderID;
		}

		public void actionPerformed(ActionEvent evt) {
			if (wifeApplet.isAlarmSoundLock()) {
				writeAlarmButton(wifeApplet, FALSE_DATA);
				logger.info("åxïÒâπã÷é~");
			} else {
				writeAlarmButton(wifeApplet, TRUE_DATA);
				logger.info("åxïÒâπã÷é~âèú");
			}
		}

		private void writeAlarmButton(AbstractWifeApplet wifeApplet,
				byte[] onoff) {
			DataHolder dh =
				Manager.getInstance().findDataHolder(soundOnHolderID);
			if (dh != null) {
				WifeData wd = (WifeData) dh.getValue();
				if (wd instanceof WifeDataDigital) {
					writeDigital(dh, wd, onoff);
				} else {
					logger.error("ÉfÉWÉ^Éãà»äOÇÃÉfÅ[É^Ç™éwíËÇ≥ÇÍÇƒÇ¢Ç‹Ç∑ÅB : " + soundOnHolderID);
				}
			} else {
				if (null != soundOnHolderID) {
					logger.warn(soundOnHolderID + " Ç™ìoò^Ç≥ÇÍÇƒÇ¢Ç‹ÇπÇÒ");
				}
			}
		}

		private void writeDigital(DataHolder dh, WifeData wd, byte[] onoff) {
			WifeDataDigital dd = (WifeDataDigital) wd;
			dh.setValue(
				(WifeData) dd.valueOf(onoff),
				new Date(),
				WifeQualityFlag.GOOD);
			try {
				dh.syncWrite();
			} catch (Exception e) {
				logger.error("ÉfÉWÉ^ÉãÉfÅ[É^èëÇ´çûÇ›ÉGÉâÅ[", e);
			}
		}
	}

	private static class StopSoundButtonListener implements
			DataValueChangeListener, DataReferencerOwner, ReferencerOwnerSymbol {
		private final Logger logger = Logger
			.getLogger(StopSoundButtonListener.class);
		private JButton button;
		private AbstractWifeApplet wifeApplet;
		private DataReferencer referencer;

		StopSoundButtonListener(String value,
				JButton button,
				AbstractWifeApplet wifeApplet) {
			this.button = button;
			this.wifeApplet = wifeApplet;
			connectReferencer(value);
		}

		private void connectReferencer(String value) {
			int p = value.indexOf('_');
			if (0 < p) {
				String provider = value.substring(0, p);
				String holder = value.substring(p + 1);
				referencer = new DataReferencer(provider, holder);
				referencer.connect(this);
				logger.info("åxïÒâπã÷é~É{É^ÉìÇ "
					+ provider
					+ "_"
					+ holder
					+ " Ç…äÑÇËïtÇØÇ‹ÇµÇΩ");
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
					if (dd.isOnOff(false)) {
						wifeApplet.setAlarmSoundLock(false);
						button.setToolTipText("åxïÒâπã÷é~");
						button
							.setIcon(GraphicManager.get("/images/sdlock.png"));
					} else {
						wifeApplet.setAlarmSoundLock(true);
						button.setToolTipText("åxïÒâπã÷é~âèú");
						button.setIcon(GraphicManager
							.get("/images/sdunlock.png"));
					}
				}
			}
		}

		public Class[][] getReferableDataHolderTypeInfo(DataReferencer dr) {
			return new Class[][] { { DataHolder.class, WifeData.class } };
		}
	}

}
