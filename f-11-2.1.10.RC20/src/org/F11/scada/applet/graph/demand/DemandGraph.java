/*
 * $Header: /cvsroot/f-11/F-11/src/org/F11/scada/applet/graph/demand/DemandGraph.java,v 1.22.2.9 2006/06/02 02:18:04 frdm Exp $
 * $Revision: 1.22.2.9 $
 * $Date: 2006/06/02 02:18:04 $
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

package org.F11.scada.applet.graph.demand;

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
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import javax.swing.JComponent;
import javax.swing.JPanel;

import org.F11.scada.applet.graph.GraphModel;
import org.F11.scada.applet.graph.GraphPropertyModel;
import org.F11.scada.applet.graph.LoggingData;
import org.F11.scada.applet.graph.VerticallyScale;
import org.F11.scada.applet.symbol.ColorFactory;
import org.F11.scada.applet.symbol.ReferencerOwnerSymbol;
import org.apache.log4j.Logger;

/**
 * �f�}���h�Ď��̃O���t�R���|�[�l���g�N���X�ł��B
 * 
 * @todo ���m�̃o�O�A�X�P�[���̏�������ݒl��菬��������ƁA�\�z�d�͂̕\���������B
 */
public class DemandGraph extends JPanel {

	private static final long serialVersionUID = 914253999773614129L;

	private DemandGraphView view;

	/**
	 * �R���X�g���N�^ �f�}���h�Ď��̃O���t�R���|�[�l���g�𐶐����܂��B
	 * 
	 * @param gmodel �O���t���f��
	 * @param pmodel �O���t�v���p�e�B���f��
	 */
	public DemandGraph(
			GraphModel gmodel,
			GraphPropertyModel pmodel,
			boolean alarmTimeMode,
			Color stringColor,
			boolean colorSetting) {
		super(new BorderLayout());
		view = new DemandGraphView(pmodel, gmodel, alarmTimeMode, colorSetting);
		add(view, BorderLayout.CENTER);
		VerticallyScale vs =
			VerticallyScale.createRightStringScale(pmodel, 0, stringColor);
		vs.setMinEnabled(false);
		add(vs, BorderLayout.EAST);
	}

	public void setExpectYCount(double expectYCount) {
		view.setExpectYCount(expectYCount);
	}

	/**
	 * �f�}���h�O���t�`��R���|�[�l���g�N���X�ł��B
	 */
	private static class DemandGraphView extends JComponent implements
			PropertyChangeListener, ReferencerOwnerSymbol {
		private static final long serialVersionUID = -8641438220997585758L;
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
		/** ���M���OAPI */
		private static Logger logger;
		/** �\�z�d�͂�Y���W�� (60 count / 30sec) = 2, (60 count / 60 sec) = 1 */
		private double expectYCount;

		private final boolean alarmTimeMode;

		private final boolean colorSetting;

		/**
		 * �R���X�g���N�^
		 * 
		 * @param graphPropertyModel �O���t�v���p�e�B�E���f���̎Q��
		 * @param graphModel �O���t���f���̎Q��
		 */
		DemandGraphView(
				GraphPropertyModel graphPropertyModel,
				GraphModel graphModel,
				boolean alarmTimeMode,
				boolean colorSetting) {
			super();
			logger = Logger.getLogger(getClass().getName());
			this.graphModel = graphModel;
			this.graphModel.addPropertyChangeListener(this);
			this.graphPropertyModel = graphPropertyModel;
			this.graphPropertyModel.addPropertyChangeListener(this);
			this.alarmTimeMode = alarmTimeMode;
			this.colorSetting = colorSetting;
			graphColors = graphPropertyModel.getColors();
			setDoubleBuffered(true);
			changeDisplayData();
			rescale();
		}

		public void setExpectYCount(double expectYCount) {
			this.expectYCount = expectYCount;
		}

		/**
		 * ��ʕ\������f�[�^��ύX���܂��B
		 */
		private void changeDisplayData() {
			synchronized (this) {
				currentAxisList = new ArrayList();
				// X���̃�����������(���t)���Z�o
				Calendar cal = Calendar.getInstance();
				cal.set(Calendar.MINUTE, 0);
				cal.set(Calendar.SECOND, 0);
				cal.set(Calendar.MILLISECOND, 0);
				Timestamp timestamp = new Timestamp(cal.getTimeInMillis());
				for (int i = 0; i <= graphPropertyModel
					.getHorizontalScaleCount(); i++) {
					currentAxisList.add(timestamp);
					cal.add(Calendar.MINUTE, 5);
					timestamp = new Timestamp(cal.getTimeInMillis());
				}
			}
		}

		/**
		 * �R���|�[�l���g��`�悵�܂��B
		 * 
		 * @param g �O���t�B�b�N�R���e�L�X�g
		 */
		public void paintComponent(Graphics g) {
			super.paintComponent(g);
			Graphics2D g2d = (Graphics2D) g.create();

			// �X�P�[����`��
			drawAxis(g2d);
			// �ߋ��f�[�^��`��
			drawSeries(g2d);
			// �ݒ�l�f�[�^��`��
			drawPreference(g2d);

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
			synchronized (this) {
				yScale = new double[graphPropertyModel.getSeriesSize()];
				origin = new Point[graphPropertyModel.getSeriesSize()];
				for (int i = 0; i < graphPropertyModel.getSeriesSize(); i++) {
					yScale[i] =
						(double) (scaleOneHeight * scaleCount)
							/ (graphPropertyModel.getVerticalMaximum(i) - graphPropertyModel
								.getVerticalMinimum(i));
					origin[i] =
						new Point(scaleInsets.left, scaleInsets.top
							+ scaleOneHeight
							* scaleCount
							+ (int) Math.round(graphPropertyModel
								.getVerticalMinimum(i)
								* yScale[i]));
				}
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
			Point baseOrigin =
				new Point(scaleInsets.left, scaleInsets.top
					+ scaleOneHeight
					* scaleCount);
			// �w�i���l�C�r�[��
			g2d.setColor(ColorFactory.getColor("navy"));
			graphViewBounds = new Rectangle(this.getSize());
			graphViewBounds.y = scaleInsets.top / 2;
			graphViewBounds.height =
				baseOrigin.y
					+ graphPropertyModel.getScaleOneHeightPixel()
					- scaleInsets.top
					/ 2;
			g2d.fill(graphViewBounds);
			logger.debug("graphViewBounds : " + graphViewBounds);
			// �F�𔒂�
			g2d.setColor(ColorFactory.getColor("white"));
			// X���`��
			int scaleOneWidth =
				graphPropertyModel.getHorizontalPixcelWidth()
					/ graphPropertyModel.getHorizontalScaleCount();
			g2d.drawLine(baseOrigin.x, baseOrigin.y, baseOrigin.x
				+ scaleOneWidth
				* scaleCountWidth, baseOrigin.y);
			// X���ڐ���`��
			FontMetrics metrics = g2d.getFontMetrics();
			int strHeight = metrics.getHeight();
			synchronized (this) {
				Iterator it = currentAxisList.iterator();
				SimpleDateFormat timeFormat = new SimpleDateFormat("m");
				for (int i = baseOrigin.x, j = 0, strWidth = 0; i <= baseOrigin.x
					+ scaleOneWidth
					* scaleCountWidth; i += scaleOneWidth, j++) {

					g2d.setStroke(new BasicStroke());
					// �`��F�𔒂�
					g2d.setColor(ColorFactory.getColor("white"));
					// �ڐ���̐���`��
					g2d.drawLine(i, baseOrigin.y, i, baseOrigin.y
						+ graphPropertyModel.getScaleOneHeightPixel());
					// �ڐ���̉��ɓ��t�Ǝ��Ԃ�`��
					Date timestamp = (Date) it.next();
					g2d.setColor(ColorFactory.getColor("white"));
					String timeString = timeFormat.format(timestamp);
					strWidth = metrics.stringWidth(timeString);
					g2d.drawString(timeString, i - strWidth / 2, baseOrigin.y
						+ graphPropertyModel.getScaleOneHeightPixel()
						+ strHeight);

					// �c�O���b�h�̔j����`��
					float[] dash = { 4.0f };
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
					g2d.drawLine(
						i,
						baseOrigin.y
							- graphPropertyModel.getVerticalScaleHeight()
							* graphPropertyModel.getVerticalScaleCount(),
						i,
						baseOrigin.y);
				}
			}

			// �X�g���[�N��j���ɕύX
			float[] dash = { 4.0f };
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
			for (int i = baseOrigin.y - scaleOneHeight; i >= scaleInsets.top; i -=
				scaleOneHeight) {
				g2d.drawLine(baseOrigin.x, i, baseOrigin.x
					+ scaleOneWidth
					* scaleCountWidth, i);
			}
		}

		/**
		 * �^�C���X�^���v���O���t��X�����W�ɕϊ����܂��B
		 * 
		 * @param timestamp �^�C���X�^���v
		 * @return X�����W
		 */
		private double getXtime(Timestamp timestamp) {
			long scaleOneTime = graphPropertyModel.getHorizontalScaleWidth(); // ���[�̃O���t���W���Z�o
			long currentTime =
				currentStartIndex
					* scaleOneTime
					+ ((Timestamp) graphModel.firstKey("")).getTime();
			double screenTime =
				currentStartIndex
					+ scaleOneTime
					* graphPropertyModel.getHorizontalScaleCount();
			return (double) graphPropertyModel.getHorizontalPixcelWidth()
				/ (double) screenTime
				* (timestamp.getTime() - currentTime);
		}

		private void drawSeries(Graphics2D g2d) {
			// �J�n�����ɍł��߂����R�[�h�����o����i�w�莞�Ԃ��P���O�̃��R�[�h���܂ށj
			graphModel.findRecord("", new Timestamp(0));

			// �V���[�Y�f�[�^�𒊏o(X���v�Z�ŕ\������f�[�^�������߂�)
			LoggingData loggingData1 = (LoggingData) graphModel.get("");
			Timestamp timestamp = loggingData1.getTimestamp();
			// ���[�̃O���t���W���Z�o
			double xTime1 = getXtime(timestamp);
			logger.debug("xTime1 : " + xTime1);
			if (isDisplayRight(xTime1)) {
				return;
			}

			synchronized (this) {
				for (Point p1 = null, p2 = null; graphModel.next("");) {
					LoggingData loggingData2 = (LoggingData) graphModel.get("");
					if (loggingData2 == null) {
						continue;
					}
					timestamp = loggingData2.getTimestamp();
					double xTime2 = getXtime(timestamp);
					logger.debug("xTime2 : " + xTime2);
					loggingData1.first();
					loggingData2.first();
					for (int series = 0; loggingData1.hasNext()
						&& loggingData2.hasNext(); series++) {
						// �V���[�Y�f�[�^��`�悵�܂��B
						double item1 = loggingData1.next();
						double item2 = loggingData2.next();
						p1 = dataToPoint(xTime1, item1, series);
						p2 = dataToPoint(xTime2, item2, series);
						g2d.setStroke(new BasicStroke());
						g2d.setColor(colorSetting
							? graphColors[5]
							: ColorFactory.getColor("red"));
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
		}

		private boolean isDisplayRight(double xTime) {
			int scaleCountWidth = graphPropertyModel.getHorizontalScaleCount();
			long scaleOneTime = graphPropertyModel.getHorizontalScaleWidth();

			Timestamp timestamp = (Timestamp) graphModel.firstKey("");
			double currentTime =
				currentStartIndex + scaleOneTime * scaleCountWidth;
			double screenTime =
				currentStartIndex
					* scaleOneTime
					+ timestamp.getTime()
					+ (scaleCountWidth * scaleOneTime);
			double endTime =
				(double) this.graphPropertyModel.getHorizontalPixcelWidth()
					/ screenTime
					* (timestamp.getTime() - currentTime);
			logger.debug("endTime : " + endTime);
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
			synchronized (this) {
				return new Point(
					(int) Math.round(origin[series].x + x),
					(int) Math.round(origin[series].y - yScale[series] * y));
			}
		}

		private void invalidateLineX(Point p1, Point p2, int limitX) {
			if (Math.abs(p2.x - p1.x) < 1.0) {
				p1.x = limitX;
				return;
			}

			double a = 0, y = 0;
			a =
				((double) p2.y - (double) p1.y)
					/ ((double) p2.x - (double) p1.x);
			y = a * ((double) limitX - (double) p1.x) + (double) p1.y;
			p1.x = limitX;
			p1.y = (int) Math.round(y);
		}

		private void invalidateLineY(Point p1, Point p2, Rectangle limitY) {
			int graphHeight =
				limitY.y
					+ limitY.height
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
			int scaleOneWidth =
				graphPropertyModel.getHorizontalPixcelWidth()
					/ graphPropertyModel.getHorizontalScaleCount();
			Insets scaleInsets = graphPropertyModel.getGraphiViewInsets();

			return new Dimension(scaleInsets.left
				+ scaleInsets.right
				+ scaleOneWidth
				* graphPropertyModel.getHorizontalScaleCount(), scaleInsets.top
				+ scaleInsets.bottom
				+ scaleOneHeight
				* scaleCount);
		}

		private void drawPreference(Graphics2D g2d) {

			DemandGraphModel demandGraphModel = (DemandGraphModel) graphModel;

			// �O���t�̏c�̒���
			double vHeight =
				graphPropertyModel.getVerticalScaleHeight()
					* graphPropertyModel.getVerticalScaleCount()
					+ (graphPropertyModel.getInsets().top / 2);

			// �_��d�͒l
			g2d.setStroke(new BasicStroke());
			g2d.setColor(colorSetting ? graphColors[2] : ColorFactory
				.getColor("lime"));
			synchronized (this) {
				long ce =
					origin[0].y
						- Math.round(demandGraphModel.getContractElectricity()
							* yScale[0]);
				Point p2 =
					dataToPoint(
						origin[0].x,
						origin[0].y,
						origin[0].x
							+ graphPropertyModel.getHorizontalPixcelWidth(),
						ce,
						origin[0].y - vHeight);
				g2d.drawLine(origin[0].x, origin[0].y, p2.x, p2.y);
				// �ڕW�d�͒l
				g2d.setColor(colorSetting ? graphColors[3] : ColorFactory
					.getColor("cyan"));
				long te =
					origin[0].y
						- Math.round(demandGraphModel.getTargetElectricity()
							* yScale[0]);
				p2 =
					dataToPoint(
						origin[0].x,
						origin[0].y,
						origin[0].x
							+ graphPropertyModel.getHorizontalPixcelWidth(),
						te,
						origin[0].y - vHeight);
				g2d.drawLine(origin[0].x, origin[0].y, p2.x, p2.y);

				// �x��ݒ�l
				double countPerPixel =
					(double) graphPropertyModel.getHorizontalPixcelWidth() / 30D;

				double[] alarmTimes = demandGraphModel.getAlarmTimes();
				for (int i = 0; i < alarmTimes.length; i++) {
					g2d.setColor(graphColors[i]);
					long x =
						Math.round(origin[0].x + alarmTimes[i] * countPerPixel);
					long y = 0;
					if (alarmTimeMode) {
						y =
							Math.round(origin[0].y
								- graphPropertyModel.getVerticalScaleHeight()
								* graphPropertyModel.getVerticalScaleCount());
					} else {
						y = Math.round(origin[0].y - vHeight);
					}
					g2d.drawLine((int) x, origin[0].y, (int) x, (int) y);
				}

				// �\�z�d�͒l
				g2d.setColor(colorSetting ? graphColors[4] : ColorFactory
					.getColor("yellow"));
				double x =
					origin[0].x
						+ demandGraphModel.getCounter()
						/ expectYCount
						* countPerPixel;
				double y =
					origin[0].y
						- demandGraphModel.getCurrentElectricity()
						* yScale[0];
				double fe =
					origin[0].y
						- demandGraphModel.getForecastElectricity()
						* yScale[0];
				double x2 =
					origin[0].x + graphPropertyModel.getHorizontalPixcelWidth();
				p2 = dataToPoint(x, y, x2, fe, origin[0].y - vHeight);
				g2d.drawLine(
					(int) Math.round(x),
					(int) Math.round(y),
					p2.x,
					p2.y);
			}
		}

		// �O���t�̏���z���Ƃ��̏���
		private Point dataToPoint(
				double x,
				double y,
				double x2,
				double y2,
				double limit) {
			logger.debug("(1) x:" + x + " y:" + y + " x2:" + x2 + " y2:" + y2);
			if (y2 < limit) {
				double a = (x2 - x) / (y2 - y);
				logger.debug("a : " + a);
				double tmpy2 = limit;
				double tmpx2 = Math.round(a * (tmpy2 - y) + x);
				logger.debug("(2) x:"
					+ x
					+ " y:"
					+ y
					+ " x2:"
					+ tmpx2
					+ " y2:"
					+ tmpy2);
				return new Point((int) Math.round(tmpx2), (int) Math
					.round(tmpy2));
			}
			logger.debug("(2) x:" + x + " y:" + y + " x2:" + x2 + " y2:" + y2);
			return new Point((int) Math.round(x2) - 1, (int) Math.round(y2));
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

		public void propertyChange(PropertyChangeEvent evt) {
			changeDisplayData();
			rescale();
		}

		public void disConnect() {
			if (graphModel instanceof DemandGraphModel) {
				DemandGraphModel model = (DemandGraphModel) graphModel;
				model.disConnect();
			}
		}
	}

}
