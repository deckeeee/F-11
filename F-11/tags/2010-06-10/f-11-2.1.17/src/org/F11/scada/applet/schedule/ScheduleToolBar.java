package org.F11.scada.applet.schedule;

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
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JToolBar;
import javax.swing.SwingUtilities;

import org.F11.scada.WifeUtilities;
import org.F11.scada.applet.dialog.ScheduleGroupListDialog;
import org.F11.scada.applet.schedule.point.SchedulePointFinder;
import org.F11.scada.applet.symbol.GraphicManager;
import org.F11.scada.util.ComponentUtil;
import org.F11.scada.xwife.applet.AbstractWifeApplet;
import org.apache.log4j.Logger;

/**
 * スケジュール登録・取消のボタンを保持するツールバーです。
 */
public class ScheduleToolBar extends JToolBar implements ActionListener,
		PropertyChangeListener {
	private static final long serialVersionUID = 371500372873608373L;
	/** ツールバーのデータモデルです */
	private ScheduleModel scheduleModel;
	/** 次グループボタン */
	private JButton nextGroupButton;
	/** 前グループボタン */
	private JButton previousGroupButton;
	/** 一覧表示ボタン */
	private JButton listButton;
	/** グループNo ラベル */
	private JLabel groupNoLabel;
	/** 今日・明日表示の有無 */
	private final boolean isNonTandT;
	/** ページID */
	private final String pageId;

	private static Logger logger;

	/**
	 * コンストラクタ
	 * 
	 * @param pageId
	 * 
	 * @aparam scheduleModel ツールバーのデータモデル
	 */
	public ScheduleToolBar(
			ScheduleModel scheduleModel,
			boolean isNonTandT,
			String pageId) {
		this.scheduleModel = scheduleModel;
		this.isNonTandT = isNonTandT;
		this.pageId = pageId;
		logger = Logger.getLogger(getClass().getName());
		init();
	}

	/**
	 * 初期処理です。
	 */
	private void init() {
		nextGroupButton =
			new JButton(GraphicManager
				.get("/toolbarButtonGraphics/navigation/Forward24.gif"));
		previousGroupButton =
			new JButton(GraphicManager
				.get("/toolbarButtonGraphics/navigation/Back24.gif"));
		listButton = new JButton(GraphicManager.get("/images/list.png"));
		listButton.setPreferredSize(new Dimension(36, 36));
		listButton.setMinimumSize(listButton.getPreferredSize());
		listButton.setMaximumSize(listButton.getPreferredSize());
		groupNoLabel = new JLabel(getDisplayGroupNo());
		WifeUtilities.setFontSize(groupNoLabel, 1.4);

		listButton.addActionListener(this);
		nextGroupButton.addActionListener(this);
		previousGroupButton.addActionListener(this);

		listButton.setToolTipText("グループ一覧ダイアログを表示します。");
		nextGroupButton.setToolTipText("次のグループを表示します");
		previousGroupButton.setToolTipText("前のグループを表示します");

		JButton groupDuplicate =
			new JButton(GraphicManager.get("/images/gdup.png"));
		groupDuplicate.setToolTipText("グループ複写");
		groupDuplicate.setPreferredSize(new Dimension(36, 36));
		groupDuplicate.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (scheduleModel.isEditable()) {
					GroupDuplicateDialog dialog =
						new GroupDuplicateDialog((Frame) SwingUtilities
							.getAncestorOfClass(
								Frame.class,
								ScheduleToolBar.this), scheduleModel);
					dialog.show();
				}
			}
		});

		JButton weekOfDayDuplicate =
			new JButton(GraphicManager.get("/images/wdup.png"));
		weekOfDayDuplicate.setToolTipText("曜日間複写");
		weekOfDayDuplicate.setPreferredSize(new Dimension(36, 36));
		weekOfDayDuplicate.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (scheduleModel.isEditable()) {
					WeekOfDayDuplicateDialog dialog =
						new WeekOfDayDuplicateDialog(
							(Frame) SwingUtilities.getAncestorOfClass(
								Frame.class,
								ScheduleToolBar.this),
							scheduleModel,
							isNonTandT);
					dialog.show();
				}
			}
		});

		JButton kikiTable = new JButton(GraphicManager.get("/images/kiki.png"));
		kikiTable.setToolTipText("スケジュール機器操作");
		kikiTable.setPreferredSize(new Dimension(36, 36));
		kikiTable.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (scheduleModel.isEditable()) {
					AbstractWifeApplet wifeApplet = ComponentUtil.getAncestorOfClass(
						AbstractWifeApplet.class,
						ScheduleToolBar.this);
					new SchedulePointFinder(
						(Frame) SwingUtilities.getAncestorOfClass(
							Frame.class,
							ScheduleToolBar.this),
						pageId,
						wifeApplet);
				}
			}
		});

		add(previousGroupButton);
		add(nextGroupButton);
		add(listButton);
		addSeparator();
		add(groupDuplicate);
		add(weekOfDayDuplicate);
		if (WifeUtilities.isSchedulePoint()) {
			add(kikiTable);
		}
		addSeparator();
		add(groupNoLabel);

		setFloatable(false);

		scheduleModel.addPropertyChangeListener(this);
	}

	/**
	 * ボタンのアクションイベントを処理します。
	 */
	public void actionPerformed(ActionEvent evt) {
		JComponent comp = (JComponent) evt.getSource();
		Frame frame = WifeUtilities.getParentFrame(comp);
		if (comp == listButton) {
			JDialog dialog = new ScheduleGroupListDialog(frame, scheduleModel);
			dialog.show();
		} else if (comp == nextGroupButton) {
			if (scheduleModel.getGroupNo() < scheduleModel.getGroupNoMax() - 1) {
				scheduleModel.setGroupNo(scheduleModel.getGroupNo() + 1);
			}
		} else if (comp == previousGroupButton) {
			if (scheduleModel.getGroupNo() > 0) {
				scheduleModel.setGroupNo(scheduleModel.getGroupNo() - 1);
			}
		} else {
			logger.warn("イベントが変です。");
		}
	}

	/**
	 * スケジュールデータモデルの、バウンズプロパティ変更イベントを処理します。 グループNo の変更を処理。
	 */
	public void propertyChange(PropertyChangeEvent evt) {
		logger.debug("スケジュールデータモデル変更");
		groupNoLabel.setText(getDisplayGroupNo());
		groupNoLabel.revalidate();
	}

	/**
	 * 画面に表示するグループNOを返します。
	 * 
	 * @return
	 */
	private String getDisplayGroupNo() {
		return (scheduleModel.getGroupNo() + 1)
			+ " : "
			+ scheduleModel.getGroupName();
	}
}
