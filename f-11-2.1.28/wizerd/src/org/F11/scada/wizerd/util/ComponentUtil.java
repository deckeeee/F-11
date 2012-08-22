/*
 * Project F-11 - Web SCADA for Java
 * Copyright (C) 2002-2008 Freedom, Inc. All Rights Reserved.
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

package org.F11.scada.wizerd.util;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetAdapter;
import java.awt.dnd.DropTargetDropEvent;
import java.io.File;
import java.util.List;

import javax.swing.JTextField;

/**
 * コンポーネントに関するユーティリティークラスです。
 * 
 * @author maekawa
 * 
 */
public abstract class ComponentUtil {
	/**
	 * 対象のテキストフィールドをドラッグ＆ドロップ対応に設定します。
	 * 
	 * @param field テキストフィールド
	 */
	public static void setDropTarget(final JTextField field) {
		new DropTarget(field, new DropTargetAdapter() {
			@SuppressWarnings("unchecked")
			public void drop(DropTargetDropEvent e) {
				try {
					Transferable transfer = e.getTransferable();
					if (transfer
						.isDataFlavorSupported(DataFlavor.javaFileListFlavor)) {
						e.acceptDrop(DnDConstants.ACTION_COPY_OR_MOVE);
						List<File> fileList =
							(List<File>) transfer
								.getTransferData(DataFlavor.javaFileListFlavor);
						field.setText(fileList.get(0).getAbsolutePath());
					}
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}
		});
	}
}
