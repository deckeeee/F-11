/*
 * $Header: /cvsroot/f-11/F-11/src/org/F11/scada/applet/graph/VerticallyScale.java,v 1.8.2.15 2007/07/31 01:57:08 frdm Exp $
 * $Revision: 1.8.2.15 $
 * $Date: 2007/07/31 01:57:08 $
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

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingUtilities;
import javax.swing.text.JTextComponent;

import org.F11.scada.WifeUtilities;
import org.F11.scada.applet.dialog.SelectedFieldNumberEditor;

/**
 * 縦スケールコンポーネントクラス 内部クラス VerticallyScale と JButton の組み合わせによる、縦スケールを 生成するクラスです。
 * 
 * @author Hideaki Maekawa <frdm@users.sourceforge.jp>
 */
public class VerticallyScale extends JPanel {
	private static final long serialVersionUID = 783065196442167082L;
	/** スケールビューの参照 */
	private VerticallyScaleView vsv;
	/** スケール変更ボタン */
	private JButton button;

	private VerticallyScale(
			GraphPropertyModel graphPropertyModel,
			int series,
			ScaleOrigin scaleOrigin,
			boolean isDigital,
			boolean isTrend,
			Color stringColor) {

		super(new BorderLayout());
		vsv = new VerticallyScaleView(
				graphPropertyModel,
				series,
				scaleOrigin,
				isDigital,
				isTrend,
				stringColor);
		add(vsv, BorderLayout.CENTER);
		button = new JButton("変更");
		button.addActionListener(new ScaleChangeButtonListener(vsv));
		add(button, BorderLayout.SOUTH);
		setBackground(graphPropertyModel.getVerticallyScaleProperty()
				.getBackGroundColor());
		setOpaque(true);
	}

	/**
	 * 文字列を左に表示する、VerticallyScale インスタンスを生成します。
	 * 
	 * @param graphPropertyModel グラフプロパティモデル
	 * @param series シリーズ
	 * @return VerticallyScale インスタンス
	 */
	public static VerticallyScale createLeftStringScale(
			GraphPropertyModel graphPropertyModel,
			int series) {
		return createLeftStringScale(
				graphPropertyModel,
				series,
				false,
				true,
				null);
	}

	/**
	 * 文字列を左に表示する、VerticallyScale インスタンスを生成します。
	 * 
	 * @param graphPropertyModel グラフプロパティモデル
	 * @param series シリーズ
	 * @param isDigital デジタル(ON OFFのみ表示)の有無
	 * @return VerticallyScale インスタンス
	 */
	public static VerticallyScale createLeftStringScale(
			GraphPropertyModel graphPropertyModel,
			int series,
			boolean isDigital,
			boolean isTrend,
			Color stringColor) {
		return new VerticallyScale(
				graphPropertyModel,
				series,
				ScaleOrigin.LEFT,
				isDigital,
				isTrend,
				stringColor);
	}

	/**
	 * 文字列を右に表示する、VerticallyScale インスタンスを生成します。
	 * 
	 * @param graphPropertyModel グラフプロパティモデル
	 * @param series シリーズ
	 * @return VerticallyScale インスタンス
	 */
	public static VerticallyScale createRightStringScale(
			GraphPropertyModel graphPropertyModel,
			int series) {
		return createRightStringScale(
				graphPropertyModel,
				series,
				false,
				true,
				null);
	}

	/**
	 * 文字列を右に表示する、VerticallyScale インスタンスを生成します。 デマンドグラフ用
	 * 
	 * @param graphPropertyModel グラフプロパティモデル
	 * @param series シリーズ
	 * @param スケール文字色
	 * @return VerticallyScale インスタンス
	 */
	public static VerticallyScale createRightStringScale(
			GraphPropertyModel graphPropertyModel,
			int series,
			Color stringColor) {
		return createRightStringScale(
				graphPropertyModel,
				series,
				false,
				true,
				stringColor);
	}

	/**
	 * 文字列を右に表示する、VerticallyScale インスタンスを生成します。
	 * 
	 * @param graphPropertyModel グラフプロパティモデル
	 * @param series シリーズ
	 * @param isDigital デジタル(ON OFFのみ表示)の有無
	 * @return VerticallyScale インスタンス
	 */
	public static VerticallyScale createRightStringScale(
			GraphPropertyModel graphPropertyModel,
			int series,
			boolean isDigital,
			boolean isTrend,
			Color stringColor) {
		return new VerticallyScale(
				graphPropertyModel,
				series,
				ScaleOrigin.RIGHT,
				isDigital,
				isTrend,
				stringColor);
	}

	/**
	 * スケール最大スパン変更の有無を設定します。
	 * 
	 * @param isMaxEnabled 変更可能にする場合は true、変更不可にする場合は false
	 */
	public void setMaxEnabled(boolean isMaxEnabled) {
		vsv.setMaxEnabled(isMaxEnabled);
	}

	/**
	 * スケール最小スパン変更の有無を設定します。
	 * 
	 * @param isMinEnabled 変更可能にする場合は true、変更不可にする場合は false
	 */
	public void setMinEnabled(boolean isMinEnabled) {
		vsv.setMinEnabled(isMinEnabled);
	}

	public void setScaleButtonVisible(boolean b) {
		button.setVisible(b);
	}

	public void setWidth(int width) {
		vsv.setWidth(width);
	}

	/**
	 * 左右を表すタイプセーフ enum 内部クラスです。 スケール描画処理をポリフォーフィズムで分岐します。
	 */
	private abstract static class ScaleOrigin {
		/**
		 * 文字列をスケールの左に描画を表す定数です。
		 */
		static ScaleOrigin LEFT = new ScaleOrigin() {
			void drawLine(Graphics2D g, VerticallyScaleView view) {
				setScaleColor(g, view.getVerticallyScaleProperty()
						.getForegroundColor1());
				Insets insets = view.getModel().getScaleInsets();
				Point origin = new Point(
						insets.left
								+ view.getModel().getScaleStringMaxWidth(true),
						insets.top - 5);
				g.drawLine(origin.x, origin.y, origin.x, origin.y
						+ view.getModel().getScaleCount()
						* view.getModel().getScaleOneHeight());

				origin.x++;
				origin.y++;
				g.setColor(view.getVerticallyScaleProperty()
						.getForegroundColor2());
				g.drawLine(origin.x, origin.y, origin.x, origin.y
						+ view.getModel().getScaleCount()
						* view.getModel().getScaleOneHeight());

				origin.x--;
				origin.y--;
				final FontMetrics metrics = g.getFontMetrics();
				final int strHeight = metrics.getAscent();
				String[] scaleStrings = view.getModel().getScaleStrings();
				for (int i = origin.y + view.getModel().getScaleCount()
						* view.getModel().getScaleOneHeight(), k = 0; i >= origin.y; i -= view
						.getModel().getScaleOneHeight(), k++) {
					setScaleColor(g, view.getVerticallyScaleProperty()
							.getForegroundColor1());
					g.drawLine(
							origin.x - view.getModel().getScaleOneWidth(),
							i,
							origin.x,
							i);
					if (view.isTrend) {
						if (null == view.stringColor) {
							setScaleColor(g, view.getModel()
									.getScaleStringColor());
						} else {
							setScaleColor(g, view.stringColor);
						}
					} else {
						setScaleColor(g, view.getForeground());
					}
					drawString(
							g,
							view,
							origin,
							metrics,
							strHeight,
							scaleStrings,
							i,
							k);
				}

				origin.x--;
				origin.y++;
				for (int i = origin.y + view.getModel().getScaleCount()
						* view.getModel().getScaleOneHeight(), k = 0; i >= origin.y; i -= view
						.getModel().getScaleOneHeight(), k++) {
					setScaleColor(g, view.getVerticallyScaleProperty()
							.getForegroundColor2());
					g.drawLine(origin.x - view.getModel().getScaleOneWidth()
							+ 1, i, origin.x, i);
				}
			}

			private void drawString(
					Graphics2D g,
					VerticallyScaleView view,
					Point origin,
					final FontMetrics metrics,
					final int strHeight,
					String[] scaleStrings,
					int i,
					int k) {
				if (view.isDigital) {
					g.drawString(scaleStrings[k], origin.x
							- (metrics.stringWidth(scaleStrings[k]) + view
									.getModel().getScaleOneWidth()), getHeight(
							strHeight,
							i,
							k));
				} else {
					g.drawString(scaleStrings[k], origin.x
							- (metrics.stringWidth(scaleStrings[k]) + view
									.getModel().getScaleOneWidth()), i
							+ (strHeight / 2 - 1));
				}
			}
		};

		/**
		 * 文字列をスケールの右に描画を表す定数です。
		 */
		static ScaleOrigin RIGHT = new ScaleOrigin() {
			void drawLine(Graphics2D g, VerticallyScaleView view) {

				setScaleColor(g, view.getVerticallyScaleProperty()
						.getForegroundColor2());
				Insets insets = view.getModel().getScaleInsets();
				Point origin = new Point(insets.left, insets.top - 5);
				g.drawLine(origin.x + 1, origin.y, origin.x + 1, origin.y
						+ view.getModel().getScaleCount()
						* view.getModel().getScaleOneHeight());

				origin.y++;
				for (int i = origin.y + view.getModel().getScaleCount()
						* view.getModel().getScaleOneHeight(), k = 0; i >= origin.y; i -= view
						.getModel().getScaleOneHeight(), k++) {
					setScaleColor(g, view.getVerticallyScaleProperty()
							.getForegroundColor2());
					g.drawLine(origin.x, i, origin.x
							+ view.getModel().getScaleOneWidth(), i);
					setScaleColor(g, view.getModel().getScaleStringColor());
				}

				setScaleColor(g, view.getVerticallyScaleProperty()
						.getForegroundColor1());
				insets = view.getModel().getScaleInsets();
				origin = new Point(insets.left, insets.top - 5);
				g.drawLine(origin.x, origin.y, origin.x, origin.y
						+ view.getModel().getScaleCount()
						* view.getModel().getScaleOneHeight());

				final FontMetrics metrics = g.getFontMetrics();
				final int strHeight = metrics.getAscent();
				String[] scaleStrings = view.getModel().getScaleStrings();
				for (int i = origin.y + view.getModel().getScaleCount()
						* view.getModel().getScaleOneHeight(), k = 0; i >= origin.y; i -= view
						.getModel().getScaleOneHeight(), k++) {
					setScaleColor(g, view.getVerticallyScaleProperty()
							.getForegroundColor1());
					g.drawLine(origin.x, i, origin.x
							+ view.getModel().getScaleOneWidth(), i);
					if (view.isTrend) {
						if (null == view.stringColor) {
							setScaleColor(g, view.getModel()
									.getScaleStringColor());
						} else {
							setScaleColor(g, view.stringColor);
						}
					} else {
						setScaleColor(g, view.getForeground());
					}
					drawString(
							g,
							view,
							origin,
							metrics,
							strHeight,
							scaleStrings,
							i,
							k);
				}
			}

			private void drawString(
					Graphics2D g,
					VerticallyScaleView view,
					Point origin,
					final FontMetrics metrics,
					final int strHeight,
					String[] scaleStrings,
					int i,
					int k) {
				if (view.isDigital) {
					g.drawString(scaleStrings[k], origin.x
							+ view.getModel().getScaleOneWidth()
							+ view.getModel().getScaleStringMaxWidth(false)
							- metrics.stringWidth(scaleStrings[k]), getHeight(
							strHeight,
							i,
							k));
				} else {
					g.drawString(scaleStrings[k], origin.x
							+ view.getModel().getScaleOneWidth()
							+ view.getModel().getScaleStringMaxWidth(false)
							- metrics.stringWidth(scaleStrings[k]), i
							+ (strHeight / 2 - 1));
				}
			}
		};

		/** スケール色 */
		// private static Color scaleColor;
		/**
		 * プライベートコンストラクタ
		 */
		private ScaleOrigin() {
			// scaleColor = ColorFactory.getColor("black");
		}

		/**
		 * グラフィックコンテキストに、スケールを描画する仮想メソッドです。 スタティックフィールドに、インスタンスを生成する時に、処理を実装します。
		 * 
		 * @param g Java2D グラフィックコンテキスト
		 * @param model スケールデータモデル
		 */
		abstract void drawLine(Graphics2D g, VerticallyScaleView view);

		private static void setScaleColor(Graphics2D g, Color color) {
			g.setColor(color);
		}

		int getHeight(final int strHeight, int i, int k) {
			if (k == 0) {
				return i;
			} else {
				return i + strHeight;
			}
		}
	}

	/**
	 * スケール表示クラスです。
	 */
	private static class VerticallyScaleView extends JComponent implements
			PropertyChangeListener {

		private static final long serialVersionUID = -1695398595428248599L;
		/** スケールデータモデル */
		private VerticallyScaleModel model;
		/** スケール文字列の位置クラス */
		private ScaleOrigin scaleOrigin;

		private final GraphPropertyModel graphPropertyModel;

		private boolean isSetedWidth;

		private int width;

		private boolean isDigital;

		private boolean isTrend;

		private final Color stringColor;

		public VerticallyScaleView(
				GraphPropertyModel graphPropertyModel,
				int series,
				ScaleOrigin scaleOrigin,
				boolean isDigital,
				boolean isTrend,
				Color stringColor) {

			super();
			this.scaleOrigin = scaleOrigin;
			this.graphPropertyModel = graphPropertyModel;
			this.model = getModel(series, isDigital);
			this.isDigital = isDigital;
			this.isTrend = isTrend;
			this.stringColor = stringColor;
			init();
		}

		private VerticallyScaleModel getModel(int series, boolean isDigital) {
			if (isDigital) {
				return new DigitalVerticallyScaleModel(
						this,
						graphPropertyModel,
						series);
			} else {
				return new DefaultVerticallyScaleModel(
						this,
						graphPropertyModel,
						series);
			}
		}

		private void init() {
			setDoubleBuffered(true);
			model.addPropertyChangeListener(this);
		}

		void setWidth(int width) {
			isSetedWidth = true;
			this.width = width;
		}

		public Dimension getPreferredSize() {
			Insets insets = model.getScaleInsets();

			if (isSetedWidth) {
				return new Dimension(width, insets.top + model.getScaleCount()
						* model.getScaleOneHeight() + insets.bottom);
			} else {
				return new Dimension(insets.left + insets.right
						+ model.getScaleOneWidth()
						+ model.getScaleStringMaxWidth(getIsLeft()), insets.top
						+ model.getScaleCount() * model.getScaleOneHeight()
						+ insets.bottom);
			}
		}

		private boolean getIsLeft() {
			return scaleOrigin == ScaleOrigin.LEFT;
		}

		public Dimension getMaximumSize() {
			return getPreferredSize();
		}

		public Dimension getMinimumSize() {
			return getPreferredSize();
		}

		public VerticallyScaleProperty getVerticallyScaleProperty() {
			return graphPropertyModel.getVerticallyScaleProperty();
		}

		public VerticallyScaleModel getModel() {
			return model;
		}

		/**
		 * このコンポーネントを描画します。
		 * 
		 * @todo 力率のスケール描画を追加
		 * @param g このコンポーネントのグラフィックコンテキスト
		 */
		public void paintComponent(Graphics g) {
			super.paintComponent(g);
			// 2Dグラフィックコンテキストのコピーを作成
			Graphics2D g2d = (Graphics2D) g.create();

			scaleOrigin.drawLine(g2d, this);

			// 2Dグラフィックコンテキストのコピーを破棄
			g2d.dispose();
		}

		/**
		 * スケール最大スパン変更の有無を設定します。
		 * 
		 * @param isMaxEnabled 変更可能にする場合は true、変更不可にする場合は false
		 */
		public void setMaxEnabled(boolean isMaxEnabled) {
			model.setMaxEnable(isMaxEnabled);
		}

		/**
		 * スケール最小スパン変更の有無を設定します。
		 * 
		 * @param isMinEnabled 変更可能にする場合は true、変更不可にする場合は false
		 */
		public void setMinEnabled(boolean isMinEnabled) {
			model.setMinEnable(isMinEnabled);
		}

		/**
		 * スケールデータモデルが変更された時に呼ばれます。
		 * 
		 * @param evt イベントソースおよび変更したプロパティを記述する PropertyChangeEvent オブジェクト
		 */
		public void propertyChange(PropertyChangeEvent evt) {
			revalidate();
			repaint();
		}
	}

	/**
	 * スケール変更ボタンのリスナークラスです。
	 */
	private static class ScaleChangeButtonListener implements ActionListener {
		private VerticallyScaleView verticallyScaleView;

		ScaleChangeButtonListener(VerticallyScaleView verticallyScaleView) {
			this.verticallyScaleView = verticallyScaleView;
		}

		public void actionPerformed(ActionEvent e) {
			if (verticallyScaleView.model.getSeries() < 0) {
				return;
			}

			Frame frame = WifeUtilities.getParentFrame(verticallyScaleView);
			DialogFactory factory = new DialogFactory();
			JDialog dialog = factory.createDialog(frame, verticallyScaleView);

			JComponent comp = (JComponent) e.getSource();
			Rectangle size = comp.getBounds();
			Point p = comp.getLocationOnScreen();
			dialog.setLocation(new Point(p.x, p.y - size.height));
			Rectangle bounds = dialog.getBounds();
			bounds.setLocation(p);
			dialog.setLocation(WifeUtilities.getInScreenPoint(frame
					.getToolkit().getScreenSize(), bounds));
			dialog.show();
		}

		/**
		 * スケール変更のダイアログファクトリークラスです。 コンバーターの種類で生成するダイアログを変更します。
		 */
		private static class DialogFactory {
			/**
			 * 縦スケールモデルのスパンを変更するダイアログを返します
			 * 
			 * @param frame 親フレーム
			 * @param verticallyScaleView 縦スケールモデル
			 * @return 縦スケールモデルのスパンを変更するダイアログを返します
			 */
			JDialog createDialog(
					Frame frame,
					VerticallyScaleView verticallyScaleView) {
				// TODO コンバーターで返すダイアログを変更する
				// if (getValueType.equal("ANALOG")) {
				// return new ScaleChangeDialog(frame, verticallyScaleView);
				// } else {
				// return new etc..
				// }
				return new ScaleChangeDialog(frame, verticallyScaleView);
			}
		}

		/**
		 * スケール変更の入力ダイアログです。
		 */
		private static class ScaleChangeDialog extends JDialog {
			private static final long serialVersionUID = 7172111920974254348L;
			private VerticallyScaleView verticallyScaleView;
			private JButton okButton;
			private JSpinner maxSpinner;
			private JSpinner minSpinner;
			private JButton cancelButton;

			ScaleChangeDialog(
					Frame frame,
					VerticallyScaleView verticallyScaleView) {
				super(frame, true);
				this.verticallyScaleView = verticallyScaleView;
				init();
				pack();
			}

			private void init() {
				setDefaultCloseOperation(DISPOSE_ON_CLOSE);
				JPanel mainPanel = new JPanel(new GridBagLayout());

				GridBagConstraints c = new GridBagConstraints();
				c.insets = new Insets(10, 10, 10, 10);
				c.gridx = 0;
				c.gridy = 0;
				JLabel maxLabel = new JLabel("最大");
				mainPanel.add(maxLabel, c);

				c.gridy = 1;
				JLabel minLabel = new JLabel("最小");
				mainPanel.add(minLabel, c);

				c.gridy = 2;
				okButton = new JButton("OK");
				mainPanel.add(okButton, c);

				c.gridx = 1;
				c.gridy = 1;
				setMinSpinner(mainPanel, c);

				c.gridy = 0;
				setMaxSpinner(mainPanel, c);

				c.gridy = 2;
				cancelButton = new JButton("CANCEL");
				mainPanel.add(cancelButton, c);
				getContentPane().add(mainPanel);

				okButton.addActionListener(new OkbuttonAction(this));
				cancelButton.addActionListener(new CancelButtonAction(this));
			}

			private void setMaxSpinner(JPanel mainPanel, GridBagConstraints c) {
				SpinnerNumberModel maxModel = new SpinnerNumberModel(
						verticallyScaleView.model.getScaleMax(),
						Long.MIN_VALUE,
						Long.MAX_VALUE,
						1);
				maxSpinner = new JSpinner(maxModel);
				setEditor(maxSpinner);

				if (!verticallyScaleView.model.isMaxEnable()) {
					maxSpinner.setEnabled(false);
				}

				Dimension d = maxSpinner.getPreferredSize();
				d.width = 80;
				maxSpinner.setPreferredSize(d);
				mainPanel.add(maxSpinner, c);
			}

			private void setEditor(JSpinner spinner) {
				SelectedFieldNumberEditor editer = new SelectedFieldNumberEditor(
						spinner);
				JFormattedTextField text = editer.getTextField();
				text.addFocusListener(new FocusAdapter() {
					public void focusGained(FocusEvent e) {
						if (e.getSource() instanceof JTextComponent) {
							final JTextComponent textComp = ((JTextComponent) e
									.getSource());
							SwingUtilities.invokeLater(new Runnable() {
								public void run() {
									textComp.selectAll();
								}
							});
						}
					}
				});
				spinner.setEditor(editer);
			}

			private void setMinSpinner(JPanel mainPanel, GridBagConstraints c) {
				SpinnerNumberModel minModel = new SpinnerNumberModel(
						verticallyScaleView.model.getScaleMin(),
						Long.MIN_VALUE,
						Long.MAX_VALUE,
						1);
				minSpinner = new JSpinner(minModel);
				setEditor(minSpinner);
				if (!verticallyScaleView.model.isMinEnable()) {
					minSpinner.setEnabled(false);
				}

				Dimension d = minSpinner.getPreferredSize();
				d.width = 80;
				minSpinner.setPreferredSize(d);
				mainPanel.add(minSpinner, c);
			}

			/**
			 * OK ボタンのアクションリスナークラスです。
			 */
			private static class OkbuttonAction implements ActionListener {
				private ScaleChangeDialog scaleChangeDialog;

				OkbuttonAction(ScaleChangeDialog scaleChangeDialog) {
					this.scaleChangeDialog = scaleChangeDialog;
				}

				public void actionPerformed(ActionEvent evt) {
					float max = 0;
					float min = 0;
					max = getSpinnerValue(scaleChangeDialog.maxSpinner);
					min = getSpinnerValue(scaleChangeDialog.minSpinner);
					scaleChangeDialog.verticallyScaleView.model
							.setScaleMax(max);
					scaleChangeDialog.verticallyScaleView.model
							.setScaleMin(min);
					scaleChangeDialog.verticallyScaleView.model
							.setScaleMax(max);
					scaleChangeDialog.verticallyScaleView.model
							.setScaleMin(min);
					scaleChangeDialog.verticallyScaleView.revalidate();
					scaleChangeDialog.dispose();
				}

				private float getSpinnerValue(JSpinner spinner) {
					Number number = (Number) spinner.getValue();
					return number.floatValue();
				}
			}

			/**
			 * Cancel ボタンのアクションリスナークラスです。
			 */
			private static class CancelButtonAction implements ActionListener {
				private ScaleChangeDialog scaleChangeDialog;

				CancelButtonAction(ScaleChangeDialog scaleChangeDialog) {
					this.scaleChangeDialog = scaleChangeDialog;
				}

				public void actionPerformed(ActionEvent evt) {
					scaleChangeDialog.dispose();
				}
			}
		}
	}
}
