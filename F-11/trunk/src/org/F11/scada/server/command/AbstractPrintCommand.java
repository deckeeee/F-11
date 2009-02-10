/*
 * Projrct F-11 - Web SCADA for Java Copyright (C) 2002 Freedom, Inc. All Rights
 * Reserved. This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or (at your
 * option) any later version. This program is distributed in the hope that it
 * will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty
 * of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General
 * Public License for more details. You should have received a copy of the GNU
 * General Public License along with this program; if not, write to the Free
 * Software Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA
 * 02111-1307, USA.
 */
package org.F11.scada.server.command;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;

import jp.gr.javacons.jim.DataHolder;
import jp.gr.javacons.jim.Manager;

import org.F11.scada.data.ConvertValue;
import org.F11.scada.data.WifeDataAnalog;
import org.F11.scada.server.alarm.DataValueChangeEventKey;
import org.F11.scada.xwife.server.WifeDataProvider;
import org.apache.log4j.Logger;

/**
 * 印刷用コマンドの基底クラスです。
 */
public abstract class AbstractPrintCommand implements Command {
	/** Logging API */
	private static Logger log = Logger.getLogger(AbstractPrintCommand.class);

	private String outname;
	private String header1;
	private String header2;
	private String header3;
	private String header4;
	private String provider;
	private String holder;
	private String header5;
	protected String csv_dir;
	protected String csv_head;
	protected String csv_mid;
	protected String csv_foot;
	private String path;
	private String param1;
	private String param2;

	public void setOutname(String outfile) {
		this.outname = outfile;
	}

	public void setHeader1(String header1) {
		this.header1 = header1;
	}

	public void setHeader2(String header2) {
		this.header2 = header2;
	}

	public void setHeader3(String header3) {
		this.header3 = header3;
	}

	public void setProvider(String provider) {
		this.provider = provider;
	}

	public void setHolder(String holder) {
		this.holder = holder;
	}

	public void setHeader5(String header5) {
		this.header5 = header5;
	}

	public void setCsv_dir(String csv_dir) {
		this.csv_dir = csv_dir;
	}

	public void setCsv_head(String csv_head) {
		this.csv_head = csv_head;
	}

	public void setCsv_mid(String csv_mid) {
		this.csv_mid = csv_mid;
	}

	public void setCsv_foot(String csv_foot) {
		this.csv_foot = csv_foot;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public void setParam1(String param1) {
		this.param1 = param1;
	}

	public void setParam2(String param2) {
		this.param2 = param2;
	}

	public void setHeader4(String header4) {
		this.header4 = header4;
	}

	protected class ListOutPrintTask implements Runnable {
		/** データ変更イベントの参照 */
		private final DataValueChangeEventKey evt;
		private final String msg;
		private final String csvname;
		private final String[] exec_param;

		public ListOutPrintTask(DataValueChangeEventKey evt, String msg,
				String csvname) {
			this.evt = evt;
			this.msg = msg;
			this.csvname = csvname;

			if (path != null && param1 != null && param2 != null)
				exec_param = new String[]{path, param1, param2};
			else if (path != null && param1 != null)
				exec_param = new String[]{path, param1};
			else
				exec_param = new String[]{path};
		}

		public void run() {
			if (evt.getValue().booleanValue()) {
				PrintWriter pw = null;
				BufferedReader br = null;
				try {
					pw = new PrintWriter(new BufferedWriter(new FileWriter(
							outname)));
					pw.println(header1);
					pw.println(header2);
					pw.println(header3);
					if (header4 == null) {
						DataHolder dh = Manager.getInstance().findDataHolder(
								provider, holder);
						WifeDataAnalog da = (WifeDataAnalog) dh.getValue();
						ConvertValue conv = (ConvertValue) dh.getParameter(WifeDataProvider.PARA_NAME_CONVERT);
						pw.println(conv.convertStringValue(da.doubleValue()));
					} else {
						pw.println(header4);
					}
					pw.println(header5);
					pw.flush();

					br = new BufferedReader(new FileReader(csvname));
					String line;
					while ((line = br.readLine()) != null)
						pw.println(line);
					pw.flush();

					br.close();
					br = null;
					pw.close();
					pw = null;

					Runtime run = Runtime.getRuntime();
					run.exec(exec_param);
					log.info(msg + " を印刷しました。");
				} catch (Exception e) {
					log.fatal(msg + " の印刷に失敗しました。", e);
				} finally {
					if (br != null) {
						try {
							br.close();
						} catch (Exception e) {
						}
					}
					if (pw != null) {
						try {
							pw.close();
						} catch (Exception e) {
						}
					}
				}
			}
		}
	}
}
