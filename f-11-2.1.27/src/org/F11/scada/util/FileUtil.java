package org.F11.scada.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Iterator;

public abstract class FileUtil {
	/**
	 * 指定件数を残し、編集日付の古い順にファイルを削除する。0以下が指定された場合は削除処理しない。
	 *
	 * @param files ファイルの一覧
	 * @param cnt 残す件数。0以下が指定された場合は削除処理しない。
	 */
	public static void removeOldFile(File[] files, int cnt) {
		if (cnt <= 0 || null == files || files.length <= cnt) {
			return;
		}

		ArrayList<File> fileList = new ArrayList<File>(files.length);
		for (int i = 0; i < files.length; i++) {
			fileList.add(files[i]);
		}
		while (cnt < fileList.size()) {
			long first = System.currentTimeMillis();
			File firstFile = null;
			for (Iterator<File> it = fileList.iterator(); it.hasNext();) {
				File file = it.next();
				if (file.lastModified() < first) {
					first = file.lastModified();
					firstFile = file;
				}
			}
			firstFile.delete();
			fileList.remove(firstFile);
		}
	}

	/**
	 * バッファー済みで自動フラッシュするPrintWriteを返します。
	 *
	 * @param path 出力するパス名
	 * @param append 追記モード
	 * @param csn 文字エンコード
	 * @return バッファー済みのPrintWriteを返します。
	 * @throws UnsupportedEncodingException OSでサポートされていないエンコードが指定された場合スローします。
	 * @throws FileNotFoundException 出力するパスを作成できない場合スローします。
	 */
	public static PrintWriter getPrintWriter(String path,
			boolean append,
			String csn) throws UnsupportedEncodingException,
			FileNotFoundException {
		return new PrintWriter(new BufferedWriter(new OutputStreamWriter(
			new FileOutputStream(new File(path), append),
			csn)), true);
	}

	/**
	 * バッファー済みのテキストリーダーを返します。
	 *
	 * @param path 入力するファイル名
	 * @param csn 文字エンコード
	 * @return バッファー済みのテキストリーダーを返します。
	 * @throws UnsupportedEncodingException OSでサポートされていないエンコードが指定された場合スローします。
	 * @throws FileNotFoundException 入力するパスを作成できない場合スローします。
	 */
	public static BufferedReader getReader(String path, String csn)
			throws UnsupportedEncodingException, FileNotFoundException {
		return new BufferedReader(new InputStreamReader(new FileInputStream(
			new File(path)), csn));
	}
}
