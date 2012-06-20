/*
 * $Header: /cvsroot/f-11/F-11/src/org/F11/scada/applet/graph/bargraph/BarGraphView.java,v 1.21.2.13 2007/04/24 00:46:32 frdm Exp $
 * $Revision: 1.21.2.13 $
 * $Date: 2007/04/24 00:46:32 $
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

package org.F11.scada.applet.graph.bargraph;

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
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.rmi.RemoteException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

import javax.swing.BoundedRangeModel;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JScrollBar;

import org.F11.scada.applet.graph.DefaultSelectiveGraphModel;
import org.F11.scada.applet.graph.GraphModel;
import org.F11.scada.applet.graph.GraphPropertyModel;
import org.F11.scada.applet.graph.LoggingData;
import org.F11.scada.applet.symbol.ColorFactory;
import org.F11.scada.server.register.HolderString;
import org.apache.log4j.Logger;
import org.xml.sax.SAXException;

/**
 * �_�O���t�R���|�[�l���g�N���X�ł��B
 * @author Youhei Horikawa <hori@users.sourceforge.jp>
 */
public class BarGraphView extends Box {
	private static final long serialVersionUID = -8630686458331941049L;
	/** ���M���OAPI */
	private static Logger logger;

	public BarGraphView(
		GraphModel graphModel,
		GraphPropertyModel graphPropertyModel,
		String barstep,
		int axismode)
		throws IOException, SAXException {
		super(BoxLayout.Y_AXIS);
		logger = Logger.getLogger(getClass().getName());

		BarGraphStep barGraphStep =
			BarGraphStep.createBarGraphStep(
				barstep,
				graphPropertyModel.getHorizontalScaleWidth());

		FillGraph graph =
			new FillGraph(
				graphModel,
				graphPropertyModel,
				barGraphStep,
				axismode);
		BarGraphScrollBar scrollBar = new BarGraphScrollBar(graph);
		scrollBar.addAdjustmentListener(graph);
		add(graph, BorderLayout.CENTER);
		add(scrollBar, BorderLayout.SOUTH);
	}

	/**
	 * �_�O���t�R���|�[�l���g�N���X
	 */
	private static class FillGraph
		extends JComponent
		implements AdjustmentListener, PropertyChangeListener {
		private static final long serialVersionUID = 5398372942534990318L;
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
		/** �O���t�X�e�b�v */
		private BarGraphStep barGraphStep;
		/** �ڐ��`�惂�[�h */
		private int axismode;

		/**
		 * �R���X�g���N�^
		 * @param graphPropertyModel �O���t�v���p�e�B�E���f���̎Q��
		 */
		public FillGraph(
			GraphModel graphModel,
			GraphPropertyModel graphPropertyModel,
			BarGraphStep barGraphStep,
			int axismode)
			throws IOException, SAXException {
			super();
			this.graphModel = graphModel;
			this.graphPropertyModel = graphPropertyModel;
			this.graphPropertyModel.addPropertyChangeListener(this);
			this.graphPropertyModel.addPropertyChangeListener(GraphPropertyModel.GROUP_CHANGE_EVENT, this);
			this.addMouseListener(new FillGraphMouseListener(this));
			this.barGraphStep = barGraphStep;
			this.axismode = axismode;

			updateGraphModel();

			graphColors = graphPropertyModel.getColors();
			setDoubleBuffered(true);
			changeDisplayData();
			rescale();
		}

		private void updateGraphModel() {
		    ArrayList holderStrings = new ArrayList(graphPropertyModel.getSeriesSize());
			for (int i = 0, s = graphPropertyModel.getSeriesSize();
				i < s;
				i++) {
			    HolderString hs = new HolderString();
			    hs.setProvider(graphPropertyModel.getDataProviderName(i));
			    hs.setHolder(graphPropertyModel.getDataHolderName(i));
			    holderStrings.add(hs);
			}
			try {
                setModel(new DefaultSelectiveGraphModel(
                        graphPropertyModel.getListHandlerName(), holderStrings, null));
            } catch (RemoteException e) {
                e.printStackTrace();
            }
			referenceValueTimestamp = graphPropertyModel.getReferenceTime(0);
		}

		/**
		 * �擪�f�[�^�̎������擾���܂��B
		 */
		private long getStartTime() {
			Timestamp startTimestamp;
			try {
				startTimestamp =
					(Timestamp) graphModel.firstKey(
						graphPropertyModel.getListHandlerName());
			} catch (NoSuchElementException ex) {
				// �f�[�^����
				startTimestamp = new Timestamp(System.currentTimeMillis());
			}
			long startTime = barGraphStep.omitTime(startTimestamp.getTime());
			long startMinTime =
				getEndTime()
					- graphPropertyModel.getHorizontalScaleCount()
						* graphPropertyModel.getHorizontalScaleWidth()
						* (graphPropertyModel.getFoldCount() + 1)
					+ graphPropertyModel.getHorizontalScaleWidth();
			if (startMinTime < startTime)
				return startMinTime;
			return startTime;
		}

		/**
		 * �ŏI�f�[�^�̎������擾���܂��B
		 */
		private long getEndTime() {
			Timestamp endTimestamp;
			try {
				endTimestamp =
					(Timestamp) graphModel.lastKey(
						graphPropertyModel.getListHandlerName());
			} catch (NoSuchElementException ex) {
				// �f�[�^����
				endTimestamp = new Timestamp(System.currentTimeMillis());
			}
			return barGraphStep.omitTime(endTimestamp.getTime());
		}

		/**
		 * �J�����g�̎������擾���܂��B
		 */
		private long getCurrentTime() {
			return getStartTime()
				+ currentStartIndex
					* graphPropertyModel.getHorizontalScaleWidth();
		}

		/**
		 * ��ʕ\������f�[�^��ύX���܂��B
		 */
		private void changeDisplayData() {
			long scaleOneTime = graphPropertyModel.getHorizontalScaleWidth();
			int scaleCount = graphPropertyModel.getHorizontalScaleCount();
			currentAxisList = new ArrayList();
			// X���̃�����������(���t)���Z�o
			long currentTime =
				getCurrentTime()
					+ scaleOneTime
						* scaleCount
						* graphPropertyModel.getFoldCount();
			for (int i = 0; i <= scaleCount; i++) {
				currentAxisList.add(new Date(currentTime + (scaleOneTime * i)));
			}
		}

		/**
		 * �R���|�[�l���g��`�悵�܂��B
		 * @param g �O���t�B�b�N�R���e�L�X�g
		 */
		public void paintComponent(Graphics g) {
			super.paintComponent(g);
			Graphics2D g2d = (Graphics2D) g.create();

			// �X�P�[����`��
			drawAxis(g2d);
			// ���M���O�f�[�^��`��
			drawSeries(g2d);
			// �Q�ƒl�̔j����`��
			drawReference(g2d);

			g2d.dispose();
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
				yScale[i] =
					(double) (scaleOneHeight * scaleCount)
						/ (graphPropertyModel.getVerticalMaximum(i)
							- graphPropertyModel.getVerticalMinimum(i));
				origin[i] =
					new Point(
						scaleInsets.left,
						scaleInsets.top
							+ scaleOneHeight * scaleCount
							+ (int) Math.round(
								graphPropertyModel.getVerticalMinimum(i)
									* yScale[i]));
			}
			repaint();
		}

		/**
		 * �X�P�[���E�O���b�h�E�ڐ��蓙��`�悵�܂��B
		 * @param g �O���t�B�b�N�R���e�L�X�g
		 */
		private void drawAxis(Graphics2D g2d) {
			int scaleOneHeight = graphPropertyModel.getVerticalScaleHeight();
			int scaleCount = graphPropertyModel.getVerticalScaleCount();
			int scaleCountWidth = graphPropertyModel.getHorizontalScaleCount();
			Insets scaleInsets = graphPropertyModel.getGraphiViewInsets();
			// X���̈ʒu���Z�o
			Point baseOrigin =
				new Point(
					scaleInsets.left,
					scaleInsets.top + scaleOneHeight * scaleCount);

			// �w�i���l�C�r�[��
			g2d.setColor(ColorFactory.getColor("navy"));
			graphViewBounds = new Rectangle(this.getSize());
			graphViewBounds.y = scaleInsets.top / 2;
			graphViewBounds.height =
				baseOrigin.y
					+ graphPropertyModel.getScaleOneHeightPixel()
					- scaleInsets.top / 2;
			g2d.fill(graphViewBounds);
			logger.debug("graphViewBounds : " + graphViewBounds);
			// �F�𔒂�
			g2d.setColor(ColorFactory.getColor("white"));
			// X���`��
			long currentTime = getCurrentTime();
			long scaleOneTime = graphPropertyModel.getHorizontalScaleWidth();
			int scaleOneWidth =
				graphPropertyModel.getHorizontalPixcelWidth() / scaleCountWidth;
			g2d.drawLine(
				baseOrigin.x,
				baseOrigin.y,
				baseOrigin.x
					+ (int) getXtime(currentTime
						+ scaleOneTime * scaleCountWidth),
				baseOrigin.y);
			// X���ڐ���`��
			FontMetrics metrics = g2d.getFontMetrics();
			int strHeight = metrics.getHeight();
			long time = 0;
			Iterator it = currentAxisList.iterator();
			for (int sc = 0; sc < scaleCountWidth; sc++) {
				Date timestamp = (Date) it.next();
				// �`��F�𔒂�
				g2d.setColor(ColorFactory.getColor("white"));
				// �ڐ���̐���`��
				int x = baseOrigin.x + (int) getXtime(currentTime + time);
				g2d.drawLine(
					x,
					baseOrigin.y,
					x,
					baseOrigin.y + graphPropertyModel.getScaleOneHeightPixel());
				// �ڐ���̊Ԃɓ��t�Ǝ��Ԃ�`��
				g2d.setColor(ColorFactory.getColor("white"));
				if (axismode == 1) {
					// �P�ڐ��u���ɂQ�s�`��
					if (sc % 2 != scaleCountWidth % 2) { // �ŐV�ɕK���`�悷���
						String timeString =
							barGraphStep.getAxisString1(timestamp.getTime());
						int strWidth = metrics.stringWidth(timeString);
						g2d.drawString(
							timeString,
							x + scaleOneWidth / 2 - strWidth / 2,
							baseOrigin.y
								+ graphPropertyModel.getScaleOneHeightPixel()
								+ strHeight);
						timeString =
							barGraphStep.getAxisString2(timestamp.getTime());
						strWidth = metrics.stringWidth(timeString);
						g2d.drawString(
							timeString,
							x + scaleOneWidth / 2 - strWidth / 2,
							baseOrigin.y
								+ graphPropertyModel.getScaleOneHeightPixel()
								+ strHeight * 2);
					}
				} else {
					String timeString =
						barGraphStep.getAxisString(timestamp.getTime());
					int strWidth = metrics.stringWidth(timeString);
					g2d.drawString(
						timeString,
						x + scaleOneWidth / 2 - strWidth / 2,
						baseOrigin.y
							+ graphPropertyModel.getScaleOneHeightPixel()
							+ strHeight);
				}
				time += scaleOneTime;
			}

			// �X�g���[�N��j���ɕύX
			float[] dash = { 3.1f };
			BasicStroke bs =
				new BasicStroke(
					1.0f,
					BasicStroke.CAP_BUTT,
					BasicStroke.JOIN_MITER,
					10.0f,
					dash,
					0.0f);
			g2d.setStroke(bs);
			g2d.setColor(ColorFactory.getColor("cornflowerblue"));
			int x =
				(int) getXtime(currentTime + scaleOneTime * scaleCountWidth);
			for (int i = baseOrigin.y - scaleOneHeight;
				i >= scaleInsets.top;
				i -= scaleOneHeight) {
				g2d.drawLine(baseOrigin.x, i, baseOrigin.x + x, i);
			}
		}

		/**
		 * �������O���t��X�����W�ɕϊ����܂��B
		 * @param timep ����
		 * @return X�����W
		 */
		private double getXtime(long time) {
			long offsTime = time - getCurrentTime();
			long fullScale =
				graphPropertyModel.getHorizontalScaleWidth()
					* graphPropertyModel.getHorizontalScaleCount();
			return offsTime
				* graphPropertyModel.getHorizontalPixcelWidth()
				/ fullScale;
		}

		private LoggingData findLoggingData(
			long findTime,
			String handlerName) {
			LoggingData loggingData;
			graphModel.findRecord(handlerName, new Timestamp(findTime));
			if (!graphModel.next(handlerName))
				return null;
			loggingData = (LoggingData) graphModel.get(handlerName);
			if (findTime
				!= barGraphStep.omitTime(loggingData.getTimestamp().getTime())) {
				if (!graphModel.next(handlerName))
					return null;
				loggingData = (LoggingData) graphModel.get(handlerName);
				if (findTime
					!= barGraphStep.omitTime(
						loggingData.getTimestamp().getTime()))
					return null;
			}
			return loggingData;
		}

		private void drawSeries(Graphics2D g2d) {
			// �J�n�����ɍł��߂����R�[�h�����o����i�w�莞�Ԃ��P���O�̃��R�[�h���܂ށj
			long scaleOneTime = graphPropertyModel.getHorizontalScaleWidth();
			long scaleCount = graphPropertyModel.getHorizontalScaleCount();
			long scaleFoldTime = scaleOneTime * scaleCount;
			int barWidth =
				graphPropertyModel.getHorizontalPixcelWidth()
					/ barGraphStep.getBarCount(scaleFoldTime)
					/ (graphPropertyModel.getFoldCount() + 2);
			long currentTime = getCurrentTime();

			SimpleDateFormat form = new SimpleDateFormat("yyyy/MM/dd HH:mm");
			logger.debug(
				"draw BAR curtime : " + form.format(new Date(currentTime)));
			// BAR�̕`��
			for (int fold = 0;
				fold <= graphPropertyModel.getFoldCount();
				fold++) {
				for (int sc = 0;
					sc < graphPropertyModel.getHorizontalScaleCount();
					sc++) {
					long keyTime =
						barGraphStep.indexToTime(
							currentTime + scaleFoldTime * fold,
							sc);
					LoggingData loggingData =
						findLoggingData(
							keyTime,
							graphPropertyModel.getListHandlerName());
					if (loggingData != null) {
						double xTime1 =
							getXtime(currentTime + scaleOneTime * sc);

						loggingData.first();
						double item1 = loggingData.next();
						Point p = dataToPoint(xTime1, item1, 0);
						Point p2 = dataToPoint(xTime1, 0.0, 0);
						Rectangle rect = new Rectangle(p);
						g2d.setColor(graphColors[fold]);
						rect.x += fold * barWidth + barWidth / 2;
						rect.setSize(barWidth - 1, p2.y - p.y);
						invalidateLineY(rect);
						g2d.fill(rect);
					}
				}
			}
		}

		private void invalidateLineY(Rectangle rec) {
			int graphHeight = graphViewBounds.y + graphViewBounds.height - graphPropertyModel.getScaleOneHeightPixel();
			int total = rec.y + rec.height;
			if (total > graphHeight) {
				rec.height -= total - graphHeight;
			}
		}

		/**
		 * �f�[�^���O���t���W�ɕϊ����܂��B
		 * @param x X�����W
		 * @param y �V���[�Y�f�[�^
		 * @param series �V���[�Y
		 * @return �`�悷��|�C���g
		 */
		private Point dataToPoint(double x, double y, int series) {
			return new Point(
				(int) Math.round(origin[series].x + x),
				(int) Math.round(origin[series].y - yScale[series] * y));
		}

		public Dimension getPreferredSize() {
			int scaleOneHeight = graphPropertyModel.getVerticalScaleHeight();
			int scaleCount = graphPropertyModel.getVerticalScaleCount();
			int scaleOneWidth =
				graphPropertyModel.getHorizontalPixcelWidth()
					/ graphPropertyModel.getHorizontalScaleCount();
			Insets scaleInsets = graphPropertyModel.getGraphiViewInsets();

			return new Dimension(
				scaleInsets.left
					+ scaleInsets.right
					+ scaleOneWidth
						* graphPropertyModel.getHorizontalScaleCount(),
				scaleInsets.top
					+ scaleInsets.bottom
					+ scaleOneHeight * scaleCount);
		}

		/**
		 * �Q�ƒl�̔j���`�揈��
		 * @param g �O���t�B�b�N�R���e�L�X�g
		 */
		private void drawReference(Graphics2D g2d) {
			if (!referenceValueFlag) {
				return;
			}

			// �O���t�\����X�������߂�
			double xTime1 = getXtime(referenceValueTimestamp.getTime());
			int referenceValue =
				graphPropertyModel.getGraphiViewInsets().left
					+ (int) Math.round(xTime1);
			logger.debug(
				"timestamp : "
					+ referenceValueTimestamp
					+ " xTime1 : "
					+ xTime1
					+ " referenceValue : "
					+ referenceValue);
			if (isNotReferenceDraw(referenceValue)) {
				return;
			}

			// �X�g���[�N��j���ɕύX
			float[] dash = { 16.0f, 4.0f };
			BasicStroke bs =
				new BasicStroke(
					1.0f,
					BasicStroke.CAP_BUTT,
					BasicStroke.JOIN_MITER,
					10.0f,
					dash,
					0.0f);
			g2d.setStroke(bs);
			g2d.setColor(ColorFactory.getColor("white"));
			long scaleOneTime = graphPropertyModel.getHorizontalScaleWidth();
			long scaleCount = graphPropertyModel.getHorizontalScaleCount();
			int barWidth =
				graphPropertyModel.getHorizontalPixcelWidth()
					/ barGraphStep.getBarCount(scaleOneTime * scaleCount);
			g2d.drawRect(
				referenceValue,
				graphViewBounds.y,
				barWidth - 1,
				graphViewBounds.height);
			/*
			g2d.drawLine(
				referenceValue,
				graphViewBounds.y,
				referenceValue,
				graphViewBounds.y + graphViewBounds.height);
			*/
		}

		private boolean isNotReferenceDraw(int referenceValue) {
			Insets insets = graphPropertyModel.getGraphiViewInsets();
			logger.debug(
				"referenceValue : "
					+ referenceValue
					+ " left : "
					+ insets.left
					+ " right : "
					+ (insets.left
						+ graphPropertyModel.getHorizontalPixcelWidth()));
			if (referenceValue < insets.left
				|| referenceValue
					> (insets.left
						+ graphPropertyModel.getHorizontalPixcelWidth())) {
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
				throw new IllegalArgumentException("Cannot set a null GraphModel.");
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
				firePropertyChange("model", old, this.graphModel);
			}
		}

		public void adjustmentValueChanged(AdjustmentEvent e) {
			currentStartIndex = e.getValue();
			if (logger.isDebugEnabled()) {
				logger.debug("value : " + e.getValue());
			}
			changeDisplayData();
			repaint();
		}

		public void propertyChange(PropertyChangeEvent evt) {
			//			logger.debug("");
		    if (GraphPropertyModel.GROUP_CHANGE_EVENT.equals(
		            evt.getPropertyName())) {
				updateGraphModel();
		    }
			changeDisplayData();
			rescale();
		}

		/**
		 * �O���t�`��G���A�̃}�E�X���X�i�[�A�_�v�^�[�N���X�ł��B
		 * �Q�ƒl�Z�o�������������܂��B
		 */
		private static class FillGraphMouseListener extends MouseAdapter {
			private FillGraph fillGraph;

			FillGraphMouseListener(FillGraph fillGraph) {
				this.fillGraph = fillGraph;
			}

			public void mouseReleased(MouseEvent e) {
				Point point = e.getPoint();
				if (fillGraph.graphViewBounds.contains(point)) {
					// �Q�ƒl�\���t���O�𗧂Ă�
					fillGraph.referenceValueFlag = true;

					// �N���b�N���ꂽ X�� ���W����A�����̎��Ԃ��Z�o
					long scaleOneTime =
						fillGraph.graphPropertyModel.getHorizontalScaleWidth();
					long scaleCount =
						fillGraph.graphPropertyModel.getHorizontalScaleCount();
					long scaleFoldTime = scaleOneTime * scaleCount;
					int scaleOneWidth =
						fillGraph.graphPropertyModel.getHorizontalPixcelWidth()
							/ fillGraph.barGraphStep.getBarCount(scaleFoldTime);
					int clickIndex =
						(point.x
							- fillGraph
								.graphPropertyModel
								.getGraphiViewInsets()
								.left)
							/ scaleOneWidth;
					if (fillGraph.barGraphStep.getBarCount(scaleFoldTime)
						<= clickIndex) {
						return;
					}
					long referenceValue =
						fillGraph.barGraphStep.indexToTime(
							fillGraph.getCurrentTime(),
							clickIndex);
					logger.debug(
						this.getClass().getName()
							+ " : "
							+ new Timestamp(referenceValue)
							+ "\n currentTime : "
							+ new Timestamp(fillGraph.getCurrentTime())
							+ " clickIndex : "
							+ clickIndex);

					fillGraph.referenceValueTimestamp =
						new Timestamp(referenceValue);

					// �Z�o�������Ԃ��A�܂�Ԃ��f�[�^����������B
					for (int fold = 0;
						fold <= fillGraph.graphPropertyModel.getFoldCount();
						fold++) {
						// �Z�o�������Ԃ��A�V���[�Y�f�[�^����������
						long keyTime = referenceValue + scaleFoldTime * fold;
						LoggingData loggingData =
							fillGraph.findLoggingData(
								keyTime,
								fillGraph
									.graphPropertyModel
									.getListHandlerName());
						setReferenceValues(
							fold,
							loggingData,
							new Timestamp(keyTime));
						fillGraph.repaint();
					}
				}
			}

			private void setReferenceValues(
				int fold,
				LoggingData loggingData,
				Timestamp timestamp) {

				if (loggingData != null)
					loggingData.first();
				for (int series = 0;
					series < fillGraph.graphPropertyModel.getSeriesSize();
					series++) {

					double value = 0.0;
					if (loggingData != null)
						value = loggingData.next();

					fillGraph.graphPropertyModel.setReferenceValue(
						series,
						fold,
						value);
					fillGraph.graphPropertyModel.setReferenceTime(
						series,
						fold,
						timestamp);
				}
			}
		}
	}

	/**
	 * �O���t���f�����v���p�e�B���o�E���Y����X�N���[���o�[�ł��B
	 */
	private static class BarGraphScrollBar
		extends JScrollBar
		implements PropertyChangeListener {
		private static final long serialVersionUID = 170492832047340277L;
		/** �O���t�R���|�[�l���g�̎Q�� */
		private FillGraph fillGraph;

		/**
		 * �R���X�g���N�^
		 * @param fillGraph �O���t�R���|�[�l���g�̎Q��
		 */
		BarGraphScrollBar(FillGraph fillGraph) {
			super(JScrollBar.HORIZONTAL);
			this.fillGraph = fillGraph;

			fillGraph.graphPropertyModel.addPropertyChangeListener(this);
			init();
		}

		/**
		 * ��������
		 */
		private void init() {
			long startTime = fillGraph.getStartTime();
			long endTime = fillGraph.getEndTime();
			long scaleOneTime =
				fillGraph.graphPropertyModel.getHorizontalScaleWidth();
			int extentSize =
				fillGraph.graphPropertyModel.getHorizontalScaleCount()
					* (fillGraph.graphPropertyModel.getFoldCount() + 1)
					- 1;

			int maxIndexCount =
				Math.round((endTime - startTime) / scaleOneTime);
			// �X�^�[�g�ʒu�w��
			fillGraph.currentStartIndex = maxIndexCount - extentSize;

			setPropertys(extentSize, 0, maxIndexCount);
		}

		/**
		 * �X�N���[���o�[�̃v���p�e�B��ݒ肵�܂��B
		 */
		private void setPropertys(int extent, int min, int max) {
			// �X�N���[���͈͂ƌ��݈ʒu��ݒ�
			this.setValues(fillGraph.currentStartIndex, extent, min, max);
			// �����̖ڐ���\��
			fillGraph.changeDisplayData();
			// �X�N���[���͈͂�������΁A�X�N���[���o�[��\�����Ȃ�
			BoundedRangeModel boundedRengeModel = this.getModel();
			if (boundedRengeModel.getMaximum() - boundedRengeModel.getMinimum()
				<= boundedRengeModel.getExtent()) {
				setVisible(false);
			} else {
				setVisible(true);
			}
		}

		/**
		 * �O���t�f�[�^���f���̃o�E���Y�v���p�e�B�C�x���g���������܂��B
		 * @param evt PropertyChangeEvent
		 */
		public void propertyChange(PropertyChangeEvent evt) {
			long startTime = fillGraph.getStartTime();
			long endTime = fillGraph.getEndTime();
			long scaleOneTime =
				fillGraph.graphPropertyModel.getHorizontalScaleWidth();
			int extentSize =
				fillGraph.graphPropertyModel.getHorizontalScaleCount()
					* (fillGraph.graphPropertyModel.getFoldCount() + 1)
					- 1;

			int maxIndexCount =
				Math.round((endTime - startTime) / scaleOneTime);
			double coefficient = (double) maxIndexCount / (double) getMaximum();
			double index = (double) fillGraph.currentStartIndex * coefficient;

			int diff = maxIndexCount - extentSize;
			if (diff < index) {
				index = diff;
			}
			fillGraph.currentStartIndex = (int) Math.max(Math.round(index), 0);

			setPropertys(extentSize, 0, maxIndexCount);
		}
	}

}
