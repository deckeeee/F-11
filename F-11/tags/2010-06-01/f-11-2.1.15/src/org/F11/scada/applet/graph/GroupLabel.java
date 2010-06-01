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

package org.F11.scada.applet.graph;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.text.MessageFormat;

import javax.swing.JLabel;

/**
 * GraphPropertyModelの変更イベントよりグループ名の表示するクラスです。
 * 
 * @author Hideaki Maekawa <frdm@users.sourceforge.jp>
 */
class GroupLabel extends JLabel implements PropertyChangeListener {
	private static final long serialVersionUID = 5385610551572473558L;

	/** 表示するテキストのフォーマッタークラス */
	private static final MessageFormat format = new MessageFormat("グループ：{0}");

	GroupLabel(GraphPropertyModel model) {
		super(format.format(new String[] { model.getGroupName() }));
		setFont(getFont().deriveFont((float) (getFont().getSize2D() * 1.4)));
		model.addPropertyChangeListener(GraphPropertyModel.GROUP_CHANGE_EVENT,
				this);
	}

	/**
	 * GraphModelPropertyの内容が変更された時に呼び出されます。
	 */
	public void propertyChange(PropertyChangeEvent evt) {
		GraphPropertyModel model = (GraphPropertyModel) evt.getSource();

		final String[] msg = new String[] { model.getGroupName() };
		setText(format.format(msg));
	}
}
