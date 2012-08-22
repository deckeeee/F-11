package org.F11.scada.hello;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetAdapter;
import java.awt.dnd.DropTargetDropEvent;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

import javax.swing.ActionMap;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jdesktop.application.Action;
import org.jdesktop.application.SingleFrameApplication;
import org.jdesktop.application.Task;
import org.jdesktop.application.Task.BlockingScope;
import org.xml.sax.SAXException;

/**
 * �y�[�W�W�����v�����ݒ�c�[��(Hello for F-11)
 *
 * @author maekawa
 *
 */
public class HelloFrame extends SingleFrameApplication {
	/** ���M���O */
	private final Log log = LogFactory.getLog(HelloFrame.class);
	private JTextField fileField;

	@Override
	protected void startup() {
		JPanel mainPanel = new JPanel(new BorderLayout());
		setNorth(mainPanel);
		setSouth(mainPanel);
		show(mainPanel);
	}

	private void setNorth(JPanel mainPanel) {
		JPanel descriptionPanel = new JPanel(new BorderLayout());
		descriptionPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 0,
				10));
		JTextArea description = new JTextArea();
		description.setName("description");
		description.setBorder(BorderFactory.createLoweredBevelBorder());
		description.setBackground(description.getBackground());
		description.setOpaque(false);
		descriptionPanel.add(description, BorderLayout.CENTER);
		descriptionPanel.add(getFileParh(), BorderLayout.SOUTH);
		mainPanel.add(descriptionPanel, BorderLayout.NORTH);
	}

	private Component getFileParh() {
		Box box = Box.createHorizontalBox();
		box.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
		JLabel fileLabel = new JLabel();
		fileLabel.setName("fileLabel");
		box.add(fileLabel);
		fileField = new JTextField(40);
		box.add(fileField);
		new DropTarget(fileField, new DropTargetAdapter() {
			public void drop(DropTargetDropEvent e) {
				try {
					Transferable transfer = e.getTransferable();
					if (transfer
							.isDataFlavorSupported(DataFlavor.javaFileListFlavor)) {
						e.acceptDrop(DnDConstants.ACTION_COPY_OR_MOVE);
						List<File> fileList =
							(List<File>) transfer
									.getTransferData(DataFlavor.javaFileListFlavor);
						fileField.setText(fileList.get(0).getAbsolutePath());
					}
				} catch (Exception ex) {
					ex.printStackTrace();
				}

			}
		});

		box.add(Box.createHorizontalStrut(5));
		JButton referenceButton = new JButton();
		referenceButton.setName("referenceButton");
		ActionMap map = getContext().getActionMap();
		referenceButton.setAction(map.get("referenceLogic"));
		box.add(referenceButton);
		return box;
	}

	private void setSouth(JPanel mainPanel) {
		JPanel southPanel = new JPanel(new BorderLayout());
		southPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		Box box = Box.createHorizontalBox();
		box.add(Box.createHorizontalGlue());
		ActionMap map = getContext().getActionMap();

		JButton executeButton = new JButton();
		executeButton.setName("executeButton");
		executeButton.setAction(map.get("executeLogic"));
		box.add(executeButton);

		box.add(Box.createHorizontalStrut(5));

		JButton exitButton = new JButton();
		exitButton.setName("exitButton");
		exitButton.setAction(map.get("exitLogic"));
		box.add(exitButton);
		southPanel.add(box, BorderLayout.CENTER);
		mainPanel.add(southPanel, BorderLayout.SOUTH);
	}

	@Action
	public void referenceLogic() {
		JFileChooser chooser = new JFileChooser();
		chooser.setFileFilter(new javax.swing.filechooser.FileFilter() {
			@Override
			public String getDescription() {
				return "FileList�`���̃e�L�X�g�t�@�C��";
			}

			@Override
			public boolean accept(File f) {
				return f.getName().endsWith(".txt");
			}
		});
		int returnVal = chooser.showOpenDialog(fileField);
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			fileField.setText(chooser.getSelectedFile().getAbsolutePath());
		}
	}

	@Action(block = BlockingScope.APPLICATION)
	public Task<Void, Void> executeLogic() {
		if (fileField.getText().length() != 0) {
			try {
				return new HelloTask(this, fileField);
			} catch (FileNotFoundException e) {
				JOptionPane.showMessageDialog(fileField, "�ݒ肳�ꂽ�t�@�C��������܂���B",
						"�t�@�C���ݒ�G���[", JOptionPane.ERROR_MESSAGE);
				return null;
			} catch (IOException e) {
				log.error("�t�@�C��I/O�G���[", e);
				JOptionPane.showMessageDialog(fileField,
						"�t�@�C������������I/O�G���[���������܂����B", "�t�@�C��I/O�G���[",
						JOptionPane.ERROR_MESSAGE);
				return null;
			} catch (SAXException e) {
				log.error("�t�@�C����̓G���[", e);
				JOptionPane.showMessageDialog(fileField,
						"�t�@�C�����������ɉ�̓G���[���������܂����B", "�t�@�C����̓G���[",
						JOptionPane.ERROR_MESSAGE);
				return null;
			}
		} else {
			JOptionPane.showMessageDialog(fileField, "�t�@�C����I�����Ă��������B",
					"�t�@�C���ݒ�G���[", JOptionPane.ERROR_MESSAGE);
			return null;
		}
	}

	@Action
	public void exitLogic() {
		exit();
	}

	public static void main(String[] args) {
		launch(HelloFrame.class, args);
	}
}
