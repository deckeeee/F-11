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
 * デマンド監視のグラフコンポーネントクラスです。
 * 
 * @todo 既知のバグ、スケールの上限を現在値より小さくすると、予想電力の表示が乱れる。
 */
public class DemandGraph extends JPanel {

	private static final long serialVersionUID = 914253999773614129L;

	private DemandGraphView view;

	/**
	 * コンストラクタ デマンド監視のグラフコンポーネントを生成します。
	 * 
	 * @param gmodel グラフモデル
	 * @param pmodel グラフプロパティモデル
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
	 * デマンドグラフ描画コンポーネントクラスです。
	 */
	private static class DemandGraphView extends JComponent implements
			PropertyChangeListener, ReferencerOwnerSymbol {
		private static final long serialVersionUID = -8641438220997585758L;
		/** グラフデータモデル */
		private GraphModel graphModel;
		/** グラフプロパティモデル */
		private GraphPropertyModel graphPropertyModel;
		/** データ値とグラフ描画の比率 */
		private double[] yScale;
		/** グラフのオリジン */
		private Point[] origin;
		/** グラフ表示する色 */
		private Color[] graphColors;

		/** 現在表示しているデータのスタートインデックス */
		private int currentStartIndex;
		/** 現在表示しているX軸目盛りのリスト */
		private List currentAxisList;
		/** グラフ描画領域 */
		private Rectangle graphViewBounds;
		/** ロギングAPI */
		private static Logger logger;
		/** 予想電力のY軸係数 (60 count / 30sec) = 2, (60 count / 60 sec) = 1 */
		private double expectYCount;

		private final boolean alarmTimeMode;

		private final boolean colorSetting;

		/**
		 * コンストラクタ
		 * 
		 * @param graphPropertyModel グラフプロパティ・モデルの参照
		 * @param graphModel グラフモデルの参照
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
		 * 画面表示するデータを変更します。
		 */
		private void changeDisplayData() {
			synchronized (this) {
				currentAxisList = new ArrayList();
				// X軸のメモリ文字列(日付)を算出
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
		 * コンポーネントを描画します。
		 * 
		 * @param g グラフィックコンテキスト
		 */
		public void paintComponent(Graphics g) {
			super.paintComponent(g);
			Graphics2D g2d = (Graphics2D) g.create();

			// スケールを描画
			drawAxis(g2d);
			// 過去データを描画
			drawSeries(g2d);
			// 設定値データを描画
			drawPreference(g2d);

			g2d.dispose();
		}

		/**
		 * 最大値・最小値より描画変換率を算出します。
		 */
		private void rescale() {
			int scaleOneHeight = graphPropertyModel.getVerticalScaleHeight();
			int scaleCount = graphPropertyModel.getVerticalScaleCount();
			Insets scaleInsets = graphPropertyModel.getGraphiViewInsets();
			// Y軸とデータの比率を算出
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
		 * スケール・グリッド・目盛り等を描画します。
		 * 
		 * @param g グラフィックコンテキスト
		 */
		private void drawAxis(Graphics2D g2d) {
			int scaleOneHeight = graphPropertyModel.getVerticalScaleHeight();
			int scaleCount = graphPropertyModel.getVerticalScaleCount();
			int scaleCountWidth = graphPropertyModel.getHorizontalScaleCount();
			Insets scaleInsets = graphPropertyModel.getGraphiViewInsets();
			// X軸の位置を算出
			Point baseOrigin =
				new Point(scaleInsets.left, scaleInsets.top
					+ scaleOneHeight
					* scaleCount);
			// 背景をネイビーに
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
			// 色を白に
			g2d.setColor(ColorFactory.getColor("white"));
			// X軸描画
			int scaleOneWidth =
				graphPropertyModel.getHorizontalPixcelWidth()
					/ graphPropertyModel.getHorizontalScaleCount();
			g2d.drawLine(baseOrigin.x, baseOrigin.y, baseOrigin.x
				+ scaleOneWidth
				* scaleCountWidth, baseOrigin.y);
			// X軸目盛り描画
			FontMetrics metrics = g2d.getFontMetrics();
			int strHeight = metrics.getHeight();
			synchronized (this) {
				Iterator it = currentAxisList.iterator();
				SimpleDateFormat timeFormat = new SimpleDateFormat("m");
				for (int i = baseOrigin.x, j = 0, strWidth = 0; i <= baseOrigin.x
					+ scaleOneWidth
					* scaleCountWidth; i += scaleOneWidth, j++) {

					g2d.setStroke(new BasicStroke());
					// 描画色を白に
					g2d.setColor(ColorFactory.getColor("white"));
					// 目盛りの線を描画
					g2d.drawLine(i, baseOrigin.y, i, baseOrigin.y
						+ graphPropertyModel.getScaleOneHeightPixel());
					// 目盛りの下に日付と時間を描画
					Date timestamp = (Date) it.next();
					g2d.setColor(ColorFactory.getColor("white"));
					String timeString = timeFormat.format(timestamp);
					strWidth = metrics.stringWidth(timeString);
					g2d.drawString(timeString, i - strWidth / 2, baseOrigin.y
						+ graphPropertyModel.getScaleOneHeightPixel()
						+ strHeight);

					// 縦グリッドの破線を描画
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

			// ストロークを破線に変更
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
		 * タイムスタンプをグラフのX軸座標に変換します。
		 * 
		 * @param timestamp タイムスタンプ
		 * @return X軸座標
		 */
		private double getXtime(Timestamp timestamp) {
			long scaleOneTime = graphPropertyModel.getHorizontalScaleWidth(); // 左端のグラフ座標を算出
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
			// 開始時刻に最も近いレコードを検出する（指定時間より１件前のレコードを含む）
			graphModel.findRecord("", new Timestamp(0));

			// シリーズデータを抽出(X軸計算で表示するデータ数を決める)
			LoggingData loggingData1 = (LoggingData) graphModel.get("");
			Timestamp timestamp = loggingData1.getTimestamp();
			// 左端のグラフ座標を算出
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
						// シリーズデータを描画します。
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
		 * データをグラフ座標に変換します。
		 * 
		 * @param x X軸座標
		 * @param y シリーズデータ
		 * @param series シリーズ
		 * @return 描画するポイント
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

			// グラフの縦の長さ
			double vHeight =
				graphPropertyModel.getVerticalScaleHeight()
					* graphPropertyModel.getVerticalScaleCount()
					+ (graphPropertyModel.getInsets().top / 2);

			// 契約電力値
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
				// 目標電力値
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

				// 警報設定値
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

				// 予想電力値
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

		// グラフの上を越すときの処理
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
