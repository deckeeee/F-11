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

package org.F11.scada.server.logging;

import org.apache.commons.digester.Digester;
import org.apache.commons.digester.RuleSetBase;

/**
 * ページ定義からデータホルダを抽出するルールセットです。
 * 
 * @author maekawa
 */
public class LoggingRuleSet extends RuleSetBase {
	private static final String[] TASK_ATTRIBUTE = { "name", "tables" };
	private static final String COLUMN = "org.F11.scada.server.logging.Column";
	private static final String[] COLUMN_ATTRIBUTE =
		{ "index", "provider", "holder" };

	public void addRuleInstances(Digester digester) {
		digester.setNamespaceAware(true);
		digester.addCallMethod("*/task", "put", 2);
		digester.addCallParam("*/task", 0, "name");
		digester.addObjectCreate("*/task", Task.class);
		digester.addSetProperties("*/task", TASK_ATTRIBUTE, TASK_ATTRIBUTE);
		digester.addObjectCreate("*/column", COLUMN, COLUMN);
		digester.addSetProperties(
			"*/column",
			COLUMN_ATTRIBUTE,
			COLUMN_ATTRIBUTE);
		digester.addSetNext("*/column", "addColumn", COLUMN);
		digester.addCallParam("*/task", 1, true);
	}
}