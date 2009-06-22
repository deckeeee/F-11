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

import java.io.Serializable;


/**
 * @author Hideaki Maekawa <frdm@user.sourceforge.jp>
 */
public class HorizontalScaleButtonModel implements Serializable {
	private static final long serialVersionUID = 6098869063997562812L;    
	private String icon;
    private int horizontalScaleCount;
    private long horizontalScaleWidth;
    private int listHandlerIndex;
    private String firstFormat;
    private String secondFormat;

    public HorizontalScaleButtonModel() {}
    
    public int getHorizontalScaleCount() {
        return horizontalScaleCount;
    }
    public void setHorizontalScaleCount(int horizontalScaleCount) {
        this.horizontalScaleCount = horizontalScaleCount;
    }
    public long getHorizontalScaleWidth() {
        return horizontalScaleWidth;
    }
    public void setHorizontalScaleWidth(long horizontalScaleWidth) {
        this.horizontalScaleWidth = horizontalScaleWidth;
    }
    public String getIcon() {
        return icon;
    }
    public void setIcon(String icon) {
        this.icon = icon;
    }
    public int getListHandlerIndex() {
        return listHandlerIndex;
    }
    public void setListHandlerIndex(int listHandlerIndex) {
        this.listHandlerIndex = listHandlerIndex;
    }
    public String getFirstFormat() {
        return firstFormat;
    }
    public void setFirstFormat(String firstFormat) {
        this.firstFormat = firstFormat;
    }
    public String getSecondFormat() {
        return secondFormat;
    }
    public void setSecondFormat(String secondFormat) {
        this.secondFormat = secondFormat;
    }
}
