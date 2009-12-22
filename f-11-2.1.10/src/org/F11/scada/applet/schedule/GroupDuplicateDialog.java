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

package org.F11.scada.applet.schedule;

import java.awt.Container;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListModel;

import org.F11.scada.WifeUtilities;

public class GroupDuplicateDialog extends JDialog {
	private static final long serialVersionUID = 8204814342762000834L;
	private JList list;
	private final ScheduleModel model;
	
	public GroupDuplicateDialog(Frame frame, ScheduleModel model) {
		super(frame, true);
		this.model = model;
		initConponent();
	}

	private void initConponent() {
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        GridBagConstraints gridBagConstraints;

        createList();

        JPanel main = new JPanel(new GridBagLayout());
        main.setBorder(BorderFactory.createTitledBorder("複写先を選択"));

        JScrollPane scrollPane = new JScrollPane(list);
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        main.add(scrollPane, gridBagConstraints);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.X_AXIS));

        JButton okButton = createOkButton();
        buttonPanel.add(okButton);

        JButton cancelButton = createCancelButton();
        buttonPanel.add(cancelButton);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = GridBagConstraints.LAST_LINE_END;
        main.add(buttonPanel, gridBagConstraints);

        Container container = getContentPane();
        container.add(main);
        
        pack();
        WifeUtilities.setCenter(this);
        setTitle("グループ複写");
	}

	private JButton createCancelButton() {
		JButton cancelButton = new JButton();
        cancelButton.setText("CANCEL");
        cancelButton.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent e) {
        		dispose();
        	}
        });
		return cancelButton;
	}

	private JButton createOkButton() {
		JButton okButton = new JButton();
        okButton.setText("OK");
        okButton.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent e) {
        		int[] dest = createDests(list.getSelectedValues());
        		if (dest.length > 0) {
        			model.duplicateGroup(dest);
        		}
        		dispose();
        	}
        	
        	private int[] createDests(Object[] selectValues) {
        		Object[] selectIndex = list.getSelectedValues();
        		int[] dest = new int[selectIndex.length];
        		for (int i = 0; i < selectIndex.length; i++) {
        			GroupElement element = (GroupElement) selectIndex[i];
					dest[i] = element.getIndex();
				}
        		return dest;
        	}
        });
		return okButton;
	}

	private void createList() {
		list = new JList(createListModel());
        list.setFixedCellWidth(500);
//        String tip = "Shiftキーを押しながらマウスクリックで連続エリアをCtrlキー押しながらマウスクリックで非連続エリアを選択することが可能です。";
//        list.setToolTipText(tip);
	}

	private ListModel createListModel() {
		DefaultListModel listModel = new DefaultListModel();
		GroupElement[] groups = model.getGroupNames();
		for (int i = 0; i < groups.length; i++) {
			listModel.addElement(groups[i]);
		}
		return listModel;
	}
}
