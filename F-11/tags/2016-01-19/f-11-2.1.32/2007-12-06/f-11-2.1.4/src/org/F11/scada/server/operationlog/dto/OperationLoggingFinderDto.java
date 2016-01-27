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


/**
 * @author Hideaki Maekawa <frdm@user.sourceforge.jp>
 */
public class OperationLoggingFinderDto extends OperationLogging implements Serializable {
    private static final long serialVersionUID = 2281837983162942817L;
    
    public static final String TABLE = "operation_logging_table";
    public static final String id_ID = "identity";

    private String unit;
    private String name;
    private String message;
    
    public OperationLoggingFinderDto() {
    }
    public String getUnit() {
        return unit;
    }
    public void setUnit(String unit) {
        this.unit = unit;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public boolean equals(Object obj) {
		if (obj == this) {
			return true;
		}
		if (!(obj instanceof OperationLogging)) {
			return false;
		}
		OperationLoggingFinderDto log = (OperationLoggingFinderDto) obj;
		return getId() == log.getId()
			&& getOpeDate().equals(log.getOpeDate())
			&& getOpeIp().equals(log.getOpeIp())
			&& getOpeUser().equals(log.getOpeUser())
			&& getOpeBeforeValue().equals(log.getOpeBeforeValue())
			&& getOpeAfterValue().equals(log.getOpeAfterValue())
			&& getOpeProvider().equals(log.getOpeProvider())
			&& getOpeHolder().equals(log.getOpeHolder())
			&& unit.equals(log.unit)
			&& name.equals(log.name)
			&& message.equals(log.message);
    }
    public int hashCode() {
		int result = 17;
		result = 37 * result + (int) (getId() ^ (getId() >>> 32));
		result = 37 * result + getOpeDate().hashCode();
		result = 37 * result + getOpeIp().hashCode();
		result = 37 * result + getOpeUser().hashCode();
		result = 37 * result + getOpeBeforeValue().hashCode();
		result = 37 * result + getOpeAfterValue().hashCode();
		result = 37 * result + getOpeProvider().hashCode();
		result = 37 * result + getOpeHolder().hashCode();
		result = 37 * result + unit.hashCode();
		result = 37 * result + name.hashCode();
		result = 37 * result + message.hashCode();
		return result;
    }
    public String toString() {
        return super.toString() + ", unit=" + unit + ", name=" + name + ", message=" + message;
    }
}
