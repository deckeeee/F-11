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

package org.F11.scada.scheduling;

/**
 * スケジュールを表すクラスです。
 * スケジュールは実行タスクとイテレーターを保持するオブジェクトです
 * @author Hideaki Maekawa <frdm@users.sourceforge.jp>
 */
public class Schedule {
	/** 実行タスクオブジェクト */
	private SchedulerTask task;
	/** タスクを実行する間隔を提供するイテレータ */
	private ScheduleIterator scheduleIterator;

	/**
	 * タスクを実行する間隔を提供するイテレータを返します
	 * @return タスクを実行する間隔を提供するイテレータ
	 */
	public ScheduleIterator getScheduleIterator() {
		return scheduleIterator;
	}

	/**
	 * 実行タスクオブジェクトを返します
	 * @return 実行タスクオブジェクト
	 */
	public SchedulerTask getTask() {
		return task;
	}

	/**
	 * タスクを実行する間隔を提供するイテレータを設定します
	 * @param iterator タスクを実行する間隔を提供するイテレータ
	 */
	public void setScheduleIterator(ScheduleIterator iterator) {
		scheduleIterator = iterator;
	}

	/**
	 * 実行タスクオブジェクトを設定します
	 * @param task 実行タスクオブジェクト
	 */
	public void setTask(SchedulerTask task) {
		this.task = task;
	}
}
