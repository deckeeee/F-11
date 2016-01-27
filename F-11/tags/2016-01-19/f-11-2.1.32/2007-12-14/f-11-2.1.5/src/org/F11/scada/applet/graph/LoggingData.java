package org.F11.scada.applet.graph;

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
 *
 */

import java.sql.Timestamp;

import org.F11.scada.data.LoggingRowData;
import org.apache.commons.collections.primitives.ArrayDoubleList;
import org.apache.commons.collections.primitives.DoubleIterator;
import org.apache.commons.collections.primitives.DoubleList;

/**
 * ���M���O�f�[�^��\���N���X�ł��B
 */
public class LoggingData implements DoubleIterator, LoggingRowData, java.io.Serializable {
	/** serial version UID */
	private static final long serialVersionUID = 1439365531437150528L;
	/**
	 * �^�C���X�^���v
	 * @serial ���̃��R�[�h�̃^�C���X�^���v
	 */
	private final Timestamp timestamp;
	/**
	 * �V���[�Y�f�[�^���X�g
	 * @serial ���̃��R�[�h�̃V���[�Y�f�[�^���X�g
	 */
	private final DoubleList seriesList;
	/** �V���[�Y�f�[�^�����q */
	private transient DoubleIterator seriesIterator;

	/**
	 * �R���X�g���N�^
	 * @param timestamp �^�C���X�^���v
	 * @param seriesList �V���[�Y�f�[�^���X�g
	 */
	public LoggingData (Timestamp timestamp, DoubleList seriesList) {
		this.timestamp = timestamp;
		this.seriesList = seriesList;
	}

	/**
	 * ���R�[�h�̃^�C���X�^���v��Ԃ��܂�
	 * @return ���R�[�h�̃^�C���X�^���v
	 */
	public Timestamp getTimestamp() {
		return timestamp;
	}

	/**
	 * ���̃��R�[�h�̎w���l��Ԃ��܂��B
	 * @param column �w���
	 * @return ���̃��R�[�h�̎w���l
	 */
	public double getDouble(int column) {
		return seriesList.get(column);
	}

	/**
	 * �V���[�Y�f�[�^��Ԃ��A�|�C���^�����ɐi�߂܂��B
	 * @return �V���[�Y�f�[�^�̃C���X�^���X�B
	 */
	public double next() {
		createIterator();
		return seriesIterator.next();
	}

	/**
	 * ���̃V���[�Y�f�[�^�����݂���΁Atrue ��Ԃ��܂��B
	 * @return ���̃V���[�Y�f�[�^�����݂���΁Atrue �𑶍݂��Ȃ���� false ��Ԃ��܂��B
	 */
	public boolean hasNext() {
		createIterator();
		return seriesIterator.hasNext();
	}

	/**
	 * �V���[�Y�f�[�^���X�g�̃|�C���^���ŏ��ɐݒ肵�܂��B
	 */
	public void first() {
		seriesIterator = seriesList.iterator();
	}

	/**
	 * ���̃��\�b�h�̓T�|�[�g���Ă��܂���B
	 */
	public void remove() {
		throw new UnsupportedOperationException();
	}

	private void createIterator() {
		if (seriesIterator == null) {
			seriesIterator = seriesList.iterator();
		}
	}
	
	/**
	 * ���̃I�u�W�F�N�g�̕�����\����Ԃ��܂��B
	 */
	public String toString() {
		return "timestamp=" + timestamp + ", seriesList=" + seriesList;
	}

	/**
	 * ���̃��R�[�h�̃f�[�^�̃��X�g��Ԃ��܂��B
	 * @return ���̃��R�[�h�̃f�[�^�̃��X�g��Ԃ��܂��B
	 */
	public DoubleList getList() {
		return new ArrayDoubleList(seriesList);
	}

}
