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
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.util.ListIterator;

import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JViewport;
import javax.swing.Scrollable;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

import org.F11.scada.Service;
import org.apache.log4j.Logger;

/**
 * ページのベースペインクラスです。
 * 
 * @author Youhei Horikawa <hori@users.sourceforge.jp>
 */
public class BasePane extends JScrollPane {
	private static final long serialVersionUID = -5643328626848707519L;
	/** 初期化用ダミーページ */
	public static final BasePane DUMMY_PAGE = new BasePane();
	/** ページの名前 */
	private String pageName;
	/** メインページコンポーネント */
	private JLabel mainPage = new ScrollableLabel();
	/** Logging API */
	private static Logger logger = Logger.getLogger(BasePane.class);

	/**
	 * クライアントのページをキャッシュする場合は true
	 * 
	 * @since 1.1.1
	 */
	private boolean isCache;

	public BasePane() {
		setBorder(BorderFactory.createLoweredBevelBorder());
		setViewportView(mainPage);
	}

	/**
	 * ページ名称を設定します。
	 */
	public void setPageName(String name) {
		this.pageName = name;
	}

	/**
	 * ページ名称を返します。
	 */
	public String getPageName() {
		return this.pageName;
	}

	/**
	 * ページにコンポーネントを追加します。
	 */
	public Component addPageSymbol(Component comp) {
		return mainPage.add(comp);
	}

	/**
	 * ページにアイコンを設定します。
	 */
	public void setPageIcon(final Icon icon) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				mainPage.setIcon(icon);
			}
		});
	}

	/**
	 * ページのサイズを設定します。
	 */
	public void setPageSize(final int x, final int y) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				mainPage.setPreferredSize(new Dimension(x, y));
			}
		});
	}

	/**
	 * このページを破棄します。 破棄する際に各シンボルが接続している、リファレンサーに対して 接続解除を実行します。
	 */
	public void destroyPage() {
		Component[] components = mainPage.getComponents();
		disConnect(components);
		mainPage.removeAll();
		removeAll();
	}

	private void disConnect(Component[] components) {
		for (int i = 0; i < components.length; i++) {
			try {
				Component component = components[i];
				// logger.info(component);
				if (component instanceof ReferencerOwnerSymbol) {
					ReferencerOwnerSymbol symbol = (ReferencerOwnerSymbol) component;
					symbol.disConnect();
				} else if (component instanceof Service) {
					Service service = (Service) component;
					service.stop();
				} else if (component instanceof JScrollPane) {
					JScrollPane sc = (JScrollPane) component;
					JViewport viewport = sc.getViewport();
					disConnect(viewport.getComponents());
				} else if (component instanceof Container) {
					Container container = (Container) component;
					disConnect(container.getComponents());
				}
			} catch (Exception e) {
				continue;
			}
		}
	}

	/**
	 * 
	 */
	private class ScrollableLabel extends JLabel implements Scrollable,
			SymbolCollection {
		private static final long serialVersionUID = -1510789158844032973L;

		/** 1スクロールの単位(ピクセル) */
		private int maxUnitIncrement = 10;

		public ScrollableLabel() {
		}

		public Dimension getPreferredScrollableViewportSize() {
			return getPreferredSize();
		}

		public int getScrollableUnitIncrement(
				Rectangle visibleRect,
				int orientation,
				int direction) {
			// Get the current position.
			int currentPosition = 0;
			if (orientation == SwingConstants.HORIZONTAL)
				currentPosition = visibleRect.x;
			else
				currentPosition = visibleRect.y;

			// Return the number of pixels between currentPosition
			// and the nearest tick mark in the indicated direction.
			if (direction < 0) {
				int newPosition = currentPosition
						- (currentPosition / maxUnitIncrement)
						* maxUnitIncrement;
				return (newPosition == 0) ? maxUnitIncrement : newPosition;
			} else {
				return ((currentPosition / maxUnitIncrement) + 1)
						* maxUnitIncrement - currentPosition;
			}
		}

		public int getScrollableBlockIncrement(
				Rectangle visibleRect,
				int orientation,
				int direction) {
			if (orientation == SwingConstants.HORIZONTAL)
				return visibleRect.width - maxUnitIncrement;
			else
				return visibleRect.height - maxUnitIncrement;
		}

		public boolean getScrollableTracksViewportWidth() {
			return false;
		}

		public boolean getScrollableTracksViewportHeight() {
			return false;
		}

		public void setMaxUnitIncrement(int pixels) {
			maxUnitIncrement = pixels;
		}

		public ListIterator listIterator(java.util.List para) {
			return new ContainerListIterator(
					this,
					(Class) para.get(0),
					(Editable) para.get(1));
		}
	}

	/**
	 * クライアントのページをキャッシュする場合は true を返します。
	 * 
	 * @return クライアントのページをキャッシュする場合は true を返します。
	 * 
	 * @since 1.1.1
	 */
	public boolean isCache() {
		return isCache;
	}

	/**
	 * クライアントのページをキャッシュする場合は true を設定します。
	 * 
	 * @param b クライアントのページをキャッシュする場合は true を設定します。
	 * 
	 * @since 1.1.1
	 */
	public void setCache(boolean b) {
		isCache = b;
	}

	public Icon getIcon() {
		return mainPage.getIcon();
	}
}
