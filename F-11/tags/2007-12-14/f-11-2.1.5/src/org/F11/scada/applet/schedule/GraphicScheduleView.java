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

package org.F11.scada.applet.schedule;

import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.border.BevelBorder;
import javax.swing.border.Border;

import org.F11.scada.WifeUtilities;
import org.F11.scada.applet.dialog.schedule.ScheduleDialogFactory;
import org.F11.scada.applet.symbol.HandCursorListener;
import org.F11.scada.applet.symbol.ReferencerOwnerSymbol;
import org.F11.scada.applet.symbol.ScrollableBaseBox;
import org.apache.log4j.Logger;

/**
 * グラフィック画面式のスケジュールビュークラスです。
 * 
 * @todo bar と button の配列が2つあるのは、要リファクタリング項目。ついでにTABキー移動も組み込めるように、Editable等組み込む。
 */
public class GraphicScheduleView implements ActionListener,
		PropertyChangeListener {
	/** スケジュールモデルの参照です */
	private final ScheduleModel scheduleModel;
	/** グラフィックバーのリストです */
	private final JComponent[] bars;
	/** 時刻変更ボタンのリストです */
	private final MatrixBarButton[] buttons;
	/** 時刻変更用ダイアログの参照です */
	private JDialog dialog;
	/** ロギングオブジェクト */
	private static Logger logger;
	/** バーコンポーネントのファクトリー * */
	private final BarMatrixFactory factory;
	/** スケジュールダイアログのファクトリー */
	private final ScheduleDialogFactory dialogFactory;
	/** 今日・明日表示の有無 */
	private final boolean isNonTandT;
	/** ページID */
	private final String pageId;

	/**
	 * コンストラクタ
	 * 
	 * @param alarmRef リモートデータオブジェクト
	 */
	public GraphicScheduleView(
			ScheduleModel scheduleModel,
			BarMatrixFactory factory,
			ScheduleDialogFactory dialogFactory,
			boolean isNonTandT,
			String pageId) {
		logger = Logger.getLogger(getClass().getName());
		this.scheduleModel = scheduleModel;
		this.factory = factory;
		bars = new JComponent[scheduleModel.getPatternSize()];
		buttons = new MatrixBarButton[scheduleModel.getPatternSize()];
		this.scheduleModel.addPropertyChangeListener(this);
		this.dialogFactory = dialogFactory;
		this.isNonTandT = isNonTandT;
		this.pageId = pageId;
	}

	/**
	 * グラフィック画面式のスケジュールビューをスクロールペインにのせて返します。
	 * 
	 * @return スクロールペイン
	 */
	public JComponent createView() {
		return new JScrollPane(createPanel());
	}

	/**
	 * 登録・取消のツールバーコンポーネントを返します。
	 * 
	 * @return ツールバー
	 */
	public JComponent createToolBar() {
		return new ScheduleToolBar(scheduleModel, isNonTandT, pageId);
	}

	private JComponent createPanel() {
		createRowComponent();
		ScheduleBox mainBox = new ScheduleBox(
				BoxLayout.Y_AXIS,
				scheduleModel,
				buttons);
		if (!isNonTandT) {
			mainBox.add(createRowPanel(0, scheduleModel.getTopSize() - 1));
		}
		mainBox.add(createRowPanel(scheduleModel.getTopSize(), scheduleModel
				.getTopSize() + 6));
		mainBox.add(createRowPanel(
				scheduleModel.getTopSize() + 7,
				scheduleModel.getPatternSize() - 1));
		return mainBox;
	}

	private void createRowComponent() {
		for (int i = 0; i < scheduleModel.getTopSize() + 8; i++) {
			ScheduleRowModel scheduleRowModel = getScheduleRowModel(i);
			bars[scheduleModel.getDayIndex(i)] = factory
					.getBarMatrix(scheduleRowModel);
			MatrixBarButton button = new MatrixBarButton(scheduleRowModel);
			buttons[scheduleModel.getDayIndex(i)] = button;
		}

		for (int i = 0; i < scheduleModel.getPatternSize()
				- scheduleModel.getSpecialDayOfIndex(0); i++) {
			ScheduleRowModel scheduleRowModel = getScheduleRowModel(scheduleModel
					.getSpecialDayOfIndex(i));
			bars[scheduleModel.getSpecialDayOfIndex(i)] = factory
					.getBarMatrix(scheduleRowModel);
			MatrixBarButton button = new MatrixBarButton(scheduleRowModel);
			buttons[scheduleModel.getSpecialDayOfIndex(i)] = button;
		}

		for (int i = 0; i < buttons.length; i++) {
			if (buttons[i] == null) {
				logger.warn("button is null : index = " + i);
			} else {
				MatrixBarButton b = buttons[i];
				b.addActionListener(this);
				b.setPreferredSize(new Dimension(115, BarMatrix.BAR_HEIGHT));
			}
		}
	}

	private JComponent createRowPanel(int start, int end) {
		JPanel panel = new JPanel(new GridBagLayout());

		Border bevel = BorderFactory.createBevelBorder(BevelBorder.LOWERED);
		Border empty = BorderFactory.createEmptyBorder(5, 10, 15, 10);
		Border compound = BorderFactory.createCompoundBorder(bevel, empty);
		panel.setBorder(compound);

		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.BOTH;
		c.weightx = 1.0;
		c.weighty = 1.0;
		c.gridwidth = 1;
		c.gridheight = 1;

		GridBagConstraints bc = (GridBagConstraints) c.clone();
		bc.insets = new Insets(
				BarMatrix.SCALE_HEIGHT - 3,
				0,
				BarMatrix.MARGIN_BOTTOM,
				0);

		for (int i = start; i <= end; i++) {
			bc.gridx = 0;
			bc.gridy = i;
			panel.add(buttons[i], bc);

			c.gridx = 1;
			c.gridy = i;
			panel.add(bars[i], c);
		}

		return panel;
	}

	/**
	 * 引数の曜日で ScheduleRowModel を返します。
	 * 
	 * @param selectRow 曜日の項目パターンインデックス
	 * @return 引数の項目で指定された ScheduleRowModel オブジェクト。
	 */
	public ScheduleRowModel getScheduleRowModel(int selectRow) {
		return scheduleModel.getScheduleRowModel(selectRow);
	}

	/**
	 * 時刻変更ボタンのアクションイベントを処理します。
	 */
	public void actionPerformed(ActionEvent evt) {
		if (scheduleModel.isEditable()) {
			MatrixBarButton matrixBarButton = (MatrixBarButton) evt.getSource();
			Frame frame = WifeUtilities.getParentFrame(matrixBarButton);
			Dimension screenSize = frame.getToolkit().getScreenSize();

			if (dialog != null) {
				dialog.dispose();
			}

			dialog = dialogFactory.getScheduleDialog(frame, matrixBarButton
					.getScheduleRowModel());
			dialog.pack();

			Rectangle dialogBounds = dialog.getBounds();

			dialogBounds.setLocation(matrixBarButton.getLocationOnScreen());
			dialogBounds.x += matrixBarButton.getWidth();
			dialogBounds.y += matrixBarButton.getHeight();
			dialog.setLocation(WifeUtilities.getInScreenPoint(
					screenSize,
					dialogBounds));
			dialog.show();
		}
	}

	/** スケジュールモデルのバウンズプロパティを処理します */
	public void propertyChange(PropertyChangeEvent evt) {
		for (int i = 0; i < bars.length; i++) {
			bars[i].repaint();
		}
	}

	private static class MatrixBarButton extends JButton implements
			ActionListener {
		private static final long serialVersionUID = -4755292268789791509L;
		private ScheduleRowModel model;
		private final Timer timer;

		MatrixBarButton(ScheduleRowModel model) {
			super(model.getDayIndexName());
			this.model = model;
			addMouseListener(new HandCursorListener());
			timer = new Timer(1000, this);
			timer.start();
		}

		ScheduleRowModel getScheduleRowModel() {
			return model;
		}

		//日付変更時にボタンのテキストを変更する為、タイマーにて毎秒書き換える。
		public void actionPerformed(ActionEvent e) {
			if (SwingUtilities.isEventDispatchThread()) {
				setText(model.getDayIndexName());
			} else {
				SwingUtilities.invokeLater(new Runnable() {
					public void run() {
						setText(model.getDayIndexName());
					}
				});
			}
		}

		public void removeActionListener() {
			timer.removeActionListener(this);
			timer.stop();
		}
	}

	private static class ScheduleBox extends ScrollableBaseBox implements
			ReferencerOwnerSymbol {
		private static final long serialVersionUID = 5577472433191166580L;
		private final ScheduleModel scheduleModel;
		private final MatrixBarButton[] buttons;

		ScheduleBox(
				int i,
				ScheduleModel scheduleModel,
				MatrixBarButton[] buttons) {
			super(i);
			this.scheduleModel = scheduleModel;
			this.buttons = buttons;
		}

		public void disConnect() {
			scheduleModel.disConnect();
			for (int i = 0; i < buttons.length; i++) {
				buttons[i].removeActionListener();
			}
		}
	}
}
