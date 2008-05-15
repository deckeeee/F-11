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

package org.F11.scada.server.alarm.print;

import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.print.PageFormat;
import java.awt.print.Paper;
import java.awt.print.Printable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.print.Doc;
import javax.print.DocFlavor;
import javax.print.DocPrintJob;
import javax.print.PrintException;
import javax.print.PrintService;
import javax.print.PrintServiceLookup;
import javax.print.SimpleDoc;
import javax.print.attribute.PrintRequestAttributeSet;

import org.apache.log4j.Logger;

/**
 * ページ印刷を実装します。日付書式指定版
 * @author hori
 */
public class AlarmListDrawer implements Printable {
	/** ロギングAPI */
	private static Logger logger = Logger.getLogger(AlarmListDrawer.class);

	private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
	private final DocFlavor flavor = DocFlavor.SERVICE_FORMATTED.PRINTABLE;
	private final List msgs;
	private final Font font;

	public AlarmListDrawer(String printerName, PrintRequestAttributeSet aset,
			List msgs, Font font) {
		this.msgs = msgs;
		this.font = font;

		PrintService srv = null;
		PrintService[] services = PrintServiceLookup.lookupPrintServices(
				flavor, aset);
		for (int i = 0; i < services.length; i++) {
			if (services[i].getName().equals(printerName)) {
				srv = services[i];
			}
		}
		if (srv != null) {
			DocPrintJob pj = srv.createPrintJob();

			try {
				Doc doc = new SimpleDoc(this, flavor, null);
				pj.print(doc, aset);
			} catch (PrintException e) {
				logger.error("印字異常", e);
			}
		}
	}

	public int print(Graphics g, PageFormat pf, int pageIndex) {

		Paper pp = pf.getPaper();
		Graphics2D g2d = (Graphics2D) g;
		g.setFont(font);
		int stepY = g2d.getFontMetrics().getHeight();
		int dataCont = (int) (pp.getImageableHeight() / stepY);
		if (pageIndex * dataCont < msgs.size()) {
			g2d.translate(pf.getImageableX(), pf.getImageableY());
			int posY = stepY;
			for (int i = pageIndex * dataCont; i < msgs.size()
					&& i < ((pageIndex + 1) * dataCont); i++, posY += stepY) {

				PrintLineData lineData = (PrintLineData) msgs.get(i);
				g2d.setColor(lineData.getColor());
				StringBuilder sb = new StringBuilder();
				sb.append(sdf.format(new Date(lineData.getEntryDate().getTime())));
				sb.append("  ");
				sb.append(lineData.getUnit());
				sb.append("  ");
				sb.append(lineData.getKikiname());
				sb.append("  ");
				sb.append(lineData.getAlarmname());
				sb.append("  ");
				sb.append(lineData.getMessage());
				sb.append("  ");
				g2d.drawString(sb.toString(), 0, posY);
			}
			return Printable.PAGE_EXISTS;
		} else {
			return Printable.NO_SUCH_PAGE;
		}
	}

}
