/*
 * Created on 2003/02/27
 *
 * To change this generated comment go to 
 * Window>Preferences>Java>Code Generation>Code Template
 */
package org.F11.scada.server.logging.report;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Field;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;

import org.F11.scada.applet.graph.LoggingData;
import org.F11.scada.server.event.LoggingDataEvent;
import org.F11.scada.server.event.LoggingDataListener;
import org.F11.scada.server.io.ValueListHandlerElement;
/**
 * @author hori
 */
public class ReportPrintEXCEL implements LoggingDataListener {

	private static final Object lock = new Object();
	private ValueListHandlerElement handler;
	private ReportSchedule loggingSchedule;

	/**
	 * 
	 */
	public ReportPrintEXCEL(ValueListHandlerElement handler, String name) {
		super();
		this.handler = handler;
		try {
			Field scheduleType = ReportSchedule.class.getField(name);
			loggingSchedule = (ReportSchedule) scheduleType.get(null);
		} catch (NoSuchFieldException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
	}

	/* (non-Javadoc)
	 * @see org.F11.scada.server.event.LoggingDataListener#changeLoggingData(org.F11.scada.server.event.LoggingDataEvent)
	 */
	public void changeLoggingData(LoggingDataEvent event) {
		//TODO 絶対パスを埋め込むのはやめよ!!
		if (loggingSchedule.getKind() == '0')
			return;
		synchronized (lock) {
			BufferedWriter out = null;
			try {
				// csv 作成
				out =
					new BufferedWriter(
						new FileWriter(new File("./ReportExcel/ListOut.csv")));
				out.write('1');
				out.newLine();
				out.write(
					new File("./ReportExcel/TYOHYO.XLS").getAbsolutePath());
				out.newLine();
				out.write(loggingSchedule.getKind());
				out.newLine();
				out.write('0');
				out.newLine();
				out.write('4');
				out.newLine();

				handler.findRecord(
					loggingSchedule.startTime(event.getTimeStamp()));
				for (int rec = 0;
					handler.hasNext() && rec < loggingSchedule.getCount();
					rec++) {
					LoggingData data = (LoggingData) handler.next();
					DateFormat df = new SimpleDateFormat("yyyy/MM/dd,HH:mm:ss");
					out.write(df.format(data.getTimestamp()));
					for (data.first(); data.hasNext();) {
						out.write(',');
						out.write(String.valueOf(data.next()));
					}
					out.newLine();
				}
				out.flush();
				out.close();

				Process excel =
					Runtime.getRuntime().exec(
						new String[] {
							new File("c:/Program Files/Microsoft Office/Office/EXCEL.EXE")
								.getAbsolutePath(),
							new File("./ReportExcel/PRINT.XLA")
								.getAbsolutePath(),
							"/r" });
				try {
					excel.waitFor();
				} catch (InterruptedException e) {
				}

			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				if (out != null) {
					try {
						out.close();
					} catch (IOException e1) {
						e1.printStackTrace();
					}
				}
			}
		}
	}

	private static interface ReportSchedule {
		public abstract char getKind();
		public abstract Timestamp startTime(Timestamp now);
		public abstract int getCount();

		/** 分間隔のロギングスケジュール。 */
		public static final ReportSchedule MINUTE = new ReportSchedule() {
			public char getKind() {
				return '0';
			}
			public Timestamp startTime(Timestamp now) {
				return new Timestamp(now.getTime() - (now.getTime() % 3600000));
			}
			public int getCount() {
				return 60;
			}
		};
		/** 時間隔のロギングスケジュール */
		public static final ReportSchedule HOUR = new ReportSchedule() {
			public char getKind() {
				return '1';
			}
			public Timestamp startTime(Timestamp now) {
				return new Timestamp(
					now.getTime() - (now.getTime() % 86400000));
			}
			public int getCount() {
				return 24;
			}
		};
		/** 日間隔のロギングスケジュール */
		public static final ReportSchedule DAILY = new ReportSchedule() {
			public char getKind() {
				return '2';
			}
			public Timestamp startTime(Timestamp now) {
				Calendar cal = new GregorianCalendar();
				cal.setTimeInMillis(now.getTime());
				cal.set(Calendar.DAY_OF_MONTH, 1);
				cal.set(Calendar.HOUR_OF_DAY, 0);
				cal.set(Calendar.MINUTE, 0);
				cal.set(Calendar.SECOND, 0);
				cal.set(Calendar.MILLISECOND, 0);
				return new Timestamp(cal.getTimeInMillis());
			}
			public int getCount() {
				return 31;
			}
		};
		/** 月間隔のロギングスケジュール */
		public static final ReportSchedule MONTHLY = new ReportSchedule() {
			public char getKind() {
				return '3';
			}
			public Timestamp startTime(Timestamp now) {
				Calendar cal = new GregorianCalendar();
				cal.setTimeInMillis(now.getTime());
				cal.set(Calendar.MONTH, Calendar.JANUARY);
				cal.set(Calendar.DAY_OF_MONTH, 1);
				cal.set(Calendar.HOUR_OF_DAY, 0);
				cal.set(Calendar.MINUTE, 0);
				cal.set(Calendar.SECOND, 0);
				cal.set(Calendar.MILLISECOND, 0);
				return new Timestamp(cal.getTimeInMillis());
			}
			public int getCount() {
				return 12;
			}
		};
		/** 年間隔のロギングスケジュール */
		public static final ReportSchedule YEARLY = new ReportSchedule() {
			public char getKind() {
				return '0';
			}
			public Timestamp startTime(Timestamp now) {
				Calendar cal = new GregorianCalendar();
				cal.setTimeInMillis(now.getTime());
				cal.set(Calendar.MONTH, Calendar.JANUARY);
				cal.set(Calendar.DAY_OF_MONTH, 1);
				cal.set(Calendar.HOUR_OF_DAY, 0);
				cal.set(Calendar.MINUTE, 0);
				cal.set(Calendar.SECOND, 0);
				cal.set(Calendar.MILLISECOND, 0);
				cal.add(Calendar.YEAR, -5);
				return new Timestamp(cal.getTimeInMillis());
			}
			public int getCount() {
				return 5;
			}
		};
		/** 一定間隔のロギングスケジュール */
		public static final ReportSchedule REGULAR = new ReportSchedule() {
			public char getKind() {
				return '0';
			}
			public Timestamp startTime(Timestamp now) {
				return now;
			}
			public int getCount() {
				return 10;
			}
		};
	}

	/**
	 * テスト用
	 * @param args
	public static void main(String[] args) {

		File file = new File("./log");
		file.mkdir();

		try {
			URL url =
				new File("./resources/xwife_server_main_log4j.properties")
					.toURL();
			PropertyConfigurator.configure(url);

			Class.forName(WifeUtilities.getJdbcDriver());

			HandlerFactory factory =
				HandlerFactory.getHandlerFactory(
					"org.F11.scada.server.io.postgresql.PostgreSQLHandlerFactory");
			DataProvider dp = TestUtil.createDataProvider();
			DataHolder[] hds = dp.getDataHolders();
			List dataHolders = new ArrayList();
			for (int i = 0; i < hds.length; i++) {
				dataHolders.add(hds[i]);
			}

			ValueListHandlerElement handler =
				factory.createValueListHandler("log_table_hour", dataHolders);

			ReportPrintEXCEL po = new ReportPrintEXCEL(handler, "HOUR");
			po.changeLoggingData(null);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	 */

}
