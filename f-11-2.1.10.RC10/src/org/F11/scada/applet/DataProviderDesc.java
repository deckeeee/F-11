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
package org.F11.scada.applet;

import java.io.Serializable;

/**
 * @author Hideaki Maekawa <frdm@user.sourceforge.jp>
 */
public class DataProviderDesc implements Serializable {
    static final long serialVersionUID = 8716202179851927726L;    

    private final String provider;
    private final Class clazz;
    
    public DataProviderDesc(String provider, Class clazz) {
        this.provider = provider;
        this.clazz = clazz;
    }
    
    public Class getDataProviderClass() {
        return clazz;
    }

    public String getProvider() {
        return provider;
    }
    
    public String toString() {
        return provider + ", class=" + clazz.getName();
	}
}
