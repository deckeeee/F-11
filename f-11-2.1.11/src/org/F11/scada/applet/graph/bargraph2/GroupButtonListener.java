package org.F11.scada.applet.graph.bargraph2;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Point;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JViewport;
import javax.swing.ListSelectionModel;

import org.F11.scada.WifeUtilities;
import org.F11.scada.applet.ClientConfiguration;
import org.F11.scada.applet.graph.JListUtil;

/**
 * グループ操作ボタンのリスナークラスです。 フォワード、バック、一覧選択の3つのアクションリスナーを生成します。 new
 * でインスタンス生成できないので、3通りのファクトリメソッドでインタンス生成して下さい。
 */
public class GroupButtonListener implements ActionListener {
	/** グラフプロパティモデル */
	private GroupModel groupModel;

	/** グループ操作オブジェクト */
	private GroupAction groupAction;

	/**
	 * プライベートコンストラクタ new でインスタンス生成できないので、3通りのファクトリメソッドでインタンス生成して下さい。
	 * @param graphPropertyModel グラフプロパティモデル
	 * @param groupAction グループ操作オブジェクト
	 */
	private GroupButtonListener(GroupModel groupModel, GroupAction groupAction) {
		this.groupModel = groupModel;
		this.groupAction = groupAction;
	}

	/**
	 * ボタンの押下時のアクションイベント
	 * @param evt ActionEvent
	 */
	public void actionPerformed(ActionEvent evt) {
		JComponent component = (JComponent) evt.getSource();
		groupAction.groupAction(groupModel, component);
	}

	/**
	 * グループフォワード時のアクションリスナーを生成します。
	 * @param graphPropertyModel グラフプロパティモデル
	 * @return GroupButtonListener
	 */
	public static GroupButtonListener createForwardListener(
			GroupModel groupModel) {
		return new GroupButtonListener(groupModel, GroupAction.FORWARD);
	}

	/**
	 * グループバック時のアクションリスナーを生成します。
	 * @param graphPropertyModel グラフプロパティモデル
	 * @return GroupButtonListener
	 */
	public static GroupButtonListener createBackListener(GroupModel groupModel) {
		return new GroupButtonListener(groupModel, GroupAction.BACK);
	}

	/**
	 * グループ一覧選択時のアクションリスナーを生成します。
	 * @param graphPropertyModel グラフプロパティモデル
	 * @return GroupButtonListener
	 */
	public static GroupButtonListener createDialogListener(GroupModel groupModel) {
		return new GroupButtonListener(groupModel, GroupAction.DIALOG);
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
		 * @param graphPropertyModel グラフプロパティモデル
		 * @param component 親コンポーネント
		 */
		abstract void groupAction(GroupModel groupModel, JComponent component);

		/** フォワードアクション */
		static GroupAction FORWARD = new GroupAction() {
			void groupAction(GroupModel groupModel, JComponent component) {
				groupModel.nextGroup();
			}
		};

		/** バックアクション */
		static GroupAction BACK = new GroupAction() {
			void groupAction(GroupModel groupModel, JComponent component) {
				// バック
				groupModel.prevGroup();
			}
		};

		/** 一覧選択アクション */
		static GroupAction DIALOG = new GroupAction() {
			void groupAction(GroupModel groupModel, JComponent component) {
				// 一覧ダイアログ
				Frame frame = WifeUtilities.getParentFrame(component);
				new GroupSelectDialog(frame, groupModel);
			}
		};
	}

	/**
	 * グループ選択ダイアログクラスです。
	 */
	private static class GroupSelectDialog extends JDialog {
		private static final long serialVersionUID = -2839062020881087390L;

		private GroupModel groupModel;

		private JList list;

		private JButton okButton;

		private JButton cancelButton;

		private Window window;

		GroupSelectDialog(Frame frame, GroupModel groupModel) {
			super(frame, "グループ選択", true);
			this.groupModel = groupModel;
			this.window = frame;
			init();
		}

		private void init() {
			setDefaultCloseOperation(DISPOSE_ON_CLOSE);
			list = new JList(groupModel.getGroupNames().toArray());
			list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
			list.setSelectedIndex(groupModel.getGroup());
			JScrollPane scpane = new JScrollPane(list);
			JViewport p = scpane.getViewport();
			p.setViewPosition(new Point(0, groupModel.getGroup()
					* scpane.getVerticalScrollBar().getBlockIncrement(1)));
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

			setSize();

			Dimension fDimension = window.getSize();
			Dimension dDimension = getSize();
			Point location = window.getLocation();
			location.translate((fDimension.width - dDimension.width) / 2,
					(fDimension.height - dDimension.height) / 2);
			setLocation(location);
			setVisible(true);
		}

		private void setSize() {
			ClientConfiguration configuration = new ClientConfiguration();
			setSize(configuration.getInt(
					"xwife.applet.Applet.trend.dialog.width", 157),
					configuration.getInt(
							"xwife.applet.Applet.trend.dialog.height", 217));
		}

		private static class OkButtonListener implements ActionListener {
			private GroupSelectDialog groupSelectDialog;

			OkButtonListener(GroupSelectDialog groupSelectDialog) {
				this.groupSelectDialog = groupSelectDialog;
			}

			public void actionPerformed(ActionEvent evt) {
				int group = groupSelectDialog.list.getSelectedIndex();
				if (JListUtil.hasSelected(groupSelectDialog, group)) {
					groupSelectDialog.groupModel.setGroup(group);
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
					int group = groupSelectDialog.list.locationToIndex(e.getPoint());
					groupSelectDialog.groupModel.setGroup(group);
					groupSelectDialog.dispose();
				}
			}
		}
	}
}
