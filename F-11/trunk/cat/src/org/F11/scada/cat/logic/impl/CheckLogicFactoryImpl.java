/*
 * Project F-11 - Web SCADA for Java
 * Copyright (C) 2002-2008 Freedom, Inc. All Rights Reserved.
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

package org.F11.scada.cat.logic.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.F11.scada.cat.logic.CheckLogic;
import org.F11.scada.cat.logic.CheckLogicFactory;

/**
 * 処理ロジックファクトリーの実装
 * 
 * @author maekawa
 *
 */
public class CheckLogicFactoryImpl implements CheckLogicFactory {
	private final List<CheckLogic> checkLogics;

	public CheckLogicFactoryImpl() {
		checkLogics = new ArrayList<CheckLogic>();
	}

	public List<CheckLogic> getCheckLogics() {
		return Collections.unmodifiableList(checkLogics);
	}

	public void addCheckLogic(CheckLogic logic) {
		checkLogics.add(logic);
	}
}