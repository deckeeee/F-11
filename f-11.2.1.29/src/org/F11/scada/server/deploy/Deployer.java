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

package org.F11.scada.server.deploy;

import java.io.File;

/**
 * �z���I�u�W�F�N�g�̃C���^�[�t�F�C�X�ł��B
 * @author Hideaki Maekawa <frdm@users.sourceforge.jp>
 */
public interface Deployer {
	/**
	 * �z���������s���܂�
	 * @param file �z������t�@�C��
	 * @exception DeploymentException �z�����ɗ�O�����������ꍇ
	 */
	public void deploy(File file) throws DeploymentException;

	/**
	 * ��z���������s���܂�
	 * @param file �z������t�@�C��
	 * @exception DeploymentException �z�����ɗ�O�����������ꍇ
	 */
	public void undeploy(File file) throws DeploymentException;

	/**
	 * �����̃y�[�W��`XML���z������Ă��邩�𔻒肵�܂�
	 * @param pageName �y�[�W����
	 * @return ���ɔz���ς݂ł���� true �������łȂ���� false ��Ԃ��܂�
	 */
	public boolean isDeployed(String pageName);
}
