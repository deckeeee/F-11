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

package org.F11.scada.applet.symbol;

import java.awt.Component;
import java.awt.Cursor;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.Icon;
import javax.swing.JDialog;
import javax.swing.JOptionPane;

import org.F11.scada.util.ComponentUtil;
import org.F11.scada.xwife.applet.PageChangeEvent;
import org.F11.scada.xwife.applet.PageChanger;
import org.apache.log4j.Logger;

/**
 * イメージグラフィックを表示するシンボルクラスです。
 * 
 * @author Youhei Horikawa <hori@users.sourceforge.jp>
 */
public class TrendJumpButton extends Symbol {
	private final Logger log = Logger.getLogger(TrendJumpButton.class);
	private final PageChanger changer;
	private String changeTo;
	private Object argv;
	private boolean immediate;
	private String message;
	private String title;
	private Point dialogLocation;

	/**
	 * Constructor for ImageSymbol.
	 * 
	 * @see org.F11.scada.applet.symbol.Symbol#Symbol()
	 * @param property SymbolProperty オブジェクト
	 */
	public TrendJumpButton(SymbolProperty property, PageChanger changer1) {
		super(property);
		this.changer = changer1;
		changeTo = property.getProperty("changeto");
		argv = property.getProperty("argv");
		immediate = Boolean.valueOf(property.getProperty("immediate", "true"))
				.booleanValue();
		message = property.getProperty("message", "ページを移動します。よろしいですか？");
		title = property.getProperty("title", "ページ移動");
		/* マウスクリックイベントの登録 */
		final Component myComp = this;
		addMouseListener(new MouseAdapter() {
			private Rectangle rectangle;

			public void mousePressed(MouseEvent e) {
				rectangle = myComp.getBounds();
			}

			public void mouseReleased(MouseEvent e) {
				if (ComponentUtil.contains(rectangle, e.getPoint())) {
					dialogLocation = myComp.getLocationOnScreen();
					dialogLocation.y += rectangle.height;
					changePage();
				}
			}

			public void mouseEntered(MouseEvent e) {
				if (HandCursorListener.handcursor) {
					Component comp = (Component) e.getSource();
					comp.setCursor(new Cursor(Cursor.HAND_CURSOR));
				}
			}
		});
	}

	/*
	 * プロパティを変更します。
	 * 
	 * @see org.F11.scada.applet.symbol.Symbol#updateProperty()
	 */
	protected void updatePropertyImpl() {
		String path = getProperty("value");
		Icon icon = GraphicManager.get(path);
		if (icon != null) {
			setIcon(icon);
			setSize(icon.getIconWidth(), icon.getIconHeight());
		} else {
			if (null != path) {
				log.error("icon file not found = " + path);
			}
		}
	}

	public Point getDialogLocation() {
		return dialogLocation;
	}

	public void setDialogLocation(Point dialogLocation) {
		this.dialogLocation = dialogLocation;
	}

	public void changePage() {
		if (immediate) {
			performPageChangeEvent();
		} else {
			String[] option = { "OK", "CANCEL" };
			JOptionPane op = new JOptionPane(
					message,
					JOptionPane.QUESTION_MESSAGE,
					JOptionPane.YES_NO_OPTION,
					null,
					option,
					option[1]);
			JDialog d = op.createDialog(this, title);
			d.setLocation(dialogLocation);
			d.setVisible(true);
			Object obj = op.getValue();
			if (obj instanceof String) {
				if ("OK".equalsIgnoreCase((String) op.getValue())) {
					performPageChangeEvent();
				}
			}
		}
	}

	private void performPageChangeEvent() {
		PageChangeEvent evt = new PageChangeEvent(
				this,
				changeTo,
				false,
				false,
				argv);
		changer.changePage(evt);
	}
}
