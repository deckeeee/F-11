/*
 * Created on 2003/04/03
 *
 * To change this generated comment go to
 * Window>Preferences>Java>Code Generation>Code Template
 */
package org.F11.scada.applet.parser.dialog;

import java.awt.Dialog;
import java.awt.Frame;
import java.util.Stack;

import javax.swing.JDialog;

import org.F11.scada.applet.dialog.PinpointDialog;
import org.F11.scada.parser.State;
import org.F11.scada.parser.Util.DisplayState;
import org.F11.scada.util.AttributesUtil;
import org.apache.log4j.Logger;
import org.xml.sax.Attributes;

/**
 * @author maekawa
 */
public class PinpointDialogState implements State {
	DialogMapState state;
	JDialog dialog;
	private String dialogName;
	private static Logger logger = Logger.getLogger(PinpointDialogState.class);

	/**
	 * 状態オブジェクトを生成します。
	 *
	 * @param tagName タグ名称
	 * @param atts タグ属性
	 * @param 親の状態オブジェクト
	 */
	public PinpointDialogState(
			String tagName,
			Attributes atts,
			DialogMapState state) {
		this.state = state;

		if (atts.getValue("name") == null) {
			throw new IllegalArgumentException("name is null");
		}
		if (atts.getValue("width") == null) {
			throw new IllegalArgumentException("width is null");
		}
		if (atts.getValue("height") == null) {
			throw new IllegalArgumentException("height is null");
		}

		dialogName = atts.getValue("name");
		int width = Integer.parseInt(atts.getValue("width"));
		int height = Integer.parseInt(atts.getValue("height"));

		int rowWidth1 =
			AttributesUtil.getIntegerValue(atts.getValue("rowWidth1"), -1);
		int rowWidth2 =
			AttributesUtil.getIntegerValue(atts.getValue("rowWidth2"), -1);
		int rowWidth3 =
			AttributesUtil.getIntegerValue(atts.getValue("rowWidth3"), -1);

		if (state.handler.window instanceof Frame) {
			dialog =
				new PinpointDialog((Frame) state.handler.window,
						state.handler.changer, rowWidth1, rowWidth2, rowWidth3);
		} else if (state.handler.window instanceof Dialog) {
			dialog =
				new PinpointDialog((Dialog) state.handler.window,
						state.handler.changer, rowWidth1, rowWidth2, rowWidth3);
		}
		dialog.setSize(width, height);
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
		state.handler.dialogs.put(dialogName, dialog);
		stack.pop();
	}
}
