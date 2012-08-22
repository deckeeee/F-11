package org.F11.scada.server.logging.report;

import java.io.File;
import java.io.FilenameFilter;

/**
 * 先頭文字と末尾文字を指定するファイルフィルタークラス
 *
 * @author hori
 */
public class CsvFilter implements FilenameFilter {
	private String head;
	private String foot;

	public CsvFilter(String head, String foot) {
		this.head = head;
		this.foot = foot;
	}

	public boolean accept(File dir, String name) {
		return name.startsWith(head) && name.endsWith(foot);
	}
}
