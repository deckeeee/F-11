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

package org.F11.scada.parser;

import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

import org.F11.scada.applet.symbol.Editable;
import org.F11.scada.applet.symbol.FixedAnalogSetter;
import org.F11.scada.applet.symbol.FixedDigitalSetter;
import org.F11.scada.applet.symbol.ValueSetter;
import org.F11.scada.applet.symbol.VariableAnalog4Setter;
import org.F11.scada.applet.symbol.VariableAnalogSetter;
import org.apache.log4j.Logger;
import org.xml.sax.Attributes;

/**
 * XPath=/page_map/page/SYMBOL/destination/element 状態を表すクラスです。
 * 
 * @author Youhei Horikawa <hori@users.sourceforge.jp>
 */
public class ElementState implements State {
	private static Logger logger;

	private static final Map elements;
	static {
		elements = new HashMap();
		elements.put("fixeddigital", new createValueSetter() {
			public ValueSetter create(
					String provider,
					String holder,
					String value) {
				return new FixedDigitalSetter(provider, holder, value);
			}
		});
		elements.put("fixedanalog", new createValueSetter() {
			public ValueSetter create(
					String provider,
					String holder,
					String value) {
				return new FixedAnalogSetter(provider, holder, value);
			}
		});
		elements.put("variableanalog", new createValueSetter() {
			public ValueSetter create(
					String provider,
					String holder,
					String value) {
				return new VariableAnalogSetter(provider, holder);
			}
		});
		elements.put("variableanalog4", new createValueSetter() {
			public ValueSetter create(
					String provider,
					String holder,
					String value) {
				return new VariableAnalog4Setter(provider, holder);
			}
		});
	}

	private static interface createValueSetter {
		public ValueSetter create(String provider, String holder, String value);
	}

	/**
	 * Constructor for ElementState.
	 */
	public ElementState(String tagName, Attributes atts, Editable symbolEditable) {
		logger = Logger.getLogger(getClass());

		if (isSupport(tagName)) {
			logger.debug("Element:" + atts.getValue(0));

			createValueSetter setter = (createValueSetter) elements
					.get(tagName);

			symbolEditable.addValueSetter(setter.create(atts
					.getValue("provider"), atts.getValue("holder"), atts
					.getValue("value")));

		} else {
			logger.debug("tagName:" + tagName);
		}
	}

	/**
	 * @see org.F11.scada.parser.State#add(String, Attributes, Stack)
	 */
	public void add(String tagName, Attributes atts, Stack stack) {
	}

	/**
	 * @see org.F11.scada.parser.State#end(String, Stack)
	 */
	public void end(String tagName, Stack stack) {
		stack.pop();
		logger.debug("element Stack Poped.");
	}

	public static boolean isSupport(String tagName) {
		return elements.containsKey(tagName);
	}
}
