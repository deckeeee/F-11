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
package org.F11.scada.server.autoprint;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.F11.scada.server.autoprint.perser.ColumnBeans;
import org.F11.scada.server.io.AutoPrintDataService;
import org.F11.scada.server.io.postgresql.S2ContainerUtil;
import org.F11.scada.server.register.HolderString;
import org.apache.log4j.Logger;
import org.seasar.framework.container.S2Container;

/**
 * @author hori
 */
public class CsvExecAutoPrintTask implements AutoPrintTask {
	private static Logger logger = Logger.getLogger(CsvExecAutoPrintTask.class);
	private String name;
	private String logg;
	private String tmpdir;
	private String tmpfile;
	private boolean data_head = false;
	private AutoPrintSchedule schedule;
	private AutoPrintDataService stor;

	private List csvHeadLines;
	private List dataHolders;
	private List executeList;

	private Thread thread;
	private AutoPrintDataServiceFactory factory;
	
	public CsvExecAutoPrintTask() {
		this(getDefaultFactory());
	}

	private static AutoPrintDataServiceFactory getDefaultFactory() {
		S2Container container = S2ContainerUtil.getS2Container();
		return (AutoPrintDataServiceFactory)
			container.getComponent(AutoPrintDataServiceFactory.class);
	}

	public CsvExecAutoPrintTask(AutoPrintDataServiceFactory factory) {
		csvHeadLines = new ArrayList();
		dataHolders = new ArrayList();
		executeList = new ArrayList();
		this.factory = factory;
	}

	public String getName() {
		return name;
	}
	public void setAutoPrintSchedule(AutoPrintSchedule schedule) {
		this.schedule = schedule;
		if (thread == null) {
			thread = new Thread(this);
			thread.setName(getClass().getName());
			thread.start();
		}
	}

	public void setAutoPrintDataStore() {
		if (null == stor) {
			stor = factory.getAutoPrintDataService(logg);
		}
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setLogg(String logg) {
		this.logg = logg;
	}

	public void setTmpdir(String tmpdir) {
		this.tmpdir = tmpdir;
	}

	public void setTmpfile(String tmpfile) {
		this.tmpfile = tmpfile;
	}

	public synchronized void setHead(String line) {
		csvHeadLines.add(line);
	}

	public void setData_head(String dhead) {
		if (dhead != null && "true".equals(dhead.toLowerCase())) {
			data_head = true;
		} else {
			data_head = false;
		}
	}

	public synchronized void addColumnBeans(ColumnBeans column) {
	    dataHolders.add(new HolderString(column.getProvider(), column.getHolder()));
	}

	public synchronized void setExecute(String execute) {
		executeList.add(execute);
	}

	public void run() {
		boolean exeflg = false;
		Thread ct = Thread.currentThread();
		while (thread == ct) {
			if (schedule != null && schedule.isNow()) {
				if (!exeflg) {
					exeflg = true;
					try {
						createListOutCsv();
						executeCommand();
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			} else {
				exeflg = false;
			}

			try {
				Thread.sleep(5000);
			} catch (InterruptedException e) {
			}
		}
	}

	public void stopTask() {
	    if (thread != null) {
	        Thread th = thread;
			thread = null;
			th.interrupt();
	    }
	}

	private synchronized void createListOutCsv() throws IOException {
		if (null != stor) {
			logger.info("run thread.");
			makeDir();
			BufferedWriter out = null;
			try {
				out = getWriter();
				writeCsvHeadLine(out);
				writeHeader(out);
				writeData(out);
				out.flush();
			} finally {
				if (out != null) {
					out.close();
				}
			}
		}
	}

	private void makeDir() {
		File dir = new File(tmpdir);
		if (!dir.exists()) {
			dir.mkdirs();
		}
	}

	private BufferedWriter getWriter() throws UnsupportedEncodingException, FileNotFoundException {
		return
			new BufferedWriter(
				new OutputStreamWriter(
					new FileOutputStream(new File(tmpdir + "/" + tmpfile)),
					"Windows-31J"));
	}

	private void writeCsvHeadLine(BufferedWriter out) throws IOException {
		for (Iterator it = csvHeadLines.iterator(); it.hasNext();) {
			out.write((String) it.next());
			out.newLine();
		}
	}

	private void writeHeader(BufferedWriter out) throws IOException {
		if (data_head) {
			List hl = stor.getLoggingHeddarList(logg, dataHolders);
			out.write("日付,時刻");
			for (Iterator it = hl.iterator(); it.hasNext();) {
				Map rec = (Map) it.next();
				out.write(",");
				out.write((String) rec.get("unit"));
			}
			out.newLine();
			out.write("日付,時刻");
			for (Iterator it = hl.iterator(); it.hasNext();) {
				Map rec = (Map) it.next();
				out.write(",");
				out.write((String) rec.get("name"));
			}
			out.newLine();
			out.write("日付,時刻");
			for (Iterator it = hl.iterator(); it.hasNext();) {
				Map rec = (Map) it.next();
				out.write(",");
				out.write((String) rec.get("unit_mark"));
			}
			out.newLine();
		}
	}

	private void writeData(BufferedWriter out) throws IOException {
		List datas =
			stor.getLoggingDataList(
				logg,
				schedule.getStartTime(),
				schedule.getEndTime(),
				dataHolders);

		for (Iterator it = datas.iterator(); it.hasNext();) {
			String csvData = (String) it.next();
			out.write(csvData);
			out.newLine();
		}
	}

	private void executeCommand() throws IOException {
		if (stor == null)
			return;
		String[] commands = null;
		synchronized (this) {
			commands = new String[executeList.size()];
			int i = 0;
			for (Iterator it = executeList.iterator(); it.hasNext(); i++) {
				commands[i] = (String) it.next();
			}
		}
		Runtime.getRuntime().exec(commands);
		logger.info("自動印字コマンド起動 : " + "name=" + name + ", logg=" + logg);
	}

	public String toString() {
		return 
		"name=" + name + ", " +
		"logg=" + logg + ", " +
		"tmpdir=" + tmpdir + ", " +
		"tmpfile=" + tmpfile;
	}
}
