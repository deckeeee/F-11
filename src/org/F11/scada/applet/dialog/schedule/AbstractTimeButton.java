/*
 * =============================================================================
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

package org.F11.scada.applet.dialog.schedule;

import java.awt.Color;
import java.awt.Point;
import java.awt.Window;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.border.BevelBorder;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;

import org.F11.scada.applet.dialog.DialogFactory;
import org.F11.scada.applet.dialog.WifeDialog;
import org.F11.scada.applet.symbol.HandCursorListener;
import org.F11.scada.applet.symbol.SymbolCollection;
import org.F11.scada.applet.symbol.TenkeyEditable;
import org.F11.scada.applet.symbol.ValueSetter;
import org.F11.scada.xwife.applet.PageChanger;
import org.apache.log4j.Logger;

/**
 * 時刻設定用のボタンクラスです
 */
abstract class AbstractTimeButton extends AbstractScheduleButton implements
		TenkeyEditable {
	private static final long serialVersionUID = -7092769000804776115L;
	protected static Logger logger = Logger.getLogger(AbstractTimeButton.class);
	/** 時間 */
	protected int time;
	/** 時間・分の種別 */
	protected boolean hour;

	private final PageChanger changer;

	/**
	 * コンストラクタ
	 * 
	 * @param dialog スケジュール時刻設定ダイアログの参照
	 * @param time 時間
	 * @param hour 時間・分の種別
	 */
	AbstractTimeButton(
			AbstractScheduleDialog scheduleDialog,
			int time,
			boolean hour,
			PageChanger changer) {
		super(scheduleDialog);
		this.time = time;
		this.hour = hour;
		this.changer = changer;
		init();
		addMouseListener(new HandCursorListener());
	}

	/**
	 * 各初期化処理
	 */
	private void init() {
		DecimalFormat fmt = new DecimalFormat(getFormatString());
		if (hour) {
			setText(fmt.format((time / 100)));
		} else {
			setText(fmt.format((time % 100)));
		}
		Border bb = BorderFactory.createBevelBorder(BevelBorder.LOWERED);
		Border eb = BorderFactory.createEmptyBorder(1, 3, 1, 3);
		setBorder(new CompoundBorder(bb, eb));
		setOpaque(true);
		setBackground(Color.white);
		addActionListener(this.scheduleDialog);
	}

	/**
	 * 編集する為のダイアログを返します。
	 * 
	 * @param window 親ウィンドウ
	 * @param collection ベースクラスのインスタンス
	 * @param 任意のパラメータリスト
	 * @todo 任意のパラメータはもう少し、型を強制するべきかも。
	 */
	public WifeDialog getDialog(
			Window window,
			SymbolCollection collection,
			List para) {
		WifeDialog d = DialogFactory.get(window, "1", changer);
		if (d == null)
			logger.warn(this.getClass().getName() + " : scheduleDialog null");
		d.setListIterator(collection.listIterator(para));
		return d;
	}

	/**
	 * 設定ダイアログの左上の Point オブジェクトを返します。
	 */
	public Point getPoint() {
		Point p = this.getLocationOnScreen();
		p.y += getSize().height;
		return p;
	}

	/**
	 * 設定ダイアログの左上の Point オブジェクトを設定します。
	 * 
	 * @param point 設定ダイアログの左上の Point
	 */
	public void setPoint(Point point) {
		// NOP
	}

	/**
	 * シンボルの値を返します
	 */
	public String getValue() {
		return getText();
	}

	/**
	 * シンボルに値を設定します
	 */
	public void setValue(String value) {
		setText(value);
	}

	/**
	 * 最小値を返します
	 */
	public double getConvertMin() {
		return 0;
	}

	/**
	 * 数値表示フォーマット文字列を返します
	 */
	public String getFormatString() {
		return "00";
	}

	/**
	 * 時刻修正ボタンが押下された時の処理を記述します。
	 */
	public void pushButton() {
		List para = new ArrayList();
		para.add(new Integer(this.scheduleDialog.buttonList.indexOf(this)));
		if (this.scheduleDialog.tenkeyDialog != null)
			this.scheduleDialog.tenkeyDialog.dispose();
		this.scheduleDialog.tenkeyDialog =
			getDialog(this.scheduleDialog, this.scheduleDialog, para);
		// this.scheduleDialog.tenkeyDialog.selectAll();
		this.scheduleDialog.tenkeyDialog.show();
	}

	public void setEditable(boolean[] editable) {
	}

	public boolean isEditable() {
		return true;
	}

	/*
	 * @see org.F11.scada.applet.symbol.Editable#getDestinations()
	 */
	public String[] getDestinations() {
		return new String[0];
	}

	/**
	 * 書き込み先の追加はしない。
	 * 
	 * @see org.F11.scada.applet.symbol.Editable#addDestination(Map)
	 */
	public void addDestination(Map atts) {
	}

	/**
	 * 書き込み先の追加はしない。
	 * 
	 * @see org.F11.scada.applet.symbol.Editable#addElement(Map)
	 */
	public void addValueSetter(ValueSetter setter) {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.F11.scada.applet.symbol.Editable#isTabkeyMove()
	 */
	public boolean isTabkeyMove() {
		return isVisible();
	}

	public String getDialogTitle() {
		return hour ? "時" : "分";
	}
}
