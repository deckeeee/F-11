/*
 * =============================================================================
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

package org.F11.scada.util;

import java.awt.Component;

import javax.swing.JOptionPane;

import org.apache.log4j.Logger;

abstract public class RmiErrorUtil {
	public static void error(Logger logger, Exception e, Component dialog) {
		logger.error("通信エラー", e);
		JOptionPane.showMessageDialog(
				dialog,
				"通信エラーが発生しました",
				"通信エラー",
				JOptionPane.ERROR_MESSAGE);
	}
}
