/*
 * $Header: /cvsroot/f-11/F-11/src/org/F11/scada/parser/SymbolClassName.java,v 1.5.2.2 2007/01/09 09:45:13 frdm Exp $
 * $Revision: 1.5.2.2 $
 * $Date: 2007/01/09 09:45:13 $
 * 
 * =============================================================================
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
 *
 */

package org.F11.scada.parser;

import java.util.HashMap;
import java.util.Map;

/**
 * タグ名称とシンボルクラス名をマッピングするクラスです。
 * <!-- シンボルが増えそうなら、タグ名称とシンボルクラス名を外部ファイル化 -->
 * @author Hideaki Maekawa <frdm@users.sourceforge.jp>
 */
class SymbolClassName {
	private static final SymbolClassName instance = new SymbolClassName();
		
	private Map symbolMap;

	private SymbolClassName() {
		symbolMap = new HashMap();
			
		symbolMap.put("imagesymbol", "org.F11.scada.applet.symbol.ImageSymbol");
		symbolMap.put("imageanimesymbol", "org.F11.scada.applet.symbol.ImageAnimeSymbol");
		symbolMap.put("textsymbol", "org.F11.scada.applet.symbol.TextSymbol");
		symbolMap.put("textanalogsymbol", "org.F11.scada.applet.symbol.TextAnalogSymbol");
		symbolMap.put("openurlsymbol", "org.F11.scada.applet.symbol.OpenURLSymbol");
		symbolMap.put("stringdatasymbol", "org.F11.scada.applet.symbol.StringDataSymbol");
	}
		
	static String getSymbolClassName(String tagName) {
		return (String) instance.symbolMap.get(tagName);
	}
	
	static boolean findSymbolClassName(String tagName) {
		return instance.symbolMap.containsKey(tagName); 
	}
}
