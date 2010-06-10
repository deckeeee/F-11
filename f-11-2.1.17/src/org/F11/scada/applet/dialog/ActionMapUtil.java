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

package org.F11.scada.applet.dialog;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.KeyStroke;

import org.F11.scada.applet.ClientConfiguration;
import org.F11.scada.xwife.applet.PageChanger;
import org.apache.commons.configuration.Configuration;
import org.apache.log4j.Logger;

/**
 * 警報音停止のアクションマップを設定する便利クラスです。
 * 
 * @author maekawa
 * 
 */
public abstract class ActionMapUtil {
	private static final Logger logger = Logger.getLogger(ActionMapUtil.class);
	private static final String CANCEL_NAME =
		ActionMapUtil.class.getName() + ".cancel";
	/**
	 * クライアント設定オブジェクト
	 */
	private static final Configuration configuration =
		new ClientConfiguration();

	/**
	 * 引数のコンポーネントに警報音停止のアクションを設定します。
	 * 
	 * @param c アクションを設定するコンポーネント
	 * @param changer ページ変更オブジェクト(AbstractWifeApplet)
	 */
	public static void setActionMap(JComponent c, final PageChanger changer) {
		ActionMap map = c.getActionMap();
		map.put(CANCEL_NAME, new AbstractAction(CANCEL_NAME) {
			public void actionPerformed(ActionEvent e) {
				logger.info("警報音停止");
				changer.stopAlarm();
			}
		});
		InputMap imap = c.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
		imap.put(KeyStroke.getKeyStroke(configuration.getString(
			"xwife.applet.Applet.alarmStopKey",
			"F12")), CANCEL_NAME);
	}
}
