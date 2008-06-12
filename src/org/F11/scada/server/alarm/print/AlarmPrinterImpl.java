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
import java.util.List;
import java.util.StringTokenizer;

import javax.print.Doc;
import javax.print.DocFlavor;
import javax.print.DocPrintJob;
import javax.print.PrintException;
import javax.print.PrintService;
import javax.print.PrintServiceLookup;
import javax.print.SimpleDoc;
import javax.print.attribute.HashPrintRequestAttributeSet;
import javax.print.attribute.PrintRequestAttributeSet;
import javax.print.attribute.standard.Copies;
import javax.print.attribute.standard.JobName;
import javax.print.attribute.standard.MediaSizeName;
import javax.print.attribute.standard.OrientationRequested;

import org.F11.scada.EnvironmentManager;
import org.apache.log4j.Logger;

/**
 * 警報データをプリンタに印刷するクラスです
 * 
 * @author hori
 */
public class AlarmPrinterImpl implements AlarmPrinter {
	/** ロギングAPI */
	private static Logger logger = Logger.getLogger(AlarmPrinterImpl.class);

	/**
	 * PrintLineData のリストをプリンタに印刷します
	 * 
	 * @param data PrintLineData のリスト
	 */
	public void print(List data) {
		PrintRequestAttributeSet aset = new HashPrintRequestAttributeSet();
		aset.add(new Copies(1));
		aset.add(new JobName("My job", null));

		String orient =
			EnvironmentManager.get(
				"/server/alarm/print/orientation",
				"PORTRAIT");
		if ("PORTRAIT".equals(orient)) {
			aset.add(OrientationRequested.PORTRAIT);
		} else if ("LANDSCAPE".equals(orient)) {
			aset.add(OrientationRequested.PORTRAIT);
		} else if ("REVERSE_LANDSCAPE".equals(orient)) {
			aset.add(OrientationRequested.REVERSE_LANDSCAPE);
		} else if ("REVERSE_PORTRAIT".equals(orient)) {
			aset.add(OrientationRequested.REVERSE_PORTRAIT);
		}

		String size =
			EnvironmentManager.get("/server/alarm/print/size", "ISO_A4");
		if ("ISO_A4".equals(size)) {
			aset.add(MediaSizeName.ISO_A4);
		} else if ("ISO_B5".equals(size)) {
			aset.add(MediaSizeName.ISO_B5);
		}

		StringTokenizer st =
			new StringTokenizer(EnvironmentManager.get(
				"/server/alarm/print/font",
				"Monospaced,PLAIN,10"), ",");
		String fontName = st.nextToken();
		String fontStyle = st.nextToken();
		int style = Font.PLAIN;
		if ("BOLD".equals(fontStyle)) {
			style = Font.BOLD;
		} else if ("ITALIC".equals(fontStyle)) {
			style = Font.ITALIC;
		}

		int fontSize = Integer.parseInt(st.nextToken());
		Font font = new Font(fontName, style, fontSize);

		String printService =
			EnvironmentManager.get("/server/alarm/print/printservice", "lp");

		new AlarmListDrawer(printService, aset, data, font);
	}

	/**
	 * ページ印刷を実装します。
	 * 
	 * @author hori
	 */
	private static class Print2DGraphics implements Printable {
		private final DocFlavor flavor = DocFlavor.SERVICE_FORMATTED.PRINTABLE;
		private final List msgs;
		private final Font font;

		public Print2DGraphics(
				String printerName,
				PrintRequestAttributeSet aset,
				List msgs,
				Font font) {
			this.msgs = msgs;
			this.font = font;

			PrintService srv = null;
			PrintService[] services =
				PrintServiceLookup.lookupPrintServices(flavor, aset);
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
					g2d.drawString(lineData.toString(), 0, posY);
				}
				return Printable.PAGE_EXISTS;
			} else {
				return Printable.NO_SUCH_PAGE;
			}
		}
	}

}
