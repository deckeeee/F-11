/*
 * $Header: /cvsroot/f-11/F-11/src/org/F11/scada/applet/graph/bargraph/BarGraph.java,v 1.16.2.14 2007/07/31 01:58:37 frdm Exp $
 * $Revision: 1.16.2.14 $
 * $Date: 2007/07/31 01:58:37 $
 * 
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

package org.F11.scada.applet.graph.bargraph;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.text.MessageFormat;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JToolBar;
import javax.swing.ListSelectionModel;

import org.F11.scada.WifeUtilities;
import org.F11.scada.applet.ClientConfiguration;
import org.F11.scada.applet.graph.GraphModel;
import org.F11.scada.applet.graph.GraphPropertyModel;
import org.F11.scada.applet.graph.JListUtil;
import org.F11.scada.applet.graph.VerticallyScale;
import org.F11.scada.applet.symbol.GraphicManager;
import org.apache.log4j.Logger;
import org.xml.sax.SAXException;

/**
 * 棒グラフを表示するコンポーネントクラスです。
 * 
 * @author Youhei Horikawa <hori@users.sourceforge.jp>
 */
public class BarGraph extends JPanel {
	private static final long serialVersionUID = 5857677859878598549L;
	/** ロギングAPI */
	private static Logger logger;
	/** ツールバー */
	private JToolBar toolBar;
	/** メインパネル */
	private JPanel mainPanel;
	/** グラフプロパティ */
	private GraphPropertyModel graphPropertyModel;

	public BarGraph(
			GraphModel graphModel,
			GraphPropertyModel graphPropertyModel,
			String barstep,
			int axismode,
			boolean isYear) throws IOException, SAXException {
		super(new BorderLayout());

		logger = Logger.getLogger(getClass().getName());

		logger.debug("create BarGraph BarGraphPropertyModel start");
		this.graphPropertyModel = graphPropertyModel;
		logger.debug("create BarGraph BarGraphPropertyModel end");

		createToolBar();

		mainPanel = new JPanel(new BorderLayout());
		logger.debug("create BarGraph createExplanatoryNotes start");
		mainPanel.add(createExplanatoryNotes(barstep), BorderLayout.NORTH);
		logger.debug("create BarGraph createExpBarGraph lanatoryNotes end");
		logger.debug("create BarGraph createLeftVerticallyScale start");
		mainPanel.add(createLeftVerticallyScale(), BorderLayout.WEST);
		logger.debug("create BarGraph createLeftVerticallyScale end");
		logger.debug("create BarGraphView start");
		if (isYear) {
			mainPanel.add(new YearBarGraphView(
					graphModel,
					graphPropertyModel,
					barstep,
					axismode), BorderLayout.CENTER);
		} else {
			mainPanel.add(new BarGraphView(
					graphModel,
					graphPropertyModel,
					barstep,
					axismode), BorderLayout.CENTER);
		}
		logger.debug("create BarGraphView end");

		add(mainPanel, BorderLayout.CENTER);
	}

	private void createToolBar() {
		logger.debug("create BarGraph Toolbar start");
		toolBar = new JToolBar();
		toolBar.setFloatable(false);
		addButton(
				new JButton(GraphicManager
						.get("/toolbarButtonGraphics/navigation/Back24.gif")),
				GroupButtonListener.createBackListener(graphPropertyModel));
		addButton(
				new JButton(GraphicManager
						.get("/toolbarButtonGraphics/navigation/Forward24.gif")),
				GroupButtonListener.createForwardListener(graphPropertyModel));
		addButton(
				new JButton(GraphicManager.get("/images/list.png")),
				GroupButtonListener.createDialogListener(graphPropertyModel));

		toolBar.addSeparator();

		GroupLabel groupName = new GroupLabel(graphPropertyModel);
		toolBar.add(groupName);
		add(toolBar, BorderLayout.NORTH);
		logger.debug("create BarGraph Toolbar end");
	}

	/**
	 * ボタンをツールバーに追加してリスナーを登録します。
	 * 
	 * @param button ボタン
	 * @param l リスナー
	 */
	private void addButton(JButton button, ActionListener l) {
		button.addActionListener(l);
		toolBar.add(button);
	}

	private JPanel createExplanatoryNotes(String barstep) {
		JPanel panel = new JPanel(new GridLayout(graphPropertyModel
				.getSeriesSize(), 1));

		for (int i = 0; i < graphPropertyModel.getSeriesSize(); i++) {
			BarGraphStep barGraphStep = BarGraphStep.createBarGraphStep(
					barstep,
					graphPropertyModel.getHorizontalScaleWidth());
			panel.add(new BarExplanatoryNotes(
					i,
					graphPropertyModel,
					barGraphStep));
		}

		return panel;
	}

	private JPanel createLeftVerticallyScale() {
		JPanel panel = new JPanel();

		panel.add(VerticallyScale.createLeftStringScale(graphPropertyModel, 0));

		return panel;
	}

	public JComponent getMainPanel() {
		return mainPanel;
	}

	public JComponent getToolBar() {
		return toolBar;
	}

	/**
	 * グループ操作ボタンのリスナークラスです。 フォワード、バック、一覧選択の3つのアクションリスナーを生成します。 new
	 * でインスタンス生成できないので、3通りのファクトリメソッドでインタンス生成して下さい。
	 */
	private static class GroupButtonListener implements ActionListener {
		/** グラフプロパティモデル */
		private GraphPropertyModel graphPropertyModel;
		/** グループ操作オブジェクト */
		private GroupAction groupAction;

		/**
		 * プライベートコンストラクタ new でインスタンス生成できないので、3通りのファクトリメソッドでインタンス生成して下さい。
		 * 
		 * @param graphPropertyModel グラフプロパティモデル
		 * @param groupAction グループ操作オブジェクト
		 */
		private GroupButtonListener(
				GraphPropertyModel graphPropertyModel,
				GroupAction groupAction) {
			this.graphPropertyModel = graphPropertyModel;
			this.groupAction = groupAction;
		}

		/**
		 * ボタンの押下時のアクションイベント
		 * 
		 * @param evt ActionEvent
		 */
		public void actionPerformed(ActionEvent evt) {
			JComponent component = (JComponent) evt.getSource();
			groupAction.groupAction(graphPropertyModel, component);
		}

		/**
		 * グループフォワード時のアクションリスナーを生成します。
		 * 
		 * @param graphPropertyModel グラフプロパティモデル
		 * @return GroupButtonListener
		 */
		static GroupButtonListener createForwardListener(
				GraphPropertyModel graphPropertyModel) {
			return new GroupButtonListener(
					graphPropertyModel,
					GroupAction.FORWARD);
		}

		/**
		 * グループバック時のアクションリスナーを生成します。
		 * 
		 * @param graphPropertyModel グラフプロパティモデル
		 * @return GroupButtonListener
		 */
		static GroupButtonListener createBackListener(
				GraphPropertyModel graphPropertyModel) {
			return new GroupButtonListener(graphPropertyModel, GroupAction.BACK);
		}

		/**
		 * グループ一覧選択時のアクションリスナーを生成します。
		 * 
		 * @param graphPropertyModel グラフプロパティモデル
		 * @return GroupButtonListener
		 */
		static GroupButtonListener createDialogListener(
				GraphPropertyModel graphPropertyModel) {
			return new GroupButtonListener(
					graphPropertyModel,
					GroupAction.DIALOG);
		}

		/**
		 * グループアクションのタイプセーフ enum クラスです。 フォワード、バック、一覧選択をポリフォーズムで処理します。 new
		 * でインスタンス生成できません。予めインスタンス生成した静的フィールドを使用して下さい。
		 */
		private abstract static class GroupAction {
			/**
			 * プライベートコンストラクタ
			 */
			private GroupAction() {
			}

			/**
			 * グループのアクションを実際に処理する仮想メソッド。 初期化の静的フィールドインスタンス生成時に、アクションを実装します。
			 * 
			 * @param graphPropertyModel グラフプロパティモデル
			 * @param component 親コンポーネント
			 */
			abstract void groupAction(
					GraphPropertyModel graphPropertyModel,
					JComponent component);

			/** フォワードアクション */
			static GroupAction FORWARD = new GroupAction() {
				void groupAction(
						GraphPropertyModel graphPropertyModel,
						JComponent component) {
					graphPropertyModel.nextGroup();
				}
			};

			/** バックアクション */
			static GroupAction BACK = new GroupAction() {
				void groupAction(
						GraphPropertyModel graphPropertyModel,
						JComponent component) {
					// バック
					graphPropertyModel.prevGroup();
				}
			};

			/** 一覧選択アクション */
			static GroupAction DIALOG = new GroupAction() {
				void groupAction(
						GraphPropertyModel graphPropertyModel,
						JComponent component) {
					// 一覧ダイアログ
					Frame frame = WifeUtilities.getParentFrame(component);
					new GroupSelectDialog(frame, graphPropertyModel);
					logger.debug("一覧ダイアログ");
				}
			};
		}

		/**
		 * グループ選択ダイアログクラスです。
		 */
		private static class GroupSelectDialog extends JDialog {
			private static final long serialVersionUID = -3343544320301767191L;
			private GraphPropertyModel graphPropertyModel;
			private JList list;
			private JButton okButton;
			private JButton cancelButton;
			private Window window;

			GroupSelectDialog(Frame frame, GraphPropertyModel graphPropertyModel) {
				super(frame, "ポイント選択", true);
				this.graphPropertyModel = graphPropertyModel;
				this.window = frame;
				init();
			}

			private void init() {
				setDefaultCloseOperation(DISPOSE_ON_CLOSE);
				list = new JList(graphPropertyModel.getGroupNames().toArray());
				list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
				JScrollPane scpane = new JScrollPane(list);

				okButton = new JButton("OK");
				cancelButton = new JButton("CANCEL");

				okButton.addActionListener(new OkButtonListener(this));
				cancelButton.addActionListener(new CancelButtonListener(this));

				JPanel buttonPanel = new JPanel();
				buttonPanel.add(okButton);
				buttonPanel.add(cancelButton);

				Container cont = getContentPane();
				cont.add(scpane, BorderLayout.CENTER);
				cont.add(buttonPanel, BorderLayout.SOUTH);

				list.addMouseListener(new ListListener(this));

				ClientConfiguration configuration = new ClientConfiguration();
				setSize(configuration.getInt(
						"xwife.applet.Applet.trend.dialog.width",
						157), configuration.getInt(
						"xwife.applet.Applet.trend.dialog.height",
						217));

				Dimension fDimension = window.getSize();
				Dimension dDimension = getSize();
				Point location = window.getLocation();
				location.translate(
						(fDimension.width - dDimension.width) / 2,
						(fDimension.height - dDimension.height) / 2);
				setLocation(location);
				show();
			}

			private static class OkButtonListener implements ActionListener {
				private GroupSelectDialog groupSelectDialog;

				OkButtonListener(GroupSelectDialog groupSelectDialog) {
					this.groupSelectDialog = groupSelectDialog;
				}

				public void actionPerformed(ActionEvent evt) {
					int group = groupSelectDialog.list.getSelectedIndex();
					if (JListUtil.hasSelected(groupSelectDialog, group)) {
						groupSelectDialog.graphPropertyModel.setGroup(group);
						groupSelectDialog.dispose();
					}
				}
			}

			private static class CancelButtonListener implements ActionListener {
				private GroupSelectDialog groupSelectDialog;

				CancelButtonListener(GroupSelectDialog groupSelectDialog) {
					this.groupSelectDialog = groupSelectDialog;
				}

				public void actionPerformed(ActionEvent evt) {
					this.groupSelectDialog.dispose();
				}
			}

			private static class ListListener extends MouseAdapter {
				private GroupSelectDialog groupSelectDialog;

				ListListener(GroupSelectDialog groupSelectDialog) {
					this.groupSelectDialog = groupSelectDialog;
				}

				public void mouseReleased(MouseEvent e) {
					if (e.getClickCount() == 2) {
						int group = groupSelectDialog.list.locationToIndex(e
								.getPoint());
						groupSelectDialog.graphPropertyModel.setGroup(group);
						groupSelectDialog.dispose();
					}
				}
			}
		}
	}

	static class GroupLabel extends JLabel implements PropertyChangeListener {
		private static final long serialVersionUID = -5255929314423250266L;
		/** 表示するテキストのフォーマッタークラス */
		private static final MessageFormat format = new MessageFormat(
				"ポイント：{0}");

		public GroupLabel(GraphPropertyModel model) {
			super(format.format(new String[] { model.getGroupName() }));
			setFont(getFont().deriveFont((float) (getFont().getSize2D() * 1.4)));
			model.addPropertyChangeListener(
					GraphPropertyModel.GROUP_CHANGE_EVENT,
					this);
		}

		/**
		 * GraphModelPropertyの内容が変更された時に呼び出されます。
		 */
		public void propertyChange(PropertyChangeEvent evt) {
			GraphPropertyModel model = (GraphPropertyModel) evt.getSource();

			final String[] msg = new String[] { model.getGroupName() };
			setText(format.format(msg));
		}

	}
}
