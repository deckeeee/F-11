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

package org.F11.scada.server.deploy;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import junit.framework.TestCase;

/**
 * FileListerのテストケース
 * @author Hideaki Maekawa <frdm@users.sourceforge.jp>
 */
public class FileListerTest extends TestCase {

	/**
	 * Constructor for FileListerTest.
	 * @param arg0
	 */
	public FileListerTest(String arg0) {
		super(arg0);
	}
	
	protected void setUp() throws Exception {
		File root = new File("utest");
		root.mkdir();
		root.deleteOnExit();
		File rootjava = new File("utest/root.java");
		rootjava.createNewFile();
		rootjava.deleteOnExit();

		File foo = new File("utest/foo");
		foo.mkdir();
		foo.deleteOnExit();
		File foojava = new File("utest/foo/foo.java");
		foojava.createNewFile();
		foojava.deleteOnExit();

		File bar = new File("utest/bar");
		bar.mkdir();
		bar.deleteOnExit();
		File barjava = new File("utest/bar/bar.java");
		barjava.createNewFile();
		barjava.deleteOnExit();
	
	}
	
	protected void tearDown() throws Exception {
		new File("utest/bar/bar.java").delete();
		new File("utest/foo/foo.java").delete();
		new File("utest/root.java").delete();
		new File("utest/bar").delete();
		new File("utest/foo").delete();
		new File("utest").delete();
	}

	public void testListFiles() throws Exception {
		FileLister lister = new FileLister();

		List l = new ArrayList(lister.listFiles(new File("utest"), new TestFilter()));
		assertNotNull(l);
		Collections.sort(l);
		System.out.println(l);
		assertEquals(3, l.size());
		assertEquals(new File("utest/bar/bar.java"), l.get(0));
		assertEquals(new File("utest/foo/foo.java"), l.get(1));
		assertEquals(new File("utest/root.java"), l.get(2));
	}


	/**
	 * ディレクトリか拡張子がjavaのファイルを抽出するフィルターです。
	 * 
	 * @author Hideaki Maekawa <frdm@users.sourceforge.jp>
	 */
	static class TestFilter implements FileFilter {
		public boolean accept(File pathname) {
			return pathname.isDirectory() || pathname.getName().endsWith(".java") ? true : false;
		}
	}
}
