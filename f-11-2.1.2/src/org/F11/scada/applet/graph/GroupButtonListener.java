package org.F11.scada.applet.graph;

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
import javax.swing.ListSelectionModel;

import org.F11.scada.WifeUtilities;
import org.F11.scada.applet.ClientConfiguration;

/**
 * �O���[�v����{�^���̃��X�i�[�N���X�ł��B �t�H���[�h�A�o�b�N�A�ꗗ�I����3�̃A�N�V�������X�i�[�𐶐����܂��B new
 * �ŃC���X�^���X�����ł��Ȃ��̂ŁA3�ʂ�̃t�@�N�g�����\�b�h�ŃC���^���X�������ĉ������B
 */
class GroupButtonListener implements ActionListener {
	/** �O���t�v���p�e�B���f�� */
	private GraphPropertyModel graphPropertyModel;

	/** �O���[�v����I�u�W�F�N�g */
	private GroupAction groupAction;

	/**
	 * �v���C�x�[�g�R���X�g���N�^ new �ŃC���X�^���X�����ł��Ȃ��̂ŁA3�ʂ�̃t�@�N�g�����\�b�h�ŃC���^���X�������ĉ������B
	 * 
	 * @param graphPropertyModel �O���t�v���p�e�B���f��
	 * @param groupAction �O���[�v����I�u�W�F�N�g
	 */
	private GroupButtonListener(
			GraphPropertyModel graphPropertyModel,
			GroupAction groupAction) {
		this.graphPropertyModel = graphPropertyModel;
		this.groupAction = groupAction;
	}

	/**
	 * �{�^���̉������̃A�N�V�����C�x���g
	 * 
	 * @param evt ActionEvent
	 */
	public void actionPerformed(ActionEvent evt) {
		JComponent component = (JComponent) evt.getSource();
		groupAction.groupAction(graphPropertyModel, component);
	}

	/**
	 * �O���[�v�t�H���[�h���̃A�N�V�������X�i�[�𐶐����܂��B
	 * 
	 * @param graphPropertyModel �O���t�v���p�e�B���f��
	 * @return GroupButtonListener
	 */
	static GroupButtonListener createForwardListener(
			GraphPropertyModel graphPropertyModel) {
		return new GroupButtonListener(graphPropertyModel, GroupAction.FORWARD);
	}

	/**
	 * �O���[�v�o�b�N���̃A�N�V�������X�i�[�𐶐����܂��B
	 * 
	 * @param graphPropertyModel �O���t�v���p�e�B���f��
	 * @return GroupButtonListener
	 */
	static GroupButtonListener createBackListener(
			GraphPropertyModel graphPropertyModel) {
		return new GroupButtonListener(graphPropertyModel, GroupAction.BACK);
	}

	/**
	 * �O���[�v�ꗗ�I�����̃A�N�V�������X�i�[�𐶐����܂��B
	 * 
	 * @param graphPropertyModel �O���t�v���p�e�B���f��
	 * @return GroupButtonListener
	 */
	static GroupButtonListener createDialogListener(
			GraphPropertyModel graphPropertyModel) {
		return new GroupButtonListener(graphPropertyModel, GroupAction.DIALOG);
	}

	/**
	 * �O���[�v�A�N�V�����̃^�C�v�Z�[�t enum �N���X�ł��B �t�H���[�h�A�o�b�N�A�ꗗ�I�����|���t�H�[�Y���ŏ������܂��B new
	 * �ŃC���X�^���X�����ł��܂���B�\�߃C���X�^���X���������ÓI�t�B�[���h���g�p���ĉ������B
	 */
	private abstract static class GroupAction {
		/**
		 * �v���C�x�[�g�R���X�g���N�^
		 */
		private GroupAction() {
		}

		/**
		 * �O���[�v�̃A�N�V���������ۂɏ������鉼�z���\�b�h�B �������̐ÓI�t�B�[���h�C���X�^���X�������ɁA�A�N�V�������������܂��B
		 * 
		 * @param graphPropertyModel �O���t�v���p�e�B���f��
		 * @param component �e�R���|�[�l���g
		 */
		abstract void groupAction(
				GraphPropertyModel graphPropertyModel,
				JComponent component);

		/** �t�H���[�h�A�N�V���� */
		static GroupAction FORWARD = new GroupAction() {
			void groupAction(
					GraphPropertyModel graphPropertyModel,
					JComponent component) {
				graphPropertyModel.nextGroup();
			}
		};

		/** �o�b�N�A�N�V���� */
		static GroupAction BACK = new GroupAction() {
			void groupAction(
					GraphPropertyModel graphPropertyModel,
					JComponent component) {
				// �o�b�N
				graphPropertyModel.prevGroup();
			}
		};

		/** �ꗗ�I���A�N�V���� */
		static GroupAction DIALOG = new GroupAction() {
			void groupAction(
					GraphPropertyModel graphPropertyModel,
					JComponent component) {
				// �ꗗ�_�C�A���O
				Frame frame = WifeUtilities.getParentFrame(component);
				new GroupSelectDialog(frame, graphPropertyModel);
			}
		};
	}

	/**
	 * �O���[�v�I���_�C�A���O�N���X�ł��B
	 */
	private static class GroupSelectDialog extends JDialog {
		private static final long serialVersionUID = 4329010424946405060L;

		private GraphPropertyModel graphPropertyModel;

		private JList list;

		private JButton okButton;

		private JButton cancelButton;

		private Window window;

		GroupSelectDialog(Frame frame, GraphPropertyModel graphPropertyModel) {
			super(frame, "�O���[�v�I��", true);
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

			setSize();

			Dimension fDimension = window.getSize();
			Dimension dDimension = getSize();
			Point location = window.getLocation();
			location.translate(
					(fDimension.width - dDimension.width) / 2,
					(fDimension.height - dDimension.height) / 2);
			setLocation(location);

			show();
		}

		private void setSize() {
			ClientConfiguration configuration = new ClientConfiguration();
			setSize(configuration.getInt(
					"xwife.applet.Applet.trend.dialog.width",
					157), configuration.getInt(
					"xwife.applet.Applet.trend.dialog.height",
					217));
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
