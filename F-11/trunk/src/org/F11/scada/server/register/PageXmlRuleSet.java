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
 *
 */

package org.F11.scada.server.register;

import org.apache.commons.digester.Digester;
import org.apache.commons.digester.RuleSetBase;

/**
 * ページ定義からデータホルダを抽出するルールセットです。
 * 
 * @author maekawa
 */
public class PageXmlRuleSet extends RuleSetBase {
    private static final String HOLDER_STRING = "org.F11.scada.server.register.HolderString";
    private static final String[] ATTRIBUTE_STRINGS = {"provider", "holder"};
    private static final String[] DRAWLINE_ATTRIBUTE = {"start_x", "start_y"};
    private static final String[] LINETO_ATTRIBUTE = {"x", "y"};

	public void addRuleInstances(Digester digester) {
	    digester.addSetProperties("*/if", "value", "value");

	    digester.addSetProperties("*/textanalogsymbol", "value", "value");

	    digester.addObjectCreate("*/fixeddigital", HOLDER_STRING);
	    digester.addSetProperties("*/fixeddigital", ATTRIBUTE_STRINGS, ATTRIBUTE_STRINGS);
	    digester.addSetRoot("*/fixeddigital", "add");

	    digester.addObjectCreate("*/fixedanalog", HOLDER_STRING);
	    digester.addSetProperties("*/fixedanalog", ATTRIBUTE_STRINGS, ATTRIBUTE_STRINGS);
	    digester.addSetRoot("*/fixedanalog", "add");

	    digester.addObjectCreate("*/variableanalog", HOLDER_STRING);
	    digester.addSetProperties("*/variableanalog", ATTRIBUTE_STRINGS, ATTRIBUTE_STRINGS);
	    digester.addSetRoot("*/variableanalog", "add");

	    digester.addObjectCreate("*/variableanalog4", HOLDER_STRING);
	    digester.addSetProperties("*/variableanalog4", ATTRIBUTE_STRINGS, ATTRIBUTE_STRINGS);
	    digester.addSetRoot("*/variableanalog4", "add");

	    digester.addObjectCreate("*/destination/schedule", HOLDER_STRING);
	    digester.addSetProperties("*/destination/schedule", ATTRIBUTE_STRINGS, ATTRIBUTE_STRINGS);
	    digester.addSetRoot("*/destination/schedule", "add");

	    digester.addSetProperties("*/textpowerfactorsymboleditable", "value", "value");

	    digester.addSetProperties("*/textschedulesymboleditable", "value", "value");

	    digester.addSetProperties("*/textanalog4symboleditable", "value", "value");

	    digester.addSetProperties("*/textpowerfactor4symboleditable", "value", "value");

	    digester.addSetProperties("*/textanalogsymboleditable", "value", "value");
	    
	    digester.addSetProperties("*/group", "value", "value");

	    digester.addObjectCreate("*/group", HOLDER_STRING);
	    digester.addSetProperties("*/group", ATTRIBUTE_STRINGS, ATTRIBUTE_STRINGS);
	    digester.addSetRoot("*/group", "add");

	    digester.addObjectCreate("*/calendar", HOLDER_STRING);
	    digester.addSetProperties("*/calendar", ATTRIBUTE_STRINGS, ATTRIBUTE_STRINGS);
	    digester.addSetRoot("*/calendar", "add");

	    digester.addSetProperties("*/drawline", DRAWLINE_ATTRIBUTE, DRAWLINE_ATTRIBUTE);

	    digester.addSetProperties("*/lineto", LINETO_ATTRIBUTE, LINETO_ATTRIBUTE);

	    digester.addSetProperties("*/graphproperty/series/holder/name", "value", "value");

	    digester.addSetProperties("*/stringdatasymbol", "value", "value");

	    digester.addSetProperties("*/bargraph2/barseries/barprop", "holder", "value");
	    digester.addSetProperties("*/bargraph2/barseries/barprop", "nowvalue", "value");

	    digester.addSetProperties("*/imagesymboleditable/schedule", "schedule", "value");
	    digester.addSetProperties("*/imagesymboleditable/schedule", "group", "value");

	    digester.addSetProperties("*/trendgraph3/series/series-property", "holder", "holderString");
	}
}