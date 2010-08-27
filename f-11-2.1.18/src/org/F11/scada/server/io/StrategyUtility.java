/*
 * $Header: /cvsroot/f-11/F-11/src/org/F11/scada/server/io/StrategyUtility.java,v 1.6.2.2 2006/06/02 02:18:02 frdm Exp $
 * $Revision: 1.6.2.2 $
 * $Date: 2006/06/02 02:18:02 $
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

package org.F11.scada.server.io;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Properties;

import org.F11.scada.WifeUtilities;

/**
 * �f�[�^�x�[�X�ڑ��ƃv���p�e�B���f���𐶐����܂��B
 * @author Hideaki Maekawa <maekawa@frdm.co.jp>
 */
public final class StrategyUtility {
	/** �v���p�e�B�[�V�[�g */
	private final Properties properties;

	/**
	 * /resources/Sqldefine.properties��ǂ݃v���p�e�B�𐶐����܂��B
	 * @throws IOException
	 */
	public StrategyUtility() {
		properties = new Properties();
		URL url = getClass().getResource("/resources/Sqldefine.properties");
		if (url == null) {
			throw new IllegalStateException("not found property file : /resources/Sqldefine.properties");
		}

		InputStream is = null;
		try {
			is = url.openStream();
			properties.load(is);
		} catch (IOException e) {
            e.printStackTrace();
        } finally {
			if (is != null) {
				try {
					is.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * �v���R���p�C�����ꂽSQL�X�e�[�g�����g��Ԃ��܂��B
	 * @param key SQL�擾�v���p�e�B�[�L�[�l
	 * @return PreparedStatement
	 */
	public String getPrepareStatement(String key) {
		String keyName = "/" + WifeUtilities.getDBMSName() + key;
		String sp = properties.getProperty(keyName);
		if (sp == null) {
			throw new IllegalStateException("not found property : /" + WifeUtilities.getDBMSName() + key);
		}
		return sp;
	}

	/**
	 * ���̕������ ?TABLE? ���e�[�u�����ɒu��������SQL�X�e�[�g�����g��Ԃ��܂��B
	 * @param key SQL�擾�v���p�e�B�[�L�[�l
	 * @param tableName �e�[�u����
	 * @return PreparedStatement
	 */
	public String getPrepareStatement(String key, String tableName) {
		String keyName = "/" + WifeUtilities.getDBMSName() + key;
		String sqlstr = properties.getProperty(keyName);
		if (tableName != null) {
			sqlstr = sqlstr.replaceFirst("\\$TABLENAME\\$", tableName);
		}
		if (sqlstr == null) {
			throw new IllegalStateException("not found property : /" + WifeUtilities.getDBMSName() + key);
		}
		return sqlstr;
	}
}
