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
package org.F11.scada.theme;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.HeadlessException;
import java.awt.Point;
import java.awt.SystemColor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.F11.scada.WifeUtilities;
import org.F11.scada.applet.symbol.GraphicManager;
/**
 * @author Hideaki Maekawa <frdm@user.sourceforge.jp>
 */
public class AboutDialog extends JDialog implements MouseMotionListener {
	private static final long serialVersionUID = -721352761767677717L;
	private JLabel iconLabel;
	private boolean isEasterEgg;
	
    /**
     * @throws java.awt.HeadlessException
     */
    public AboutDialog() throws HeadlessException {
        super();
 		initialize();
    }

    /**
     * @param owner
     * @throws java.awt.HeadlessException
     */
    public AboutDialog(Frame owner) throws HeadlessException {
        super(owner);
 		initialize();
    }

	/**
	 * This method initializes this
	 * 
	 * @return void
	 */
	private void initialize() {
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		getContentPane().add(getJPanel());
        setTitle("About F-11");
        setSize(500, 480);
        addMouseMotionListener(this);
        setModal(true);
        setResizable(false);
        WifeUtilities.setCenter(this);
        show();
	}

	/**
	 * This method initializes jPanel	
	 * 	
	 * @return javax.swing.JPanel	
	 */    
	private JPanel getJPanel() {
		JPanel jPanel = new JPanel(new BorderLayout());
		jPanel.setBackground(SystemColor.window);
		jPanel.setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 10));
		Icon icon = GraphicManager.get("/images/f-11s.png");
		iconLabel = new JLabel(icon);
		iconLabel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
		iconLabel.addMouseListener(new MouseAdapter() {
			public void mouseReleased(MouseEvent e) {
				if (isEasterEgg) {
					isEasterEgg = false;
				}
				if (e.getClickCount() >= 5
						&& "Alt".equalsIgnoreCase(MouseEvent
								.getModifiersExText(e.getModifiersEx()))) {
					isEasterEgg = true;
				}
			}
		});
		jPanel.add(iconLabel, BorderLayout.WEST);

		JLabel jLabel = new JLabel(Version.getLicense());
		jLabel.setBorder(BorderFactory.createEmptyBorder(0, 20, 20, 20));
		jPanel.add(getJButton(), BorderLayout.SOUTH);
		jPanel.add(jLabel, BorderLayout.CENTER);
		return jPanel;
	}
	/**
	 * This method initializes jButton
	 * 
	 * @return javax.swing.JButton
	 */    
	private Component getJButton() {
		Box box = Box.createHorizontalBox();
		box.add(Box.createHorizontalStrut(350));
		box.setBorder(BorderFactory.createEmptyBorder(0, 10, 50, 10));
		JButton jButton = new JButton("OK");
		jButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				dispose();
			}
		});
		box.add(jButton);
		return box;
	}

	
    public void mouseDragged(MouseEvent e) {
    }

    public void mouseMoved(MouseEvent e) {
        if (isEasterEgg) {
            Point mp = e.getPoint();
            Dimension dimension = iconLabel.getSize();
            iconLabel.setLocation(mp.x - 4 - dimension.width / 2, mp.y - 30 - dimension.height / 2);
        }
    }

  	public static void main(String[] args) {
	    new AboutDialog();
    }
}
