/*
 * OWL(Online Watch Labour)
 * Copyright (C) 2007 Freedom, Inc. All Rights Reserved.
 *
 */

package org.F11.scada.misc.convert;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.text.Format;
import java.text.NumberFormat;
import java.util.Collection;
import java.util.Iterator;

import org.F11.scada.server.deploy.FileLister;

/**
 * ディレクトリ内のファイル名(拡張子のみも可)を小文字に統一します。
 *
 * @author maekawa
 *
 */
public class StepCount {
	/** 変名オブジェクト */
	private static FileFilter FILTER = new FileFilter() {
		public boolean accept(File pathname) {
			String n = pathname.getName();
			return pathname.isDirectory() || n.endsWith(".java");
		}
	};

	/**
	 * ディレクトリ内のファイル名(拡張子のみも可)を小文字に統一します。
	 *
	 * @param root 基本ディレクトリ
	 * @param rename 変名オブジェクト
	 */
	public StepCount(File root) {
		FileLister lister = new FileLister();
		Collection c = lister.listFiles(root, FILTER);
		int fileCount = 0;
		int count = 0;
		for (Iterator i = c.iterator(); i.hasNext();) {
			File file = (File) i.next();
			fileCount++;
			BufferedReader r = null;
			try {
				r = new BufferedReader(new FileReader(file));
				do {
					count++;
				} while (null != r.readLine());
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				if (null != r) {
					try {
						r.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}
		Format f = NumberFormat.getInstance();
		System.out.println("count = " + f.format(new Integer(count))
				+ " file = " + f.format(new Integer(fileCount)));
	}

	public static void main(String[] args) {
		if (null == args || 0 >= args.length) {
			System.out.println("Usage : StepCount <root directory>");
			return;
		}
		new StepCount(new File(args[0]));
	}
}
