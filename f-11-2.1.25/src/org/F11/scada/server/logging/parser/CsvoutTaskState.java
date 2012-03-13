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

import java.util.BitSet;
import java.util.Stack;

import org.F11.scada.parser.State;
import org.F11.scada.parser.Util.DisplayState;
import org.F11.scada.server.logging.report.CsvoutTask;
import org.F11.scada.util.AttributesUtil;
import org.F11.scada.util.BooleanUtil;
import org.apache.log4j.Logger;
import org.xml.sax.Attributes;

/**
 * xpath /logging/csvouttask 状態を表すクラスです。
 * 
 * @author hori
 */
public class CsvoutTaskState implements State {
	private static Logger logger;

	String dir;
	String csv_head;
	String csv_mid;
	String csv_foot;
	int keepCnt;
	boolean data_head;
	boolean data_mode;
	long midOffset;
	private BitSet attributeSet;

	TaskState state;

	/**
	 * 状態を表すオブジェクトを生成します。
	 */
	public CsvoutTaskState(String tagName, Attributes atts, TaskState state) {
		logger = Logger.getLogger(getClass().getName());

		dir = atts.getValue("dir");
		if (dir == null) {
			throw new IllegalArgumentException("dir is null");
		}
		csv_head = atts.getValue("csv_head");
		if (csv_head == null) {
			throw new IllegalArgumentException("csv_head is null");
		}
		csv_mid = atts.getValue("csv_mid");
		if (csv_mid == null) {
			throw new IllegalArgumentException("csv_mid is null");
		}
		csv_foot = atts.getValue("csv_foot");
		if (csv_foot == null) {
			throw new IllegalArgumentException("csv_foot is null");
		}
		String keepstr = atts.getValue("keep");
		if (keepstr == null) {
			throw new IllegalArgumentException("keep is null");
		}
		keepCnt = Integer.parseInt(keepstr);
		data_head = Boolean.valueOf(atts.getValue("data_head")).booleanValue();
		data_mode = Boolean.valueOf(atts.getValue("data_mode")).booleanValue();
		midOffset = AttributesUtil.getLongValue(atts.getValue("mid_offset"));
		attributeSet = BooleanUtil.getBitSet(atts.getValue("attribute"));
		this.state = state;
	}

	/*
	 * @see org.F11.scada.parser.State#add(String, Attributes, Stack)
	 */
	public void add(String tagName, Attributes atts, Stack stack) {
		if (logger.isDebugEnabled()) {
			logger.debug("Push : " + DisplayState.toString(tagName, stack));
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
			CsvoutTask task = new CsvoutTask(
					state.name,
					state.state.handlerManager,
					state.schedule,
					state.dataHolders,
					dir,
					csv_head,
					csv_mid,
					csv_foot,
					keepCnt,
					data_head,
					data_mode,
					midOffset,
					state.getTables(),
					attributeSet);
			state.loggingDataListeners.add(task);
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		stack.pop();
	}

}
