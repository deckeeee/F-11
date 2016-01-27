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

package org.F11.scada.xwife.applet;

import java.io.ObjectStreamException;
import java.io.Serializable;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.F11.scada.WifeUtilities;

/**
 * セッション情報の実装クラスです。
 * 不変クラスです。
 * @author Hideaki Maekawa <frdm@users.sourceforge.jp>
 */
public final class Session implements Serializable {
    private static final long serialVersionUID = -4361438510380379690L;
    private final String id;
    private InetAddress ip;

    public Session() {
        id = createId();
    }

    private Session(String id, InetAddress ip) {
        this.id = id;
        this.ip = ip;
    }

    private String createId() {
	    double seed = Math.random();
	    byte[] seedb = String.valueOf(seed).getBytes();
        try {
            ip = InetAddress.getLocalHost();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        byte[] ipb = ip.getAddress();
	    byte[] b = new byte[seedb.length + ipb.length];
	    System.arraycopy(seedb, 0, b, 0, seedb.length);
	    System.arraycopy(ipb, 0, b, seedb.length, ipb.length);

	    MessageDigest digest = null;
        try {
            digest = MessageDigest.getInstance("SHA");
        } catch (NoSuchAlgorithmException e1) {
            e1.printStackTrace();
        }
        byte[] result = digest.digest(b);
	    return (WifeUtilities.toString(result));
    }

    /**
     * このセッションに割り当てられた一意の識別子が格納された文字列を返します。
     * @return このセッションに割り当てられた一意の識別子が格納された文字列
     */
    public String getId() {
        return id;
    }

    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof Session)) {
            return false;
        }
        Session other = (Session) obj;
        return id.equals(other.id);
    }

    public int hashCode() {
        return id.hashCode();
    }

    public String toString() {
        return "id=" + id;
    }

    public String getIpaddress() {
   		return ip.getHostAddress();
    }

    private Object readResolve() throws ObjectStreamException {
        return new Session(id, ip);
    }
}
