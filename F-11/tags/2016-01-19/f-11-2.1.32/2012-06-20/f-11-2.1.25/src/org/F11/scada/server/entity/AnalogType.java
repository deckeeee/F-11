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

/**
 * analog_type_tableのエンティティ
 * @author Hideaki Maekawa <frdm@users.sourceforge.jp>
 */
public class AnalogType implements Serializable {
    private static final long serialVersionUID = 7795503710840584682L;

    public static final String TABLE = "analog_type_table";
    public static final String analogTypeId_COLUMN = "analog_type_id";
    public static final String analogTypeName_COLUMN = "analog_type_name";
    public static final String convertMin_COLUMN = "convert_min";
    public static final String convertMax_COLUMN = "convert_max";
    public static final String inputMin_COLUMN = "input_min";
    public static final String inputMax_COLUMN = "input_max";
    public static final String convertType_COLUMN = "convert_type";

    private int analogTypeId;
    private String analogTypeName;
    private Double convertMin;
    private Double convertMax;
    private Double inputMin;
    private Double inputMax;
    private String format;
    private String convertType;
    
    public int getAnalogTypeId() {
        return analogTypeId;
    }
    public void setAnalogTypeId(int analogTypeId) {
        this.analogTypeId = analogTypeId;
    }
    public String getAnalogTypeName() {
        return analogTypeName;
    }
    public void setAnalogTypeName(String analogTypeName) {
        this.analogTypeName = analogTypeName;
    }
    public Double getConvertMax() {
        return convertMax;
    }
    public void setConvertMax(Double convertMax) {
        this.convertMax = convertMax;
    }
    public Double getConvertMin() {
        return convertMin;
    }
    public void setConvertMin(Double convertMin) {
        this.convertMin = convertMin;
    }
    public String getConvertType() {
        return convertType;
    }
    public void setConvertType(String convertType) {
        this.convertType = convertType;
    }
    public String getFormat() {
        return format;
    }
    public void setFormat(String format) {
        this.format = format;
    }
    public Double getInputMax() {
        return inputMax;
    }
    public void setInputMax(Double inputMax) {
        this.inputMax = inputMax;
    }
    public Double getInputMin() {
        return inputMin;
    }
    public void setInputMin(Double inputMin) {
        this.inputMin = inputMin;
    }
    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    public String toString() {
        return
        analogTypeId + " " +
        analogTypeName + " " +
        convertMin + " " +
        convertMax + " " +
        inputMin + " " +
        inputMax + " " +
        format + " " +
        convertType;

    }
}
