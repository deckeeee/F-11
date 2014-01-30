/*
 * $Header: /cvsroot/f-11/F-11/src/org/F11/scada/parser/alarm/AlarmConfig.java,v 1.5.2.1 2005/01/17 05:57:21 frdm Exp $
 * $Revision: 1.5.2.1 $
 * $Date: 2005/01/17 05:57:21 $
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
 */

package org.F11.scada.parser.alarm;

import org.apache.commons.lang.builder.ToStringBuilder;

/**
 * �ŐV���ƌx��E��Ԉꗗ�̐ݒ��ێ�����N���X�ł��B
 * @author Hideaki Maekawa <frdm@users.sourceforge.jp>
 */
public class AlarmConfig {
	/** �ŐV���̐ݒ�N���X�ł� */
	private AlarmNewsConfig alarmNewsConfig;
	/** �x��E��Ԉꗗ�̐ݒ�N���X�ł� */
	private AlarmTableConfig alarmTableConfig;
	/** �T�[�o�[�R�l�N�V�����G���[�@���b�Z�[�W */
	private ServerErrorMessage serverErrorMessage;
	/** �N���C�A���g�̃y�[�W�ݒ� */
	private Page page;
	/** �N���C�A���g�̃c�[���o�[�ݒ� */
	private ToolBar toolBar;
	/** �����ݒ� */
	private InitConfig initConfig;
	
	private TitleConfig titleConfig;


	/**
	 * ���̃I�u�W�F�N�g�̕�����\����Ԃ��܂�
	 */
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}

	/**
	 * �ŐV���̐ݒ��Ԃ��܂�
	 * @return �ŐV���̐ݒ�
	 */
	public AlarmNewsConfig getAlarmNewsConfig() {
		return alarmNewsConfig;
	}

	/**
	 * �ŐV���̐ݒ��ݒ肵�܂�
	 * @param config �ŐV���̐ݒ�
	 */
	public void setAlarmNewsConfig(AlarmNewsConfig config) {
		alarmNewsConfig = config;
	}

	/**
	 * �x��E��Ԉꗗ�̐ݒ��Ԃ��܂�
	 * @return �x��E��Ԉꗗ�̐ݒ�
	 */
	public AlarmTableConfig getAlarmTableConfig() {
		return alarmTableConfig;
	}

	/**
	 * �x��E��Ԉꗗ�̐ݒ��ݒ肵�܂�
	 * @param config �x��E��Ԉꗗ�̐ݒ�
	 */
	public void setAlarmTableConfig(AlarmTableConfig config) {
		alarmTableConfig = config;
	}

	/**
	 * �T�[�o�[�R�l�N�V�����G���[���b�Z�[�W��Ԃ��܂�
	 * @return �T�[�o�[�R�l�N�V�����G���[���b�Z�[�W��Ԃ��܂�
	 */
	public ServerErrorMessage getServerErrorMessage() {
		return serverErrorMessage;
	}

	/**
	 * �T�[�o�[�R�l�N�V�����G���[���b�Z�[�W��ݒ肵�܂�
	 * @param message �T�[�o�[�R�l�N�V�����G���[���b�Z�[�W��ݒ肵�܂�
	 */
	public void setServerErrorMessage(ServerErrorMessage message) {
		serverErrorMessage = message;
	}

	/**
	 * �N���C�A���g�̃y�[�W�ݒ��Ԃ��܂�
	 * @return �N���C�A���g�̃y�[�W�ݒ�
	 */
	public Page getPage() {
		return page;
	}

	/**
	 * �N���C�A���g�̃y�[�W�ݒ��ݒ肵�܂�
	 * @param page �N���C�A���g�̃y�[�W�ݒ�
	 */
	public void setPage(Page page) {
		this.page = page;
	}

	/**
	 * �N���C�A���g�̃c�[���o�[�ݒ��Ԃ��܂�
	 * @return �N���C�A���g�̃c�[���o�[�ݒ�
	 */
	public ToolBar getToolBar() {
		return toolBar;
	}

	/**
	 * �N���C�A���g�̃c�[���o�[�ݒ��ݒ肵�܂�
	 * @param bar �N���C�A���g�̃c�[���o�[�ݒ�
	 */
	public void setToolBar(ToolBar bar) {
		toolBar = bar;
	}

	/**
	 * �����ݒ��Ԃ��܂�
	 * @return �����ݒ�
	 */
	public InitConfig getInitConfig() {
		return initConfig;
	}

	/**
	 * �����ݒ��ݒ肵�܂�
	 * @param config �����ݒ�
	 */
	public void setInitConfig(InitConfig config) {
		initConfig = config;
	}

    public TitleConfig getTitleConfig() {
        return titleConfig;
    }

    public void setTitleConfig(TitleConfig titleConfig) {
        this.titleConfig = titleConfig;
    }
}
