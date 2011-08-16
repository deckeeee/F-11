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
 */
package org.F11.scada.applet.operationlog;

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.TitledBorder;

import org.F11.scada.applet.symbol.GraphicManager;
import org.F11.scada.server.operationlog.dto.FinderConditionDto;

import com.toedter.calendar.JCalendar;
import com.toedter.calendar.JDateChooser;

/**
 * @author Hideaki Maekawa <frdm@user.sourceforge.jp>
 */
public class OperationLoggingFinder extends JPanel {
    private static final long serialVersionUID = 5284694046819536251L;
	private JButton searchButton;
    private JLabel dateLabel;
    private JLabel userLabel;
    private JLabel ipLabel;
    private JLabel nameLabel;
    private JLabel messageLabel;
    private JLabel fromLabel;
    private JDateChooser startDate;
    private JDateChooser endDate;
    private JTextField userField;
    private JTextField ipField;
    private JTextField nameField;
    private JTextField messageField;
    private final OperationLoggingTableModel tableModel;
    
    public OperationLoggingFinder(OperationLoggingTableModel tableModel) {
        super(new GridBagLayout());
        this.tableModel = tableModel;
        initComponents();
        setBorder(new TitledBorder("検索条件"));
        setSize(550, 150);
    }

    private void initComponents() {
        GridBagConstraints gridBagConstraints;

        dateLabel = new JLabel("日時");
        userLabel = new JLabel("ユーザー");
        ipLabel = new JLabel("IP");
        nameLabel = new JLabel("ポイント名称");
        messageLabel = new JLabel("ポイント詳細");
        startDate = createJDateChooser();
        startDate.setDate(new Date());
        fromLabel = new JLabel("～");
        endDate = createJDateChooser();
        endDate.setDate(new Date());
        userField = new JTextField(15);
        ipField = new JTextField(15);
        nameField = new JTextField(15);
        messageField = new JTextField(15);
        searchButton = new JButton("検索", GraphicManager.get("/toolbarButtonGraphics/general/Find24.gif"));
        searchButton.setToolTipText("検索");
        searchButton.addActionListener(new SearchActionLister(this));

        dateLabel.setHorizontalAlignment(SwingConstants.LEFT);
        dateLabel.setHorizontalTextPosition(SwingConstants.LEFT);
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        add(dateLabel, gridBagConstraints);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        add(userLabel, gridBagConstraints);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        add(ipLabel, gridBagConstraints);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        add(nameLabel, gridBagConstraints);
        
        if (tableModel.isPrefix()) {
            gridBagConstraints = new GridBagConstraints();
            gridBagConstraints.gridx = 0;
            gridBagConstraints.gridy = 4;
            gridBagConstraints.anchor = GridBagConstraints.WEST;
            add(messageLabel, gridBagConstraints);
        }

        Box box = Box.createHorizontalBox();
        box.add(startDate);
        box.add(Box.createHorizontalStrut(5));
        box.add(fromLabel);
        box.add(Box.createHorizontalStrut(5));
        box.add(endDate);
        Insets texiAreaInsets = new Insets(0, 10, 0, 0);
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.insets = texiAreaInsets;
        add(box, gridBagConstraints);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = texiAreaInsets;
        add(userField, gridBagConstraints);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = texiAreaInsets;
        add(ipField, gridBagConstraints);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = texiAreaInsets;
        add(nameField, gridBagConstraints);

        if (tableModel.isPrefix()) {
            gridBagConstraints = new GridBagConstraints();
            gridBagConstraints.gridx = 1;
            gridBagConstraints.gridy = 4;
            gridBagConstraints.anchor = GridBagConstraints.WEST;
            gridBagConstraints.insets = texiAreaInsets;
            add(messageField, gridBagConstraints);
        }

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 5;
        add(searchButton, gridBagConstraints);
    }
    
    private JDateChooser createJDateChooser() {
        JCalendar calendar = new JCalendar();
        calendar.setSundayForeground(Color.RED);
        calendar.setDecorationBackgroundVisible(true);
        calendar.setDecorationBordersVisible(true);
        calendar.setWeekOfYearVisible(false);
        JDateChooser chooser = new JDateChooser(calendar, "yyyy/MM/dd", false, null);
        return chooser;
    }
    
    static class SearchActionLister implements ActionListener {
        private final OperationLoggingFinder finder;
        SearchActionLister(OperationLoggingFinder finder) {
            this.finder = finder;
        }
        public void actionPerformed(ActionEvent e) {
            FinderConditionDto dto = new FinderConditionDto();
            dto.setStartDate(getStartTimestamp(finder.startDate.getDate()));
            dto.setEndDate(getEndTimestamp(finder.endDate.getDate()));
            dto.setOpeUser(nullConvert(finder.userField.getText()));
            dto.setOpeIp(nullConvert(finder.ipField.getText()));
            dto.setOpeName(nullConvert(finder.nameField.getText()));
            if (finder.tableModel.isPrefix()) {
            	dto.setOpeMessage(nullConvert(finder.messageField.getText()));
            }
            finder.tableModel.find(dto);
        }
        private Timestamp getStartTimestamp(Date date) {
            Calendar cal = Calendar.getInstance();
            cal.setTime(date);
            cal.set(Calendar.MILLISECOND, 0);
            cal.set(Calendar.SECOND, 0);
            cal.set(Calendar.MINUTE, 0);
            cal.set(Calendar.HOUR_OF_DAY, 0);
            return new Timestamp(cal.getTimeInMillis());
        }
        private Timestamp getEndTimestamp(Date date) {
            Calendar cal = Calendar.getInstance();
            cal.setTime(date);
            cal.set(Calendar.MILLISECOND, 999);
            cal.set(Calendar.SECOND, 59);
            cal.set(Calendar.MINUTE, 59);
            cal.set(Calendar.HOUR_OF_DAY, 23);
            return new Timestamp(cal.getTimeInMillis());
        }
        private String nullConvert(String s) {
            return s.length() == 0 ? null : s;
        }
    }
}
