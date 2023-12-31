/*
 * 作成日: 2009/02/10
 *
 * TODO この生成されたファイルのテンプレートを変更するには次へジャンプ:
 * ウィンドウ - 設定 - Java - コード・スタイル - コード・テンプレート
 */
package org.F11.scada.server.command;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.GregorianCalendar;

import org.F11.scada.server.alarm.DataValueChangeEventKey;

import junit.framework.TestCase;

public class GeppoPrintCommandTest extends TestCase {
	private static final String IN_FILE1 = "M200901.csv";
	private static final String IN_FILE2 = "M200812.csv";
	private static final String OUT_FILE = "GeppoPrintOut.csv";

	protected void setUp() throws Exception {
		super.setUp();
		BufferedWriter bw = new BufferedWriter(new FileWriter(IN_FILE1));
		bw.write("test1\n");
		bw.flush();
		bw.close();
		bw = new BufferedWriter(new FileWriter(IN_FILE2));
		bw.write("test2\n");
		bw.flush();
		bw.close();
	}

	protected void tearDown() throws Exception {
		new File(IN_FILE1).delete();
		new File(IN_FILE2).delete();
		new File(OUT_FILE).delete();
		super.tearDown();
	}

	public void testExecute() throws Exception {
		GeppoPrintCommand cmd = new GeppoPrintCommand();
		cmd.setOutname(OUT_FILE);
		cmd.setHeader1("head1");
		cmd.setHeader2("head2");
		cmd.setHeader3("head3");
		cmd.setHeader4("head4");
		cmd.setHeader5("head5");
		cmd.setCsv_dir("");
		cmd.setCsv_head("M");
		cmd.setCsv_mid("yyyyMM");
		cmd.setCsv_foot(".csv");
		cmd.setPath("cmd.exe");

		Calendar cal = new GregorianCalendar(2009, 1, 1, 0, 0);
		cmd.execute(new DataValueChangeEventKey(0, "P1", "D_1900000_Digital",
				Boolean.TRUE, new Timestamp(cal.getTimeInMillis())));
		Thread.sleep(200);

		BufferedReader br = new BufferedReader(new FileReader(OUT_FILE));
		assertEquals("head1", br.readLine());
		assertEquals("head2", br.readLine());
		assertEquals("head3", br.readLine());
		assertEquals("head4", br.readLine());
		assertEquals("head5", br.readLine());
		assertEquals("test1", br.readLine());
		br.close();

		cal.set(2009, 0, 1, 0, 0);
		cmd.execute(new DataValueChangeEventKey(0, "P1", "D_1900000_Digital",
				Boolean.TRUE, new Timestamp(cal.getTimeInMillis())));
		Thread.sleep(200);

		br = new BufferedReader(new FileReader(OUT_FILE));
		assertEquals("head1", br.readLine());
		assertEquals("head2", br.readLine());
		assertEquals("head3", br.readLine());
		assertEquals("head4", br.readLine());
		assertEquals("head5", br.readLine());
		assertEquals("test2", br.readLine());
		br.close();

		cal.set(2009, 1, 28, 0, 0);
		cmd.execute(new DataValueChangeEventKey(0, "P1", "D_1900000_Digital",
				Boolean.TRUE, new Timestamp(cal.getTimeInMillis())));
		Thread.sleep(200);

		br = new BufferedReader(new FileReader(OUT_FILE));
		assertEquals("head1", br.readLine());
		assertEquals("head2", br.readLine());
		assertEquals("head3", br.readLine());
		assertEquals("head4", br.readLine());
		assertEquals("head5", br.readLine());
		assertEquals("test1", br.readLine());
		br.close();
	}

}
