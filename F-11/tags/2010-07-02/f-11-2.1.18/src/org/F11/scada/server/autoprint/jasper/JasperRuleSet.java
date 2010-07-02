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

package org.F11.scada.server.autoprint.jasper;

import org.apache.commons.digester.Digester;
import org.apache.commons.digester.RuleSetBase;

/**
 * Jasper 自動印刷 Digester のルールセットクラスです。
 * @author Hideaki Maekawa <frdm@users.sourceforge.jp>
 */
public class JasperRuleSet extends RuleSetBase {

	public void addRuleInstances(Digester digester) {
		digester.addObjectCreate("tasks/schedule", "org.F11.scada.scheduling.Schedule", "className");
		digester.addSetNext("tasks/schedule", "addSchedule");
		
		digester.addObjectCreate("tasks/schedule/task", "org.F11.scada.server.autoprint.jasper.JasperAutoPrintTask", "className");

		digester.addObjectCreate("tasks/schedule/task/dataSource", "org.F11.scada.server.autoprint.jasper.DailyPrintDataSource", "className");
		digester.addCallMethod("tasks/schedule/task/dataSource/property", "setProperty", 2);
		digester.addCallParam("tasks/schedule/task/dataSource/property", 0, "name");
		digester.addCallParam("tasks/schedule/task/dataSource/property", 1, "value");

		digester.addObjectCreate("tasks/schedule/task/dataSource/exportor", "org.F11.scada.server.autoprint.jasper.PdfExportor", "className");
		digester.addCallMethod("tasks/schedule/task/dataSource/exportor/property", "setProperty", 2);
		digester.addCallParam("tasks/schedule/task/dataSource/exportor/property", 0, "name");
		digester.addCallParam("tasks/schedule/task/dataSource/exportor/property", 1, "value");

		digester.addSetNext("tasks/schedule/task/dataSource/exportor", "addExportor");
		digester.addSetNext("tasks/schedule/task/dataSource", "setPrintDataSource");
		digester.addSetNext("tasks/schedule/task", "setTask");

		digester.addObjectCreate("tasks/schedule/scheduleIterator", "org.F11.scada.server.autoprint.jasper.DailyIteratorJDBCWrapper", "className");
		digester.addCallMethod("tasks/schedule/scheduleIterator/property", "setProperty", 2);
		digester.addCallParam("tasks/schedule/scheduleIterator/property", 0, "name");
		digester.addCallParam("tasks/schedule/scheduleIterator/property", 1, "value");
		digester.addSetNext("tasks/schedule/scheduleIterator", "setScheduleIterator");
	}
}
