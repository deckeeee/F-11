/*
 * $Header: /cvsroot/f-11/F-11/src/org/F11/scada/parser/graph/SeriesState.java,v 1.9.2.5 2006/03/03 09:22:04 frdm Exp $
 * $Revision: 1.9.2.5 $
 * $Date: 2006/03/03 09:22:04 $
 * 
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
package org.F11.scada.parser.graph;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Stack;

import javax.swing.BorderFactory;

import org.F11.scada.applet.graph.ExplanatoryNotesText;
import org.F11.scada.applet.graph.GraphSeriesProperty;
import org.F11.scada.applet.symbol.SymbolProperty;
import org.F11.scada.applet.symbol.TextSymbolMessage;
import org.F11.scada.parser.State;
import org.F11.scada.parser.Util.DisplayState;
import org.apache.log4j.Logger;
import org.xml.sax.Attributes;

/**
 * XPath=/page_map/page/trendgraph/graphproperty/series 状態を表すクラスです。
 * @author Hideaki Maekawa <frdm@users.sourceforge.jp>
 */
public class SeriesState implements State {
	private static Logger logger;
	/** エラー表示ーメッセージ */
	private static final TextSymbolMessage message = new TextSymbolMessage();
	
	GraphPropertyModelState state;
	
	private int size;
	private String name;
	
	List verticalMinimums;
	List verticalMaximums;
	List verticalInputMinimums;
	List verticalInputMaximums;
//	List dataProviderNames;
	List dataHolderNames;
	List pointNames;
	List unitMarks;

	/**
	 * 状態を表すオブジェクトを生成します。
	 */
	public SeriesState(String tagName, Attributes atts, GraphPropertyModelState state) {
		logger = Logger.getLogger(getClass().getName());
		
		this.state = state;
		
		if (atts.getValue("size") != null) {
			size = Integer.parseInt(atts.getValue("size"));
		} else {
			throw new IllegalArgumentException();
		}
		if (atts.getValue("name") != null) {
			name = atts.getValue("name");
		} else {
			throw new IllegalArgumentException();
		}
		
		verticalMinimums = new ArrayList(size);
		verticalMaximums = new ArrayList(size);
		verticalInputMinimums = new ArrayList(size);
		verticalInputMaximums = new ArrayList(size);
//		dataProviderNames = new ArrayList(size);
		dataHolderNames = new ArrayList(size);
		pointNames = new ArrayList(size);
		unitMarks = new ArrayList(size);
	}

	/**
	 * @see org.F11.scada.parser.State#add(String, Attributes, Stack)
	 */
	public void add(String tagName, Attributes atts, Stack stack) {
		if (logger.isDebugEnabled()) {
			logger.debug("Push : " + DisplayState.toString(tagName, stack));
		}
		if (tagName.equals("property") || "holder".equals(tagName)) {
			stack.push(new PropertyState(tagName, atts, this));
		} else {
			logger.info("tagName : " + tagName);
		}
	}

	/**
	 * @see org.F11.scada.parser.State#end(String, Stack)
	 */
	public void end(String tagName, Stack stack) {
		if (tagName.equals("series")) {
			if (logger.isDebugEnabled()) {
				logger.debug("Pop : " + DisplayState.toString(tagName, stack));
			}
			if (state.series == null) {
				state.series = new ArrayList();
			}

//			String[] providerStrs =
//				(String[]) dataProviderNames.toArray(new String[0]);
//			String[] holderStrs =
//				(String[]) dataHolderNames.toArray(new String[0]);

			String[][] holdStr = split(dataHolderNames);

			state.series.add(new GraphSeriesProperty(
					size,
					(Double[]) verticalMinimums.toArray(new Double[0]),
					(Double[]) verticalMaximums.toArray(new Double[0]),
					(Double[]) verticalInputMinimums.toArray(new Double[0]),
					(Double[]) verticalInputMaximums.toArray(new Double[0]),
					holdStr[0],
					holdStr[1],
					state.foldCount, name,
					(String[]) pointNames.toArray(new String[0]),
					(String[]) unitMarks.toArray(new String[0]),
					createSymbols(dataHolderNames)));
			stack.pop();
		} else {
			logger.info("tagName : " + tagName);
		}
	}
	
	private String[][] split(List prohold) {
	    String[][] ret = new String[2][prohold.size()];
	    int line = 0;
	    for (Iterator i = prohold.iterator(); i.hasNext(); line++) {
            String value = (String) i.next();
            logger.debug("split : " + value);
            ret[0][line] = value.substring(0, value.indexOf("_"));
            ret[1][line] = value.substring(value.indexOf("_") + 1);
        }
	    return ret;
	}
	
	private ExplanatoryNotesText[] createSymbols(List holders) {
		if (state.isNotDemand) {
		    String error = message.getErrorText();

			ExplanatoryNotesText[] symbols = new ExplanatoryNotesText[holders.size()];
			for (int i = 0, size = holders.size(); i < size; i++) {
				SymbolProperty sysprop = new SymbolProperty();
				sysprop.setProperty("string", error);
				sysprop.setProperty("h_aligin", "RIGHT");
				sysprop.setProperty("value", (String) holders.get(i));

				symbols[i] = new ExplanatoryNotesText(sysprop);
				symbols[i].connectReferencer();
				symbols[i].setBorder(BorderFactory.createLoweredBevelBorder());
			}
			return symbols;
		} else {
			return new ExplanatoryNotesText[0];
		}
	}

}
