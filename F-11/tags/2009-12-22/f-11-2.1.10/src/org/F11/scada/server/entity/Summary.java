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
 *
 */

package org.F11.scada.server.entity;

import java.io.Serializable;
import java.sql.Timestamp;

/**
 * summary_tableのエンティティクラス
 * @author Hideaki Maekawa <frdm@users.sourceforge.jp>
 */
public class Summary implements Serializable {
    private static final long serialVersionUID = -6326860590978839606L;

    public static final String TABLE = "system_summary_table";
    public static final String point_COLUMN = "point";
    public static final String provider_COLUMN = "provider";
    public static final String holder_COLUMN = "holder";
    public static final String onDate_COLUMN = "on_date";
    public static final String offDate_COLUMN = "off_date";
    public static final String bitValue_COLUMN = "bit_value";

    private int point;
    private String provider;
    private String holder;
    private Timestamp onDate;
    private Timestamp offDate;
    private boolean bitValue;
    
    
    public boolean isBitValue() {
        return bitValue;
    }
    public void setBitValue(boolean bitValue) {
        this.bitValue = bitValue;
    }
    public String getHolder() {
        return holder;
    }
    public void setHolder(String holder) {
        this.holder = holder;
    }
    public Timestamp getOffDate() {
        return offDate;
    }
    public void setOffDate(Timestamp offDate) {
        this.offDate = offDate;
    }
    public Timestamp getOnDate() {
        return onDate;
    }
    public void setOnDate(Timestamp onDate) {
        this.onDate = onDate;
    }
    public int getPoint() {
        return point;
    }
    public void setPoint(int point) {
        this.point = point;
    }
    public String getProvider() {
        return provider;
    }
    public void setProvider(String provider) {
        this.provider = provider;
    }
    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    public String toString() {
        return
	        point + " " +
	        provider + " " +
	        holder + " " +
	        onDate + " " +
	        offDate + " " +
	        bitValue;
    }
}
