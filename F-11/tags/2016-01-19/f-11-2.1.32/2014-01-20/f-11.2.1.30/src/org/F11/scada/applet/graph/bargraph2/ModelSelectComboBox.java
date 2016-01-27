/*
 * �쐬��: 2008/08/21 TODO ���̐������ꂽ�t�@�C���̃e���v���[�g��ύX����ɂ͎��փW�����v: �E�B���h�E - �ݒ� - Java -
 * �R�[�h�E�X�^�C�� - �R�[�h�E�e���v���[�g
 */
package org.F11.scada.applet.graph.bargraph2;

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.rmi.RemoteException;

import javax.swing.JComboBox;

import org.F11.scada.Service;
import org.apache.log4j.Logger;

public class ModelSelectComboBox extends JComboBox
		implements
			Service,
			ItemListener {
	private static final long serialVersionUID = -8602207275405102928L;
	/** ���M���OAPI */
	private static Logger logger = Logger.getLogger(ModelSelectComboBox.class);
	/** �Q�ƒ��O���[�v */
	private BarSeries series;

	@Override
	public void addItem(Object anObject) {
		BarGraphModel model = (BarGraphModel) anObject;
		model.getJComponent().setVisible(false);
		super.addItem(anObject);
	}

	public void fireChangeGroup(BarSeries series) throws RemoteException {
		this.series = series;
		BarGraphModel model = (BarGraphModel) getSelectedItem();
		model.getJComponent().setVisible(true);
		model.changePoint(series);
		model.start();
	}

	public void itemStateChanged(ItemEvent e) {
		BarGraphModel model = (BarGraphModel) e.getItem();
		if (e.getStateChange() == ItemEvent.SELECTED) {
			model.getJComponent().setVisible(true);
			try {
				model.changePoint(series);
				model.start();
			} catch (RemoteException ex) {
				logger.error("change model [" + model.toString() + "] error!",
						ex);
			}
		} else if (e.getStateChange() == ItemEvent.DESELECTED) {
			model.stop();
			model.getJComponent().setVisible(false);
		}
	}

	public void start() {
	}

	public void stop() {
		logger.debug("all stop()");
		for (int i = 0; i < getItemCount(); i++) {
			BarGraphModel model = (BarGraphModel) getItemAt(i);
			model.stop();
		}
	}

}
