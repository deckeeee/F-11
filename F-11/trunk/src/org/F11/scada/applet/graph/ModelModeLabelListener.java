/*
 * =============================================================================
 * Projrct F-11 - Web SCADA for Java
 * Copyright (C) 2002-2006 Freedom, Inc. All Rights Reserved.
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

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;


class ModelModeLabelListener implements ActionListener {
	private final ActionListener onListener;
	private final ActionListener offListener;
	private boolean isOn;

	ModelModeLabelListener(GraphPropertyModel graphPropertyModel, TrendGraphView view) {
		this.onListener = getOnAction(graphPropertyModel, view);
		this.offListener = getOffAction(graphPropertyModel, view);
	}

	private SelectiveAction getOffAction(GraphPropertyModel graphPropertyModel, TrendGraphView view) {
		return SelectiveAction.getInstanceSelect(
				new DummyBalking(),
				graphPropertyModel,
				view.getLineGraph());
	}

	private SelectiveAction getOnAction(GraphPropertyModel graphPropertyModel, TrendGraphView view) {
		return SelectiveAction.getInstanceSelectAll(
				new DummyBalking(),
				graphPropertyModel,
				view.getLineGraph());
	}

	public void actionPerformed(ActionEvent e) {
		if (isOn) {
			offListener.actionPerformed(e);
			isOn = false;
		} else {
			onListener.actionPerformed(e);
			isOn = true;
		}
	}


	private static class DummyBalking implements BalkingAction {
		public boolean isBalk(Object obj) {
			return false;
		}

		public void setBalk(Object obj) {
		}
	}
}
