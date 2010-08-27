/*
 * $Header:
 * /cvsroot/f-11/F-11/src/org/F11/scada/applet/graph/GraphPropertyModel.java,v
 * 1.13.2.5 2007/07/11 07:47:18 frdm Exp $ $Revision: 1.13.2.5 $ $Date:
 * 2007/07/11 07:47:18 $
 * =============================================================================
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
package org.F11.scada.applet.graph.bargraph2;

import java.util.Collection;

public interface GroupModel {

	/**
	 * 現在選択されているグループ番号を返します。
	 * @return 現在設定されているグループ番号
	 */
	public int getGroup();

	/**
	 * グループを選択します
	 * @param group グループ
	 */
	public void setGroup(int index);

	/**
	 * 次のグループを選択します。
	 */
	void nextGroup();

	/**
	 * 前のグループを選択します。
	 */
	void prevGroup();

	/**
	 * 全てのグループ名を返します。
	 * @return グループ名のコレクション
	 */
	public Collection<String> getGroupNames();

}
