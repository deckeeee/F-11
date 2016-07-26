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
package org.F11.scada.applet.operationlog;

import javax.swing.table.TableModel;

import org.F11.scada.server.operationlog.dto.FinderConditionDto;

/**
 * @author Hideaki Maekawa <frdm@user.sourceforge.jp>
 */
public interface OperationLoggingTableModel extends TableModel {
    /**
     * 検索結果の初期データを取得します
     * @param finder 検索条件
     */
    void find(FinderConditionDto finder);
    
    /**
     * 次ページのデータを取得します
     */
    void next();
    
    /**
     * 前ページのデータを取得します
     */
    void previous();
    
    /**
     * 現在表示しているページを返します
     * @return 現在表示しているページ
     */
    int getCurrentPage();
    
    /**
     * 検索条件で取得した総レコードによる全ページ数を返します
     * @return 検索条件で取得した総レコードによる全ページ数
     */
    int getAllPage();
    
    /**
     * 操作ログプレフィックス機能の有無
     * @return 操作ログプレフィックス機能有りの場合<code> true </code>をない場合は<code> false </code>を返します。
     */
    boolean isPrefix();
}
