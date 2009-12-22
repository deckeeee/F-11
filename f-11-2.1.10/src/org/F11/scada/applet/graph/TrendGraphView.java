/*
 * $Header: /cvsroot/f-11/F-11/src/org/F11/scada/applet/graph/TrendGraphView.java,v 1.19.2.24 2006/09/01 01:07:17 frdm Exp $
 * $Revision: 1.19.2.24 $
 * $Date: 2006/09/01 01:07:17 $
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

package org.F11.scada.applet.graph;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.sql.Timestamp;
import java.text.Format;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import javax.swing.BoundedRangeModel;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JScrollBar;
import javax.swing.SwingUtilities;

import org.F11.scada.Service;
import org.F11.scada.WifeUtilities;
import org.F11.scada.applet.symbol.ColorFactory;
import org.F11.scada.server.register.HolderString;
import org.apache.commons.lang.time.FastDateFormat;
import org.apache.log4j.Logger;
import org.xml.sax.SAXException;

/**
 * �g�����h�O���t�R���|�[�l���g�N���X�ł��B �ܐ��O���t��S������ LineGraph �N���X�ƁA�X�N���[���o�[��S������
 * LineGraphScrollBar �ō\������Ă��܂��B
 * 
 * @author Hideaki Maekawa <frdm@users.sourceforge.jp>
 */
public class TrendGraphView extends Box implements PropertyChangeListener,
		Service {
	private static final long serialVersionUID = 2599137567371934139L;
	/** ���M���OAPI */
	private static Logger logger;
	private final LineGraph graph;
	private LineGraphScrollBar scrollBar;

	/**
	 * �R���X�g���N�^
	 * 
	 * @param graphPropertyModel �O���t�v���p�e�B�E���f���̎Q��
	 */
	public TrendGraphView(GraphPropertyModel graphPropertyModel)
			throws IOException, SAXException {
		this(graphPropertyModel, new DefaultGraphModelFactory(
				"org.F11.scada.applet.graph.DefaultSelectiveGraphModel",
				graphPropertyModel,
				"�����X�V"), false, true, true);
	}

	/**
	 * �R���X�g���N�^
	 * 
	 * @param graphPropertyModel �O���t�v���p�e�B�E���f���̎Q��
	 * @param isViewVerticalScale �c�O���b�h�_���̗L��
	 * @param isMouseClickEnable �O���t�I�u�W�F�N�g�̃}�E�X�C�x���g�L��
	 * @param isDrawString X������������̕\���L��
	 */
	public TrendGraphView(
			GraphPropertyModel graphPropertyModel,
			boolean isViewVerticalScale,
			boolean isMouseClickEnable,
			boolean isDrawString,
			int maxMapSize) throws IOException, SAXException {
		this(
				graphPropertyModel,
				new DefaultGraphModelFactory(
						"org.F11.scada.applet.graph.DefaultSelectiveGraphModel",
						graphPropertyModel,
						"�����X�V",
						maxMapSize),
				isViewVerticalScale,
				isMouseClickEnable,
				isDrawString);
	}

	/**
	 * �R���X�g���N�^
	 * 
	 * @param graphPropertyModel �O���t�v���p�e�B�E���f���̎Q��
	 * @param factory �O���t���f���E�t�@�N�g���[
	 * @param isViewVerticalScale �c�O���b�h�_���̗L��
	 * @param isMouseClickEnable �O���t�I�u�W�F�N�g�̃}�E�X�C�x���g�L��
	 * @param isDrawString X������������̕\���L��
	 */
	public TrendGraphView(
			GraphPropertyModel graphPropertyModel,
			GraphModelFactory factory,
			boolean isViewVerticalScale,
			boolean isMouseClickEnable,
			boolean isDrawString) throws IOException, SAXException {

		super(BoxLayout.Y_AXIS);
		logger = Logger.getLogger(getClass().getName());
		graph = new LineGraph(
				graphPropertyModel,
				factory,
				isViewVerticalScale,
				isDrawString);
		scrollBar = new LineGraphScrollBar(graph);
		if (isMouseClickEnable) {
			LineGraphMouseListener listener = new LineGraphMouseListener(
					scrollBar);
			graph.addMouseListener(listener);
		}
		add(graph, BorderLayout.CENTER);
		add(scrollBar, BorderLayout.SOUTH);
		logger.debug("TrendGraphView const");
	}

	public synchronized void addPropertyChangeListener(
			PropertyChangeListener listener) {
		graph.addPropertyChangeListener(listener);
	}

	public synchronized void addPropertyChangeListener(
			String propertyName,
			PropertyChangeListener listener) {
		graph.addPropertyChangeListener(propertyName, listener);
	}

	public synchronized void removePropertyChangeListener(
			PropertyChangeListener listener) {
		graph.removePropertyChangeListener(listener);
	}

	public synchronized void removePropertyChangeListener(
			String propertyName,
			PropertyChangeListener listener) {
		graph.removePropertyChangeListener(propertyName, listener);
	}

	public void propertyChange(PropertyChangeEvent evt) {
		graph.setBackground(getParent().getBackground());
		graph.setForeground(getParent().getForeground());
	}

	public void setScrollBarVisible(boolean b) {
		scrollBar.setScrollBarVisible(b);
	}

	public void setStringColor(Color color) {
		graph.setStringColor(color);
	}

	public void start() {
		graph.start();
	}

	public void stop() {
		graph.stop();
	}

	public void setStrokeWidth(float width) {
		graph.setStrokeWidth(width);
	}

	public LineGraph getLineGraph() {
		return graph;
	}

	/**
	 * �ܐ��O���t�R���|�[�l���g�N���X
	 */
	static class LineGraph extends JComponent implements AdjustmentListener,
			PropertyChangeListener, Service {

		private static final long serialVersionUID = 6559762538695995468L;
		/** �O���t���f���Z�b�g�̃v���p�e�B�`�F���W�C�x���g�� */
		public static final String GRAPH_MODEL_SET = "org.F11.scada.applet.graph.LineGraph.GraphModelSet";
		/** �O���t�f�[�^���f�� */
		private GraphModel graphModel;
		/** �O���t�v���p�e�B���f�� */
		private GraphPropertyModel graphPropertyModel;
		/** �f�[�^�l�ƃO���t�`��̔䗦 */
		private double[] yScale;
		/** �O���t�̃I���W�� */
		private Point[] origin;
		/** �O���t�\������F */
		private Color[] graphColors;

		/** ���ݕ\�����Ă���f�[�^�̃X�^�[�g�C���f�b�N�X */
		private int currentStartIndex;
		/** ���ݕ\�����Ă���X���ڐ���̃��X�g */
		private List currentAxisList;
		/** �O���t�`��̈� */
		private Rectangle graphViewBounds;
		/** �Q�ƒl X���^�C���X�^���v */
		private Timestamp referenceValueTimestamp;
		/** �Q�ƒl�\���t���O */
		private boolean referenceValueFlag;
		/** �O���t���f���̃t�@�N�g���[�N���X */
		private GraphModelFactory factory;
		/** ������̐F */
		private Color stringColor;
		/** �c�X�P�[���_���̗L�� */
		private final boolean isViewVerticalScale;
		/** ���X�P�[���ڐ��蕶���̗L�� */
		private final boolean isDrawString;
		/** �X�g���[�N�̕� */
		private float strokeWidth = 1.0F;

		/**
		 * �R���X�g���N�^
		 * 
		 * @param graphPropertyModel �O���t�v���p�e�B�E���f���̎Q��
		 * @param factory �O���t���f���̃t�@�N�g���[�N���X
		 */
		public LineGraph(
				GraphPropertyModel graphPropertyModel,
				GraphModelFactory factory) throws IOException, SAXException {
			this(graphPropertyModel, factory, false, true);
		}

		/**
		 * �R���X�g���N�^
		 * 
		 * @param graphPropertyModel �O���t�v���p�e�B�E���f���̎Q��
		 * @param factory �O���t���f���E�t�@�N�g���[
		 * @param isViewVerticalScale �c�O���b�h�_���̗L��
		 * @param isMouseClickEnable �O���t�I�u�W�F�N�g�̃}�E�X�C�x���g�L��
		 * @param isDrawString X������������̕\���L��
		 */
		public LineGraph(
				GraphPropertyModel graphPropertyModel,
				GraphModelFactory factory,
				boolean isViewVerticalScale,
				boolean isDrawString) throws IOException, SAXException {
			super();
			this.graphPropertyModel = graphPropertyModel;
			this.graphPropertyModel.addPropertyChangeListener(this);
			this.graphPropertyModel.addPropertyChangeListener(
					GraphPropertyModel.GROUP_CHANGE_EVENT,
					this);
			this.graphPropertyModel.addPropertyChangeListener(
					GraphPropertyModel.X_SCALE_CHANGE_EVENT,
					this);
			this.factory = factory;
			this.isViewVerticalScale = isViewVerticalScale;
			this.isDrawString = isDrawString;

			updateGraphModel();

			graphColors = graphPropertyModel.getColors();
			setDoubleBuffered(true);
			changeDisplayData();
			rescale();
		}

		void setGraphModelFactory(GraphModelFactory factory) {
			GraphModelFactory old = this.factory;
			this.factory = factory;
			updateGraphModel();
			changeDisplayData();
			rescale();
			firePropertyChange(GRAPH_MODEL_SET, old, this.factory);
		}

		private void updateGraphModel() {
			ArrayList holderStrings = new ArrayList(graphPropertyModel
					.getSeriesSize());
			for (int i = 0, s = graphPropertyModel.getSeriesSize(); i < s; i++) {
				HolderString hs = new HolderString();
				hs.setProvider(graphPropertyModel.getDataProviderName(i));
				hs.setHolder(graphPropertyModel.getDataHolderName(i));
				holderStrings.add(hs);
			}
			GraphModel model = factory.getGraphModel(graphPropertyModel
					.getListHandlerName(), holderStrings);
			setModel(model);
		}

		/**
		 * ��ʕ\������f�[�^��ύX���܂��B
		 */
		private void changeDisplayData() {
			long scaleOneTime = graphPropertyModel.getHorizontalScaleWidth();
			currentAxisList = Collections.synchronizedList(new ArrayList(
					graphPropertyModel.getHorizontalScaleCount()));
			// X���̃�����������(���t)���Z�o
			Timestamp timestamp = (Timestamp) graphModel
					.firstKey(graphPropertyModel.getListHandlerName());
			long currentTime = currentStartIndex * scaleOneTime
					+ timestamp.getTime();
			for (int i = 0; i <= graphPropertyModel.getHorizontalScaleCount(); i++) {
				currentAxisList.add(new Date(currentTime + (scaleOneTime * i)));
			}
		}

		/**
		 * �R���|�[�l���g��`�悵�܂��B
		 * 
		 * @param g �O���t�B�b�N�R���e�L�X�g
		 */
		public void paintComponent(Graphics g) {
			super.paintComponent(g);
			Graphics2D g2d = (Graphics2D) g;
			// �X�P�[����`��
			drawAxis(g2d);
			// ���M���O�f�[�^��`��
			drawSeries(g2d);
			// �Q�ƒl�̔j����`��
			drawReference(g2d);
		}

		/**
		 * �ő�l�E�ŏ��l���`��ϊ������Z�o���܂��B
		 */
		private void rescale() {
			int scaleOneHeight = graphPropertyModel.getVerticalScaleHeight();
			int scaleCount = graphPropertyModel.getVerticalScaleCount();
			Insets scaleInsets = graphPropertyModel.getGraphiViewInsets();
			// Y���ƃf�[�^�̔䗦���Z�o
			yScale = new double[graphPropertyModel.getSeriesSize()];
			origin = new Point[graphPropertyModel.getSeriesSize()];
			for (int i = 0; i < graphPropertyModel.getSeriesSize(); i++) {
				yScale[i] = (double) (scaleOneHeight * scaleCount)
						/ (graphPropertyModel.getVerticalMaximum(i) - graphPropertyModel
								.getVerticalMinimum(i));
				origin[i] = new Point(scaleInsets.left, scaleInsets.top
						+ scaleOneHeight
						* scaleCount
						+ (int) Math.round(graphPropertyModel
								.getVerticalMinimum(i)
								* yScale[i]));
			}
			repaint();
		}

		/**
		 * �X�P�[���E�O���b�h�E�ڐ��蓙��`�悵�܂��B
		 * 
		 * @param g �O���t�B�b�N�R���e�L�X�g
		 */
		private void drawAxis(Graphics2D g2d) {
			int scaleOneHeight = graphPropertyModel.getVerticalScaleHeight();
			int scaleCount = graphPropertyModel.getVerticalScaleCount();
			int scaleCountWidth = graphPropertyModel.getHorizontalScaleCount();
			Insets scaleInsets = graphPropertyModel.getGraphiViewInsets();
			// X���̈ʒu���Z�o
			Point baseOrigin = new Point(scaleInsets.left, scaleInsets.top
					+ scaleOneHeight * scaleCount);
			// �w�i�F
			g2d.setColor(getBackground());
			graphViewBounds = new Rectangle(this.getSize());
			graphViewBounds.y = scaleInsets.top / 2;
			graphViewBounds.height = baseOrigin.y
					+ graphPropertyModel.getScaleOneHeightPixel()
					- scaleInsets.top / 2;
			g2d.fill(graphViewBounds);
			// �F�𔒂�
			g2d.setColor(getForeground());
			// X���`��
			int scaleOneWidth = graphPropertyModel.getHorizontalPixcelWidth()
					/ graphPropertyModel.getHorizontalScaleCount();
			if (!isDrawString) {
				float[] dash = { 3.1f };
				BasicStroke bs = new BasicStroke(
						1.0f,
						BasicStroke.CAP_BUTT,
						BasicStroke.JOIN_MITER,
						10.0f,
						dash,
						0.0f);
				g2d.setStroke(bs);
				g2d.setColor(ColorFactory.getColor("cornflowerblue"));
			}
			g2d.drawLine(baseOrigin.x, baseOrigin.y, baseOrigin.x
					+ scaleOneWidth * scaleCountWidth, baseOrigin.y);
			// X���ڐ���`��
			FontMetrics metrics = g2d.getFontMetrics();
			int strHeight = metrics.getHeight();
			synchronized (currentAxisList) {
				Iterator it = currentAxisList.iterator();
				Format dateFormat = FastDateFormat
						.getInstance(graphPropertyModel.getFirstFormat());
				Format timeFormat = FastDateFormat
						.getInstance(graphPropertyModel.getSecondFormat());
				for (int i = baseOrigin.x, j = 0, strWidth = 0; i <= baseOrigin.x
						+ scaleOneWidth * scaleCountWidth
						&& it.hasNext(); i += scaleOneWidth, j++) {

					if (isViewVerticalScale) {
						// �c�O���b�h�̔j����`��
						float[] dash = { 4.0f };
						BasicStroke bs = new BasicStroke(
								1.0f,
								BasicStroke.CAP_BUTT,
								BasicStroke.JOIN_MITER,
								10.0f,
								dash,
								0.0f);
						g2d.setStroke(bs);
						g2d.setColor(ColorFactory.getColor("cornflowerblue"));
						g2d.drawLine(
								i,
								baseOrigin.y
										- graphPropertyModel
												.getVerticalScaleHeight()
										* graphPropertyModel
												.getVerticalScaleCount(),
								i,
								baseOrigin.y);
						g2d.drawLine(
								getHeaf(i, scaleOneWidth),
								baseOrigin.y
										- graphPropertyModel
												.getVerticalScaleHeight()
										* graphPropertyModel
												.getVerticalScaleCount(),
								getHeaf(i, scaleOneWidth),
								baseOrigin.y);
					}

					// �`��F�𔒂�
					if (isDrawString) {
						g2d.setColor(getForeground());
						// �ڐ���̐���`��
						g2d.setStroke(new BasicStroke());
						g2d.drawLine(i, baseOrigin.y, i, baseOrigin.y
								+ graphPropertyModel.getScaleOneHeightPixel());
						// �ڐ���̉��ɓ��t�Ǝ��Ԃ�`��
						Date timestamp = (Date) it.next();
						String dateString = dateFormat.format(timestamp);
						strWidth = metrics.stringWidth(dateString);
						g2d.setColor(getStringColor());
						g2d.drawString(
								dateString,
								i - strWidth / 2,
								baseOrigin.y
										+ graphPropertyModel
												.getScaleOneHeightPixel()
										+ strHeight);
						String timeString = timeFormat.format(timestamp);
						strWidth = metrics.stringWidth(timeString);
						g2d.drawString(
								timeString,
								i - strWidth / 2,
								baseOrigin.y
										+ graphPropertyModel
												.getScaleOneHeightPixel()
										+ Math.round(strHeight * 2.1));
					}
				}
			}

			// �X�g���[�N��j���ɕύX
			float[] dash = { 3.1f };
			BasicStroke bs = new BasicStroke(
					1.0f,
					BasicStroke.CAP_BUTT,
					BasicStroke.JOIN_MITER,
					10.0f,
					dash,
					0.0f);
			g2d.setStroke(bs);
			g2d.setColor(ColorFactory.getColor("cornflowerblue"));
			for (int i = baseOrigin.y - scaleOneHeight; i >= scaleInsets.top; i -= scaleOneHeight) {
				g2d.drawLine(baseOrigin.x, i, baseOrigin.x + scaleOneWidth
						* scaleCountWidth, i);
			}
		}

		private int getHeaf(int i, int scaleOneWidth) {
			return scaleOneWidth / 2 + i;
		}

		/**
		 * �^�C���X�^���v���O���t��X�����W�ɕϊ����܂��B
		 * 
		 * @param timestamp �^�C���X�^���v
		 * @return X�����W
		 */
		private double getXtime(Timestamp timestamp) {
			if (null == timestamp) {
				return 0;
			}
			long scaleOneTime = graphPropertyModel.getHorizontalScaleWidth();
			// ���[�̃O���t���W���Z�o
			long currentTime = currentStartIndex
					* scaleOneTime
					+ ((Timestamp) graphModel.firstKey(graphPropertyModel
							.getListHandlerName())).getTime();
			// ���X�P�[���̒���(�_�b)���Z�o
			double screenTime = +scaleOneTime
					* graphPropertyModel.getHorizontalScaleCount();
			// �����̎��Ԃ�艡�X�P�[���̎���(�_�b)���Z�o
			return (double) graphPropertyModel.getHorizontalPixcelWidth()
					/ screenTime * (timestamp.getTime() - currentTime);
		}

		private void drawSeries(Graphics2D g2d) {
			// �J�n�����ɍł��߂����R�[�h�����o����i�w�莞�Ԃ��P���O�̃��R�[�h���܂ށj
			long scaleOneTime = graphPropertyModel.getHorizontalScaleWidth();
			long currentTime = currentStartIndex
					* scaleOneTime
					+ ((Timestamp) graphModel.firstKey(graphPropertyModel
							.getListHandlerName())).getTime();
			graphModel.findRecord(
					graphPropertyModel.getListHandlerName(),
					new Timestamp(currentTime));

			// �V���[�Y�f�[�^�𒊏o(X���v�Z�ŕ\������f�[�^�������߂�)
			if (!graphModel.next(graphPropertyModel.getListHandlerName())) {
				return;
			}
			LoggingData loggingData1 = (LoggingData) graphModel
					.get(graphPropertyModel.getListHandlerName());
			if (logger.isDebugEnabled()) {
				logger.debug("1:" + loggingData1);
			}
			Timestamp timestamp = loggingData1.getTimestamp();
			// ���[�̃O���t���W���Z�o
			double xTime1 = getXtime(timestamp);
			if (isDisplayRight(xTime1)) {
				return;
			}

			g2d.setStroke(new BasicStroke(strokeWidth));

			for (Point p1 = null, p2 = null; graphModel.next(graphPropertyModel
					.getListHandlerName());) {
				LoggingData loggingData2 = (LoggingData) graphModel
						.get(graphPropertyModel.getListHandlerName());
				if (logger.isDebugEnabled()) {
					logger.debug("2:" + loggingData2);
				}
				timestamp = loggingData2.getTimestamp();
				double xTime2 = getXtime(timestamp);
				loggingData1.first();
				loggingData2.first();

				for (int series = 0; (loggingData1.hasNext() && loggingData2
						.hasNext())
						&& (series < graphPropertyModel.getSeriesSize()); series++) {
					// �V���[�Y�f�[�^��`�悵�܂��B
					double item1 = loggingData1.next();
					double item2 = loggingData2.next();

					p1 = dataToPoint(xTime1, item1, series);
					p2 = dataToPoint(xTime2, item2, series);

					g2d.setColor(graphColors[series]);
					if (p1.x < origin[series].x) {
						invalidateLineX(p1, p2, origin[series].x);
					}
					if (p2.x > (origin[series].x + graphPropertyModel
							.getHorizontalPixcelWidth())) {
						invalidateLineX(p2, p1, origin[series].x
								+ graphPropertyModel.getHorizontalPixcelWidth());
					}
					invalidateLineY(p1, p2, graphViewBounds);
					g2d.drawLine(p1.x, p1.y, p2.x, p2.y);
				}
				if (isDisplayRight(xTime2)) {
					break;
				}
				loggingData1 = loggingData2;
				xTime1 = xTime2;
			}
		}

		private boolean isDisplayRight(double xTime) {
			int scaleCountWidth = graphPropertyModel.getHorizontalScaleCount();
			long scaleOneTime = graphPropertyModel.getHorizontalScaleWidth();

			Timestamp timestamp = (Timestamp) graphModel
					.firstKey(graphPropertyModel.getListHandlerName());
			double currentTime = currentStartIndex + scaleOneTime
					* scaleCountWidth;
			double screenTime = currentStartIndex * scaleOneTime
					+ timestamp.getTime() + (scaleCountWidth * scaleOneTime);
			double endTime = (double) graphPropertyModel
					.getHorizontalPixcelWidth()
					/ screenTime * (timestamp.getTime() - currentTime);
			if (xTime > endTime) {
				return true;
			}
			return false;
		}

		/**
		 * �f�[�^���O���t���W�ɕϊ����܂��B
		 * 
		 * @param x X�����W
		 * @param y �V���[�Y�f�[�^
		 * @param series �V���[�Y
		 * @return �`�悷��|�C���g
		 */
		private Point dataToPoint(double x, double y, int series) {
			return new Point((int) Math.round(origin[series].x + x), (int) Math
					.round(origin[series].y - yScale[series] * y));
		}

		private void invalidateLineX(Point p1, Point p2, int limitX) {
			if (Math.abs(p2.x - p1.x) < 1.0) {
				p1.x = limitX;
				return;
			}

			double a = 0, y = 0;
			a = ((double) p2.y - (double) p1.y)
					/ ((double) p2.x - (double) p1.x);
			y = a * ((double) limitX - (double) p1.x) + (double) p1.y;
			p1.x = limitX;
			p1.y = (int) Math.round(y);
		}

		private void invalidateLineY(Point p1, Point p2, Rectangle limitY) {
			int graphHeight = limitY.y + limitY.height
					- graphPropertyModel.getScaleOneHeightPixel();
			if (p1.y < limitY.y) {
				p1.y = limitY.y;
			} else if (p1.y > graphHeight) {
				p1.y = graphHeight;
			}

			if (p2.y < limitY.y) {
				p2.y = limitY.y;
			} else if (p2.y > graphHeight) {
				p2.y = graphHeight;
			}
		}

		public Dimension getPreferredSize() {
			int scaleOneHeight = graphPropertyModel.getVerticalScaleHeight();
			int scaleCount = graphPropertyModel.getVerticalScaleCount();
			int scaleOneWidth = graphPropertyModel.getHorizontalPixcelWidth()
					/ graphPropertyModel.getHorizontalScaleCount();
			Insets scaleInsets = graphPropertyModel.getGraphiViewInsets();

			return new Dimension(
					scaleInsets.left + scaleInsets.right + scaleOneWidth
							* graphPropertyModel.getHorizontalScaleCount(),
					scaleInsets.top + scaleInsets.bottom + scaleOneHeight
							* scaleCount);
		}

		/**
		 * �Q�ƒl�̔j���`�揈��
		 * 
		 * @param g �O���t�B�b�N�R���e�L�X�g
		 */
		private void drawReference(Graphics2D g2d) {
			if (!referenceValueFlag) {
				return;
			}

			// �O���t�\����X�������߂�
			double xTime1 = getXtime(referenceValueTimestamp);
			int referenceValue = graphPropertyModel.getGraphiViewInsets().left
					+ (int) Math.round(xTime1);
			if (isNotReferenceDraw(referenceValue)) {
				return;
			}

			// �X�g���[�N��j���ɕύX
			float[] dash = { 16.0f, 4.0f };
			BasicStroke bs = new BasicStroke(
					1.0f,
					BasicStroke.CAP_BUTT,
					BasicStroke.JOIN_MITER,
					10.0f,
					dash,
					0.0f);
			g2d.setStroke(bs);
			g2d.setColor(getForeground());
			g2d.drawLine(
					referenceValue,
					graphViewBounds.y,
					referenceValue,
					graphViewBounds.y + graphViewBounds.height);
		}

		private boolean isNotReferenceDraw(int referenceValue) {
			Insets insets = graphPropertyModel.getGraphiViewInsets();
			if (referenceValue < insets.left
					|| referenceValue > (insets.left + graphPropertyModel
							.getHorizontalPixcelWidth())) {
				return true;
			}
			return false;
		}

		public Dimension getMaximumSize() {
			return getPreferredSize();
		}

		public Dimension getMinimumSize() {
			return getPreferredSize();
		}

		public GraphModel getModel() {
			return this.graphModel;
		}

		public void setModel(GraphModel model) {
			setModel(model, true);
		}

		private void setModel(GraphModel model, boolean withRescale) {
			if (model == null) {
				throw new IllegalArgumentException(
						"Cannot set a null SelectiveGraphModel.");
			}

			if (graphModel != model) {
				GraphModel old = graphModel;
				if (old != null) {
					old.removePropertyChangeListener(this);
					old.stop();
				}
				graphModel = model;
				graphModel.addPropertyChangeListener(this);
				if (withRescale) {
					rescale();
				}
				firePropertyChange("model", old, graphModel);
			}
		}

		public void adjustmentValueChanged(AdjustmentEvent e) {
			currentStartIndex = e.getValue();
			changeDisplayData();
			repaint();
		}

		public void propertyChange(PropertyChangeEvent evt) {
			if (logger.isDebugEnabled()) {
				Object obj = evt.getSource();
				logger.debug("source:" + evt.getSource());
				if (obj instanceof GraphPropertyModel) {
					GraphPropertyModel gp = (GraphPropertyModel) obj;
					logger.debug("property model : "
							+ (gp == this.graphPropertyModel));
					logger.debug("provider : " + gp.getDataProviderName(0)
							+ "holder : " + gp.getDataHolderName(0));
				} else if (obj instanceof GraphModel) {
					GraphPropertyModel gp = this.graphPropertyModel;
					logger.debug("provider : " + gp.getDataProviderName(0)
							+ "holder : " + gp.getDataHolderName(0));
				}
			}
			if (GraphPropertyModel.GROUP_CHANGE_EVENT.equals(evt
					.getPropertyName())) {
				updateGraphModel();
				referenceValueFlag = false;
			}
			changeDisplayData();
			rescale();
		}

		public void setStringColor(Color color) {
			this.stringColor = color;
		}

		public Color getStringColor() {
			return stringColor == null ? getForeground() : stringColor;
		}

		public void start() {
			if (graphModel != null) {
				graphModel.start();
			}
		}

		public void stop() {
			if (graphModel != null) {
				graphModel.removePropertyChangeListener(this);
				graphModel.stop();
			}
			PropertyChangeListener[] listeners = getPropertyChangeListeners();
			for (int i = 0; i < listeners.length; i++) {
				removePropertyChangeListener(listeners[i]);
			}
		}

		public void setStrokeWidth(float strokeWidth) {
			this.strokeWidth = strokeWidth;
		}
	}

	/**
	 * �O���t�`��G���A�̃}�E�X���X�i�[�A�_�v�^�[�N���X�ł��B �Q�ƒl�Z�o�������������܂��B ���[�h�ύX��PopUp���j���[���������܂��B
	 */
	private static class LineGraphMouseListener extends MouseAdapter implements
			BalkingAction {
		private LineGraph lineGraph;
		private final JPopupMenu menu;
		private Object currentMode;
		private LineGraphScrollBar scrollBar;

		LineGraphMouseListener(LineGraphScrollBar scrollBar) {
			this.scrollBar = scrollBar;
			this.lineGraph = scrollBar.lineGraph;
			this.menu = createMenu();
		}

		private JPopupMenu createMenu() {
			JPopupMenu menu = new JPopupMenu("GraphPopUpMenu");
			JMenuItem setDateMenu = createDateMenu();
			menu.add(setDateMenu);

			return menu;
		}

		private JMenuItem createDateMenu() {
			JMenuItem setDateMenu = new JMenuItem("�\�����t");
			setDateMenu.addActionListener(new SetDateAction(this));
			return setDateMenu;
		}

		public void mouseClicked(MouseEvent e) {
			if (SwingUtilities.isLeftMouseButton(e)) {
				Point point = e.getPoint();
				doReference(point);
			}
		}

		private void doReference(Point point) {
			if (lineGraph.graphViewBounds.contains(point)) {
				// �Q�ƒl�\���t���O�𗧂Ă�
				lineGraph.referenceValueFlag = true;

				// �N���b�N���ꂽ X�� ���W����A�����̎��Ԃ��Z�o
				Date startDate = (Date) lineGraph.currentAxisList.get(0);
				Date endDate = (Date) lineGraph.currentAxisList
						.get(lineGraph.currentAxisList.size() - 1);
				double startTime = startDate.getTime();
				double endTime = endDate.getTime();
				double pit = (endTime - startTime)
						/ (double) lineGraph.graphPropertyModel
								.getHorizontalPixcelWidth();
				double ref = pit
						* (point.x - lineGraph.graphPropertyModel
								.getGraphiViewInsets().left) + startTime;
				long referenceValue = Math.round(ref);
				// �Z�o�������Ԃ��A�V���[�Y�f�[�^����������
				Timestamp referenceTime = new Timestamp(referenceValue);
				lineGraph.graphModel.findRecord(lineGraph.graphPropertyModel
						.getListHandlerName(), referenceTime);
				if (lineGraph.graphModel.next(lineGraph.graphPropertyModel
						.getListHandlerName())) {
					LoggingData loggingData1 = (LoggingData) lineGraph.graphModel
							.get(lineGraph.graphPropertyModel
									.getListHandlerName());
					lineGraph.referenceValueTimestamp = loggingData1
							.getTimestamp();
					setReferenceValues(loggingData1);
					lineGraph.graphPropertyModel
							.setReferenceTime(lineGraph.referenceValueTimestamp);
					lineGraph.repaint();
				} else {
					logger.info("No logging data.");
				}
			}
		}

		private void setReferenceValues(LoggingData loggingData) {
			for (int i = 0, s = lineGraph.graphPropertyModel.getSeriesSize(); loggingData
					.hasNext()
					&& i < s; i++) {
				double value = loggingData.next();
				lineGraph.graphPropertyModel.setReferenceValue(i, value);
			}
		}

		public void mousePressed(MouseEvent e) {
			showPopUp(e);
		}

		public void mouseReleased(MouseEvent e) {
			showPopUp(e);
		}

		private void showPopUp(MouseEvent e) {
			if (e.isPopupTrigger()) {
				menu.show(e.getComponent(), e.getX(), e.getY());
			}
		}

		public boolean isBalk(Object obj) {
			return currentMode == obj;
		}

		public void setBalk(Object obj) {
			currentMode = obj;
		}

		private static class SetDateAction implements ActionListener {
			/** �O���t�I�u�W�F�N�g */
			private final LineGraphMouseListener listener;

			SetDateAction(LineGraphMouseListener listener) {
				this.listener = listener;
			}

			public void actionPerformed(ActionEvent e) {
				SetDateDialog dialog = new SetDateDialog(WifeUtilities
						.getParentFrame(listener.lineGraph), "�����I��");
				WifeUtilities.setCenter(dialog);
				dialog.show();
				Timestamp time = dialog.getFindDate();
				if (time != null) {
					listener.scrollBar.setValue(dialog.getFindDate());
				}
			}
		}
	}

	/**
	 * �O���t���f�����v���p�e�B���o�E���Y����X�N���[���o�[�ł��B
	 */
	private static class LineGraphScrollBar extends JScrollBar implements
			PropertyChangeListener {
		private static final long serialVersionUID = -1262334180888792426L;
		/** �O���t�R���|�[�l���g�̎Q�� */
		private LineGraph lineGraph;
		/** �X�N���[���o�[�\���̗L�� */
		private boolean isBarVisible = true;

		/**
		 * �R���X�g���N�^
		 * 
		 * @param lineGraph �O���t�R���|�[�l���g�̎Q��
		 */
		LineGraphScrollBar(LineGraph lineGraph) {
			super(JScrollBar.HORIZONTAL);
			this.lineGraph = lineGraph;
			init();
			addAdjustmentListener(this.lineGraph);
			this.lineGraph.addPropertyChangeListener(this);
		}

		/**
		 * ��������
		 */
		private void init() {
			lineGraph.graphPropertyModel.addPropertyChangeListener(this);
			lineGraph.graphPropertyModel.addPropertyChangeListener(
					GraphPropertyModel.X_SCALE_CHANGE_EVENT,
					this);
			lineGraph.graphModel.addPropertyChangeListener(this);

			GraphModel graphModel = lineGraph.getModel();

			Timestamp startTimestamp = (Timestamp) graphModel
					.firstKey(lineGraph.graphPropertyModel.getListHandlerName());
			Timestamp endTimestamp = (Timestamp) graphModel
					.lastKey(lineGraph.graphPropertyModel.getListHandlerName());
			long startTime = startTimestamp.getTime();
			long endTime = endTimestamp.getTime();
			long scaleOneTime = lineGraph.graphPropertyModel
					.getHorizontalScaleWidth();
			lineGraph.currentStartIndex = Math.round((endTime - startTime)
					/ scaleOneTime)
					- Math.round(lineGraph.graphPropertyModel
							.getHorizontalScaleCount() - 1);
			setPropertys();
		}

		/**
		 * �X�N���[���o�[�̃v���p�e�B��ݒ肵�܂��B
		 */
		private void setPropertys() {
			GraphModel graphModel = lineGraph.getModel();

			Timestamp startTimestamp = (Timestamp) graphModel
					.firstKey(lineGraph.graphPropertyModel.getListHandlerName());
			Timestamp endTimestamp = (Timestamp) graphModel
					.lastKey(lineGraph.graphPropertyModel.getListHandlerName());
			long startTime = startTimestamp.getTime();
			long endTime = endTimestamp.getTime();
			long scaleOneTime = lineGraph.graphPropertyModel
					.getHorizontalScaleWidth();
			this.setValues(lineGraph.currentStartIndex, Math
					.round(lineGraph.graphPropertyModel
							.getHorizontalScaleCount() - 1), 0, Math
					.round((endTime - startTime) / scaleOneTime));
			lineGraph.changeDisplayData();
			// �X�N���[���͈͂�������΁A�X�N���[���o�[��\�����Ȃ�
			BoundedRangeModel boundedRengeModel = this.getModel();
			if (boundedRengeModel.getMaximum() - boundedRengeModel.getMinimum() <= boundedRengeModel
					.getExtent()) {
				setVisible(false);
			} else {
				setVisible(isBarVisible);
			}
		}

		/**
		 * �O���t�f�[�^���f���̃o�E���Y�v���p�e�B�C�x���g���������܂��B
		 * 
		 * @param evt PropertyChangeEvent
		 */
		public void propertyChange(PropertyChangeEvent evt) {
			// ���[�h�ύX�Ȃ珉�����������ă{�[�L���O
			if (LineGraph.GRAPH_MODEL_SET.equals(evt.getPropertyName())) {
				init();
				return;
			}
			long startTime = getMinTimeMillsecond();
			long endTime = getMaxTimeMillsecond();
			long scaleOneTime = lineGraph.graphPropertyModel
					.getHorizontalScaleWidth();
			int max = Math.round((endTime - startTime) / scaleOneTime);
			double coefficient = (double) max / (double) getMaximum();
			double index = (double) lineGraph.currentStartIndex;
			index *= coefficient;

			int diff = max
					- Math.round(lineGraph.graphPropertyModel
							.getHorizontalScaleCount() - 1);
			if (index >= diff) {
				index -= index - diff;
			}
			lineGraph.currentStartIndex = (int) Math.max(Math.round(index), 0);
			if (logger.isDebugEnabled()) {
				logger.debug("index : " + lineGraph.currentStartIndex);
			}
			setPropertys();
			if (logger.isDebugEnabled()) {
				logger.debug(evt.getPropertyName());
			}
			if (GraphPropertyModel.X_SCALE_CHANGE_EVENT.equals(evt
					.getPropertyName())) {
				// X���ύX�̓X�N���[���o�[�����[�ɂ���
				if (logger.isDebugEnabled()) {
					logger.debug("current : "
							+ lineGraph.currentStartIndex
							+ " max : "
							+ getMaximum()
							+ " extent : "
							+ (lineGraph.graphPropertyModel
									.getHorizontalScaleCount() - 1));
				}
				lineGraph.currentStartIndex = getMaximum()
						- (lineGraph.graphPropertyModel
								.getHorizontalScaleCount() - 1);
				setValue(lineGraph.currentStartIndex);
				lineGraph.changeDisplayData();
			}
		}

		public void setLineGraph(LineGraph lineGraph) {
			removeAdjustmentListener(this.lineGraph);
			this.lineGraph = lineGraph;
			init();
			addAdjustmentListener(this.lineGraph);
		}

		public void setValue(Timestamp time) {
			long findTime = 0;
			findTime = getMinTime(time.getTime());
			findTime = getMaxTime(findTime);

			findTime = findTime - getMinTimeMillsecond();
			long scaleOneTime = lineGraph.graphPropertyModel
					.getHorizontalScaleWidth();
			lineGraph.currentStartIndex = Math.max(
					(int) (findTime / scaleOneTime) - 1,
					0);
			logger.debug("index : " + lineGraph.currentStartIndex);

			setPropertys();
			int id = AdjustmentEvent.ADJUSTMENT_VALUE_CHANGED;
			int type = AdjustmentEvent.TRACK;
			fireAdjustmentValueChanged(id, type, lineGraph.currentStartIndex);
		}

		private long getMinTimeMillsecond() {
			GraphModel graphModel = lineGraph.getModel();
			Timestamp time = (Timestamp) graphModel
					.firstKey(lineGraph.graphPropertyModel.getListHandlerName());
			return time.getTime();
		}

		private long getMaxTimeMillsecond() {
			GraphModel graphModel = lineGraph.getModel();
			Timestamp time = (Timestamp) graphModel
					.lastKey(lineGraph.graphPropertyModel.getListHandlerName());
			return time.getTime();
		}

		private long getMinTime(long time) {
			long startTime = getMinTimeMillsecond();
			return startTime > time ? startTime : time;
		}

		private long getMaxTime(long time) {
			long endTime = getMaxTimeMillsecond();
			return endTime < time ? endTime : time;
		}

		public void setScrollBarVisible(boolean isBarVisible) {
			this.isBarVisible = isBarVisible;
			setPropertys();
		}

	}
}
