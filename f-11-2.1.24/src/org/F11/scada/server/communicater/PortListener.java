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

package org.F11.scada.server.communicater;

import java.io.IOException;
import java.nio.channels.SelectableChannel;

/**
 * セレクタのリスナーインターフェースです。
 */
public interface PortListener {
	/** このリスナーの保持するポートを開きます。 */
	public SelectableChannel open() throws IOException, InterruptedException;
	/** このリスナーの保持するポートを閉じます。 */
	public void close() throws IOException;

	/** ポートがAccept可能状態になった時に呼出されます。 */
	public void onAccept() throws IOException;
	/** ポートがConnect可能状態になった時に呼出されます。 */
	public void onConnect() throws IOException, InterruptedException;
	/** ポートがRead可能状態になった時に呼出されます。 */
	public void onRead() throws IOException;
	/** ポートがWrite可能状態になった時に呼出されます。 */
	public void onWrite() throws IOException, InterruptedException;
}
