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
import javax.swing.ListSelectionModel;

import org.F11.scada.WifeUtilities;
import org.F11.scada.applet.ClientConfiguration;
import org.apache.commons.configuration.Configuration;

public class WeekOfDayDuplicateDialog extends JDialog {
	private static final long serialVersionUID = 8204814342762000834L;
	private JList srcList;
	private JList destList;
	private final ScheduleModel model;
	private final boolean isNonTandT;
	private Configuration configuration = new ClientConfiguration();

	public WeekOfDayDuplicateDialog(
			Frame frame,
			ScheduleModel model,
			boolean isNonTandT) {
		super(frame, true);
		this.model = model;
		this.isNonTandT = isNonTandT;
		initConponent();
	}

	private void initConponent() {
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		GridBagConstraints gridBagConstraints;

		createList();
		createDest();

		JPanel main = new JPanel(new GridBagLayout());

		JScrollPane scrollPane = new JScrollPane(srcList);
		scrollPane.setBorder(BorderFactory.createTitledBorder("コピー元"));
		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 0;
		main.add(scrollPane, gridBagConstraints);

		JScrollPane destScrollPane = new JScrollPane(destList);
		destScrollPane.setBorder(BorderFactory.createTitledBorder("コピー先"));
		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridx = 1;
		gridBagConstraints.gridy = 0;
		main.add(destScrollPane, gridBagConstraints);

		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.X_AXIS));

		JButton okButton = createOkButton();
		buttonPanel.add(okButton);

		JButton cancelButton = createCancelButton();
		buttonPanel.add(cancelButton);

		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridx = 1;
		gridBagConstraints.gridy = 1;
		gridBagConstraints.anchor = GridBagConstraints.LAST_LINE_END;
		main.add(buttonPanel, gridBagConstraints);

		Container container = getContentPane();
		container.add(main);

		pack();
		WifeUtilities.setCenter(this);
		setTitle("曜日間複写");
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
				int src = getSelectedIndex();
				int[] dest = getSelectedIndices();
				if (src >= 0 && dest.length > 0) {
					model.duplicateWeekOfDay(src, dest);
				}
				dispose();
			}

			private int getSelectedIndex() {
				ListElement element = (ListElement) srcList.getSelectedValue();
				return element.getIndex();
			}

			private int[] getSelectedIndices() {
				Object[] dest = destList.getSelectedValues();
				int[] ret = new int[dest.length];
				for (int i = 0; i < dest.length; i++) {
					ret[i] = ((ListElement) dest[i]).getIndex();
				}
				return ret;
			}
		});
		return okButton;
	}

	private void createList() {
		srcList = new JList(createListModel());
		srcList.setFixedCellWidth(100);
		srcList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		srcList.setSelectedIndex(0);
	}

	private void createDest() {
		destList = new JList(createListModel());
		destList.setFixedCellWidth(100);
	}

	private ListModel createListModel() {
		DefaultListModel listModel = new DefaultListModel();
		for (int i = getStartIndex(); i < model.getPatternSize(); i++) {
			listModel.addElement(new ListElement(model.getDayIndexName(i), i));
		}
		return listModel;
	}

	private int getStartIndex() {
		boolean todayOrTomorrow =
			configuration.getBoolean(
					"org.F11.scada.applet.schedule.todayOrTomorrow", false);
		return isNonTandT || todayOrTomorrow ? 2 : 0;
	}

	private static class ListElement {
		private final String name;
		private final int index;

		public ListElement(String name, int index) {
			this.name = name;
			this.index = index;
		}

		public int getIndex() {
			return index;
		}

		public String getName() {
			return name;
		}

		public String toString() {
			return name;
		}
	}
}
