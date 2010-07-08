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
package org.F11.scada.server.operationlog.dto;

import java.io.Serializable;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;

/**
 * @author Hideaki Maekawa <frdm@user.sourceforge.jp>
 */
public class OperationLogging implements Serializable {
    private static final long serialVersionUID = -1048633876267944473L;    

    public static final String TABLE = "operation_logging_table";
//    public static final String id_ID = "identity";
    public static final String NO_PERSISTENT_PROPS = "id";
    
    private long id;
    private Timestamp opeDate;
    private String opeIp;
    private String opeUser;
    private String opeBeforeValue;
    private String opeAfterValue;
    private String opeProvider;
    private String opeHolder;
    
    public long getId() {
        return id;
    }
    public void setId(long id) {
        this.id = id;
    }
    public String getOpeAfterValue() {
        return opeAfterValue;
    }
    public void setOpeAfterValue(String opeAfterValue) {
        this.opeAfterValue = opeAfterValue;
    }
    public String getOpeBeforeValue() {
        return opeBeforeValue;
    }
    public void setOpeBeforeValue(String opeBeforeValue) {
        this.opeBeforeValue = opeBeforeValue;
    }
    public Timestamp getOpeDate() {
        return opeDate;
    }
    public void setOpeDate(Timestamp opeDate) {
        this.opeDate = opeDate;
    }
    public String getOpeHolder() {
        return opeHolder;
    }
    public void setOpeHolder(String opeHolder) {
        this.opeHolder = opeHolder;
    }
    public String getOpeIp() {
        return opeIp;
    }
    public void setOpeIp(String opeIp) {
        this.opeIp = opeIp;
    }
    public String getOpeProvider() {
        return opeProvider;
    }
    public void setOpeProvider(String opeProvider) {
        this.opeProvider = opeProvider;
    }
    public String getOpeUser() {
        return opeUser;
    }
    public void setOpeUser(String opeUser) {
        this.opeUser = opeUser;
    }

    public boolean equals(Object obj) {
		if (obj == this) {
			return true;
		}
		if (!(obj instanceof OperationLogging)) {
			return false;
		}
		OperationLogging log = (OperationLogging) obj;
		return id == log.id
			&& opeDate.equals(log.opeDate)
			&& opeIp.equals(log.opeIp)
			&& opeUser.equals(log.opeUser)
			&& opeBeforeValue.equals(log.opeBeforeValue)
			&& opeAfterValue.equals(log.opeAfterValue)
			&& opeProvider.equals(log.opeProvider)
			&& opeHolder.equals(log.opeHolder);
    }
    
    public int hashCode() {
		int result = 17;
		result = 37 * result + (int) (id ^ (id >>> 32));
		result = 37 * result + opeDate.hashCode();
		result = 37 * result + opeIp.hashCode();
		result = 37 * result + opeUser.hashCode();
		result = 37 * result + opeBeforeValue.hashCode();
		result = 37 * result + opeAfterValue.hashCode();
		result = 37 * result + opeProvider.hashCode();
		result = 37 * result + opeHolder.hashCode();
		return result;
    }

    public String toString() {
        SimpleDateFormat f = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        StringBuffer b = new StringBuffer(100);
        b.append("id=").append(id)
        .append(", opeDate=").append(f.format(opeDate))
        .append(", opeIp=").append(opeIp)
        .append(", opeUser=").append(opeUser)
        .append(", opeBeforeValue=").append(opeBeforeValue)
        .append(", opeAfterValue=").append(opeAfterValue)
        .append(", opeProvider=").append(opeProvider)
        .append(", opeHolder=").append(opeHolder);

        return b.toString();
    }
}
