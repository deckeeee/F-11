/*
 * $Header: /cvsroot/f-11/F-11/src/org/F11/scada/server/logging/column/ColumnManager.java,v 1.5.2.5 2006/05/18 06:52:58 frdm Exp $
 * $Revision: 1.5.2.5 $
 * $Date: 2006/05/18 06:52:58 $
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
package org.F11.scada.server.logging.column;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.Map;

import org.F11.scada.EnvironmentManager;
import org.F11.scada.server.entity.Item;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

/**
 * ���M���O��`�t�@�C���x�[�X�̗�}�l�[�W���[�ł��B
 * @author Hideaki Maekawa <frdm@users.sourceforge.jp>
 */
public class ColumnManager {
	//�^�X�N�Ɨ�}�b�v�̃}�b�v�ł��B
	Map taskMap;

	/**
	 * /resources/Logging.xml ���g�p���ė�}�l�[�W���[�𐶐����܂��B
	 * @exception SAXException SAX�p�[�T�[�Ɋւ����O
	 * @exception IOException ���\�[�X�̏ꏊ���s���ȏꍇ
	 */
	public ColumnManager() {
		this("/resources/Logging.xml");
	}

	/**
	 * �w�肵�����\�[�X xml ���g�p���āA��}�l�[�W���[�𐶐����܂��B
	 * @param src ���\�[�X�̏ꏊ
	 * @exception SAXException SAX�p�[�T�[�Ɋւ����O
	 * @exception IOException ���\�[�X�̏ꏊ���s���ȏꍇ
	 */
	public ColumnManager(String src) {
		InputStream stream = null;
		try {
			XMLHandler handler = new XMLHandler(this);
			XMLReader parser =
				XMLReaderFactory.createXMLReader(
					EnvironmentManager.get("/org.xml.sax.driver", ""));
			parser.setContentHandler(handler);
			stream = getClass().getResource(src).openStream();
			InputSource is = new InputSource(stream);
			parser.parse(is);
			stream.close();
		} catch (SAXException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
			if (stream != null) {
				try {
                    stream.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
			}
		}
	}

	/**
	 * �^�X�N���A�f�[�^�v���o�C�_���A�f�[�^�z���_�[�������ԍ���Ԃ��܂��B
	 * @param taskName �^�X�N��
	 * @param provider �f�[�^�v���o�C�_��
	 * @param holder �f�[�^�z���_�[��
	 * @return ��ԍ�
	 * @exception IllegalStateException ���񂪌����ł��Ȃ��ꍇ�ɃX���[���܂��B
	 * @see org.F11.scada.server.logging.column.ColumnManageable#getColumnIndex(String, String, String)
	 */
	public int getColumnIndex(String taskName, String provider, String holder) {
		return getColumnIndex(taskName, provider + "_" + holder);
	}

	public int getColumnIndex(String taskName, String holderid) {
		if (taskMap == null) {
			throw new IllegalStateException("task Map is null.");
		}
		Map cmap = (Map) taskMap.get(taskName);
		if (cmap == null) {
			throw new IllegalStateException("cmap Map is null.");
		}
		Integer col = (Integer) cmap.get(holderid);
		if (col == null) {
			throw new IllegalStateException("col is null." + " task : " + taskName + " holderid : " + holderid);
		}
		return col.intValue();
	}

	public Item[] sortLogging(Item[] items, String tableName) {
		Item[] manageSortedItems = new Item[items.length];
		for (int i = 0; i < items.length; i++) {
		    Item item = items[i];
		    int column = getColumnIndex(tableName, item.getProvider(), item.getHolder());
		    manageSortedItems[column] = item;
        }
		return manageSortedItems;
	}

	public Map getTaskMap() {
		return Collections.unmodifiableMap(taskMap);
	}

	public int getColumnSize(String taskName) {
		if (taskMap.containsKey(taskName)) {
			Map map = (Map) taskMap.get(taskName);
			return map.size();
		} else {
			throw new IllegalArgumentException("not found task = " + taskName);
		}
	}
}
