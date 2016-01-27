/*
 * OWL(Online Watch Labour)
 * Copyright (C) 2007 Freedom, Inc. All Rights Reserved.
 *
 */

package org.F11.scada.misc.convert;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import org.F11.scada.server.deploy.FileLister;

/**
 * �f�B���N�g�����̃t�@�C����(�g���q�݂̂���)���������ɓ��ꂵ�܂��B
 * 
 * @author maekawa
 * 
 */
public class Rename {
	/** �ϖ��I�u�W�F�N�g */
	private final Renamer rename;
	private static FileFilter FILTER = new FileFilter() {
		public boolean accept(File pathname) {
			return true;
		}
	};

	/**
	 * �f�B���N�g�����̃t�@�C����(�g���q�݂̂���)���������ɓ��ꂵ�܂��B
	 * 
	 * @param root ��{�f�B���N�g��
	 * @param rename �ϖ��I�u�W�F�N�g
	 */
	public Rename(File root, Renamer rename) {
		this.rename = rename;

		ArrayList errors = new ArrayList();
		FileLister lister = new FileLister();
		Collection c = lister.listFiles(root, FILTER);
		for (Iterator i = c.iterator(); i.hasNext();) {
			File file = (File) i.next();
			File newFile = getNewFile(file);
			if (!file.renameTo(newFile)) {
				errors.add(file);
			}
		}
		if (0 < errors.size()) {
			printErrors(errors);
		}
	}

	private File getNewFile(File file) {
		File newFile = new File(file.getParent(), rename
				.getName(file.getName()));
		return newFile;
	}

	private void printErrors(ArrayList errors) {
		System.out.println("�ȉ��̃t�@�C�����ύX�ł��܂���B");
		for (Iterator i = errors.iterator(); i.hasNext();) {
			File file = (File) i.next();
			System.out.println(file.getName());
		}
	}

	/**
	 * �ϖ��I�u�W�F�N�g
	 * 
	 * @author maekawa
	 *
	 */
	interface Renamer {
		String getName(String s);
	}

	/**
	 * �t�@�C�����S�̂��������ɂ��܂��B
	 * 
	 * @author maekawa
	 * 
	 */
	private static class AllName implements Renamer {
		public String getName(String s) {
			return s.toLowerCase();
		}
	}

	/**
	 * �g���q�̂ݏ������ɂ��܂��B
	 * 
	 * @author maekawa
	 * 
	 */
	private static class ExtentOnly implements Renamer {
		public String getName(String s) {
			int conma = s.lastIndexOf('.');
			if (0 >= conma) {
				return s;
			}
			String befour = s.substring(0, conma);
			String ext = s.substring(conma).toLowerCase();
			return befour + ext;
		}
	}

	public static void main(String[] args) {
		if (null == args || 0 >= args.length) {
			System.out.println("Usage : Rename [-e] <root directory>");
			return;
		}

		String rootName = null;
		Renamer rename = new AllName();
		for (int i = 0; i < args.length; i++) {
			String argsv = args[i];
			if ("-e".equals(argsv)) {
				rename = new ExtentOnly();
			} else if (!argsv.startsWith("-")) {
				rootName = argsv;
			}
		}

		if (null == rootName) {
			System.out.println("Usage : Rename [-e] <root directory>");
			return;
		}
		new Rename(new File(rootName), rename);
	}
}
