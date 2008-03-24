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
 * トレンドグラフコンポーネントクラスです。 折線グラフを担当する LineGraph クラスと、スクロールバーを担当する
 * LineGraphScrollBar で構成されています。
 * 
 * @author Hideaki Maekawa <frdm@users.sourceforge.jp>
 */
public class TrendGraphView extends Box implements PropertyChangeListener,
		Service {
	private static final long serialVersionUID = 2599137567371934139L;
	/** ロギングAPI */
	private static Logger logger;
	private final LineGraph graph;
	private LineGraphScrollBar scrollBar;

	/**
	 * コンストラクタ
	 * 
	 * @param graphPropertyModel グラフプロパティ・モデルの参照
	 */
	public TrendGraphView(GraphPropertyModel graphPropertyModel)
			throws IOException, SAXException {
		this(graphPropertyModel, new DefaultGraphModelFactory(
				"org.F11.scada.applet.graph.DefaultSelectiveGraphModel",
				graphPropertyModel,
				"自動更新"), false, true, true);
	}

	/**
	 * コンストラクタ
	 * 
	 * @param graphPropertyModel グラフプロパティ・モデルの参照
	 * @param isViewVerticalScale 縦グリッド点線の有無
	 * @param isMouseClickEnable グラフオブジェクトのマウスイベント有無
	 * @param isDrawString X軸日時文字列の表示有無
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
						"自動更新",
						maxMapSize),
				isViewVerticalScale,
				isMouseClickEnable,
				isDrawString);
	}

	/**
	 * コンストラクタ
	 * 
	 * @param graphPropertyModel グラフプロパティ・モデルの参照
	 * @param factory グラフモデル・ファクトリー
	 * @param isViewVerticalScale 縦グリッド点線の有無
	 * @param isMouseClickEnable グラフオブジェクトのマウスイベント有無
	 * @param isDrawString X軸日時文字列の表示有無
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
	 * 折線グラフコンポーネントクラス
	 */
	static class LineGraph extends JComponent implements AdjustmentListener,
			PropertyChangeListener, Service {

		private static final long serialVersionUID = 6559762538695995468L;
		/** グラフモデルセットのプロパティチェンジイベント名 */
		public static final String GRAPH_MODEL_SET = "org.F11.scada.applet.graph.LineGraph.GraphModelSet";
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
		/** 参照値 X軸タイムスタンプ */
		private Timestamp referenceValueTimestamp;
		/** 参照値表示フラグ */
		private boolean referenceValueFlag;
		/** グラフモデルのファクトリークラス */
		private GraphModelFactory factory;
		/** 文字列の色 */
		private Color stringColor;
		/** 縦スケール点線の有無 */
		private final boolean isViewVerticalScale;
		/** 横スケール目盛り文字の有無 */
		private final boolean isDrawString;
		/** ストロークの幅 */
		private float strokeWidth = 1.0F;

		/**
		 * コンストラクタ
		 * 
		 * @param graphPropertyModel グラフプロパティ・モデルの参照
		 * @param factory グラフモデルのファクトリークラス
		 */
		public LineGraph(
				GraphPropertyModel graphPropertyModel,
				GraphModelFactory factory) throws IOException, SAXException {
			this(graphPropertyModel, factory, false, true);
		}

		/**
		 * コンストラクタ
		 * 
		 * @param graphPropertyModel グラフプロパティ・モデルの参照
		 * @param factory グラフモデル・ファクトリー
		 * @param isViewVerticalScale 縦グリッド点線の有無
		 * @param isMouseClickEnable グラフオブジェクトのマウスイベント有無
		 * @param isDrawString X軸日時文字列の表示有無
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
		 * 画面表示するデータを変更します。
		 */
		private void changeDisplayData() {
			long scaleOneTime = graphPropertyModel.getHorizontalScaleWidth();
			currentAxisList = Collections.synchronizedList(new ArrayList(
					graphPropertyModel.getHorizontalScaleCount()));
			// X軸のメモリ文字列(日付)を算出
			Timestamp timestamp = (Timestamp) graphModel
					.firstKey(graphPropertyModel.getListHandlerName());
			long currentTime = currentStartIndex * scaleOneTime
					+ timestamp.getTime();
			for (int i = 0; i <= graphPropertyModel.getHorizontalScaleCount(); i++) {
				currentAxisList.add(new Date(currentTime + (scaleOneTime * i)));
			}
		}

		/**
		 * コンポーネントを描画します。
		 * 
		 * @param g グラフィックコンテキスト
		 */
		public void paintComponent(Graphics g) {
			super.paintComponent(g);
			Graphics2D g2d = (Graphics2D) g;
			// スケールを描画
			drawAxis(g2d);
			// ロギングデータを描画
			drawSeries(g2d);
			// 参照値の破線を描画
			drawReference(g2d);
		}

		/**
		 * 最大値・最小値より描画変換率を算出します。
		 */
		private void rescale() {
			int scaleOneHeight = graphPropertyModel.getVerticalScaleHeight();
			int scaleCount = graphPropertyModel.getVerticalScaleCount();
			Insets scaleInsets = graphPropertyModel.getGraphiViewInsets();
			// Y軸とデータの比率を算出
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
			Point baseOrigin = new Point(scaleInsets.left, scaleInsets.top
					+ scaleOneHeight * scaleCount);
			// 背景色
			g2d.setColor(getBackground());
			graphViewBounds = new Rectangle(this.getSize());
			graphViewBounds.y = scaleInsets.top / 2;
			graphViewBounds.height = baseOrigin.y
					+ graphPropertyModel.getScaleOneHeightPixel()
					- scaleInsets.top / 2;
			g2d.fill(graphViewBounds);
			// 色を白に
			g2d.setColor(getForeground());
			// X軸描画
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
			// X軸目盛り描画
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
						// 縦グリッドの破線を描画
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

					// 描画色を白に
					if (isDrawString) {
						g2d.setColor(getForeground());
						// 目盛りの線を描画
						g2d.setStroke(new BasicStroke());
						g2d.drawLine(i, baseOrigin.y, i, baseOrigin.y
								+ graphPropertyModel.getScaleOneHeightPixel());
						// 目盛りの下に日付と時間を描画
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

			// ストロークを破線に変更
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
		 * タイムスタンプをグラフのX軸座標に変換します。
		 * 
		 * @param timestamp タイムスタンプ
		 * @return X軸座標
		 */
		private double getXtime(Timestamp timestamp) {
			if (null == timestamp) {
				return 0;
			}
			long scaleOneTime = graphPropertyModel.getHorizontalScaleWidth();
			// 左端のグラフ座標を算出
			long currentTime = currentStartIndex
					* scaleOneTime
					+ ((Timestamp) graphModel.firstKey(graphPropertyModel
							.getListHandlerName())).getTime();
			// 横スケールの長さ(㍉秒)を算出
			double screenTime = +scaleOneTime
					* graphPropertyModel.getHorizontalScaleCount();
			// 引数の時間より横スケールの時間(㍉秒)を算出
			return (double) graphPropertyModel.getHorizontalPixcelWidth()
					/ screenTime * (timestamp.getTime() - currentTime);
		}

		private void drawSeries(Graphics2D g2d) {
			// 開始時刻に最も近いレコードを検出する（指定時間より１件前のレコードを含む）
			long scaleOneTime = graphPropertyModel.getHorizontalScaleWidth();
			long currentTime = currentStartIndex
					* scaleOneTime
					+ ((Timestamp) graphModel.firstKey(graphPropertyModel
							.getListHandlerName())).getTime();
			graphModel.findRecord(
					graphPropertyModel.getListHandlerName(),
					new Timestamp(currentTime));

			// シリーズデータを抽出(X軸計算で表示するデータ数を決める)
			if (!graphModel.next(graphPropertyModel.getListHandlerName())) {
				return;
			}
			LoggingData loggingData1 = (LoggingData) graphModel
					.get(graphPropertyModel.getListHandlerName());
			if (logger.isDebugEnabled()) {
				logger.debug("1:" + loggingData1);
			}
			Timestamp timestamp = loggingData1.getTimestamp();
			// 左端のグラフ座標を算出
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
					// シリーズデータを描画します。
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
		 * データをグラフ座標に変換します。
		 * 
		 * @param x X軸座標
		 * @param y シリーズデータ
		 * @param series シリーズ
		 * @return 描画するポイント
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
		 * 参照値の破線描画処理
		 * 
		 * @param g グラフィックコンテキスト
		 */
		private void drawReference(Graphics2D g2d) {
			if (!referenceValueFlag) {
				return;
			}

			// グラフ表示のX軸を求める
			double xTime1 = getXtime(referenceValueTimestamp);
			int referenceValue = graphPropertyModel.getGraphiViewInsets().left
					+ (int) Math.round(xTime1);
			if (isNotReferenceDraw(referenceValue)) {
				return;
			}

			// ストロークを破線に変更
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
	 * グラフ描画エリアのマウスリスナーアダプタークラスです。 参照値算出処理を実装します。 モード変更のPopUpメニューを実装します。
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
			JMenuItem setDateMenu = new JMenuItem("表示日付");
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
				// 参照値表示フラグを立てる
				lineGraph.referenceValueFlag = true;

				// クリックされた X軸 座標から、横軸の時間を算出
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
				// 算出した時間より、シリーズデータを検索する
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
			/** グラフオブジェクト */
			private final LineGraphMouseListener listener;

			SetDateAction(LineGraphMouseListener listener) {
				this.listener = listener;
			}

			public void actionPerformed(ActionEvent e) {
				SetDateDialog dialog = new SetDateDialog(WifeUtilities
						.getParentFrame(listener.lineGraph), "日時選択");
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
	 * グラフモデルよりプロパティをバウンズするスクロールバーです。
	 */
	private static class LineGraphScrollBar extends JScrollBar implements
			PropertyChangeListener {
		private static final long serialVersionUID = -1262334180888792426L;
		/** グラフコンポーネントの参照 */
		private LineGraph lineGraph;
		/** スクロールバー表示の有無 */
		private boolean isBarVisible = true;

		/**
		 * コンストラクタ
		 * 
		 * @param lineGraph グラフコンポーネントの参照
		 */
		LineGraphScrollBar(LineGraph lineGraph) {
			super(JScrollBar.HORIZONTAL);
			this.lineGraph = lineGraph;
			init();
			addAdjustmentListener(this.lineGraph);
			this.lineGraph.addPropertyChangeListener(this);
		}

		/**
		 * 初期処理
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
		 * スクロールバーのプロパティを設定します。
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
			// スクロール範囲が無ければ、スクロールバーを表示しない
			BoundedRangeModel boundedRengeModel = this.getModel();
			if (boundedRengeModel.getMaximum() - boundedRengeModel.getMinimum() <= boundedRengeModel
					.getExtent()) {
				setVisible(false);
			} else {
				setVisible(isBarVisible);
			}
		}

		/**
		 * グラフデータモデルのバウンズプロパティイベントを処理します。
		 * 
		 * @param evt PropertyChangeEvent
		 */
		public void propertyChange(PropertyChangeEvent evt) {
			// モード変更なら初期化処理してボーキング
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
				// X軸変更はスクロールバーを左端にする
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
