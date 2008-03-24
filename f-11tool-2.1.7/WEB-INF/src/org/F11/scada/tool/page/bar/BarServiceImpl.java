/*
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

package org.F11.scada.tool.page.bar;

import java.rmi.RemoteException;

import org.F11.scada.tool.io.PageListBean;
import org.F11.scada.tool.page.parser.DOMPageDefine;
import org.F11.scada.tool.page.parser.bar.BarGraphDefine;
import org.F11.scada.util.RmiUtil;

public class BarServiceImpl implements BarService {
	public BarServiceImpl(int regport, int objport) {
		RmiUtil.registryServer(this, BarService.class, regport, objport);
	}

	public void setBar(PageListBean page, BarPointForm actionForm)
			throws RemoteException {
		try {
			DOMPageDefine pageDefine = new DOMPageDefine(page.getPageXmlPath());
			BarGraphDefine barDefine = pageDefine.getBarGraphDefine();
			barDefine.setBarPoint(actionForm);
			pageDefine.send();
		} catch (Exception e) {
			throw new RemoteException("バー定義送信時にエラー発生 : ", e);
		}
	}
}
