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

import java.io.File;
import java.io.FileInputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Date;

import jp.gr.javacons.jim.DataProvider;
import jp.gr.javacons.jim.Manager;

import org.F11.scada.server.autoprint.jasper.exportor.PdfExportor;
import org.F11.scada.server.autoprint.jasper.exportor.PrintExportor;
import org.F11.scada.test.util.TestConnectionUtil;
import org.F11.scada.test.util.TestUtil;
import org.F11.scada.test.util.TimestampUtil;
import org.F11.scada.util.ConnectionUtil;
import org.dbunit.DatabaseTestCase;
import org.dbunit.database.DatabaseConnection;
import org.dbunit.database.IDatabaseConnection;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSet;

/**
 * PrintDataSourceAlgorithmのテストケース
 * @author Hideaki Maekawa <frdm@users.sourceforge.jp>
 */
public class MonthlyPrintDataSourceTest extends DatabaseTestCase {
	private static final String DROP_SQL = "DROP TABLE log_table_monthly";
	private static final String SQL = "SELECT * FROM log_table_monthly";
	private static final String DESIGN = "design.xml";
	private static final String BASE = "src/org/F11/scada/server/autoprint/jasper/data/";
	private static final String CREATE_SQL = "create table log_table_monthly (f_date datetime, f_revision integer, f_P1_D_500_BcdSingle double, f_P1_D_501_BcdSingle double, f_P1_D_502_BcdSingle double, f_P1_D_503_BcdSingle double, f_P1_D_504_BcdSingle double, f_P1_D_505_BcdSingle double, f_P1_D_506_BcdSingle double, f_P1_D_507_BcdSingle double)";

	/**
	 * Constructor for PrintDataSourceAlgorithmTest.
	 * @param arg0
	 */
	public MonthlyPrintDataSourceTest(String arg0) {
		super(arg0);
	    Connection con = null;
	    Statement st = null;
		try {
		    con = TestConnectionUtil.getTestConnection();
		    st = con.createStatement();
		    st.execute(DROP_SQL);
		    st.execute(CREATE_SQL);
		} catch (Exception e) {
		    e.printStackTrace();
		} finally {
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

	protected void setUp() throws Exception {
		super.setUp();
		DataProvider dp = TestUtil.createDataProvider();
		Manager.getInstance().addDataProvider(dp);
	}

	protected void tearDown() throws Exception {
		DataProvider dp = Manager.getInstance().getDataProvider("P1");
		Manager.getInstance().removeDataProvider(dp);
		super.tearDown();
	}


	protected IDatabaseConnection getConnection() throws Exception {
		Connection con = ConnectionUtil.getConnection();
		return new DatabaseConnection(con);
	}

	protected IDataSet getDataSet() throws Exception {
		return new FlatXmlDataSet(new FileInputStream(BASE + "log_table_monthly.xml"));
	}

	public void testExeption() throws Exception {
		MonthlyPrintDataSource p = new MonthlyPrintDataSource();
		try {
			p.run();
			fail();
		} catch (IllegalStateException ex) {}

		p.setProperty(MonthlyPrintDataSource.PROPERTY_DATASOURCE, SQL);
		try {
			p.run();
			fail();
		} catch (IllegalStateException ex) {}

		p.setProperty(MonthlyPrintDataSource.PROPERTY_DESIGNPATH, DESIGN);
		try {
			p.run();
			fail();
		} catch (IllegalStateException ex) {}

		p = new MonthlyPrintDataSource();
		p.addExportor(new PrintExportor());
		try {
			p.run();
			fail();
		} catch (IllegalStateException ex) {}
	}

	public void testRun() throws Exception {
		MonthlyPrintDataSource p = new MonthlyPrintDataSource();
		p.setStartTimestamp(TimestampUtil.parse("2004/05/01 00:01:00"));
		p.setEndTimestamp(TimestampUtil.parse("2004/05/31 23:59:59"));
		p.setProperty(MonthlyPrintDataSource.PROPERTY_DATASOURCE, SQL);
		p.setProperty(MonthlyPrintDataSource.PROPERTY_DESIGNPATH, BASE + DESIGN);
		PdfExportor e = new PdfExportor();
		e.setProperty(PdfExportor.PROPERTY_OUT, BASE + "monthly.pdf");
		p.addExportor(e);
		p.run();
		File pdf = new File(BASE + "monthly.pdf");
		assertTrue(pdf.exists());
		pdf.delete();
	}

	public void testRun2() throws Exception {
		MonthlyPrintDataSource p = new MonthlyPrintDataSource();
		p.setStartTimestamp(TimestampUtil.parse("2004/05/01 00:01:00"));
		p.setEndTimestamp(TimestampUtil.parse("2004/05/31 23:59:59"));
		p.setProperty(MonthlyPrintDataSource.PROPERTY_DATASOURCE, SQL);
		p.setProperty(MonthlyPrintDataSource.PROPERTY_DESIGNPATH, BASE + DESIGN);
		PdfExportor e = new PdfExportor();
		e.setProperty(PdfExportor.PROPERTY_OUT, BASE + "monthly%yyyyMM%.pdf");
		p.addExportor(e);
		p.run();
		String nowStr = new SimpleDateFormat("yyyyMM").format(new Date());
		File pdf = new File(BASE + "monthly" + nowStr + ".pdf");
		assertTrue(pdf.exists());
		pdf.delete();
	}

	public void testRun3() throws Exception {
		MonthlyPrintDataSource p = new MonthlyPrintDataSource();
		p.setProperty(MonthlyPrintDataSource.PROPERTY_DATASOURCE, SQL);
		p.setProperty(MonthlyPrintDataSource.PROPERTY_DESIGNPATH, BASE + DESIGN);
		PdfExportor e = new PdfExportor();
		e.setProperty(PdfExportor.PROPERTY_OUT, BASE + "monthly.pdf");
		p.addExportor(e);
		p.run();
		File pdf = new File(BASE + "monthly.pdf");
		assertTrue(pdf.exists());
		pdf.delete();
	}
}
