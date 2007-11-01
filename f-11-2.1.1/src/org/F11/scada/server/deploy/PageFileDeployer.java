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

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.F11.scada.server.frame.FrameDefineManager;
import org.apache.log4j.Logger;

/**
 * �y�[�W��`�t�@�C����z������������N���X�ł��B
 * 
 * @author Hideaki Maekawa <frdm@users.sourceforge.jp>
 */
public class PageFileDeployer implements Deployer {
	/** ���M���OAPI */
	private static Logger logger = Logger.getLogger(PageFileDeployer.class);
	/** �y�[�W��`�}�l�[�W���[ */
	private final FrameDefineManager manager;
	/** �y�[�W��`�t�@�C�����ƃy�[�W��`�I�u�W�F�N�g�̃}�b�v */
	private Map fileToDefine;
	/**
	 * �y�[�W���� Set �I�u�W�F�N�g
	 * �y�[�W���̏d���`�F�b�N�Ɏg�p���܂��B
	 */
	private Set pages;
	
	/**
	 * �y�[�W��`�z���I�u�W�F�N�g�����������܂��B
	 * @param manager �y�[�W��`�}�l�[�W���[
	 */
	public PageFileDeployer(FrameDefineManager manager) {
		this.manager = manager;
		fileToDefine = new HashMap();
		pages = new HashSet();
	}

	/**
	 * �z���������s���܂�
	 * @param file �z������y�[�W��`XML�t�@�C��
	 */
	public void deploy(File file) throws DeploymentException {
		logger.info("deploy : " + file);

		BufferedInputStream stream = null;
		try {
			// �p�[�X
			stream = new BufferedInputStream(new FileInputStream(file));
			Map map = PageDefineUtil.parse(stream);
			if (logger.isDebugEnabled()) {
				logger.debug(map);
			}
			for (Iterator it = map.keySet().iterator(); it.hasNext();) {
				String pageName = (String) it.next();
				if (pages.contains(pageName)) {
					logger.error("deploy faild. Already " + pageName + " pageName at " + file + ".");
					return;
				} else {
					logger.info("deploy succsess. " + pageName + " pageName at " + file + ".");
				}
			}

			fileToDefine.put(file, map);

			manager.putAll(map);
			pages.addAll(map.keySet());
			stream.close();
		} catch (Exception e) {
			throw new DeploymentException("Error file = " + file, e);
		} finally {
			if (stream != null) {
				try {
					stream.close();
				} catch (IOException e1) {
					throw new DeploymentException(e1);
				}
			}
		}
	}
	
	/**
	 * ��z���������s���܂�
	 * @param file �z������y�[�W��`XML�t�@�C��
	 */
	public void undeploy(File file) throws DeploymentException {
		logger.info("undeploy : " + file);
		Map map = (Map) fileToDefine.remove(file);
		if (logger.isDebugEnabled()) {
			logger.debug(map);
		}
		if (map == null) {
			logger.error("undeploy faild. Not deployed " + file + ".");
			return;
		}
		for (Iterator it = map.keySet().iterator(); it.hasNext(); ) {
			String pageName = (String) it.next();
			if (!pages.remove(pageName)) {
				logger.error("undeploy faild. Not found " + pageName + " pageName at " + file + ".");
			} else {
				logger.info("undeploy succsess. " + pageName + " pageName at " + file + ".");
			}
			manager.removePageString(pageName);
		}
	}

	/**
	 * �����̃y�[�W��`XML���z������Ă��邩�𔻒肵�܂�
	 * @param pageName �y�[�W����
	 * @return ���ɔz���ς݂ł���� true �������łȂ���� false ��Ԃ��܂�
	 */
	public boolean isDeployed(String pageName) {
		return manager.containsKey(pageName);
	}
}
