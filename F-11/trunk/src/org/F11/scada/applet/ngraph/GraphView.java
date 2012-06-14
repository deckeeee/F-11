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

import java.awt.BasicStroke;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import javax.swing.JPanel;
import javax.swing.JScrollBar;

import org.F11.scada.applet.ngraph.draw.CompositGraphDraw;
import org.F11.scada.applet.ngraph.draw.GraphDraw;
import org.F11.scada.applet.ngraph.draw.SegmentasionGraphDraw;
import org.F11.scada.applet.ngraph.event.GraphChangeEvent;
import org.F11.scada.applet.ngraph.model.GraphModel;
import org.F11.scada.applet.ngraph.util.MapUtil;
import org.apache.commons.collections.primitives.DoubleCollections;
import org.apache.commons.lang.time.FastDateFormat;
import org.apache.log4j.Logger;

/**
 * �܂���O���t��\������R���|�[�l���g
 *
 * @author maekawa
 *
 */
public class GraphView extends JPanel implements AdjustmentListener, Mediator,
		Colleague {
	private static final long serialVersionUID = -7135402419995170758L;
	/** �Q�ƃf�[�^�\���ׂ̈ɃO���t��ʃN���b�N���ɔ�������C�x���g���� */
	public static final String GRAPH_CLICKED_CHANGE = GraphView.class.getName()
		+ "graph.clicked.change";
	/** ��{�`��X�g���[�N */
	private static final BasicStroke BASIC_STROKE = new BasicStroke();
	/** �c�ڐ��_���X�g���[�N */
	private static final BasicStroke GRID_LINE_STROKE = getGridLineStroke();
	/** �Q�ƒl�c���X�g���[�N */
	private static final BasicStroke CLICK_POINT_STROKE = getClickPointStroke();
	/** �����~���R���p���[�^ */
	private static final Comparator<LogData> DATE_COMPARATOR = getComparator();
	/** ���M���OAPI */
	private final Logger logger = Logger.getLogger(GraphView.class.getName());
	/** �O���t���f�� */
	private GraphModel model;
	/** �O���t���f���̕ύX���X�i�[ */
	private GraphModelListener modelListener;
	/** �X�N���[���o�[�̌��ݒl */
	private int scrollBarIndex;
	/** �O���t�v���p�e�B�[ */
	private GraphProperties graphProperties;
	/** �\������f�[�^�̔z�� */
	private LogData[] displayDatas;
	/** �I������Ă���V���[�Y�̃C���f�b�N�X */
	private int selectSeriesIndex;
	/** �V���[�Y�̍ő�l */
	private int seriesMaxCount;
	/** ���݂̕\���f�[�^���[�h */
	private boolean isAllDataDisplayMode;
	/** �O���t�`��I�u�W�F�N�g�̃}�b�v */
	private final Map<Boolean, GraphDraw> graphDrawMap;
	/** �O���t��ʃN���b�N���̎Q�ƃf�[�^ */
	private LogData graphClickedData;
	/** ���C���p�l�� */
	private final Mediator mediator;
	/** �O���t�̃}�E�X���X�i�[ */
	private GraphMouseListener mouseListener;

	/**
	 * �O���t�R���|�[�l���g�����������܂�
	 *
	 * @param model �O���t���f��
	 * @param properties �O���t�v���p�e�B�[
	 */
	public GraphView(Mediator mediator,
			GraphModel model,
			GraphProperties properties) {
		this.mediator = mediator;
		this.model = model;
		modelListener = new GraphModelListener(this);
		this.model.addPropertyChangeListener(modelListener);
		this.graphProperties = properties;
		displayDatas = new LogData[model.getMaxRecord() + 1];
		Arrays.fill(displayDatas, LogData.ZERO);
		graphDrawMap = getGraphDrawMap();
		setPreferredSize(getInitialSize());
		mouseListener = new GraphMouseListener(this);
		addMouseListener(mouseListener);
	}

	/**
	 * �X�p�����\���ƑS�\���̕`��I�u�W�F�N�g�𐶐����}�b�v�ŕԂ��܂��B
	 *
	 * @return �X�p�����\���ƑS�\���̕`��I�u�W�F�N�g�𐶐����}�b�v�ŕԂ��܂��B
	 */
	private Map<Boolean, GraphDraw> getGraphDrawMap() {
		HashMap<Boolean, GraphDraw> map = new HashMap<Boolean, GraphDraw>();
		map.put(Boolean.TRUE, new CompositGraphDraw(graphProperties));
		map.put(Boolean.FALSE, new SegmentasionGraphDraw(graphProperties));
		return map;
	}

	private Dimension getInitialSize() {
		Insets insets = graphProperties.getInsets();
		return new Dimension(
			insets.left + getHorizontalLine() + insets.right,
			insets.top + graphProperties.getVerticalLine() + insets.bottom);
	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		setBackground(graphProperties.getBackGround());
		paintSeries(g);
		if (null != graphClickedData) {
			paintGraphClickedPoint(g);
		}
	}

	private void paintSeries(Graphics g) {
		paintData(g);
		paintHorizontalLine(g);
		paintVerticalLine(g);
	}

	private void paintData(Graphics g) {
		drawVerticalGridLine(g);
		drawSeries(g);
		drawHorizontalString(g);
	}

	/**
	 * �c���̃O���b�h���C��(�_��)��`��
	 *
	 * @param g
	 */
	private void drawVerticalGridLine(Graphics g) {
		Graphics2D g2d = (Graphics2D) g;
		int verticalScale = graphProperties.getVerticalScale();
		int top = graphProperties.getInsets().top;
		int left = graphProperties.getInsets().left;
		int x1 = left + getHorizontalLine();
		int verticalCount = graphProperties.getVerticalCount();
		for (int i = 0; i <= verticalCount; i++) {
			if (i != verticalCount) {
				g2d.setStroke(GRID_LINE_STROKE);
				g2d.setColor(graphProperties.getVerticalScaleColor());
				int vs = i * verticalScale;
				g2d.drawLine(left + 1, top + vs, x1 + 1, top + vs);
			}
		}
		g2d.setStroke(BASIC_STROKE);
	}

	private void drawSeries(Graphics g) {
		try {
			graphDrawMap.get(graphProperties.isCompositionMode()).drawSeries(
				g,
				scrollBarIndex,
				displayDatas,
				graphProperties.isAllSpanDisplayMode());
		} catch (Exception e) {
			logger.info("scrollBarIndex=" + scrollBarIndex, e);
		}
	}

	/**
	 * �����̓����������`��
	 *
	 * @param g
	 */
	private void drawHorizontalString(Graphics g) {
		g.setColor(graphProperties.getLineColor());
		g.setFont(graphProperties.getFont());
		Insets insets = graphProperties.getInsets();
		long horizontalLineSpan = graphProperties.getHorizontalLineSpan();
		boolean zerodata = false;
		if (displayDatas[scrollBarIndex] == LogData.ZERO) {
			zerodata = true;
		}
		long startDate =
			displayDatas[scrollBarIndex].getDate().getTime()
				- horizontalLineSpan;
		int horizontalCount = graphProperties.getHorizontalCount();
		long span = horizontalLineSpan / horizontalCount;
		for (long i = 0; i <= horizontalLineSpan; i += span) {
			Date date = new Date(startDate + i);
			int x1 = getX(date, startDate);
			drawHorizontalString(
				g,
				insets,
				date,
				x1,
				graphProperties.getDateFormat(),
				1.4F,
				zerodata);
			drawHorizontalString(
				g,
				insets,
				date,
				x1,
				graphProperties.getTimeFormat(),
				2.4F,
				zerodata);
		}
	}

	private int getX(Date value, long startDate) {
		return (int) Math.round((double) getHorizontalLine()
			/ (double) graphProperties.getHorizontalLineSpan()
			* (double) (value.getTime() - startDate));
	}

	private boolean isWriteHScale(int i) {
		return i == 0
			|| i
				% graphProperties.getHorizontalScale(graphProperties
					.isAllSpanDisplayMode()) == 0;
	}

	private void drawHorizontalString(Graphics g,
			Insets insets,
			Date date,
			int x2,
			String format,
			float z,
			boolean zerodata) {
		String dateStr = "";
		if (!zerodata) {
			FastDateFormat f = FastDateFormat.getInstance(format);
			dateStr = f.format(date);
		}
		FontMetrics metrics = g.getFontMetrics();
		g.drawString(
			dateStr,
			(x2 - metrics.stringWidth(dateStr) / 2) + insets.left,
			insets.top
				+ Math.round(graphProperties.getVerticalLine()
					+ metrics.getAscent()
					* z));
	}

	/**
	 * �������Ɩڐ�����`��
	 *
	 * @param g
	 */
	private void paintHorizontalLine(Graphics g) {
		int horizontalLine = getHorizontalLine();
		Insets insets = graphProperties.getInsets();
		int vertical = graphProperties.getVerticalLine();
		g.setColor(graphProperties.getLineColor());
		g.drawLine(insets.left, vertical + insets.top, horizontalLine
			+ insets.left, vertical + insets.top);
		for (int i = 0; i <= horizontalLine; i++) {
			int x1 = horizontalLine - i;
			if (isWriteHScale(i)) {
				g.drawLine(
					x1 + insets.left,
					vertical + insets.top,
					x1 + insets.left,
					vertical
						+ graphProperties.getScalePixcelSize()
						+ insets.top);
			}
		}
	}

	/**
	 * �c������`��
	 *
	 * @param g
	 */
	private void paintVerticalLine(Graphics g) {
		drawLeft(g);
		drawRight(g);
	}

	/**
	 * ���c����`��
	 *
	 * @param g
	 */
	private void drawLeft(Graphics g) {
		int top = graphProperties.getInsets().top;
		int x1 = graphProperties.getInsets().left;
		int vertical = graphProperties.getVerticalLine();
		Graphics2D g2d = (Graphics2D) g;
		g2d.setColor(graphProperties.getLineColor());
		g2d.drawLine(x1, top, x1, top + vertical);
		drawLeftString(g2d);
	}

	/**
	 * ���c���̕�����(�X�P�[��)��`��
	 *
	 * @param g
	 */
	private void drawLeftString(Graphics g) {
		int top = graphProperties.getInsets().top;
		int x1 = graphProperties.getInsets().left;
		g.setFont(graphProperties.getFont());
		GraphDraw gd = graphDrawMap.get(graphProperties.isCompositionMode());
		gd.drawUnitMark(g, top, x1, selectSeriesIndex);
		for (int i = 0; i <= graphProperties.getVerticalCount(); i++) {
			int vs = i * graphProperties.getVerticalScale();
			g.setColor(graphProperties.getLineColor());
			g.drawLine(
				x1 - graphProperties.getScalePixcelSize(),
				top + vs,
				x1,
				top + vs);
			gd.drawVerticalString(g, top, x1, vs, i, selectSeriesIndex);
		}
	}

	/**
	 * �E�c���̂�`��
	 *
	 * @param g
	 */
	private void drawRight(Graphics g) {
		int horizontalLine = getHorizontalLine();
		int top = graphProperties.getInsets().top;
		int left = graphProperties.getInsets().left;
		int vertical = graphProperties.getVerticalLine();
		int verticalScale = graphProperties.getVerticalScale();
		int x1 = left + horizontalLine;
		Graphics2D g2d = (Graphics2D) g;
		g2d.setColor(graphProperties.getLineColor());
		g2d.drawLine(x1, top, x1, top + vertical);
		g2d.setFont(graphProperties.getFont());
		for (int i = 0; i <= graphProperties.getVerticalCount(); i++) {
			int vs = i * verticalScale;
			g2d.drawLine(
				x1,
				top + vs,
				x1 + graphProperties.getScalePixcelSize(),
				top + vs);
		}
		if (graphProperties.isAllSpanDisplayMode()) {
			drawAllSpan(g);
		}
	}

	/**
	 * �X�p���S�\���p�̉E�c����`��
	 *
	 * @param g
	 */
	private void drawAllSpan(Graphics g) {
		SeriesProperties[] sps = getSeriesProperties();
		int horizontalLine = getHorizontalLine();
		int top = graphProperties.getInsets().top;
		int left = graphProperties.getInsets().left;
		int verticalScale = graphProperties.getVerticalScale();
		Graphics2D g2d = (Graphics2D) g;
		g2d.setFont(graphProperties.getFont());
		int x1 = left + horizontalLine;
		int i = 1;
		GraphDraw gd = graphDrawMap.get(graphProperties.isCompositionMode());
		for (SeriesProperties sp : sps) {
			if (sp.isVisible()) {
				// �S�X�p���\�����̊Ԋu�B
				int stringWidth = graphProperties.getVerticalLineInterval();
				int x2 = x1 + i * stringWidth;
				for (int j = 0; j <= graphProperties.getVerticalCount(); j++) {
					int vs = j * verticalScale;
					g2d.setColor(graphProperties.getLineColor());
					g2d.drawLine(
						x2,
						top + vs,
						x2 + graphProperties.getScalePixcelSize(),
						top + vs);
					gd.drawVerticalString(
						g,
						top,
						x2 + graphProperties.getScalePixcelSize(),
						vs,
						j,
						sp.getIndex());
				}
				g2d.setColor(graphProperties.getLineColor());
				gd.drawUnitMark(
					g,
					top,
					x2 + graphProperties.getScalePixcelSize(),
					sp.getIndex());
				i++;
			}
		}
	}

	private SeriesProperties[] getSeriesProperties() {
		List<SeriesProperties> sp =
			graphProperties.getSeriesGroup().getSeriesProperties();
		SeriesProperties[] sps = new SeriesProperties[sp.size() - 1];
		int i = 0, j = 0;
		for (SeriesProperties seriesProperties : sp) {
			if (i != selectSeriesIndex) {
				sps[j] = seriesProperties;
				j++;
			}
			i++;
		}
		return sps;
	}

	/**
	 * �O���t���N���b�N(�Q�ƒl�\��)�����ꍇ�́A�Q�ƈʒu�̏c���̕`��
	 *
	 * @param g
	 */
	private void paintGraphClickedPoint(Graphics g) {
		int x = getClickedPoint() + graphProperties.getInsets().left;
		if (isGraphViewContent(x)) {
			Graphics2D g2d = (Graphics2D) g;
			// �_����`�悵��
			g2d.setColor(graphProperties.getLineColor());
			g2d.setStroke(CLICK_POINT_STROKE);
			Insets insets = graphProperties.getInsets();
			g2d.drawLine(
				x,
				insets.top,
				x,
				insets.top + graphProperties.getVerticalLine());
			g2d.setStroke(BASIC_STROKE);
			// �t�O�p��`�悷��
			g2d.fill(getPolygon(x, insets));
		}
	}

	private int getClickedPoint() {
		long startDate =
			displayDatas[scrollBarIndex].getDate().getTime()
				- graphProperties.getHorizontalLineSpan();
		return getX(graphClickedData.getDate(), startDate);
	}

	private boolean isGraphViewContent(int x) {
		return graphProperties.isVisibleReferenceLine()
			&& graphProperties.getInsets().left <= x
			&& graphProperties.getInsets().left + getHorizontalLine() >= x;
	}

	private Polygon getPolygon(int x, Insets insets) {
		int height = 15;
		return new Polygon(new int[] { x, x - 9, x + 9 }, new int[] {
			insets.top,
			insets.top - height,
			insets.top - height }, 3);
	}

	/**
	 * �O���t���f����Ԃ��܂�
	 *
	 * @return �O���t���f����Ԃ��܂�
	 */
	public GraphModel getModel() {
		return model;
	}

	/**
	 * �O���t���f����ݒ肵�܂��B
	 *
	 * @param newModel �O���t���f��
	 */
	public void setModel(GraphModel newModel) {
		if (null != newModel && model != newModel) {
			GraphModel old = model;
			old.shutdown();
			old.removePropertyChangeListener(modelListener);
			model = newModel;
			model.addPropertyChangeListener(modelListener);
			repaint();
		} else {
			throw new IllegalArgumentException("������ GraphModel �� null �ł��B");
		}
	}

	/**
	 * �\���f�[�^�̔z���Ԃ��܂��B
	 *
	 * @return �\���f�[�^�̔z���Ԃ��܂��B
	 */
	public LogData[] getDisplayDatas() {
		return displayDatas;
	}

	/**
	 * �O���t�v���p�e�B�I�u�W�F�N�g��Ԃ��܂��B
	 *
	 * @return �O���t�v���p�e�B�I�u�W�F�N�g��Ԃ��܂��B
	 */
	public GraphProperties getGraphProperties() {
		return graphProperties;
	}

	/**
	 * �X�N���[���o�[�̕ύX�C�x���g�������ɌĂ΂�܂��B
	 *
	 * @param e �ύX�C�x���g�I�u�W�F�N�g
	 */
	public void adjustmentValueChanged(AdjustmentEvent e) {
		scrollBarIndex = model.getMaxRecord() - e.getValue();
		if (isReachedMinimum(e)) {
			if (model.reachedMinimum()) {
				setCenterPosition(e);
			}
		} else if (isReachedMaximum(e)) {
			if (model.reachedMaximum()) {
				setCenterPosition(e);
			}
		}
		repaint();
	}

	private boolean isReachedMinimum(AdjustmentEvent e) {
		JScrollBar bar = (JScrollBar) e.getSource();
		return bar.getMinimum() == bar.getValue() && !e.getValueIsAdjusting();
	}

	private boolean isReachedMaximum(AdjustmentEvent e) {
		JScrollBar bar = (JScrollBar) e.getSource();
		return bar.getMaximum() == bar.getValue() && !e.getValueIsAdjusting();
	}

	private void setCenterPosition(AdjustmentEvent e) {
		if (isAllDataDisplayMode) {
			JScrollBar bar = (JScrollBar) e.getSource();
			bar.setValue((bar.getMaximum() + bar.getMinimum()) / 2);
		}
	}

	/**
	 * �\���f�[�^�͈͂�ύX���܂�
	 *
	 */
	public void changeDataArea() {
		isAllDataDisplayMode = isAllDataDisplayMode ? false : true;
		mediator.colleaguChanged(this);
	}

	/**
	 * �\���f�[�^�͈̓��[�h��Ԃ��܂��B
	 *
	 * @return �S�f�[�^�\�����[�h�ł���� true �������łȂ���� false ��Ԃ��܂��B
	 */
	public boolean isAllDataDisplayMode() {
		return isAllDataDisplayMode;
	}

	/**
	 * �X�p���\�����[�h��ύX���܂�
	 *
	 */
	public void changeSpanDisplayMode() {
		graphProperties.setAllSpanDisplayMode(graphProperties
			.isAllSpanDisplayMode() ? false : true);
		repaint();
	}

	/**
	 * �����E�����\����ύX���܂�
	 *
	 */
	public void changeDrawSeriesMode() {
		graphProperties.setCompositionMode((graphProperties.isCompositionMode()
			? false
			: true));
		setVerticalCount();
		repaint();
	}

	private void setVerticalCount() {
		if (graphProperties.isCompositionMode()) {
			setVerticalCount(graphProperties.getCompositionVerticalCount());
		} else {
			setVerticalCount(seriesMaxCount * 2);
		}
	}

	private void setVerticalCount(int verticalCount) {
		graphProperties.setVerticalCount(verticalCount);
		graphProperties.setVerticalScale(graphProperties.getVerticalLine()
			/ verticalCount);
	}

	/**
	 * �I������Ă���V���[�Y�̃C���f�b�N�X��ݒ肵�܂�
	 *
	 * @param selectSeriesIndex �V���[�Y�̃C���f�b�N�X
	 */
	public void setSelectSeries(int selectSeriesIndex) {
		this.selectSeriesIndex = selectSeriesIndex;
		repaint();
	}

	/**
	 * �V���[�Y�̍ő�l��ݒ肵�܂�
	 *
	 * @param maxCount �V���[�Y�̍ő�l
	 */
	public void setSeriesMaxCount(int maxCount) {
		seriesMaxCount = maxCount;
		setVerticalCount();
	}

	/**
	 * �N���b�N���ꂽ�ʒu�̎Q�ƒl�f�[�^��Ԃ��܂��B
	 *
	 * @return �N���b�N���ꂽ�ʒu�̎Q�ƒl�f�[�^��Ԃ��܂�
	 */
	public LogData getGraphClickedData() {
		return graphClickedData;
	}

	/**
	 * �N���b�N���ꂽ�ʒu�̎Q�ƒl�f�[�^��ݒ肵�܂��B
	 *
	 * @param logData �N���b�N���ꂽ�ʒu�̎Q�ƒl�f�[�^
	 */
	public void setGraphClickedData(LogData logData) {
		graphClickedData = logData;
	}

	/**
	 * �O���t���N���b�N��������x���W��ݒ肵�܂��B
	 *
	 * @param p �O���t���N���b�N�������̍��W
	 */
	public void setGraphClickedPoint(Point p) {
		LogData old = graphClickedData;
		long startDate =
			displayDatas[scrollBarIndex].getDate().getTime()
				- graphProperties.getHorizontalLineSpan();
		// �~���b������̃s�N�Z����
		double f =
			((double) getHorizontalLine() / graphProperties
				.getHorizontalLineSpan());
		long c =
			Math
				.round((p.x - graphProperties.getInsets().left) / f + startDate);
		int clickedDataIndex =
			getClickedDataIndex(new LogData(
				new Date(c),
				DoubleCollections.EMPTY_DOUBLE_LIST));
		if (0 <= clickedDataIndex) {
			setGraphClickedData(displayDatas[clickedDataIndex]);
		} else {
			int i =
				Math.min(Math.abs(clickedDataIndex + 1), model.getMaxRecord());
			setGraphClickedData(displayDatas[i]);
		}
		firePropertyChange(GRAPH_CLICKED_CHANGE, old, graphClickedData);
		repaint();
	}

	private int getClickedDataIndex(LogData logData) {
		return Arrays.binarySearch(displayDatas, logData, DATE_COMPARATOR);
	}

	/**
	 * ���X�P�[���̕\���v���p�e�B��ݒ肵�܂�
	 *
	 * @param horizontalCount �ڐ��̐�
	 * @param horizontalForAllSpanMode �X�p���S�\�����̖ڐ��ЂƂ̒���(�s�N�Z��)
	 * @param horizontalForSelectSpanMode �X�p�����\�����̖ڐ��ЂƂ̒���(�s�N�Z��)
	 * @param horizontalLineSpan ���X�P�[���S�̂���������(�~���b)
	 * @param recordeSpan ���R�[�h�̊Ԋu(�~���b)
	 */
	public void setHorizontalScale(int horizontalCount,
			int horizontalForAllSpanMode,
			int horizontalForSelectSpanMode,
			long horizontalLineSpan,
			String logName) {
		graphProperties.setHorizontalCount(horizontalCount);
		graphProperties.setHorizontalForAllSpanMode(horizontalForAllSpanMode);
		graphProperties
			.setHorizontalForSelectSpanMode(horizontalForSelectSpanMode);
		graphProperties.setHorizontalLineSpan(horizontalLineSpan);
		model.setLogName(logName);
		repaint();
	}

	/**
	 * �X�p���\�����[�h�ɂ��킹�āA�O���t�̉����̒�����Ԃ��܂�(�s�N�Z��)
	 *
	 * @return �O���t�̉����̒�����Ԃ��܂�(�s�N�Z��)
	 */
	public int getHorizontalLine() {
		return graphProperties.getHorizontalLine(graphProperties
			.isAllSpanDisplayMode());
	}

	public void colleaguChanged(Colleague colleague) {
		if (mouseListener == colleague) {
			mouseListener.performColleagueChange(getGraphChangeEvent());
			mediator.colleaguChanged(this);
		} else if (modelListener == colleague) {
			modelListener.performColleagueChange(getGraphChangeEvent());
			mediator.colleaguChanged(this);
		}
	}

	public GraphChangeEvent getGraphChangeEvent() {
		return mediator.getGraphChangeEvent();
	}

	public void performColleagueChange(GraphChangeEvent e) {

	}

	/**
	 * �O���t���f���̕ύX�C�x���g���X�i�[
	 *
	 * @author maekawa
	 *
	 */
	private static class GraphModelListener implements PropertyChangeListener,
			Colleague {
		private final Mediator mediator;
		private PropertyChangeEvent event;
		private SortedMap<Date, LogData> mainMap = new TreeMap<Date, LogData>(
			new ReverseDateComparator());

		private final Logger logger = Logger
			.getLogger(GraphModelListener.class);

		GraphModelListener(Mediator mediator) {
			this.mediator = mediator;
		}

		public void propertyChange(PropertyChangeEvent evt) {
			this.event = evt;
			mediator.colleaguChanged(this);
		}

		public synchronized void performColleagueChange(GraphChangeEvent e) {
			GraphView view = e.getView();
			if (GraphModel.INITIALIZE.equals(event.getPropertyName())) {
				List<LogData> logDatas = (List<LogData>) event.getNewValue();
				mainMap.clear();
				Arrays.fill(view.getDisplayDatas(), LogData.ZERO);
				for (LogData logData : logDatas) {
					mainMap.put(logData.getDate(), logData);
				}
				setDisplayDatas(view);
			} else if (GraphModel.GROUP_CHANGE.equals(event.getPropertyName())) {
				view.setGraphClickedData(null);
			} else {
				LogData logData = (LogData) event.getNewValue();
				mainMap.put(logData.getDate(), logData);
				MapUtil.trimSortedMap(
					mainMap,
					view.getModel().getMaxRecord() + 1);
				setDisplayDatas(view);
			}
			view.repaint();
			// printMainMap();
		}

		// private void printMainMap() {
		// logger.info("printMainMap>>");
		// int i = 0;
		// for (Map.Entry<Date, LogData> entry : mainMap.entrySet()) {
		// Date date = entry.getKey();
		// logger.info(date);
		// i++;
		// if (i >= 10) {
		// break;
		// }
		// }
		// logger.info("printMainMap<<");
		// ThreadUtil.printSS();
		// }

		private void setDisplayDatas(GraphView view) {
			LogData[] displayDatas = view.getDisplayDatas();
			int index = 0;
			for (Map.Entry<Date, LogData> set : mainMap.entrySet()) {
				if (displayDatas.length - 1 < index) {
					break;
				}
				displayDatas[index++] = set.getValue();
			}
		}
	}

	/**
	 * �O���t�̃N���b�N�����m����}�E�X���X�i�[
	 *
	 * @author maekawa
	 *
	 */
	private static class GraphMouseListener extends MouseAdapter implements
			Colleague {
		private final Mediator mediator;
		private Point clickPoint;

		GraphMouseListener(Mediator mediator) {
			this.mediator = mediator;
		}

		@Override
		public void mousePressed(MouseEvent e) {
			clickPoint = e.getPoint();
			mediator.colleaguChanged(this);
		}

		public void performColleagueChange(GraphChangeEvent e) {
			GraphView view = e.getView();
			if (isGraphClicked(clickPoint, view)) {
				view.setGraphClickedPoint(clickPoint);
			}
		}

		private boolean isGraphClicked(Point p, GraphView view) {
			GraphProperties graphProperties = view.getGraphProperties();
			Insets insets = graphProperties.getInsets();
			Rectangle r =
				new Rectangle(
					insets.left,
					insets.top,
					view.getHorizontalLine()
						+ graphProperties.getScalePixcelSize(),
					graphProperties.getVerticalLine());
			return r.contains(p);
		}
	}

	/**
	 * �������~���Ń~���b�A�b�𖳎�����R���p���[�^��Ԃ��܂�
	 *
	 * @return �������~���Ń~���b�A�b�𖳎�����R���p���[�^��Ԃ��܂�
	 */
	private static Comparator<LogData> getComparator() {
		return new Comparator<LogData>() {
			Calendar cal = Calendar.getInstance();

			public int compare(LogData o1, LogData o2) {
				// �~���b�A�b�͖�������
				cal.setTime(o1.getDate());
				cal.set(Calendar.MILLISECOND, 0);
				cal.set(Calendar.SECOND, 0);
				Date d1 = cal.getTime();
				cal.setTime(o2.getDate());
				cal.set(Calendar.MILLISECOND, 0);
				cal.set(Calendar.SECOND, 0);
				Date d2 = cal.getTime();
				return d1.compareTo(d2) * -1;
			}
		};
	}

	/**
	 * ���O���b�h�̓_���X�g���[�N��Ԃ��܂�
	 *
	 * @return ���O���b�h�̓_���X�g���[�N��Ԃ��܂�
	 */
	private static BasicStroke getGridLineStroke() {
		return new BasicStroke(
			1.0f,
			BasicStroke.CAP_BUTT,
			BasicStroke.JOIN_MITER,
			10.0f,
			new float[] { 4.0f },
			0.0f);
	}

	private static BasicStroke getClickPointStroke() {
		return new BasicStroke(
			1.0f,
			BasicStroke.CAP_BUTT,
			BasicStroke.JOIN_MITER,
			10.0f,
			new float[] { 14f, 4f },
			0.0f);
	}
}
