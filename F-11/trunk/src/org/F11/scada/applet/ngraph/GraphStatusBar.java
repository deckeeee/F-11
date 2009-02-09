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

package org.F11.scada.applet.ngraph;


import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.F11.scada.applet.ngraph.event.GraphChangeEvent;

/**
 * �O���t�\����ύX����{�^���ƎQ�ƃf�[�^�̓�����\������R���|�[�l���g�N���X
 * 
 * @author maekawa
 * 
 */
public class GraphStatusBar extends JPanel implements Mediator, Colleague {
	private static final long serialVersionUID = 3577124294020824114L;
	private static final Insets BUTTON_INSETS = new Insets(2, 0, 2, 0);
	private final Mediator mediator;
	private JLabel dataCycleLabel;
	private JLabel referenceDateLabel;
	private DataAreaListener dataAreaListener;
	private VerticalModeListener verticalModeListener;
	private DrawSeriesModeListener drawSeriesModeListener;
	private HorizontalScaleButtonListener listener1;
	private HorizontalScaleButtonListener listener2;
	private HorizontalScaleButtonListener listener3;
	private HorizontalScaleButtonListener listener4;
	private HorizontalScaleButtonListener listener5;
	private HorizontalScaleButtonListener listener6;
	private boolean isDataAreaListener;

	public GraphStatusBar(Mediator mediator) {
		super(new BorderLayout());
		this.mediator = mediator;
		setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 10));
		add(getCenter(), BorderLayout.CENTER);
	}

	private Component getCenter() {
		Box box = Box.createHorizontalBox();
		box.add(getChangeDataArea());
		box.add(getChangeVerticalMode());
		box.add(getChangeDrawSeriesMode());
		dataCycleLabel = new JLabel("�f�[�^�����F1��");
		setHorizontalScaleButtons(box);
		box.add(Box.createHorizontalGlue());
		box.add(dataCycleLabel);
		box.add(Box.createHorizontalStrut(10));
		setReferenceDateLabel(box);
		return box;
	}

	private Component getChangeDataArea() {
		JButton button = new JButton("�����X�V");
		button.setMargin(BUTTON_INSETS);
		dataAreaListener = new DataAreaListener(this, "�����X�V", "�S�f�[�^");
		button.addActionListener(dataAreaListener);
		return button;
	}

	private Component getChangeVerticalMode() {
		JButton button = new JButton("���ݗ��\��");
		button.setMargin(BUTTON_INSETS);
		verticalModeListener =
			new VerticalModeListener(this, "���ݗ��\��", "���ݑS�\��");
		button.addActionListener(verticalModeListener);
		return button;
	}

	private Component getChangeDrawSeriesMode() {
		JButton button = new JButton("�����\��");
		button.setMargin(BUTTON_INSETS);
		drawSeriesModeListener =
			new DrawSeriesModeListener(this, "�����\��", "�����\��");
		button.addActionListener(drawSeriesModeListener);
		return button;
	}

	private void setHorizontalScaleButtons(Box box) {
		box.add(Box.createHorizontalStrut(50));
		listener1 =
			getButtonListener(
				dataCycleLabel,
				"4����",
				"1��",
				5,
				112,
				168,
				18000000L,
				60000L,
				"log_table_minute");
		box.add(getButton(listener1, "4����"));
		listener2 =
			getButtonListener(
				dataCycleLabel,
				"12����",
				"1��",
				8,
				70,
				105,
				54000000L,
				60000L,
				"log_table_minute");
		box.add(getButton(listener2, "12����"));
		listener3 =
			getButtonListener(
				dataCycleLabel,
				"1����",
				"10��",
				8,
				70,
				105,
				86400000L,
				600000L,
				"log_table_10_minute");
		box.add(getButton(listener3, "1����"));
		listener4 =
			getButtonListener(
				dataCycleLabel,
				"3����",
				"10��",
				8,
				70,
				105,
				259200000L,
				600000L,
				"log_table_10_minute");
		box.add(getButton(listener4, "3����"));
		listener5 =
			getButtonListener(
				dataCycleLabel,
				"1�T��",
				"1����",
				8,
				70,
				105,
				604800000L,
				3600000L,
				"log_table_hour");
		box.add(getButton(listener5, "1�T��"));
		listener6 =
			getButtonListener(
				dataCycleLabel,
				"1����",
				"1����",
				7,
				80,
				120,
				18144000000L,
				3600000L,
				"log_table_hour");
		box.add(getButton(listener6, "1����"));
	}

	private HorizontalScaleButtonListener getButtonListener(
			JLabel dataCycleLabel,
			String buttonText,
			String labelText,
			int horizontalCount,
			int horizontalAllSpanMode,
			int horizontalSelectSpanMode,
			long horizontalLineSpan,
			long recordeSpan,
			String logName) {
		return new HorizontalScaleButtonListener(
			this,
			dataCycleLabel,
			labelText,
			horizontalCount,
			horizontalAllSpanMode,
			horizontalSelectSpanMode,
			horizontalLineSpan,
			recordeSpan,
			logName);
	}

	private Component getButton(
			HorizontalScaleButtonListener l,
			String buttonText) {
		JButton button = new JButton(buttonText);
		button.setMargin(BUTTON_INSETS);
		button.addActionListener(l);
		return button;
	}

	private void setReferenceDateLabel(Box box) {
		referenceDateLabel = new JLabel("�Q�Ɠ����F                     ");
		box.add(referenceDateLabel);
	}

	public GraphChangeEvent getGraphChangeEvent() {
		return mediator.getGraphChangeEvent();
	}

	public void colleaguChanged(Colleague colleague) {
		isDataAreaListener = false;
		if (dataAreaListener == colleague) {
			dataAreaListener.performColleagueChange(getGraphChangeEvent());
			isDataAreaListener = true;
		} else if (drawSeriesModeListener == colleague) {
			drawSeriesModeListener
				.performColleagueChange(getGraphChangeEvent());
		} else if (verticalModeListener == colleague) {
			verticalModeListener.performColleagueChange(getGraphChangeEvent());
		} else if (listener1 == colleague) {
			listener1.performColleagueChange(getGraphChangeEvent());
		} else if (listener2 == colleague) {
			listener2.performColleagueChange(getGraphChangeEvent());
		} else if (listener3 == colleague) {
			listener3.performColleagueChange(getGraphChangeEvent());
		} else if (listener4 == colleague) {
			listener4.performColleagueChange(getGraphChangeEvent());
		} else if (listener5 == colleague) {
			listener5.performColleagueChange(getGraphChangeEvent());
		} else if (listener6 == colleague) {
			listener6.performColleagueChange(getGraphChangeEvent());
		}
		mediator.colleaguChanged(this);
	}

	public void performColleagueChange(GraphChangeEvent e) {
		Format f = new SimpleDateFormat("'�Q�Ɠ����F'yyyy/MM/dd HH:mm:ss");
		LogData graphClickedData = e.getView().getGraphClickedData();
		if (null == graphClickedData) {
			referenceDateLabel.setText("�Q�Ɠ����F");
		} else {
			Date date = graphClickedData.getDate();
			referenceDateLabel.setText(f.format(date));
		}
	}
	
	public boolean isDataAreaListener() {
		return isDataAreaListener;
	}

	private static abstract class AbstractActionListener implements
			ActionListener, Colleague {
		protected final Mediator mediator;
		private boolean isChange;
		private final String[] labels;

		public AbstractActionListener(Mediator mediator, String... labels) {
			this.mediator = mediator;
			this.labels = labels;
		}

		public void actionPerformed(ActionEvent e) {
			JButton button = (JButton) e.getSource();
			if (isChange) {
				String text = labels[0];
				button.setText(text);
				isChange = false;
				changeMode();
			} else {
				String text = labels[1];
				button.setText(text);
				isChange = true;
				changeMode();
			}
		}

		protected abstract void changeMode();
	}

	private static class DataAreaListener extends AbstractActionListener {
		public DataAreaListener(Mediator mediator, String... labels) {
			super(mediator, labels);
		}

		@Override
		protected void changeMode() {
			mediator.colleaguChanged(this);
		}

		public void performColleagueChange(GraphChangeEvent e) {
			e.getView().changeDataArea();
		}
	}

	private static class VerticalModeListener extends AbstractActionListener {
		public VerticalModeListener(Mediator mediator, String... labels) {
			super(mediator, labels);
		}

		@Override
		protected void changeMode() {
			mediator.colleaguChanged(this);
		}

		public void performColleagueChange(GraphChangeEvent e) {
			e.getView().changeSpanDisplayMode();
		}
	}

	private static class DrawSeriesModeListener extends AbstractActionListener {
		public DrawSeriesModeListener(Mediator mediator, String... labels) {
			super(mediator, labels);
		}

		@Override
		protected void changeMode() {
			mediator.colleaguChanged(this);
		}

		public void performColleagueChange(GraphChangeEvent e) {
			e.getView().changeDrawSeriesMode();
		}
	}

	private static class HorizontalScaleButtonListener implements
			ActionListener, Colleague {
		private final Mediator mediator;
		private final JLabel label;
		private final String labelText;
		private final int horizontalCount;
		private final int horizontalForAllSpanMode;
		private final int horizontalForSelectSpanMode;
		private final long horizontalLineSpan;
		private final long recordeSpan;
		private final String logName;

		public HorizontalScaleButtonListener(
				Mediator mediator,
				JLabel label,
				String labelText,
				int horizontalCount,
				int horizontalForAllSpanMode,
				int horizontalForSelectSpanMode,
				long horizontalLineSpan,
				long recordeSpan,
				String logName) {
			this.mediator = mediator;
			this.label = label;
			this.labelText = labelText;
			this.horizontalCount = horizontalCount;
			this.horizontalForAllSpanMode = horizontalForAllSpanMode;
			this.horizontalForSelectSpanMode = horizontalForSelectSpanMode;
			this.horizontalLineSpan = horizontalLineSpan;
			this.recordeSpan = recordeSpan;
			this.logName = logName;
		}

		public void actionPerformed(ActionEvent e) {
			mediator.colleaguChanged(this);
		}

		public void performColleagueChange(GraphChangeEvent e) {
			label.setText("�f�[�^�����F" + labelText);
			e.getView().setHorizontalScale(
				horizontalCount,
				horizontalForAllSpanMode,
				horizontalForSelectSpanMode,
				horizontalLineSpan,
				logName);
			int min = (int) (horizontalLineSpan / recordeSpan);
			e.getScrollBar().setMinimum(min);
		}
	}
}
