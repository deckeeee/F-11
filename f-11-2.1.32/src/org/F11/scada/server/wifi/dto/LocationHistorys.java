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
package org.F11.scada.server.wifi.dto;

import java.io.Serializable;
import java.sql.Timestamp;

/**
 * @author Hideaki Maekawa <frdm@user.sourceforge.jp>
 */
public class LocationHistorys implements Serializable {
    static final long serialVersionUID = 7533274331805936647L;    

    public static final String TABLE = "location_historys";
    public static final String NO_PERSISTENT_PROPS = "rowNum";

    private long rowNum;
    private Timestamp timeStamp;
    private String id;
    private int xPosition;
    private int yPosition;
    private int floorId;

    
    public int getFloorId() {
        return floorId;
    }
    public void setFloorId(int floorId) {
        this.floorId = floorId;
    }
    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }
    public long getRowNum() {
        return rowNum;
    }
    public void setRowNum(long rowNum) {
        this.rowNum = rowNum;
    }
    public Timestamp getTimeStamp() {
        return timeStamp;
    }
    public void setTimeStamp(Timestamp timeStamp) {
        this.timeStamp = timeStamp;
    }
    public int getXPosition() {
        return xPosition;
    }
    public void setXPosition(int position) {
        xPosition = position;
    }
    public int getYPosition() {
        return yPosition;
    }
    public void setYPosition(int position) {
        yPosition = position;
    }
    
    public String toString() {
        return rowNum
	        + " " + timeStamp
	        + " " + id
	        + " " + xPosition
	        + " " + yPosition
	        + " " + floorId;
    }
}
