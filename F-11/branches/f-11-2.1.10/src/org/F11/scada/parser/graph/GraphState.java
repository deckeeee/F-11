/*
 * $Header: /cvsroot/f-11/F-11/src/org/F11/scada/parser/graph/GraphState.java,v 1.2 2002/12/24 07:27:05 frdm Exp $
 * $Revision: 1.2 $
 * $Date: 2002/12/24 07:27:05 $
 * 
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
package org.F11.scada.parser.graph;

import org.F11.scada.applet.graph.GraphModel;
import org.F11.scada.applet.graph.GraphPropertyModel;
import org.F11.scada.applet.graph.GraphSeriesProperty;
import org.F11.scada.parser.State;

/**
 * �O���t��ԃN���X�̃C���^�[�t�F�C�X�ł��B
 * @author Hideaki Maekawa <frdm@users.sourceforge.jp>
 */
public interface GraphState extends State{
	/**
	 * ��ԃI�u�W�F�N�g�ɃO���t�v���p�e�B���f����ݒ肵�܂��B
	 * @param graphPropertyModel �O���t�v���p�e�B���f��
	 */
	public void setGraphPropertyModel(GraphPropertyModel graphPropertyModel);

	/**
	 * ��ԃI�u�W�F�N�g�̃O���t�v���p�e�B���f���ɃV���[�Y�v���p�e�B��ǉ����܂��B
	 * @param property �V���[�Y�v���p�e�B
	 */
	public void addSeriesProperty(GraphSeriesProperty property);

	/**
	 * ��ԃI�u�W�F�N�g�ɃO���t���f����ݒ肵�܂��B
	 * @param graphModel �O���t�v���p�e�B���f��
	 */
	public void setGraphModel(GraphModel graphModel);
}
