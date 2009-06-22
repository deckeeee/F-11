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
package org.F11.scada.server.output.dto;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.F11.scada.server.communicater.Communicater;
import org.F11.scada.server.communicater.CommunicaterFactory;
import org.F11.scada.server.communicater.EnvironmentMap;

/**
 * ファイル出力サービスのDTOクラスです。
 * @author Hideaki Maekawa <frdm@user.sourceforge.jp>
 */
public class FileOutputDesc {
    private String filePath;
    private List holders;
    private String provider;
    private Communicater communicater;
    private CommunicaterFactory communicaterFactory;
    
    public FileOutputDesc() {
        holders = new ArrayList();
    }
    
    public String getFilePath() {
        return filePath;
    }
    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }
    public String getProvider() {
        return provider;
    }
    public void setProvider(String provider) {
        this.provider = provider;
    }
    public Communicater getCommunicater() {
        if (communicater == null) {
            try {
                communicater = communicaterFactory.createCommunicator(EnvironmentMap.get(provider));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return communicater;
    }
    public void setCommunicater(Communicater communicater) {
        this.communicater = communicater;
    }
    public void setCommunicaterFactory(CommunicaterFactory communicaterFactory) {
        this.communicaterFactory = communicaterFactory;
    }
    public void addHolder(String holder) {
        holders.add(holder);
    }
    public List getHolders() {
        return Collections.unmodifiableList(holders);
    }
    public String toString() {
        return "filePath=" + filePath + ", provider=" + provider + ", holders=" + holders;
    }
}
