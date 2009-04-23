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
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.HeadlessException;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.List;
import java.util.Vector;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JToolBar;
import javax.swing.JViewport;
import javax.swing.ListSelectionModel;
import javax.swing.border.CompoundBorder;

import org.F11.scada.WifeUtilities;
import org.F11.scada.applet.graph.JListUtil;
import org.F11.scada.applet.ngraph.editor.EditorMainPanel;
import org.F11.scada.applet.ngraph.event.GraphChangeEvent;
import org.F11.scada.applet.ngraph.model.GraphModel;
import org.F11.scada.applet.symbol.GraphicManager;
import org.F11.scada.util.ComponentUtil;
import org.F11.scada.util.FontUtil;

/**
 * グループの操作をする処理を集約したツールバー
 * 
 * @author maekawa
 * 
 */
public class GraphToolBar extends JToolBar implements Mediator, Colleague {
	private static final long serialVersionUID = -3659589949473526923L;
	private final Mediator mediator;
	private final GraphProperties graphProperties;
	private final JLabel groupNameLabel;
	private BackAction backAction;
	private ForwardAction forwardAction;
	private ListAction listAction;

	public GraphToolBar(Mediator mediator, GraphProperties graphProperties) {
		this.mediator = mediator;
		this.graphProperties = graphProperties;
		groupNameLabel = getGroupNameLabel();
		add(getTrendOpButton());
		add(getBackButton());
		add(getForwardButton());
		addSeparator();
		add(groupNameLabel);
		addSeparator(new Dimension(5, 0));
		add(getListButton());
		//初期表示グループラベル表示のため
		performColleagueChange(getGraphChangeEvent());
		setFloatable(false);
	}

	private JButton getTrendOpButton() {
		JButton button = new JButton(GraphicManager.get("/images/trendop.png"));
		button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JButton b = (JButton) e.getSource();
				Frame frame = ComponentUtil.getAncestorOfClass(Frame.class, b);
				EditorMainPanel editor = new EditorMainPanel(frame, graphProperties);
				editor.setVisible(true);
			}
		});
		return button;
	}

	private JLabel getGroupNameLabel() {
		JLabel label = new JLabel();
		label.setOpaque(true);
		label.setBackground(Color.white);
		CompoundBorder border =
			new CompoundBorder(
				BorderFactory.createLoweredBevelBorder(),
				BorderFactory.createEmptyBorder(0, 10, 0, 10));
		label.setBorder(border);
		FontUtil.setFont("Monospaced", "PLAIN", 18, label);
		label.setMinimumSize(new Dimension(400, 30));
		label.setMaximumSize(new Dimension(400, 30));
		label.setPreferredSize(new Dimension(400, 30));
		return label;
	}

	private String getGroupName(GraphModel model) {
		int no = model.getGroupNo();
		String s = model.getGroupName();
		return String.format("No.%03d : %s%n", no, s);
	}

	private JButton getBackButton() {
		JButton button = new JButton();
		backAction =
			new BackAction(this, GraphicManager
				.get("/toolbarButtonGraphics/navigation/Back24.gif"));
		button.setAction(backAction);
		return button;
	}

	private JButton getForwardButton() {
		JButton button = new JButton();
		forwardAction =
			new ForwardAction(this, GraphicManager
				.get("/toolbarButtonGraphics/navigation/Forward24.gif"));
		button.setAction(forwardAction);
		return button;
	}

	private JButton getListButton() {
		JButton button = new JButton();
		listAction =
			new ListAction(this, GraphicManager
				.get("/toolbarButtonGraphics/navigation/Down24.gif"));
		button.setAction(listAction);
		return button;
	}

	public void colleaguChanged(Colleague colleague) {
		if (backAction == colleague) {
			backAction.performColleagueChange(getGraphChangeEvent());
		} else if (forwardAction == colleague) {
			forwardAction.performColleagueChange(getGraphChangeEvent());
		} else if (listAction == colleague) {
			listAction.performColleagueChange(getGraphChangeEvent());
		}
		mediator.colleaguChanged(this);
	}

	public GraphChangeEvent getGraphChangeEvent() {
		return mediator.getGraphChangeEvent();
	}

	public void performColleagueChange(GraphChangeEvent e) {
		groupNameLabel.setText(getGroupName(e.getModel()));
	}

	private static class BackAction extends AbstractAction implements Colleague {
		private static final long serialVersionUID = -3076564414498716740L;
		private final Mediator mediator;

		public BackAction(Mediator mediator, Icon icon) {
			super(null, icon);
			this.mediator = mediator;
		}

		public void actionPerformed(ActionEvent e) {
			mediator.colleaguChanged(this);
		}

		public void performColleagueChange(GraphChangeEvent e) {
			GraphModel model = e.getModel();
			model.setGroupNo(model.getGroupNo() - 1);
		}
	}

	private static class ForwardAction extends AbstractAction implements Colleague {
		private static final long serialVersionUID = -4345125917459288044L;
		private final Mediator mediator;

		public ForwardAction(Mediator mediator, Icon icon) {
			super(null, icon);
			this.mediator = mediator;
		}

		public void actionPerformed(ActionEvent e) {
			mediator.colleaguChanged(this);
		}

		public void performColleagueChange(GraphChangeEvent e) {
			GraphModel model = e.getModel();
			model.setGroupNo(model.getGroupNo() + 1);
		}
	}

	private class ListAction extends AbstractAction implements Colleague {
		private static final long serialVersionUID = -2965709206126519966L;
		private final Mediator mediator;

		public ListAction(Mediator mediator, Icon icon) {
			super(null, icon);
			this.mediator = mediator;
		}

		public void actionPerformed(ActionEvent e) {
			mediator.colleaguChanged(this);
		}

		public void performColleagueChange(GraphChangeEvent e) {
			ListDialog dialog =
				new ListDialog(ComponentUtil.getAncestorOfClass(
					Frame.class,
					GraphToolBar.this), "グループ選択", e.getModel());
			dialog.setVisible(true);
		}

		private class ListDialog extends JDialog {
			private static final long serialVersionUID = 6251389515389603697L;
			private final GraphModel model;
			private JList list;

			public ListDialog(Frame owner, String title, GraphModel model)
					throws HeadlessException {
				super(owner, title, true);
				this.model = model;
				setDefaultCloseOperation(DISPOSE_ON_CLOSE);
				add(getCentor(), BorderLayout.CENTER);
				add(getSouth(), BorderLayout.SOUTH);
				setSize(400, 300);
				WifeUtilities.setCenter(this);
			}

			private Component getCentor() {
				JPanel panel = new JPanel(new BorderLayout());
				panel
					.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
				list = new JList(getModel());
				list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
				list.setSelectedIndex(model.getGroupNo());
				list.addMouseListener(getMouseListener());
				JScrollPane scrollPane = new JScrollPane(list);
				JViewport p = scrollPane.getViewport();
				p.setViewPosition(new Point(0, model.getGroupNo()
					* scrollPane.getVerticalScrollBar().getBlockIncrement(1)));
				panel.add(scrollPane);
				return panel;
			}

			private Vector<String> getModel() {
				Vector<String> v = new Vector<String>();
				List<SeriesGroup> l = model.getSeriesGroups();
				int i = 0;
				for (SeriesGroup seriesGroup : l) {
					v.add(String.format("%03d : %s", i++, seriesGroup
						.getGroupName()));
				}
				return v;
			}

			private MouseListener getMouseListener() {
				return new MouseAdapter() {
					public void mouseClicked(MouseEvent e) {
						if (e.getClickCount() == 2) {
							int select = list.locationToIndex(e.getPoint());
							if (JListUtil.hasSelected(ListDialog.this, select)) {
								model.setGroupNo(select);
								groupNameLabel.setText(getGroupName(model));
								dispose();
							}
						}
					}
				};
			}

			private Component getSouth() {
				Box box = Box.createHorizontalBox();
				box.setBorder(BorderFactory.createEmptyBorder(0, 20, 10, 20));
				box.add(Box.createHorizontalGlue());
				box.add(getOk());
				box.add(Box.createHorizontalStrut(5));
				box.add(getCancel());
				return box;
			}

			private JButton getOk() {
				JButton button = new JButton("OK");
				button.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						int select = list.getSelectedIndex();
						if (JListUtil.hasSelected(ListDialog.this, select)) {
							model.setGroupNo(select);
							groupNameLabel.setText(getGroupName(model));
							dispose();
						}
					}
				});
				return button;
			}

			private JButton getCancel() {
				JButton button = new JButton("CANCEL");
				button.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						dispose();
					}
				});
				return button;
			}
		}
	}
}
