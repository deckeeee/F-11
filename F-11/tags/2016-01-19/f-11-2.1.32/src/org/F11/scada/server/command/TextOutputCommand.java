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

package org.F11.scada.server.command;

import static org.F11.scada.util.AttributesUtil.*;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.nio.charset.Charset;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.RejectedExecutionException;

import jp.gr.javacons.jim.DataHolder;
import jp.gr.javacons.jim.Manager;

import org.F11.scada.data.ConvertValue;
import org.F11.scada.data.WifeData;
import org.F11.scada.data.WifeDataAnalog;
import org.F11.scada.data.WifeDataDigital;
import org.F11.scada.server.alarm.DataValueChangeEventKey;
import org.F11.scada.server.logging.report.CsvFilter;
import org.F11.scada.util.FileUtil;
import org.F11.scada.xwife.server.WifeDataProvider;
import org.apache.commons.digester.Digester;
import org.apache.log4j.Logger;
import org.xml.sax.SAXException;

/**
 * 任意の形式でテキストファイルを出力できる、サーバーコマンドです。
 *
 * @author Hideaki Maekawa <frdm@users.sourceforge.jp>
 */
public class TextOutputCommand implements Command {
	/** 出力定義ファイルパス名 */
	private String defPath;
	/** 出力ファイルフォルダ */
	private String outDir;
	/** 出力ファイルヘッダ */
	private String outHead;
	/** 出力ファイル日時フォーマット */
	private String outMid = "";
	/** 出力ファイル拡張子 */
	private String outFoot;
	/** 出力ファイル保持ファイル数 */
	private int keep;
	/** true の場合、データはファイルの先頭ではなく最後に書き込まれる */
	private boolean isAppend;
	/** 出力ファイルの文字エンコーディング */
	private String csn = "Windows-31J";
	/** 書き込みエラーリトライ間隔 */
	private long errorRetryTime = 10000;
	/** 書き込みエラーリトライ回数 */
	private int errorRetryCount = 10;
	/** 出力ファイル日時フォーマットのオフセット(秒) */
	private long outMidOffset;
	/** デジタルホルダの出力形式(trueならboolean形式、falseなら1 or 0で出力) */
	private boolean isDigitalMode;
	/** Logging API */
	private static Logger log = Logger.getLogger(TextOutputCommand.class);
	/** スレッドプール実行クラス */
	private static Executor executor = Executors.newCachedThreadPool();
	/** 出力ファイル定義プロパティ */
	private List<Property> properties = new ArrayList<Property>();
	/** テストモードの場合出力定義プロパティを毎回読み直す */
	private boolean isTestMode;

	/**
	 * コマンドを実行します
	 *
	 * @param evt データ変更イベント
	 */
	public void execute(DataValueChangeEventKey evt) {
		checkPath();
		if (isTestMode) {
			properties.clear(); // テスト用
		}
		if (properties.isEmpty()) {
			perseXml();
		}
		executeTask(evt);
	}

	private void checkPath() {
		if (isSpaceOrNull(defPath)) {
			throw new IllegalStateException("出力定義ファイルパス名(defPath)が設定されていません。");
		}
		if (isSpaceOrNull(outDir)) {
			throw new IllegalStateException("出力ファイルフォルダ(outDir)が設定されていません。 ");
		}
		if (isSpaceOrNull(outHead)) {
			throw new IllegalStateException("出力ファイルヘッダ(outHead)が設定されていません。 ");
		}
		if (isSpaceOrNull(outFoot)) {
			throw new IllegalStateException("出力ファイル拡張子(outFoot)が設定されていません。 ");
		}
		if (!Charset.isSupported(csn)) {
			throw new IllegalStateException("サポートされない文字エンコードが指定されました。csn = "
				+ csn);
		}
	}

	private void executeTask(DataValueChangeEventKey evt) {
		if (evt.getValue()) {
			try {
				executor.execute(new TextOutputCommandTask(evt));
			} catch (RejectedExecutionException e) {
				log.fatal("TextOutputCommandTask実行時にエラーが発生", e);
			}
		}
	}

	private void perseXml() {
		URL xml = getClass().getResource(defPath);
		if (xml == null) {
			IllegalStateException e =
				new IllegalStateException("出力定義ファイル " + defPath + " が存在しません。");
			log.error("出力定義ファイル " + defPath + " が存在しません。", e);
			throw e;
		}

		Digester digester = getDigester();
		InputStream is = null;
		try {
			is = xml.openStream();
			digester.parse(is);
		} catch (IOException e) {
			log.error("出力定義ファイル読み込み中にI/Oエラーが発生", e);
		} catch (SAXException e) {
			log.error("出力定義ファイル解析中に文法エラーが発生", e);
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

	private Digester getDigester() {
		Digester digester = new Digester();
		URL url = getClass().getResource("/resources/textoutput10.dtd");
		if (null == url) {
			throw new IllegalStateException(
				"/resources/textoutput10.dtd がクラスパス上に存在しません");
		}
		digester.register(
			"-//F-11 2.0//DTD F11 Textout Configuration//EN",
			url.toString());
		digester.setValidating(true);
		digester.push(this);
		addPageRule(digester);
		return digester;
	}

	private void addPageRule(Digester digester) {
		digester.addCallMethod("textout/property", "addProperty", 2);
		digester.addCallParam("textout/property", 0, "name");
		digester.addCallParam("textout/property", 1, "value");
	}

	/**
	 * テキスト出力定義フォーマットをプロパティに追加します
	 *
	 * @param name プロパティ名
	 * @param value 値
	 */
	public void addProperty(String name, String value) {
		Property o = new Property();
		o.setName(name);
		o.setValue(value);
		properties.add(o);
	}

	/**
	 * 定義パス名を設定します
	 *
	 * @param string 定義パス名
	 */
	public void setDefPath(String string) {
		defPath = string;
	}

	/**
	 * 出力ディレクトリーを設定します。末尾が"/"でなければ追加します。
	 *
	 * @param outDir 出力ディレクトリーを設定します。末尾が"/"でなければ追加します。
	 */
	public void setOutDir(String outDir) {
		if (outDir.endsWith("/") || outDir.endsWith("\\")) {
			this.outDir = outDir;
		} else {
			this.outDir = outDir + "/";
		}
	}

	public void setOutHead(String outHead) {
		this.outHead = outHead;
	}

	public void setOutMid(String outMid) {
		this.outMid = outMid;
	}

	public void setOutFoot(String outFoot) {
		this.outFoot = outFoot;
	}

	public void setKeep(int keep) {
		this.keep = keep;
	}

	public void setCsn(String csn) {
		this.csn = csn;
	}

	/**
	 * 書き込みエラーリトライ回数を設定します
	 *
	 * @param i 書き込みエラーリトライ回数
	 */
	public void setErrorRetryCount(int i) {
		errorRetryCount = i;
	}

	/**
	 * 書き込みエラーリトライ間隔を設定します。
	 *
	 * @param i 書き込みエラーリトライ間隔
	 */
	public void setErrorRetryTime(int i) {
		errorRetryTime = i;
	}

	public void setOutMidOffset(long outMidOffset) {
		this.outMidOffset = outMidOffset;
	}

	/**
	 * テストモードを設定します。テストモードは毎回出力定義プロパティを読み直します。
	 *
	 * @param isTestMode trueの場合テストモード。
	 */
	public void setTestMode(boolean isTestMode) {
		this.isTestMode = isTestMode;
	}

	/**
	 * アウトプットファイルの書込モードを設定します。trueなら追記書込をします。
	 *
	 * @param isAppend アウトプットファイルの書込モードを設定します。trueなら追記書込をします。
	 */
	public void setAppend(boolean isAppend) {
		this.isAppend = isAppend;
	}

	public void setDigitalMode(boolean isDigital) {
		this.isDigitalMode = isDigital;
	}

	/**
	 * Executor で実行されるタスクのクラスです。
	 *
	 * @author Hideaki Maekawa <frdm@users.sourceforge.jp>
	 */
	private class TextOutputCommandTask implements Runnable {
		private final DataValueChangeEventKey evt;

		public TextOutputCommandTask(DataValueChangeEventKey evt) {
			this.evt = evt;
		}

		/**
		 * Executor により実行されるメソッドです。
		 */
		public void run() {
			PrintWriter out = null;
			try {
				for (int i = 1; i <= errorRetryCount; i++) {
					try {
						out =
							FileUtil
								.getPrintWriter(getOutPath(), isAppend, csn);
						writeFile(out);
						log.info("TextOutputCommandTask 出力しました。 file = "
							+ getOutPath());
						break;
					} catch (UnsupportedEncodingException e) {
						log.info("サポートされない文字エンコードが指定されました。 csn = " + csn, e);
					} catch (IOException e) {
						logWrite(i);
						sleep();
						continue;
					}
				}
			} finally {
				if (out != null) {
					out.close();
				}
			}
			removeOldFile();
		}

		private String getOutPath() {
			Format f = new SimpleDateFormat(outMid);
			return outDir
				+ outHead
				+ f.format(new Date(evt.getTimeStamp().getTime()
					- outMidOffset
					* 1000))
				+ outFoot;
		}

		private void sleep() {
			try {
				Thread.sleep(errorRetryTime);
			} catch (InterruptedException e1) {
			}
		}

		private void removeOldFile() {
			FilenameFilter filter = new CsvFilter(outHead, outFoot);
			File dir = new File(outDir);
			FileUtil.removeOldFile(dir.listFiles(filter), keep);
		}

		private void logWrite(int i) {
			log.error("書き込みエラーが発生しました。"
				+ errorRetryTime
				+ "ミリ秒後にリトライします。 ("
				+ i
				+ "/"
				+ errorRetryCount
				+ ")");
		}

		private void writeFile(PrintWriter out) {
			for (Property p : properties) {
				if (p.isData()) {
					DataHolder dh =
						Manager.getInstance().findDataHolder(p.getValue());
					if (dh == null) {
						IllegalStateException e =
							new IllegalStateException(p.getValue()
								+ " が存在しません。");
						log.error("", e);
						throw e;
					} else {
						WifeData wa = (WifeData) dh.getValue();
						if (wa instanceof WifeDataAnalog) {
							WifeDataAnalog da = (WifeDataAnalog) dh.getValue();
							ConvertValue conv =
								(ConvertValue) dh
									.getParameter(WifeDataProvider.PARA_NAME_CONVERT);
							out
								.print(conv.convertStringValue(da.doubleValue()));
						} else if (wa instanceof WifeDataDigital) {
							WifeDataDigital da =
								(WifeDataDigital) dh.getValue();
							boolean bit = da.isOnOff(true);
							out.print(getDigitalValue(bit));
						} else {
							IllegalStateException e =
								new IllegalStateException(p.getValue()
									+ " がデジタル又はアナログではありません。");
							log.error("", e);
							throw e;
						}
					}
				} else if ("text".equalsIgnoreCase(p.getName())) {
					out.print(p.getValue());
				} else {
					IllegalArgumentException e =
						new IllegalArgumentException(
							"name属性がtext又はdataではありません。");
					log.error("", e);
					throw e;
				}
			}
		}

		private String getDigitalValue(boolean bit) {
			if (isDigitalMode) {
				return bit ? "true" : "false";
			} else {
				return bit ? "1" : "0";
			}
		}
	}

	/**
	 * テキスト出力のフォーマット定義プロパティ name:text か dataを設定。
	 * value:name属性がtextならvalue値をそのままテキスト出力。dataならホルダの内容を出力。
	 *
	 * @author maekawa
	 *
	 */
	private static class Property {
		private String name;
		private String value;

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public String getValue() {
			return value;
		}

		public void setValue(String value) {
			this.value = value;
		}

		public boolean isData() {
			return "data".equalsIgnoreCase(name);
		}

		@Override
		public String toString() {
			return name + " " + value;
		}
	}
}
