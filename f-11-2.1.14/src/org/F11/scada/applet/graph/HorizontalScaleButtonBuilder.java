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

import java.io.IOException;
import java.net.URL;

import org.F11.scada.applet.graph.impl.HorizontalScaleButtonFactoryImpl;
import org.apache.commons.digester.Digester;
import org.xml.sax.SAXException;

/**
 * @author Hideaki Maekawa <frdm@user.sourceforge.jp>
 */
public class HorizontalScaleButtonBuilder {
    private static final String DEFAULT_FILE = "/org/F11/scada/applet/graph/HorizontalScaleButtonFactory.xml";
    private final String file;

    public HorizontalScaleButtonBuilder() {
        this(DEFAULT_FILE);
    }

    public HorizontalScaleButtonBuilder(String file) {
        if (file == null) {
        	this.file = DEFAULT_FILE;
        } else {
            this.file = file;
        }
    }

    public HorizontalScaleButtonFactory create() {
		Digester digester = new Digester();
		digester.addRuleSet(new HorizontalScaleButtonRuleSet());
		HorizontalScaleButtonFactory factory = new HorizontalScaleButtonFactoryImpl();
		digester.push(factory);
		URL url = HorizontalScaleButtonBuilder.class.getResource(file);
		if (url == null) {
		    throw new IllegalStateException("トレンドグラフ横スケール定義が見つかりません : " + file);
		}
		try {
            digester.parse(url.openStream());
        } catch (IOException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        }
		
		return factory;
    }
}
