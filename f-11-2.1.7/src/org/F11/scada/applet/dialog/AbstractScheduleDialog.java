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
package org.F11.scada.applet.dialog;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dialog;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.NoSuchElementException;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.BevelBorder;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;

import org.F11.scada.WifeUtilities;
import org.F11.scada.applet.schedule.GraphicScheduleViewCreator;
import org.F11.scada.applet.schedule.ScheduleModel;
import org.F11.scada.applet.symbol.ScheduleEditable;
import org.F11.scada.applet.symbol.SymbolCollection;
import org.F11.scada.applet.symbol.TenkeyEditable;
import org.F11.scada.applet.symbol.ValueSetter;
import org.apache.log4j.Logger;

/**
 * @author hori
 */
abstract public class AbstractScheduleDialog extends WifeDialog implements
		SymbolCollection, ActionListener {
	/** シンボルのイテレーター */
	private ListIterator listIterator;
	/** 編集対象シンボル */
	private ScheduleEditable symbol;
	/** グループ番号のパネルです */
	private JPanel groupNoPanel;
	/** グループ番号ボタンです */
	private JButton grupNoButton;
	/** テンキーダイアログの参照 */
	private WifeDialog tenkeyDialog;

	private final boolean isSort;
	private final boolean isLenient;
	/** ロギングクラスです */
	private final Logger logger = Logger
			.getLogger(AbstractScheduleDialog.class);

	/**
	 * コンストラクタ
	 * 
	 * @param frame 親のフレームです
	 */
	protected AbstractScheduleDialog(
			Frame frame,
			boolean isSort,
			boolean isLenient) {
		super(frame);
		this.isSort = isSort;
		this.isLenient = isLenient;
		init();
	}

	/**
	 * コンストラクタ
	 * 
	 * @param dialog 親のダイアログです
	 */
	protected AbstractScheduleDialog(
			Dialog dialog,
			boolean isSort,
			boolean isLenient) {
		super(dialog);
		this.isSort = isSort;
		this.isLenient = isLenient;
		init();
	}

	/**
	 * 初期処理です。
	 */
	private void init() {
		logger.info("init開始");

		JPanel subPanel = new JPanel(new BorderLayout());
		JPanel manipulatePanel = new JPanel(new FlowLayout());
		OkButton okButton = new OkButton(this, "OK");
		CancelButton cancelButton = new CancelButton(this, "Cancel");
		manipulatePanel.add(okButton);
		manipulatePanel.add(cancelButton);
		subPanel.add(manipulatePanel, BorderLayout.EAST);

		groupNoPanel = new JPanel(new FlowLayout());
		subPanel.add(groupNoPanel, BorderLayout.CENTER);

		getContentPane().add(subPanel, BorderLayout.SOUTH);
	}

	/**
	 * このダイアログを表示します。
	 */
	public void show() {
		logger.info("show開始");
		Rectangle dialogBounds = getBounds();
		dialogBounds.setLocation(symbol.getPoint());
		setLocation(WifeUtilities.getInScreenPoint(screenSize, dialogBounds));
		setDialogValue();
		setDefaultFocus();
		super.show();
	}

	public void dispose() {
		setDefaultFocus();
		super.dispose();
	}

	public void selectAll() {
		logger.info("selectAll開始");
	}

	/**
	 * 編集可能アナログオブジェクトを設定します。
	 */
	private void setDialogValue() {
		logger.info("setDialogValue開始");
		GraphicScheduleViewCreator view = createView(
				symbol.getScheduleModel(),
				isSort,
				isLenient);
		JComponent viewPanel = view.createView();
		getContentPane().add(viewPanel, BorderLayout.CENTER);

		grupNoButton = new GroupNoButton(this, symbol.getValue());
		groupNoPanel.add(new JLabel("GroupNo : "));
		groupNoPanel.add(grupNoButton);
	}

	abstract public GraphicScheduleViewCreator createView(
			ScheduleModel scheduleModel,
			boolean isSort,
			boolean isLenient);

	/**
	 * イテレーターをセットします
	 * 
	 * @param listIterator 編集可能シンボルのイテレーター
	 */
	public void setListIterator(ListIterator listIterator) {
		logger.info("setListIterator開始");
		this.listIterator = listIterator;
		this.symbol = (ScheduleEditable) this.listIterator.next();
	}

	/**
	 * 各ボタンの押下時の動作を処理します。
	 */
	public void actionPerformed(ActionEvent e) {
		logger.info("actionPerformed開始");
		((DialogButton) e.getSource()).pushButton();
	}

	/**
	 * コンポーネント上の時刻シンボルイテレーターを返します。
	 * 
	 * @param para 任意のパラメーター
	 */
	public ListIterator listIterator(List para) {
		logger.info("listIterator開始");
		List list = new ArrayList();
		list.add(grupNoButton);
		return new ScheduleIterator(para, list);
	}

	/**
	 * 時刻シンボルイテレータークラスです。
	 */
	private static final class ScheduleIterator implements ListIterator {
		private final Logger logger = Logger.getLogger(ScheduleIterator.class);
		/** シンボルのリストの参照です */
		private List symbols;
		/** リストイテレーターです */
		private ListIterator listIterator;
		/** 正・逆順モードの保持 */
		private boolean isPreviousMode;
		/** クリックされたボタンのインデックスです */
		private int startIndex;

		/**
		 * コンストラクタ
		 * 
		 * @param para 任意のパラメーター
		 */
		ScheduleIterator(List para, List buttonList) {
			symbols = new ArrayList(buttonList);
			startIndex = ((Integer) para.get(0)).intValue();
		}

		public boolean hasNext() {
			logger.info("hasNext開始");
			return true;
		}

		public Object next() {
			logger.info("next開始");
			if (listIterator == null)
				listIterator = symbols.listIterator(startIndex);

			if (isPreviousMode) {
				isPreviousMode = false;
				try {
					listIterator.next();
				} catch (NoSuchElementException ex) {
					listIterator = symbols.listIterator(symbols.size());
					listIterator.next();
				}
			}

			try {
				return listIterator.next();
			} catch (NoSuchElementException ex) {
				listIterator = symbols.listIterator();
				return listIterator.next();
			}
		}

		public boolean hasPrevious() {
			logger.info("hasPrevious開始");
			return true;
		}

		public Object previous() {
			logger.info("previous開始");
			if (listIterator == null)
				listIterator = symbols.listIterator(symbols.size());
			if (!isPreviousMode) {
				isPreviousMode = true;
				try {
					listIterator.previous();
				} catch (NoSuchElementException ex) {
					listIterator = symbols.listIterator(symbols.size());
					listIterator.previous();
				}
			}

			try {
				return listIterator.previous();
			} catch (NoSuchElementException ex) {
				listIterator = symbols.listIterator(symbols.size());
				return listIterator.previous();
			}
		}

		public int nextIndex() {
			logger.info("nextIndex開始");
			int index = listIterator.nextIndex();
			if (isPreviousMode && index == symbols.size()) {
				ListIterator lit = symbols.listIterator();
				index = lit.nextIndex();
			}
			return index;
		}

		public int previousIndex() {
			logger.info("previousIndex開始");
			int index = listIterator.previousIndex();
			if (!isPreviousMode && index < 0) {
				ListIterator lit = symbols.listIterator(symbols.size());
				index = lit.previousIndex();
			}
			return index;
		}

		public void add(Object o) {
			// non suport
			throw new UnsupportedOperationException();
		}

		public void remove() {
			// non suport
			throw new UnsupportedOperationException();
		}

		public void set(Object o) {
			// non suport
			throw new UnsupportedOperationException();
		}
	}

	/**
	 * ダイアログに表示するボタンの基底クラスです。
	 */
	private static abstract class DialogButton extends JButton {
		/** 親ダイアログの参照です。 */
		protected AbstractScheduleDialog dialog;

		/**
		 * テキスト表示ボタンを作成するコンストラクタです。
		 * 
		 * @param dialog 親ダイアログの参照
		 * @param text ボタンに表示するテキスト
		 */
		protected DialogButton(AbstractScheduleDialog dialog, String text) {
			super(text);
			this.dialog = dialog;
			init();
		}

		/**
		 * 各種初期処理です。
		 */
		private void init() {
			addActionListener(dialog);
		}

		/**
		 * 仮想関数です。 サブクラスで、ボタンが押下されたときの、処理を記述します。
		 */
		abstract public void pushButton();
	}

	/**
	 * OKボタンクラスです。
	 */
	private static class OkButton extends DialogButton {
		private static final long serialVersionUID = -3466462930674647017L;
		private final Logger logger = Logger.getLogger(OkButton.class);

		public OkButton(AbstractScheduleDialog dialog, String text) {
			super(dialog, text);
		}

		public void pushButton() {
			logger.info("pushButton開始");
			dialog.symbol.getScheduleModel().setValue();
			dialog.symbol.getScheduleModel().writeData();
			dialog.symbol.setValue(dialog.grupNoButton.getText());
			dialog.dispose();
		}
	}

	/**
	 * Cancelボタンクラスです。
	 */
	private static class CancelButton extends DialogButton {
		private static final long serialVersionUID = 6331220843398896457L;
		private final Logger logger = Logger.getLogger(CancelButton.class);

		public CancelButton(AbstractScheduleDialog dialog, String text) {
			super(dialog, text);
		}

		public void pushButton() {
			logger.info("pushButton開始");
			dialog.symbol.getScheduleModel().undoData();
			dialog.dispose();
		}
	}

	/**
	 * グループ番号設定用のボタンクラスです
	 */
	private final static class GroupNoButton extends DialogButton implements
			TenkeyEditable {
		private static final long serialVersionUID = -3613346010335544912L;
		private final Logger logger = Logger.getLogger(GroupNoButton.class);
		/** グループ番号 */
		private String groupNo;

		/**
		 * コンストラクタ
		 * 
		 * @param dialog スケジュール時刻設定ダイアログの参照
		 * @param time 時間
		 * @param hour 時間・分の種別
		 */
		GroupNoButton(AbstractScheduleDialog scheduleDialog, String groupNo) {
			super(scheduleDialog, groupNo);
			this.groupNo = groupNo;
			init();
		}

		/**
		 * 各初期化処理
		 */
		private void init() {
			logger.info("init開始");
			DecimalFormat fmt = new DecimalFormat(getFormatString());
			setText(fmt.format(new Integer(groupNo)));
			Border bb = BorderFactory.createBevelBorder(BevelBorder.LOWERED);
			Border eb = BorderFactory.createEmptyBorder(1, 3, 1, 3);
			setBorder(new CompoundBorder(bb, eb));
			setOpaque(true);
			setBackground(Color.WHITE);
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
			logger.info("getDialog開始");
			WifeDialog d = DialogFactory.get(window, "1");
			if (d == null)
				logger.warn(this.getClass().getName()
						+ " : scheduleDialog null");
			d.setListIterator(collection.listIterator(para));
			return d;
		}

		/**
		 * 設定ダイアログの左上の Point オブジェクトを返します。
		 */
		public Point getPoint() {
			logger.info("getPoint開始");
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
			logger.info("setPoint開始");
			// NOP
		}

		/**
		 * シンボルの値を返します
		 */
		public String getValue() {
			logger.info("getValue開始");
			return getText();
		}

		/**
		 * シンボルに値を設定します
		 */
		public void setValue(String value) {
			logger.info("setValue開始");
			setText(value);
		}

		/**
		 * 最小値を返します
		 */
		public double getConvertMin() {
			logger.info("getConvertMin開始");
			return dialog.symbol.getConvertValue().getConvertMin();
		}

		/**
		 * 最大値を返します
		 */
		public double getConvertMax() {
			logger.info("getConvertMax開始");
			return dialog.symbol.getConvertValue().getConvertMax();
		}

		/**
		 * 数値表示フォーマット文字列を返します
		 */
		public String getFormatString() {
			logger.info("getFormatString開始");
			return dialog.symbol.getConvertValue().getPattern();
		}

		/**
		 * 時刻修正ボタンが押下された時の処理を記述します。
		 */
		public void pushButton() {
			logger.info("pushButton開始");
			List para = new ArrayList();
			para.add(new Integer(0));
			if (this.dialog.tenkeyDialog != null)
				this.dialog.tenkeyDialog.dispose();
			this.dialog.tenkeyDialog = getDialog(this.dialog, this.dialog, para);
			this.dialog.tenkeyDialog.show();
			// logger.info("" + buttonList.indexOf(evt.getSource()));
		}

		public void setEditable(boolean[] editable) {
			logger.info("setEditable開始");
		}

		public boolean isEditable() {
			logger.info("isEditable開始");
			return true;
		}

		/*
		 * @see org.F11.scada.applet.symbol.Editable#getDestinations()
		 */
		public String[] getDestinations() {
			logger.info("getDestinations開始");
			return new String[0];
		}

		/**
		 * 書き込み先の追加はしない。
		 * 
		 * @see org.F11.scada.applet.symbol.Editable#addDestination(Map)
		 */
		public void addDestination(Map atts) {
			logger.info("addDestination開始");
		}

		/**
		 * 書き込み先の追加はしない。
		 * 
		 * @see org.F11.scada.applet.symbol.Editable#addElement(Map)
		 */
		public void addValueSetter(ValueSetter setter) {
			logger.info("addValueSetter開始");
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.F11.scada.applet.symbol.Editable#isTabkeyMove()
		 */
		public boolean isTabkeyMove() {
			logger.info("isTabkeyMove開始");
			return isVisible();
		}

		public String getDialogTitle() {
			logger.info("getDialogTitle開始");
			return "グループNo.";
		}
	}

}
