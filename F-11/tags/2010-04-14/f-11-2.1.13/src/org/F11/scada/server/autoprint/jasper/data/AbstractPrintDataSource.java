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

package org.F11.scada.server.autoprint.jasper.data;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import org.F11.scada.server.autoprint.jasper.exportor.*;
import org.F11.scada.util.ConnectionUtil;
import org.apache.log4j.Logger;

import dori.jasper.engine.JRException;
import dori.jasper.engine.JasperPrint;
import dori.jasper.engine.JasperReport;
import dori.jasper.engine.design.JRDefaultCompiler;
import dori.jasper.engine.design.JasperDesign;
import dori.jasper.engine.fill.JRFiller;
import dori.jasper.engine.xml.JRXmlLoader;

/**
 * �f�[�^�x�[�X��������̊��A���S���Y���N���X�ł��B
 * @author Hideaki Maekawa <frdm@users.sourceforge.jp>
 */
abstract public class AbstractPrintDataSource implements PrintDataSource {
	/** ����f�U�C���\�[�X�̃v���p�e�B�[�� */
	static final String PROPERTY_DESIGNPATH = "designPath";
	/** �f�[�^�\�[�X�̃v���p�e�B�[�� */
	static final String PROPERTY_DATASOURCE = "dataSourceSql";
	/** ���̃N���X�̃v���p�e�B */
	private final Properties properties;
	/** �o�̓N���X */
	private List exportors;
	/** Logging API */
	protected static Logger log = Logger.getLogger(AbstractPrintDataSource.class);

	/** ���������܂� */
	protected AbstractPrintDataSource() {
		this.properties = new Properties();
	}

	/**
	 * ���̃N���X�̃v���p�e�B�[��ݒ肵�܂�
	 * @param key �v���p�e�B�[��
	 * @param value �v���p�e�B�[�l
	 */
	public void setProperty(String key, String value) {
		this.properties.setProperty(key, value);
	}

	/**
	 * �o�̓N���X��ݒ肵�܂�
	 * @param exportor �o�̓N���X
	 */
	public void addExportor(Exportor exportor) {
		if (this.exportors == null) {
			this.exportors = new ArrayList();
		}
		this.exportors.add(exportor);
	}

	/**
	 * ������������s
	 */
	public void run() {
		String dataSourceSql = this.properties.getProperty(PROPERTY_DATASOURCE);
		String designPath = this.properties.getProperty(PROPERTY_DESIGNPATH);
		if (dataSourceSql == null) {
			throw new IllegalStateException("Property dataSourceSql not found.");
		}
		if (designPath == null) {
			throw new IllegalStateException("Property designSql not found.");
		}
		if (this.exportors == null || this.exportors.size() < 1) {
			throw new IllegalStateException("exportor not found.");
		}

		Connection con = null;
		PreparedStatement st = null;
		ResultSet rs = null;
		try {
			if (log.isInfoEnabled()) {
				log.info("Jasper Design Path : " + designPath);
			}
			JasperDesign jasperDesign = JRXmlLoader.load(designPath);
			jasperDesign.setQuery(null);
			JasperReport jasperReport =
				new JRDefaultCompiler().compileReport(jasperDesign);
	
			con = ConnectionUtil.getConnection();
			st = con.prepareStatement(dataSourceSql
                    + " WHERE f_date BETWEEN ? AND ? AND f_revision = 0",
                    ResultSet.TYPE_SCROLL_INSENSITIVE,
                    ResultSet.CONCUR_READ_ONLY);
			st.setTimestamp(1, getStartTimestamp());
			st.setTimestamp(2, getEndTimestamp());
			rs = st.executeQuery();
			if (log.isInfoEnabled()) {
				rs.last();
				log.info("Logging data fetch count : " + rs.getRow());
				rs.beforeFirst();
			}
			HashMap parameters = new HashMap();
			parameters.put("DataFile", "JRLogingDataSource.java");
			JasperPrint print =
				JRFiller.fillReport(
					jasperReport,
					parameters,
					new JRLogingDataSource(rs));
			printExportor(print);
		} catch (JRException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
			}
			if (st != null) {
				try {
					st.close();
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
			}
			if (con != null) {
				try {
					con.close();
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
			}
		}
	}

	private void printExportor(JasperPrint print) throws JRException {
		for (Iterator it = this.exportors.iterator(); it.hasNext();) {
			Exportor exportor = (Exportor) it.next();
			exportor.export(print);
		}
	}

	/**
	 * �f�[�^�\�[�X�̊J�n������Ԃ��悤�Ɏ������܂��B
	 * @return �f�[�^�\�[�X�̊J�n����
	 */
	abstract protected Timestamp getStartTimestamp();

	/**
	 * �f�[�^�\�[�X�̏I��������Ԃ��悤�Ɏ������܂��B
	 * @return �f�[�^�\�[�X�̏I������
	 */
	abstract protected Timestamp getEndTimestamp();

	/**
	 * ���̃I�u�W�F�N�g�̕�����\����Ԃ��܂�
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return "properties=[" + properties + "], exportors=[" + exportors + "]";
	}

}
