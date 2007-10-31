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
package org.F11.scada.applet.graph;

import java.util.List;

/**
 * GraphModelのファクトリーです
 * @author Hideaki Maekawa <frdm@user.sourceforge.jp>
 */
public interface GraphModelFactory {
    /**
     * GraphModelを返します。
     * @param handlerName ハンドラ名
     * @param holderStrings モデルを生成するHolderStringのリスト
     * @return GraphModelを返します。
     */
    GraphModel getGraphModel(String handlerName, List holderStrings);

    /**
     * モードを示す文字列を返します
     * @return モードを示す文字列
     */
    String getModeName();
}
