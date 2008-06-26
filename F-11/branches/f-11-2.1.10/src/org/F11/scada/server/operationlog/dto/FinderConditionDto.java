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

import org.apache.commons.lang.time.DateFormatUtils;

/**
 * @author Hideaki Maekawa <frdm@user.sourceforge.jp>
 */
public class FinderConditionDto implements Serializable, Cloneable {
	private static final long serialVersionUID = 5224871913559471190L;    
	private Timestamp startDate;
    private Timestamp endDate;
    private String opeUser;
    private String opeIp;
    private String opeName;
    private String opeMessage;
    private Long currentId;
    private Integer limit;
    
    public Timestamp getEndDate() {
        return endDate;
    }
    public void setEndDate(Timestamp endDate) {
        this.endDate = endDate;
    }
    public String getOpeIp() {
        return opeIp;
    }
    public void setOpeIp(String opeIp) {
        this.opeIp = opeIp;
    }
    public String getOpeName() {
        return null == opeName ? null : "%" + opeName + "%";
    }
    public void setOpeName(String opeUnit) {
        this.opeName = opeUnit;
    }
    public String getOpeUser() {
        return opeUser;
    }
    public void setOpeUser(String opeUser) {
        this.opeUser = opeUser;
    }
    public Timestamp getStartDate() {
        return startDate;
    }
    public void setStartDate(Timestamp startDate) {
        this.startDate = startDate;
    }
    public Long getCurrentId() {
        return currentId;
    }
    public void setCurrentId(Long currentId) {
        this.currentId = currentId;
    }
    public Integer getLimit() {
        return limit;
    }
    public void setLimit(Integer limit) {
        this.limit = limit;
    }
    public String getOpeMessage() {
		return null == opeMessage ? null : "%" + opeMessage + "%";
	}
	public void setOpeMessage(String opeMessage) {
		this.opeMessage = opeMessage;
	}

	public String toString() {
        StringBuffer b = new StringBuffer(100);
        b.append("startDate=").append(DateFormatUtils.format(startDate, "yyyy/MM/dd HH:mm:ss"))
        .append(", endDate=").append(endDate)
        .append(", opeUser=").append(opeUser)
        .append(", opeIp=").append(opeIp)
        .append(", opeName=").append(opeName)
        .append(", opeMessage=").append(opeMessage)
        .append(", currentId=").append(currentId)
        .append(", limit=").append(limit);
        return b.toString();
    }
    
    public Object clone() {
        try {
            return super.clone();
        } catch (CloneNotSupportedException e) {
            throw new Error("Assertion failure");
        }
    }
}
