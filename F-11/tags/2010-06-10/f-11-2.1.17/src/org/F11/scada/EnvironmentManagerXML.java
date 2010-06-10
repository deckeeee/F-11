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
package org.F11.scada;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Properties;

import org.apache.commons.digester.Digester;
import org.apache.commons.digester.RuleSetBase;

/**
 * @author Hideaki Maekawa <frdm@user.sourceforge.jp>
 */
public class EnvironmentManagerXML implements EnvironmentManagerStrategy {
    public static final String DEFAULT_FILE = "/resources/Preferences.xml";
    private final Properties properties;

    public EnvironmentManagerXML() {
        this(DEFAULT_FILE);
    }

    public EnvironmentManagerXML(String file) {
        properties = new Properties();
		Digester digester = new Digester();
		digester.push(properties);
		digester.addRuleSet(new EnvironmentManagerXMLRuleSet());

		URL xml = getClass().getResource(file);
		InputStream is = null;
		try {
			is = xml.openStream();
			digester.parse(is);
			is.close();
		} catch (Exception e) {
		    e.printStackTrace();
		} finally {
			if (is != null) {
			    try {
			        is.close();
			    } catch (IOException e) {
			        e.printStackTrace();
			    }
			}
		}
    }

    /* (non-Javadoc)
     * @see org.F11.scada.EnvironmentManagerStrategy#get(java.lang.String, java.lang.String)
     */
    public String get(String key, String def) {
        return properties.getProperty(key, def);
    }

    static class EnvironmentManagerXMLRuleSet extends RuleSetBase {
        
        /* (non-Javadoc)
         * @see org.apache.commons.digester.RuleSet#addRuleInstances(org.apache.commons.digester.Digester)
         */
        public void addRuleInstances(Digester digester) {
    		digester.addCallMethod("environment/property", "setProperty", 2);
    		digester.addCallParam("environment/property", 0, "key");
    		digester.addCallParam("environment/property", 1, "value");
        }
    }
}
