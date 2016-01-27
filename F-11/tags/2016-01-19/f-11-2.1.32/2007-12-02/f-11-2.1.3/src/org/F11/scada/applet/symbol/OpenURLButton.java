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
package org.F11.scada.applet.symbol;

import java.applet.Applet;
import java.applet.AppletContext;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import javax.swing.SwingUtilities;

import org.F11.scada.util.BrowserLauncher;
import org.F11.scada.xwife.applet.AbstractWifeApplet;

/**
 * @author Hideaki Maekawa <frdm@user.sourceforge.jp>
 */
public class OpenURLButton extends AbstractButtonSymbol implements ActionListener {
	private static final long serialVersionUID = -8871971338695527754L;
	private String url;

	public OpenURLButton(SymbolProperty property) {
	    super(property);

	    url = property.getProperty("url");
		this.addActionListener(this);
	}
	
    public void actionPerformed(ActionEvent e) {
    	AbstractWifeApplet applet =
			(AbstractWifeApplet) SwingUtilities.getAncestorOfClass(AbstractWifeApplet.class, this);
		if (applet == null) {
			throw new IllegalStateException("WifeApplet noting");
		}
	
		if (applet.isStandAlone()) {
		    try {
                BrowserLauncher.openURL(url);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
		} else {
			try {
				showDocument(applet);
			} catch (MalformedURLException ex) {
				ex.printStackTrace();
			}
		}
    }
	
	private void showDocument(Applet applet) throws MalformedURLException {
		AppletContext context = applet.getAppletContext();
		if (url != null) {
			context.showDocument(new URL(url), "_blank");
		} else {
			System.out.println("url not found");
		}
	}
}
