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

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.beans.XMLDecoder;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JToolBar;

import org.F11.scada.parser.alarm.AlarmDefine;
import org.F11.scada.parser.alarm.ToolBar;
import org.F11.scada.xwife.applet.AbstractWifeApplet;
import org.apache.log4j.Logger;

/**
 * 全画面共通のツールバーを管理するクラス。
 *
 * /resources/AlarmDefine.xml &lt;toolbar displayLogin="false" /&gt;が false
 * ならブラウザ表示の時ログインボタンを表示しない。
 * /resources/login.xmlが存在している場合で、自IPアドレスが定義されていない場合はログインボタンを表示しない。
 */
public class SystemToolBar extends JToolBar {
	private static final long serialVersionUID = -4914267991421051617L;
	private static final String LOGIN_XML = "/resources/login.xml";
	private final Logger logger = Logger.getLogger(SystemToolBar.class);

	/**
	 * コンストラクタ
	 *
	 * @param pageMap ページマップオブジェクト
	 */
	public SystemToolBar(AbstractWifeApplet wifeApplet) {
		super();
		setFloatable(false);
		add(new StopAlarmButton(wifeApplet));
		addSeparator();
		if (isDisplayToolBar(wifeApplet) && isLoginXml()) {
			addChangeUser(wifeApplet);
		}
		add(new ScreenLockButton(wifeApplet));
		add(getAlarmSoundLock(wifeApplet));
		if (isShowScreenShot(wifeApplet)) {
			add(new ScreenShotButton(wifeApplet));
		}
		addSeparator();
	}

	private AlarmSoundLockButton getAlarmSoundLock(AbstractWifeApplet wifeApplet) {
		AlarmSoundLockButton b = new AlarmSoundLockButton(wifeApplet);
		String value =
			wifeApplet.getConfiguration().getString(
					"xwife.applet.Applet.soundOnHolder", "");
		if ("".equals(value)) {
			if (wifeApplet.isSoundoffAtStarted()) {
				b.doClick();
			}
		}
		return b;
	}

	private JLabel getUserNameLabel() {
		JLabel userNameLabel = new JLabel();
		userNameLabel.setFont(new Font("serif", Font.PLAIN, 22));
		userNameLabel.setBorder(BorderFactory.createLineBorder(Color.black));
		userNameLabel.setPreferredSize(new Dimension(200, 30));
		userNameLabel.setMinimumSize(new Dimension(200, 30));
		userNameLabel.setMaximumSize(new Dimension(200, 30));
		return userNameLabel;
	}

	private boolean isLoginXml() {
		URL url = SystemToolBar.class.getResource(LOGIN_XML);
		if (url == null) {
			logger.info(LOGIN_XML + "が定義されていません。通常通りログインボタンを表示します。");
			return true;
		} else {
			return readXML(url);
		}
	}

	private boolean readXML(URL url) {
		XMLDecoder decoder = null;
		try {
			decoder = new XMLDecoder(new BufferedInputStream(url.openStream()));
			Set<InetAddress> set = getAddressSet(decoder);
			return set.contains(InetAddress.getLocalHost());
		} catch (UnknownHostException e) {
			logger.warn("IPアドレスが見つかりません", e);
			return false;
		} catch (IOException e) {
			logger.warn(LOGIN_XML + "の読み込み中にエラーが発生", e);
			return false;
		} finally {
			if (decoder != null) {
				decoder.close();
			}
		}
	}

	private Set<InetAddress> getAddressSet(XMLDecoder decoder)
		throws UnknownHostException {
		ArrayList<String> list = (ArrayList) decoder.readObject();
		HashSet<InetAddress> set = new HashSet<InetAddress>(list.size());
		for (String ipAddress : list) {
			set.add(InetAddress.getByName(ipAddress));
		}
		return set;
	}

	private void addChangeUser(AbstractWifeApplet wifeApplet) {
		JLabel userNameLabel = getUserNameLabel();
		JButton changeUser = new ChangeUser(wifeApplet, userNameLabel);
		add(changeUser);
		add(userNameLabel);
		addSeparator();
	}

	private boolean isShowScreenShot(AbstractWifeApplet wifeApplet) {
		return wifeApplet.isStandAlone()
			&& wifeApplet.getConfiguration().getBoolean(
					"org.F11.scada.xwife.applet.isShowScreenShot", false);
	}

	private boolean isDisplayToolBar(AbstractWifeApplet wifeApplet) {
		AlarmDefine ad = null;
		try {
			ad = new AlarmDefine();
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("AlarmDefine.xml is invalid.");
			return true;
		}

		if (wifeApplet.isStandAlone()) {
			return true;
		}

		ToolBar tb = ad.getAlarmConfig().getToolBar();
		return ad == null || tb == null || tb.isDisplayLogin();
	}
}
