/*
 * OWL(Online Watch Labour)
 * Copyright (C) 2007 Freedom, Inc. All Rights Reserved.
 *
 */

package org.F11.scada.misc.convert;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileFilter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;

import org.F11.scada.server.deploy.FileLister;

/**
 * ディレクトリ内のファイル名(拡張子のみも可)を小文字に統一します。
 * 
 * @author maekawa
 * 
 */
public class PngCaseConvertor {
	/** 変名オブジェクト */
	private static FileFilter FILTER = new FileFilter() {
		public boolean accept(File pathname) {
			String n = pathname.getName();
			return pathname.isDirectory() || n.endsWith(".xml");
		}
	};

	/**
	 * ディレクトリ内のファイル名(拡張子のみも可)を小文字に統一します。
	 * 
	 * @param root 基本ディレクトリ
	 * @param rename 変名オブジェクト
	 */
	public PngCaseConvertor(File root) {
		FileLister lister = new FileLister();
		Collection c = lister.listFiles(root, FILTER);
		String newRootStr = root.getName() + "_new";
		File newRoot = new File(newRootStr);
		newRoot.mkdirs();
		for (Iterator i = c.iterator(); i.hasNext();) {
			File file = (File) i.next();
			File newFile = new File(newRootStr, file.getName());
			BufferedReader r = null;
			BufferedWriter w = null;
			try {
				r = new BufferedReader(new FileReader(file));
				w = new BufferedWriter(new FileWriter(newFile));
				try {
					for (String line = r.readLine(); null != line; line = r
							.readLine()) {
						if (0 < line.indexOf("/images")) {
							w.write(toLowerCase(line));
						} else {
							w.write(line);
						}
						w.newLine();
					}
				} finally {
					if (null != r) {
						try {
							r.close();
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
					if (null != w) {
						try {
							w.close();
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				}
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private String toLowerCase(String st) {
		StringBuilder s = new StringBuilder(st);
		int start = s.indexOf("/images");
		int end = s.indexOf(".png");
		s.replace(start, end, s.substring(start, end).toLowerCase());
		return s.toString();
	}

	public static void main(String[] args) {
		if (null == args || 0 >= args.length) {
			System.out.println("Usage : StepCount <root directory>");
			return;
		}
		new PngCaseConvertor(new File(args[0]));
	}
}
