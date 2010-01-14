/*
 * Projrct F-11 - Web SCADA for Java Copyright (C) 2002 Freedom, Inc. All Rights
 * Reserved. This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or (at your
 * option) any later version. This program is distributed in the hope that it
 * will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty
 * of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General
 * Public License for more details. You should have received a copy of the GNU
 * General Public License along with this program; if not, write to the Free
 * Software Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA
 * 02111-1307, USA.
 */
package org.F11.scada.applet.graph.bargraph2;

import java.awt.event.ActionListener;
import java.rmi.RemoteException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JToolBar;

import org.F11.scada.applet.symbol.GraphicManager;
import org.apache.log4j.Logger;

public class BarGraph2 implements GroupModel {
	private static final long serialVersionUID = 8988896279455190803L;
	/** ロギングAPI */
	private static Logger logger = Logger.getLogger(BarGraph2.class);
	/** 表示するテキストのフォーマッタークラス */
	private static final MessageFormat format = new MessageFormat("ポイント：{0}");
	/** メインパネル */
	private JPanel mainPanel;
	/** ツールバー */
	private JToolBar toolBar;
	/** ツールバー グループ名 */
	private JLabel groupName;
	/** コンボボックス */
	private ModelSelectComboBox comboBox;
	/** グループのリスト */
	private List<BarSeries> group = new ArrayList<BarSeries>();
	/** 参照グループindex */
	private int groupIndex;

	public BarGraph2() {
		logger.debug("create BarGraph2");

		createToolBar();

		mainPanel = new JPanel();
		mainPanel.setLayout(null);
	}

	private void createToolBar() {
		toolBar = new JToolBar();
		toolBar.setFloatable(false);
		addButton(
				new JButton(
						GraphicManager.get("/toolbarButtonGraphics/navigation/Back24.gif")),
				GroupButtonListener.createBackListener(this));
		addButton(
				new JButton(
						GraphicManager.get("/toolbarButtonGraphics/navigation/Forward24.gif")),
				GroupButtonListener.createForwardListener(this));
		addButton(new JButton(GraphicManager.get("/images/list.png")),
				GroupButtonListener.createDialogListener(this));

		toolBar.addSeparator();

		groupName = new JLabel();
		toolBar.add(groupName);
	}

	/*
	 * ボタンをツールバーに追加してリスナーを登録します。 @param button ボタン @param l リスナー
	 */
	private void addButton(JButton button, ActionListener l) {
		button.addActionListener(l);
		toolBar.add(button);
	}

	public JComponent getMainPanel() {
		return mainPanel;
	}

	public JComponent getToolBar() {
		return toolBar;
	}

	public void setModelSelecter(ModelSelectComboBox cb) {
		this.comboBox = cb;
		mainPanel.add(comboBox);
		comboBox.setBounds(comboBox.getBounds());
		comboBox.addItemListener(comboBox);

		for (int i = 0; i < comboBox.getItemCount(); i++) {
			BarGraphModel model = (BarGraphModel) comboBox.getItemAt(i);
			mainPanel.add(model.getJComponent());
			model.getJComponent().setBounds(0, 0, mainPanel.getWidth(),
					mainPanel.getHeight());
		}
	}

	public void addBarSeries(BarSeries series) {
		group.add(series);
	}

	public Collection<String> getGroupNames() {
		Collection<String> ret = new ArrayList<String>();
		for (BarSeries series : group) {
			ret.add(series.getName());
		}
		return ret;
	}

	public int getGroup() {
		return groupIndex;
	}

	public void nextGroup() {
		groupIndex++;
		if (group.size() <= groupIndex)
			groupIndex = group.size() - 1;
		fireChangeGroup();
	}

	public void prevGroup() {
		groupIndex--;
		if (groupIndex < 0)
			groupIndex = 0;
		fireChangeGroup();
	}

	public void setGroup(int index) {
		groupIndex = index;
		fireChangeGroup();
	}

	private void fireChangeGroup() {
		BarSeries series = group.get(groupIndex);
		try {
			comboBox.fireChangeGroup(series);
			final String[] msg = new String[]{series.getName()};
			groupName.setText(format.format(msg));
		} catch (RemoteException ex) {
			logger.error("change group [" + series.getName() + "] error!", ex);
		}
	}
}
