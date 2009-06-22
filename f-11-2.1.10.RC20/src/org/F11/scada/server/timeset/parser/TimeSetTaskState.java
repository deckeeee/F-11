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

package org.F11.scada.server.timeset.parser;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Stack;

import org.F11.scada.parser.State;
import org.F11.scada.parser.Util.DisplayState;
import org.F11.scada.server.logging.LoggingSchedule;
import org.F11.scada.server.timeset.LifeCheckTask;
import org.F11.scada.server.timeset.TimeSetTask;
import org.apache.log4j.Logger;
import org.xml.sax.Attributes;

/**
 * xpath /logging/task 状態を表すクラスです。
 * 
 * @author Hideaki Maekawa <h@f-11.org>
 */
public class TimeSetTaskState implements State {
	private static Logger logger;

	String providerName;
	String holderName;
	List dataHolders;

	String schedule;
	int offset;
	TimeSetState state;

	List lifeCheckHolders;
	private boolean milliOffsetMode;

	/**
	 * 状態を表すオブジェクトを生成します。
	 */
	public TimeSetTaskState(String tagName, Attributes atts, TimeSetState state) {
		logger = Logger.getLogger(getClass().getName());
		schedule = atts.getValue("schedule");
		if (schedule == null) {
			throw new IllegalArgumentException("schedule is null");
		}
		offset = Integer.parseInt(atts.getValue("offset"));
		if (schedule == null) {
			offset = 0;
		}

		dataHolders = new ArrayList();
		this.state = state;
		lifeCheckHolders = new ArrayList();

		milliOffsetMode = Boolean.valueOf(atts.getValue("milliOffsetMode"))
				.booleanValue();
	}

	/*
	 * @see org.F11.scada.parser.State#add(String, Attributes, Stack)
	 */
	public void add(String tagName, Attributes atts, Stack stack) {
		if (logger.isDebugEnabled()) {
			logger.debug("Push : " + DisplayState.toString(tagName, stack));
		}
		if (tagName.equals("read")) {
			stack.push(new ReadTimeState(tagName, atts, this));
		} else if (tagName.equals("write")) {
			stack.push(new WriteTimeState(tagName, atts, this));
		} else if (tagName.equals("lifecheck")) {
			stack.push(new LifeCheckState(tagName, atts, this));
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
			logger.debug(providerName + "_" + holderName);
		}
		TimeSetTask task = new TimeSetTask(providerName, holderName);
		for (Iterator it = dataHolders.iterator(); it.hasNext();) {
			task.addWriteProvidertHolder((String) it.next());
		}
		try {
			Field scheduleType = LoggingSchedule.class.getField(schedule);
			LoggingSchedule loggingSchedule = (LoggingSchedule) scheduleType
					.get(null);
			if (milliOffsetMode) {
				loggingSchedule.addMilliOffset(task, offset);
			} else {
				loggingSchedule.add(task, offset);
			}

			scheduleLifeCheck(loggingSchedule);
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		stack.pop();
	}

	private void scheduleLifeCheck(LoggingSchedule loggingSchedule) {
		LifeCheckTask task = new LifeCheckTask();
		for (Iterator i = lifeCheckHolders.iterator(); i.hasNext();) {
			String holder = (String) i.next();
			task.addWriteProvidertHolder(holder);
		}
		if (milliOffsetMode) {
			loggingSchedule.addMilliOffset(task, offset);
		} else {
			loggingSchedule.add(task, offset);
		}
	}

	public void setHolderName(String holderName) {
		this.holderName = holderName;
	}

	public void setProviderName(String providerName) {
		this.providerName = providerName;
	}
}
