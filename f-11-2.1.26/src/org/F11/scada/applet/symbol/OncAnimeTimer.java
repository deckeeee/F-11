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

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Timer;

/**
 * アニメーション表示のタイマーオブジェクトです。
 * @author Youhei Horikawa <hori@users.sourceforge.jp>
 */
public class OncAnimeTimer extends Timer implements ActionListener {
	private static final long serialVersionUID = -4749840783967552311L;
	/** 唯一のインスタンス。シングルトンパターン */
	private static OncAnimeTimer singleton = new OncAnimeTimer();
	/** デフォルトのイベント間隔時間 */
	public static final int INITIAL_DELAY = 200;
	/** アニメーションカウンタ */
	private int animeCount;

	/**
	 * コンストラクタ
	 */
	private OncAnimeTimer() {
		super(INITIAL_DELAY, new ActionListener() {
			public void actionPerformed(ActionEvent e) {
			}
		});
		/** 自分自身を登録します。 */
		addActionListener(this);
		start();
	}

	/**
	 * インスタンスを返します。
	 */
	public static OncAnimeTimer getInstance() {
		return singleton;
	}

	/**
	 * アニメーションカウンタ取得
	 * @para maxIconCount アニメーションするIconの最大数
	 */
	public int getAnimeCount(int maxIconCount) {
		if (maxIconCount <= 0)
			return 0;
		return animeCount % maxIconCount;
	}

	/**
	 * タイマーイベント処理。
	 */
	public void actionPerformed(ActionEvent e) {
		if (animeCount < 362880)
			animeCount++;
		else
			animeCount = 0;
	}
}
