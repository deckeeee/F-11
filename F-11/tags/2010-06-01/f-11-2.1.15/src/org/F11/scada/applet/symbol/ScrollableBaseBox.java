package org.F11.scada.applet.symbol;

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

import java.awt.Dimension;
import java.awt.Rectangle;

import javax.swing.Box;
import javax.swing.Scrollable;
import javax.swing.SwingConstants;

/**
 * スクロール可能なBoxクラスです。
 * @author Youhei Horikawa <hori@users.sourceforge.jp>
 */
public class ScrollableBaseBox extends Box implements Scrollable {

	private static final long serialVersionUID = 3731160639963913800L;

	public ScrollableBaseBox(int axis) {
		super(axis);
	}
	/** 矢印ボタンのスクロール量です。 */
	private int maxUnitIncrement = 10;

	// 以下のメソッドは Scrollable インターフェイスを実装しています。

	public Dimension getPreferredScrollableViewportSize() {
		return getPreferredSize();
	}

	public int getScrollableUnitIncrement(Rectangle visibleRect,
										  int orientation,
										  int direction) {
		//Get the current position.
		int currentPosition = 0;
		if (orientation == SwingConstants.HORIZONTAL)
			currentPosition = visibleRect.x;
		else
			currentPosition = visibleRect.y;

		//Return the number of pixels between currentPosition
		//and the nearest tick mark in the indicated direction.
		if (direction < 0) {
			int newPosition = currentPosition -
							 (currentPosition / maxUnitIncrement) *
							  maxUnitIncrement;
			return (newPosition == 0) ? maxUnitIncrement : newPosition;
		} else {
			return ((currentPosition / maxUnitIncrement) + 1) *
				   maxUnitIncrement - currentPosition;
		}
	}

	public int getScrollableBlockIncrement(Rectangle visibleRect,
										   int orientation,
										   int direction) {
		if (orientation == SwingConstants.HORIZONTAL)
			return visibleRect.width - maxUnitIncrement;
		else
			return visibleRect.height - maxUnitIncrement;
	}

	/**
	 * @return 常に false を返します。
	 */
	public boolean getScrollableTracksViewportWidth() {
		return false;
	}

	/**
	 * @return 常に false を返します。
	 */
	public boolean getScrollableTracksViewportHeight() {
		return false;
	}

	public void setMaxUnitIncrement(int pixels) {
		maxUnitIncrement = pixels;
	}
}
