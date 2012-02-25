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
package org.F11.scada.applet.graph;

import java.awt.Color;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.HeadlessException;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Timestamp;
import java.util.Date;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;

import com.toedter.calendar.JCalendar;
import com.toedter.calendar.JDateChooser;

/**
 * @author Hideaki Maekawa <frdm@user.sourceforge.jp>
 */
public class SetDateDialog extends JDialog {
    private static final long serialVersionUID = -1951364396704506120L;
	private JButton okButton;
    private JButton cancelButton;
    private JDateChooser chooser;
    private Timestamp findDate;

    /**
     * @param owner
     * @param title
     * @param modal
     * @throws java.awt.HeadlessException
     */
    public SetDateDialog(Frame owner, String title)
            throws HeadlessException {
        super(owner, title, true);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        getContentPane().add(createPanel());

        setSize(250, 150);
    }

    private JPanel createPanel() {
        JPanel main = new JPanel(new GridBagLayout());
        
        chooser = createJDateChooser();
        chooser.setDate(new Date());

        GridBagConstraints gridBagConstraints;

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        main.add(chooser, gridBagConstraints);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = GridBagConstraints.EAST;
        gridBagConstraints.insets = new Insets(10, 0, 0, 0);
        main.add(createButtons(), gridBagConstraints);
        
        main.setBorder(BorderFactory.createTitledBorder("•\Ž¦“ú•t"));

        return main;
    }

    private JDateChooser createJDateChooser() {
        JCalendar calendar = new JCalendar();
        calendar.setSundayForeground(Color.RED);
        calendar.setDecorationBackgroundVisible(true);
        calendar.setDecorationBordersVisible(true);
        calendar.setWeekOfYearVisible(false);
        JDateChooser chooser = new JDateChooser(calendar, "yyyy/MM/dd (E) HH:mm:ss", false, null);
        return chooser;
    }
    
    private Box createButtons() {
        Box box = Box.createHorizontalBox();
        box.add(Box.createGlue());
        okButton = new JButton("OK");
        okButton.addActionListener(new OkAction(this));
        cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(new CancelAction(this));
        box.add(okButton);
        box.add(cancelButton);
        
        return box;
    }
    
    public Timestamp getFindDate() {
        return findDate;
    }
    
    private static class CancelAction implements ActionListener {
        private final SetDateDialog dialog;
        
        CancelAction(SetDateDialog dialog) {
            this.dialog = dialog;
        }
        
        public void actionPerformed(ActionEvent e) {
            dialog.dispose();
        }
    }
    
    private static class OkAction implements ActionListener {
        private final SetDateDialog dialog;
        
        OkAction(SetDateDialog dialog) {
            this.dialog = dialog;
        }

        public void actionPerformed(ActionEvent e) {
            dialog.findDate = getTimestamp(dialog.chooser.getDate());
            dialog.dispose();
        }
        
        private Timestamp getTimestamp(Date date) {
            return date != null ? new Timestamp(date.getTime()) : null;
        }
    }

    public static void main(String[] args) {
        SetDateDialog d = new SetDateDialog(null, "test");
        d.show();
    }
}
