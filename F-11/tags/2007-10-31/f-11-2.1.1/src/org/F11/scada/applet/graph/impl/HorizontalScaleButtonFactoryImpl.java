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
package org.F11.scada.applet.graph.impl;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import javax.swing.Icon;
import javax.swing.JButton;

import org.F11.scada.applet.graph.GraphPropertyModel;
import org.F11.scada.applet.graph.HorizontalScaleButtonFactory;
import org.F11.scada.applet.graph.HorizontalScaleButtonModel;
import org.F11.scada.applet.symbol.GraphicManager;

/**
 * @author Hideaki Maekawa <frdm@user.sourceforge.jp>
 */
public class HorizontalScaleButtonFactoryImpl implements
        HorizontalScaleButtonFactory {
    
    private List buttonModels;

    /* (non-Javadoc)
     * @see org.F11.scada.applet.graph.HorizontalScaleButtonFactory#getHorizontalScaleButtons(org.F11.scada.applet.graph.GraphPropertyModel)
     */
    public Collection getHorizontalScaleButtons(GraphPropertyModel model) {
        ArrayList buttons = new ArrayList(buttonModels.size());
        for (Iterator i = buttonModels.iterator(); i.hasNext();) {
            HorizontalScaleButtonModel buttonModel = (HorizontalScaleButtonModel) i.next();
            Icon icon = GraphicManager.get(buttonModel.getIcon());
            JButton button = new JButton(icon);
            button.addActionListener(createActionListener(model, buttonModel));
            buttons.add(button);
        }
        return buttons;
    }
    
    private ActionListener createActionListener(GraphPropertyModel model, HorizontalScaleButtonModel buttonModel) {
        return new HorizontalScaleListener(model, buttonModel);
    }

    public void addHorizontalScaleButtonModel(HorizontalScaleButtonModel model) {
        if (buttonModels == null) {
            buttonModels = new ArrayList(6);
        }
        buttonModels.add(model);
    }

    static class HorizontalScaleListener implements ActionListener {
		private final GraphPropertyModel graphPropertyModel;
		private final HorizontalScaleButtonModel buttonModel;

		private HorizontalScaleListener(GraphPropertyModel graphPropertyModel, HorizontalScaleButtonModel buttonModel) {
			this.graphPropertyModel = graphPropertyModel;
			this.buttonModel = buttonModel;
		}

		public void actionPerformed(ActionEvent evt) {
			graphPropertyModel.setHorizontalScaleCount(buttonModel.getHorizontalScaleCount());
			graphPropertyModel.setHorizontalScaleWidth(buttonModel.getHorizontalScaleWidth());
			graphPropertyModel.setListHandlerIndex(buttonModel.getListHandlerIndex());
			graphPropertyModel.setFirstFormat(buttonModel.getFirstFormat());
			graphPropertyModel.setSecondFormat(buttonModel.getSecondFormat());
		}
    }
}
