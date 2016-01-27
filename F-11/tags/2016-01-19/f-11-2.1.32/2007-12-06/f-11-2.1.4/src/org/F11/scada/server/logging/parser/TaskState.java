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

package org.F11.scada.server.logging.parser;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Stack;

import org.F11.scada.parser.State;
import org.F11.scada.parser.Util.DisplayState;
import org.F11.scada.server.event.LoggingDataListener;
import org.F11.scada.server.logging.LoggingSchedule;
import org.F11.scada.server.logging.LoggingTask;
import org.apache.log4j.Logger;
import org.xml.sax.Attributes;

/**
 * xpath /logging/task 状態を表すクラスです。
 * 
 * @author Hideaki Maekawa <h@f-11.org>
 */
public class TaskState implements State, TaskStateble {
	private static Logger logger;

	String name;
	String schedule;
	int offset;
	String factoryName;
	List dataHolders;

	List loggingDataListeners;

	LoggingState state;

	private boolean milliOffsetMode;

	/**
	 * 状態を表すオブジェクトを生成します。
	 */
	public TaskState(String tagName, Attributes atts, LoggingState state) {
		logger = Logger.getLogger(getClass().getName());
		name = atts.getValue("name");
		if (name == null) {
			throw new IllegalArgumentException("name is null");
		}
		schedule = atts.getValue("schedule");
		if (schedule == null) {
			throw new IllegalArgumentException("schedule is null");
		}
		offset = Integer.parseInt(atts.getValue("offset"));
		if (schedule == null) {
			offset = 0;
		}
		factoryName = atts.getValue("factoryName");
		if (factoryName == null) {
			throw new IllegalArgumentException("factoryName is null");
		}
		milliOffsetMode = Boolean.valueOf(atts.getValue("milliOffsetMode"))
				.booleanValue();

		dataHolders = new ArrayList();
		loggingDataListeners = new ArrayList();
		this.state = state;
	}

	/*
	 * @see org.F11.scada.parser.State#add(String, Attributes, Stack)
	 */
	public void add(String tagName, Attributes atts, Stack stack) {
		if (logger.isDebugEnabled()) {
			logger.debug("Push : " + DisplayState.toString(tagName, stack));
		}
		if (tagName.equals("column")) {
			stack.push(new ColumnState(tagName, atts, this));
		} else if (tagName.equals("csvout")) {
			stack.push(new CsvoutTaskState(tagName, atts, this));
		} else {
			logger.debug("tagName:" + tagName);
		}
	}

	/*
	 * @see org.F11.scada.parser.State#end(String, Stack)
	 */
	public void end(String tagName, Stack stack) {
		if (logger.isDebugEnabled()) {
			logger.debug("Pop : " + DisplayState.toString(tagName, stack));
		}

		try {
			LoggingTask task = new LoggingTask(
					name,
					dataHolders,
					factoryName,
					state.handlerManager,
					schedule);
			Field scheduleType = LoggingSchedule.class.getField(schedule);
			LoggingSchedule loggingSchedule = (LoggingSchedule) scheduleType
					.get(null);
			if (milliOffsetMode) {
				loggingSchedule.addMilliOffset(task, offset);
			} else {
				loggingSchedule.add(task, offset);
			}

			for (Iterator it = loggingDataListeners.iterator(); it.hasNext();) {
				task.addElementLoggingListener((LoggingDataListener) it.next());
			}

			state.putLoggingTask(name, task);
		} catch (Exception e) {
			// logger.error("スケジュールに無い種類が指定されています。 : " + schedule);
			logger.error("Exception caught: ", e);
		}

		stack.pop();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.F11.scada.server.logging.parser.TaskStateble#add(java.lang.Object)
	 */
	public void add(Object obj) {
		dataHolders.add(obj);
	}
}
