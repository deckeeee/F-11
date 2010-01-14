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

import org.F11.scada.applet.graph.TrendGraphView.LineGraph;

/**
 * ���[�h�ύX����A�N�V�����N���X
 * 
 * @author Hideaki Maekawa <frdm@user.sourceforge.jp>
 */
class SelectiveAction implements ActionListener {
	public static final String SELECTIVALL_MODENAME = "�S�Ẵf�[�^";
	public static final String SELECTIVE_MODENAME = "�����X�V";
	private static final String SELECTIVEGRAPHMODEL = "org.F11.scada.applet.graph.DefaultSelectiveGraphModel";
	private static final String SELECTIVEALLDATAGRAPHMODEL = "org.F11.scada.applet.graph.DefaultSelectiveAllDataGraphModel";

	/** �O���t�I�u�W�F�N�g */
	private final BalkingAction balkingAction;
	/** �O���t���f���̃N���X�� */
	private final String modelName;
	/** �O�������f���̃��[�h�� */
	private final String modeName;
	/** �O���t�v���p�e�B���f�� */
	private final GraphPropertyModel graphPropertyModel;
	/** �ܐ��O���t�̎Q�� */
	private final LineGraph lineGraph;

	SelectiveAction(
			BalkingAction balkingAction,
			String modelName,
			String modeName,
			GraphPropertyModel graphPropertyModel,
			LineGraph lineGraph) {
		this.balkingAction = balkingAction;
		this.modelName = modelName;
		this.modeName = modeName;
		this.graphPropertyModel = graphPropertyModel;
		this.lineGraph = lineGraph;
	}

	public void actionPerformed(ActionEvent e) {
		Object source = e.getSource();
		// �O��I���A�N�V�����������Ȃ�{�[�L���O
		if (!balkingAction.isBalk(source)) {
			lineGraph.setGraphModelFactory(new DefaultGraphModelFactory(
					modelName,
					graphPropertyModel,
					modeName));
			balkingAction.setBalk(source);
		}
	}

	public static SelectiveAction getInstanceSelect(
			BalkingAction balkingAction,
			GraphPropertyModel graphPropertyModel,
			LineGraph lineGraph) {

		return new SelectiveAction(
				balkingAction,
				SELECTIVEGRAPHMODEL,
				SELECTIVE_MODENAME,
				graphPropertyModel,
				lineGraph);
	}

	public static SelectiveAction getInstanceSelectAll(
			BalkingAction balkingAction,
			GraphPropertyModel graphPropertyModel,
			LineGraph lineGraph) {

		return new SelectiveAction(
				balkingAction,
				SELECTIVEALLDATAGRAPHMODEL,
				SELECTIVALL_MODENAME,
				graphPropertyModel,
				lineGraph);
	}
}
