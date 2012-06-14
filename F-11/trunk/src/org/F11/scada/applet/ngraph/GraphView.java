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
 * 折れ線グラフを表示するコンポーネント
 *
 * @author maekawa
 *
 */
public class GraphView extends JPanel implements AdjustmentListener, Mediator,
		Colleague {
	private static final long serialVersionUID = -7135402419995170758L;
	/** 参照データ表示の為にグラフ画面クリック時に発生するイベント名称 */
	public static final String GRAPH_CLICKED_CHANGE = GraphView.class.getName()
		+ "graph.clicked.change";
	/** 基本描画ストローク */
	private static final BasicStroke BASIC_STROKE = new BasicStroke();
	/** 縦目盛点線ストローク */
	private static final BasicStroke GRID_LINE_STROKE = getGridLineStroke();
	/** 参照値縦線ストローク */
	private static final BasicStroke CLICK_POINT_STROKE = getClickPointStroke();
	/** 日時降順コンパレータ */
	private static final Comparator<LogData> DATE_COMPARATOR = getComparator();
	/** ロギングAPI */
	private final Logger logger = Logger.getLogger(GraphView.class.getName());
	/** グラフモデル */
	private GraphModel model;
	/** グラフモデルの変更リスナー */
	private GraphModelListener modelListener;
	/** スクロールバーの現在値 */
	private int scrollBarIndex;
	/** グラフプロパティー */
	private GraphProperties graphProperties;
	/** 表示するデータの配列 */
	private LogData[] displayDatas;
	/** 選択されているシリーズのインデックス */
	private int selectSeriesIndex;
	/** シリーズの最大値 */
	private int seriesMaxCount;
	/** 現在の表示データモード */
	private boolean isAllDataDisplayMode;
	/** グラフ描画オブジェクトのマップ */
	private final Map<Boolean, GraphDraw> graphDrawMap;
	/** グラフ画面クリック時の参照データ */
	private LogData graphClickedData;
	/** メインパネル */
	private final Mediator mediator;
	/** グラフのマウスリスナー */
	private GraphMouseListener mouseListener;

	/**
	 * グラフコンポーネントを初期化します
	 *
	 * @param model グラフモデル
	 * @param properties グラフプロパティー
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
	 * スパン略表示と全表示の描画オブジェクトを生成しマップで返します。
	 *
	 * @return スパン略表示と全表示の描画オブジェクトを生成しマップで返します。
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
	 * 縦軸のグリッドライン(点線)を描画
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
	 * 横軸の日時文字列を描画
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
	 * 横軸線と目盛線を描画
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
	 * 縦軸線を描画
	 *
	 * @param g
	 */
	private void paintVerticalLine(Graphics g) {
		drawLeft(g);
		drawRight(g);
	}

	/**
	 * 左縦軸を描画
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
	 * 左縦軸の文字列(スケール)を描画
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
	 * 右縦軸のを描画
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
	 * スパン全表示用の右縦軸を描画
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
				// 全スパン表示時の間隔。
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
	 * グラフをクリック(参照値表示)した場合の、参照位置の縦線の描画
	 *
	 * @param g
	 */
	private void paintGraphClickedPoint(Graphics g) {
		int x = getClickedPoint() + graphProperties.getInsets().left;
		if (isGraphViewContent(x)) {
			Graphics2D g2d = (Graphics2D) g;
			// 点線を描画して
			g2d.setColor(graphProperties.getLineColor());
			g2d.setStroke(CLICK_POINT_STROKE);
			Insets insets = graphProperties.getInsets();
			g2d.drawLine(
				x,
				insets.top,
				x,
				insets.top + graphProperties.getVerticalLine());
			g2d.setStroke(BASIC_STROKE);
			// 逆三角を描画する
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
	 * グラフモデルを返します
	 *
	 * @return グラフモデルを返します
	 */
	public GraphModel getModel() {
		return model;
	}

	/**
	 * グラフモデルを設定します。
	 *
	 * @param newModel グラフモデル
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
			throw new IllegalArgumentException("引数の GraphModel が null です。");
		}
	}

	/**
	 * 表示データの配列を返します。
	 *
	 * @return 表示データの配列を返します。
	 */
	public LogData[] getDisplayDatas() {
		return displayDatas;
	}

	/**
	 * グラフプロパティオブジェクトを返します。
	 *
	 * @return グラフプロパティオブジェクトを返します。
	 */
	public GraphProperties getGraphProperties() {
		return graphProperties;
	}

	/**
	 * スクロールバーの変更イベント発生時に呼ばれます。
	 *
	 * @param e 変更イベントオブジェクト
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
	 * 表示データ範囲を変更します
	 *
	 */
	public void changeDataArea() {
		isAllDataDisplayMode = isAllDataDisplayMode ? false : true;
		mediator.colleaguChanged(this);
	}

	/**
	 * 表示データ範囲モードを返します。
	 *
	 * @return 全データ表示モードであれば true をそうでなければ false を返します。
	 */
	public boolean isAllDataDisplayMode() {
		return isAllDataDisplayMode;
	}

	/**
	 * スパン表示モードを変更します
	 *
	 */
	public void changeSpanDisplayMode() {
		graphProperties.setAllSpanDisplayMode(graphProperties
			.isAllSpanDisplayMode() ? false : true);
		repaint();
	}

	/**
	 * 分離・合成表示を変更します
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
	 * 選択されているシリーズのインデックスを設定します
	 *
	 * @param selectSeriesIndex シリーズのインデックス
	 */
	public void setSelectSeries(int selectSeriesIndex) {
		this.selectSeriesIndex = selectSeriesIndex;
		repaint();
	}

	/**
	 * シリーズの最大値を設定します
	 *
	 * @param maxCount シリーズの最大値
	 */
	public void setSeriesMaxCount(int maxCount) {
		seriesMaxCount = maxCount;
		setVerticalCount();
	}

	/**
	 * クリックされた位置の参照値データを返します。
	 *
	 * @return クリックされた位置の参照値データを返します
	 */
	public LogData getGraphClickedData() {
		return graphClickedData;
	}

	/**
	 * クリックされた位置の参照値データを設定します。
	 *
	 * @param logData クリックされた位置の参照値データ
	 */
	public void setGraphClickedData(LogData logData) {
		graphClickedData = logData;
	}

	/**
	 * グラフをクリックした時のx座標を設定します。
	 *
	 * @param p グラフをクリックした時の座標
	 */
	public void setGraphClickedPoint(Point p) {
		LogData old = graphClickedData;
		long startDate =
			displayDatas[scrollBarIndex].getDate().getTime()
				- graphProperties.getHorizontalLineSpan();
		// ミリ秒あたりのピクセル数
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
	 * 横スケールの表示プロパティを設定します
	 *
	 * @param horizontalCount 目盛の数
	 * @param horizontalForAllSpanMode スパン全表示時の目盛ひとつの長さ(ピクセル)
	 * @param horizontalForSelectSpanMode スパン略表示時の目盛ひとつの長さ(ピクセル)
	 * @param horizontalLineSpan 横スケール全体が示す時間(ミリ秒)
	 * @param recordeSpan レコードの間隔(ミリ秒)
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
	 * スパン表示モードにあわせて、グラフの横軸の長さを返します(ピクセル)
	 *
	 * @return グラフの横軸の長さを返します(ピクセル)
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
	 * グラフモデルの変更イベントリスナー
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
	 * グラフのクリックを検知するマウスリスナー
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
	 * 日時を降順でミリ秒、秒を無視するコンパレータを返します
	 *
	 * @return 日時を降順でミリ秒、秒を無視するコンパレータを返します
	 */
	private static Comparator<LogData> getComparator() {
		return new Comparator<LogData>() {
			Calendar cal = Calendar.getInstance();

			public int compare(LogData o1, LogData o2) {
				// ミリ秒、秒は無視する
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
	 * 横グリッドの点線ストロークを返します
	 *
	 * @return 横グリッドの点線ストロークを返します
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
