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

package org.F11.scada.applet.graph;

import java.awt.Color;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.SwingConstants;

class ReferenceTimestampLabel extends JLabel implements
		PropertyChangeListener {

	private static final long serialVersionUID = -3608797323022320423L;

	/** プレフィックス */
	private static final String PREFIX = "参照日時　：　";

	ReferenceTimestampLabel(GraphPropertyModel propertyModel) {
		super(PREFIX + "                   ");
		setFont(getFont().deriveFont(16f));
		setHorizontalAlignment(SwingConstants.CENTER);
		setBorder(BorderFactory.createLineBorder(Color.BLACK));
		propertyModel.addPropertyChangeListener(this);
	}

	/**
	 * GraphModelPropertyの内容が変更された時に呼び出されます。
	 */
	public void propertyChange(PropertyChangeEvent evt) {
		GraphPropertyModel model = (GraphPropertyModel) evt.getSource();

		/** 参照日時のフォーマットです */
		final Timestamp ref = model.getReferenceTime();
		if (ref != null) {
			final SimpleDateFormat fmt = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
			setText(PREFIX + fmt.format(ref));
		}
	}
}
