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

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.ConfigurationRuntimeException;
import org.apache.commons.configuration.XMLPropertiesConfiguration;

/**
 * @author Hideaki Maekawa <frdm@user.sourceforge.jp>
 */
public class ClientConfiguration implements Configuration {
    private final Configuration configuration;

    public ClientConfiguration() {
        this("/resources/ClientConfiguration.xml");
    }

    public ClientConfiguration(String file) {
        try {
            configuration = new XMLPropertiesConfiguration(getClass().getResource(file));
        } catch (ConfigurationException e) {
            throw new ConfigurationRuntimeException(e);
        }
    }
    
    public void addProperty(String key, Object value) {
        configuration.addProperty(key, value);
    }
    public void clear() {
        configuration.clear();
    }
    public void clearProperty(String key) {
        configuration.clearProperty(key);
    }
    public boolean containsKey(String key) {
        return configuration.containsKey(key);
    }
    public boolean equals(Object obj) {
        return configuration.equals(obj);
    }
    public BigDecimal getBigDecimal(String key) {
        return configuration.getBigDecimal(key);
    }
    public BigDecimal getBigDecimal(String key, BigDecimal defaultValue) {
        return configuration.getBigDecimal(key, defaultValue);
    }
    public BigInteger getBigInteger(String key) {
        return configuration.getBigInteger(key);
    }
    public BigInteger getBigInteger(String key, BigInteger defaultValue) {
        return configuration.getBigInteger(key, defaultValue);
    }
    public boolean getBoolean(String key) {
        return configuration.getBoolean(key);
    }
    public boolean getBoolean(String key, boolean defaultValue) {
        return configuration.getBoolean(key, defaultValue);
    }
    public Boolean getBoolean(String key, Boolean defaultValue)
            throws NoClassDefFoundError {
        return configuration.getBoolean(key, defaultValue);
    }
    public byte getByte(String key) {
        return configuration.getByte(key);
    }
    public byte getByte(String key, byte defaultValue) {
        return configuration.getByte(key, defaultValue);
    }
    public Byte getByte(String key, Byte defaultValue) {
        return configuration.getByte(key, defaultValue);
    }
    public double getDouble(String key) {
        return configuration.getDouble(key);
    }
    public double getDouble(String key, double defaultValue) {
        return configuration.getDouble(key, defaultValue);
    }
    public Double getDouble(String key, Double defaultValue) {
        return configuration.getDouble(key, defaultValue);
    }
    public float getFloat(String key) {
        return configuration.getFloat(key);
    }
    public float getFloat(String key, float defaultValue) {
        return configuration.getFloat(key, defaultValue);
    }
    public Float getFloat(String key, Float defaultValue) {
        return configuration.getFloat(key, defaultValue);
    }
    public int getInt(String key) {
        return configuration.getInt(key);
    }
    public int getInt(String key, int defaultValue) {
        return configuration.getInt(key, defaultValue);
    }
    public Integer getInteger(String key, Integer defaultValue) {
        return configuration.getInteger(key, defaultValue);
    }
    public Iterator getKeys() {
        return configuration.getKeys();
    }
    public Iterator getKeys(String prefix) {
        return configuration.getKeys(prefix);
    }
    public List getList(String key) {
        return configuration.getList(key);
    }
    public List getList(String key, List defaultValue) {
        return configuration.getList(key, defaultValue);
    }
    public long getLong(String key) {
        return configuration.getLong(key);
    }
    public Long getLong(String key, Long defaultValue) {
        return configuration.getLong(key, defaultValue);
    }
    public long getLong(String key, long defaultValue) {
        return configuration.getLong(key, defaultValue);
    }
    public Properties getProperties(String key) {
        return configuration.getProperties(key);
    }
    public Object getProperty(String key) {
        return configuration.getProperty(key);
    }
    public short getShort(String key) {
        return configuration.getShort(key);
    }
    public Short getShort(String key, Short defaultValue) {
        return configuration.getShort(key, defaultValue);
    }
    public short getShort(String key, short defaultValue) {
        return configuration.getShort(key, defaultValue);
    }
    public String getString(String key) {
        return configuration.getString(key);
    }
    public String getString(String key, String defaultValue) {
        return configuration.getString(key, defaultValue);
    }
    public String[] getStringArray(String key) {
        return configuration.getStringArray(key);
    }
    public int hashCode() {
        return configuration.hashCode();
    }
    public boolean isEmpty() {
        return configuration.isEmpty();
    }
    public void setProperty(String key, Object value) {
        configuration.setProperty(key, value);
    }
    public Configuration subset(String prefix) {
        return configuration.subset(prefix);
    }
    public String toString() {
        return configuration.toString();
    }
}
