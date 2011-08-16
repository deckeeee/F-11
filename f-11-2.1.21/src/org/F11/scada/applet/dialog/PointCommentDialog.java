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

package org.F11.scada.applet.dialog;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dialog;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ListIterator;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import org.F11.scada.WifeUtilities;
import org.F11.scada.applet.symbol.CommentEditable;
import org.F11.scada.xwife.applet.PageChanger;
import org.apache.log4j.Logger;

public class PointCommentDialog extends WifeDialog {
	private static final long serialVersionUID = 5521427625389449050L;
	private final Logger logger = Logger.getLogger(PointCommentDialog.class);
	private JTextArea textArea;
	private CommentEditable symbol;
	private final PageChanger changer;

	public PointCommentDialog(Dialog dialog, PageChanger changer) {
		super(dialog);
		this.changer = changer;
		initComponents();
	}

	public PointCommentDialog(Frame frame, PageChanger changer) {
		super(frame);
		this.changer = changer;
		initComponents();
	}

	public void selectAll() {
	}

	public void setListIterator(ListIterator listIterator) {
		symbol = (CommentEditable) listIterator.next();
		textArea.setText(symbol.getComment());
	}

	private void setDialogLocation() {
		Rectangle dialogBounds = getBounds();
		dialogBounds.setLocation(symbol.getPoint());
		setLocation(WifeUtilities.getInScreenPoint(screenSize, dialogBounds));
	}

	public void show() {
		setDialogLocation();
		super.show();
	}

	private void initComponents() {
		GridBagConstraints gridBagConstraints;

		JScrollPane scrollPane = new JScrollPane();
		textArea = new JTextArea();
		JPanel buttonPanel = new JPanel();
		JButton okButton = new JButton();
		JButton cancelButton = new JButton();

		JPanel main = new JPanel(new GridBagLayout());
		main.setBorder(BorderFactory.createTitledBorder("ÉRÉÅÉìÉgÇì¸óÕ"));

		textArea.setColumns(30);
		textArea.setRows(5);
		scrollPane.setViewportView(textArea);

		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 0;
		main.add(scrollPane, gridBagConstraints);

		buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.X_AXIS));

		okButton.setText("OK");
		final Component parent = this;
		okButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (ConfirmUtil.isConfirm(parent)) {
					symbol.setComment(textArea.getText());
					dispose();
				}
			}
		});
		buttonPanel.add(okButton);

		cancelButton.setText("CANCEL");
		cancelButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				dispose();
			}
		});
		ActionMapUtil.setActionMap(cancelButton, changer);
		buttonPanel.add(cancelButton);

		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 1;
		gridBagConstraints.anchor = GridBagConstraints.LAST_LINE_END;
		main.add(buttonPanel, gridBagConstraints);

		Container container = getContentPane();
		container.add(main);
	}
}
