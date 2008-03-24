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

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import javax.swing.Icon;

/**
 * イメージグラフィックをアニメーション表示するシンボルクラスです。
 * @author Youhei Horikawa <hori@users.sourceforge.jp>
 */
public class ImageAnimeSymbol extends ImageSymbol {
	private static final long serialVersionUID = -6614962176890988290L;
	/** アニメーション用のタイマーです */
	private AnimeTimer aniTimer = AnimeTimer.getInstance();
	/** アニメーションするIconのセットです。 */
	private List icons = new ArrayList();
	/** アニメーション実行フラグです。 */
	private boolean isMove;
	
	private final ActionListener listener = new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			if (isVisible() && 0 < icons.size()) {
				if (isMove) {
					Icon icon =
						(Icon) icons.get(
							aniTimer.getAnimeCount(icons.size()));
					setIcon(icon);
				} else {
					Icon icon = (Icon) icons.get(0);
					setIcon(icon);
				}
			}
		}
	};

	/**
	 * Constructor for ImageAnimeSymbol.
	 * @see org.F11.scada.applet.symbol.ImageSymbol#ImageSymbol()
	 * @param property SymbolProperty オブジェクト
	 */
	public ImageAnimeSymbol(SymbolProperty property) {
		super(property);
		init();
	}

	/**
	 * Constructor for ImageAnimeSymbol.
	 * @see org.F11.scada.applet.symbol.ImageSymbol#ImageSymbol()
	 */
	public ImageAnimeSymbol() {
		this(null);
	}

	private void init() {
		String imagePattern = getProperty("value");
		if (imagePattern != null) {
			setIcons(imagePattern);
		}

		aniTimer.addActionListener(listener);
	}

	/**
	 *
	 */
	private void setIcons(String imagePattern) {
		DecimalFormat format = new DecimalFormat(imagePattern);
		icons.clear();
		Icon icon = null;
		for (int i = 1;; i++) {
			if (GraphicManager.get(format.format(i)) == null)
				break;
			icon = GraphicManager.get(format.format(i));
			icons.add(icon);
		}
		if (icon != null) {
			this.setIcon(icon);
			this.setSize(icon.getIconWidth(), icon.getIconHeight());
		}
	}

	/**
	 * プロパティを変更します。
	 */
	protected void updatePropertyImpl() {
		super.updatePropertyImpl();

		/** trueデフォルト */
		if ("false".equals(getProperty("move")))
			isMove = false;
		else
			isMove = true;
	}
	
    public void disConnect() {
    	icons.clear();
        aniTimer.removeActionListener(listener);
        super.disConnect();
    }
}
