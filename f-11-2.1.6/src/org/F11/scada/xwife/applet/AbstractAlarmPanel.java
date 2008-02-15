/*
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

package org.F11.scada.xwife.applet;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.Timestamp;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableColumnModel;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumn;
import javax.swing.table.TableModel;

import org.F11.scada.Globals;
import org.F11.scada.WifeUtilities;
import org.F11.scada.applet.ClientConfiguration;
import org.F11.scada.applet.dialog.FindAlarmDialog;
import org.F11.scada.applet.operationlog.DefaultOperationLoggingTableModel;
import org.F11.scada.applet.operationlog.OperationLoggingFinder;
import org.F11.scada.applet.operationlog.OperationLoggingTable;
import org.F11.scada.applet.symbol.ColorFactory;
import org.F11.scada.applet.symbol.GraphicManager;
import org.F11.scada.parser.alarm.AlarmConfig;
import org.F11.scada.parser.alarm.AlarmDefine;
import org.F11.scada.parser.alarm.AlarmTableConfig;
import org.F11.scada.parser.alarm.FontConfig;
import org.F11.scada.security.auth.Subject;
import org.F11.scada.server.alarm.table.AttributeRecord;
import org.F11.scada.server.alarm.table.FindAlarmCondition;
import org.F11.scada.server.alarm.table.FindAlarmPosition;
import org.F11.scada.server.alarm.table.FindAlarmTable;
import org.F11.scada.server.alarm.table.Priority;
import org.F11.scada.server.alarm.table.FindAlarmCondition.RadioStat;
import org.F11.scada.util.TableUtil;
import org.F11.scada.xwife.applet.alarm.PageJump;
import org.apache.commons.lang.time.FastDateFormat;
import org.apache.log4j.Logger;

public abstract class AbstractAlarmPanel extends JPanel {
	protected static Logger logger = Logger.getLogger(AbstractAlarmPanel.class);
	/** �T�}���A�q�X�g���A�����̃e�[�u���ł��B */
	protected JPanel panelAlarmList;
	protected AlarmTable summary;
	protected AlarmTable history;
	protected AlarmTable career;
	/** ���C���A�v���b�g�̎Q�Ƃł� */
	protected AbstractNewApplet wifeApplet;
	/** �x��ݒ�v���p�e�B�I�u�W�F�N�g�ł� */
	protected AlarmDefine alarmDefine;
	/** �T�}���A�q�X�g���A�����̕\���ʒu�ł��B */
	protected FindAlarmPosition fac_s;
	protected JLabel summary_page;
	protected FindAlarmPosition fac_h;
	protected JLabel history_page;
	protected FindAlarmPosition fac_c;
	protected JLabel career_page;
	/** �������� */
	protected FindAlarmCondition condition;
	protected JComboBox s_order;
	protected JComboBox h_order;
	protected JComboBox c_order;
	/** ���������\���R���|�[�l���g */
	protected static final FastDateFormat formatDate = FastDateFormat
			.getInstance("yyyy/MM/dd HH:mm:ss");
	protected JLabel labelStDate;
	protected JLabel labelEdDate;
	protected JLabel labelKind;
	protected JLabel labelPriority;
	protected JLabel labelOnOff;
	protected JLabel labelHistCheck;
	protected Component alarmConditions;
	protected JLabel unit;
	protected JLabel name;
	protected final boolean isShowSortColumn;

	/**
	 * ���t�A�L���A��Ԃ̃J�����̃T�C�Y�ł��B
	 */
	protected int DATE_FIELD_WIDTH = 120;
	protected int UNIT_FIELD_WIDTH = 150;
	protected int STATS_FIELD_WIDTH = 70;
	/** �{�^���t�F�[�X�ł��B */
	protected static final String VIEWMODE_UP = "   ��   ";
	protected static final String VIEWMODE_DW = "   ��   ";
	protected JComponent panelNewAlarm;

	/**
	 * �v���C�x�[�g�ȃR���X�g���N�^�ł��B createAlarmPanel ���g�p���ăC���X�^���X�𐶐����Ă��������B
	 */
	protected AbstractAlarmPanel(
			final AbstractNewApplet wifeApplet,
			String configFile) {
		super(new BorderLayout());
		this.wifeApplet = wifeApplet;
		this.alarmDefine = new AlarmDefine(configFile);
		isShowSortColumn = SortColumnUtil.getShowSortColumn(wifeApplet);

		setPanelNewAlarm();
		setPanelAlarmList();
	}

	protected void setPanelNewAlarm() {
		panelNewAlarm = getAlarmNewLine(wifeApplet);
		// �\���ؑփ{�^��
		Box panelBut = getButton();
		panelNewAlarm.add(panelBut, BorderLayout.EAST);
		add(panelNewAlarm, BorderLayout.NORTH);
	}

	protected void setPanelAlarmList() {
		// ��������
		panelAlarmList = new JPanel(new BorderLayout());
		if (isNewLayout()) {
			alarmConditions = createNewAlarmConditions();
		} else {
			alarmConditions = createAlarmConditions();
		}
		panelAlarmList.add(alarmConditions, BorderLayout.NORTH);
		// �x��ꗗ
		panelAlarmList.add(getAlarmTable(), BorderLayout.CENTER);
		panelAlarmList.setVisible(false);
		add(panelAlarmList, BorderLayout.CENTER);
	}

	protected boolean isNewLayout() {
		return wifeApplet.configuration.getBoolean(
				"org.F11.scada.xwife.applet.newalarm",
				false);
	}

	protected Component createNewAlarmConditions() {
		JPanel panelRet = new JPanel(new BorderLayout());
		panelRet.add(getConditionDisplay(), BorderLayout.CENTER);
		panelRet.add(getConditionButton(), BorderLayout.EAST);

		condition = new FindAlarmCondition();
		updateAlarmConditionLabels();
		return panelRet;
	}

	private Component getConditionDisplay() {
		Box box = Box.createVerticalBox();
		box.setBorder(BorderFactory.createTitledBorder("��������"));
		box.add(getNewAlarmConditions());
		return box;
	}

	private Component getNewAlarmConditions() {
		Box box = Box.createVerticalBox();
		box.setBorder(BorderFactory.createEmptyBorder(0, 50, 10, 15));
		box.add(getDateComponent());
		box.add(getKindComponent());
		box.add(getCheckComponent());
		box.add(getUnitComponent());
		box.add(getNameComponent());
		return box;
	}

	private Component getDateComponent() {
		// �J�n����
		Box box = Box.createHorizontalBox();
		box.add(new JLabel("�����F"));
		labelStDate = new JLabel();
		labelStDate.setOpaque(true);
		labelStDate.setBackground(ColorFactory.getColor("white"));
		labelStDate.setBorder(BorderFactory.createLoweredBevelBorder());
		box.add(labelStDate);

		box.add(new JLabel(" �` "));

		// �I������
		labelEdDate = new JLabel();
		labelEdDate.setOpaque(true);
		labelEdDate.setBackground(ColorFactory.getColor("white"));
		labelEdDate.setBorder(BorderFactory.createLoweredBevelBorder());
		box.add(labelEdDate);
		box.add(Box.createHorizontalGlue());
		return box;
	}

	private Component getKindComponent() {
		Box box = Box.createHorizontalBox();
		box.add(new JLabel("�����F"));
		labelKind = new JLabel();
		labelKind.setOpaque(true);
		labelKind.setBackground(ColorFactory.getColor("white"));
		labelKind.setBorder(BorderFactory.createLoweredBevelBorder());
		setComponentSize(labelKind, 400, 20);
		box.add(labelKind);

		if (isShowSortColumn) {
			box.add(Box.createHorizontalStrut(15));
			box.add(new JLabel("��ʁF"));
			labelPriority = new JLabel();
			labelPriority.setOpaque(true);
			labelPriority.setBackground(ColorFactory.getColor("white"));
			labelPriority.setBorder(BorderFactory.createLoweredBevelBorder());
			setComponentSize(labelPriority, 400, 20);
			box.add(labelPriority);
		}
		box.add(Box.createHorizontalGlue());
		return box;
	}

	private Component getCheckComponent() {
		Box box = Box.createHorizontalBox();
		box.add(new JLabel("�����F"));
		labelOnOff = new JLabel();
		labelOnOff.setOpaque(true);
		labelOnOff.setBackground(ColorFactory.getColor("white"));
		labelOnOff.setBorder(BorderFactory.createLoweredBevelBorder());
		box.add(labelOnOff);

		box.add(Box.createHorizontalStrut(30));

		box.add(new JLabel("�m�F�i�q�X�g���̂݁j�F"));
		labelHistCheck = new JLabel();
		labelHistCheck.setOpaque(true);
		labelHistCheck.setBackground(ColorFactory.getColor("white"));
		labelHistCheck.setBorder(BorderFactory.createLoweredBevelBorder());
		box.add(labelHistCheck);
		box.add(Box.createHorizontalGlue());
		return box;
	}

	private void setComponentSize(JComponent comp, int width, int height) {
		Dimension d = new Dimension(width, height);
		comp.setPreferredSize(d);
		comp.setMinimumSize(d);
		comp.setMaximumSize(d);
	}

	private Component getUnitComponent() {
		Box box = Box.createHorizontalBox();
		box.add(new JLabel("�L���F"));
		unit = new JLabel();
		unit.setOpaque(true);
		unit.setBackground(ColorFactory.getColor("white"));
		unit.setBorder(BorderFactory.createLoweredBevelBorder());
		setComponentSize(unit, 400, 20);
		box.add(unit);
		box.add(Box.createHorizontalGlue());
		return box;
	}

	private Component getNameComponent() {
		Box box = Box.createHorizontalBox();
		box.add(new JLabel("���́F"));
		name = new JLabel();
		name.setOpaque(true);
		name.setBackground(ColorFactory.getColor("white"));
		name.setBorder(BorderFactory.createLoweredBevelBorder());
		setComponentSize(name, 400, 20);
		box.add(name);
		box.add(Box.createHorizontalGlue());
		return box;
	}

	private Component getConditionButton() {
		// �����{�^��
		Box box = Box.createVerticalBox();
		box.setBorder(BorderFactory.createEmptyBorder(10, 5, 0, 5));
		JButton findButton = new JButton("���������ݒ�");
		findButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					final AttributeRecord[] attris = wifeApplet.alarmListFinder
							.getAttributeRecords();
					final List priorityList = wifeApplet.alarmListFinder
							.getPriorityTable();
					FindAlarmCondition cond_new = FindAlarmDialog
							.showFindAlarmDialog(
									WifeUtilities.getParentFrame(wifeApplet),
									condition,
									attris,
									priorityList);
					if (condition != cond_new) {
						condition = cond_new;
						s_order.setSelectedIndex(0);
						h_order.setSelectedIndex(0);
						c_order.setSelectedIndex(0);
						updateAlarmConditionLabels();
					}
				} catch (Exception ex) {
					ex.printStackTrace();
					JOptionPane.showMessageDialog(
							WifeUtilities.getDialogParent(wifeApplet),
							alarmDefine.getAlarmConfig()
									.getServerErrorMessage().getMessage(),
							"F-11 server error",
							JOptionPane.ERROR_MESSAGE);
				}
			}
		});
		box.add(findButton);
		box.add(Box.createVerticalGlue());
		return box;
	}

	protected Component getAlarmTable() {
		JTabbedPane tabbedPane = createAlarmTables();
		tabbedPane.setSelectedIndex(alarmDefine.getAlarmConfig()
				.getAlarmTableConfig().getDefaultTabNo());
		createOperationFinder(tabbedPane);
		return tabbedPane;
	}

	protected Box getButton() {
		Box panelBut = new Box(BoxLayout.Y_AXIS);
		JButton button = new JButton(VIEWMODE_UP);
		button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				changeAlarmPanel(e);
			}
		});
		panelBut.add(button);
		return panelBut;
	}

	protected abstract JComponent getAlarmNewLine(
			final AbstractWifeApplet wifeApplet);

	protected void createOperationFinder(JTabbedPane tabbedPane) {
		ClientConfiguration configuration = new ClientConfiguration();
		if (configuration.getBoolean("operationlogging.addalarm", true)) {
			DefaultOperationLoggingTableModel loggingTableModel = new DefaultOperationLoggingTableModel();
			final Component loggingFinder = new OperationLoggingFinder(
					loggingTableModel);
			tabbedPane.addTab("���샍�O", null, new OperationLoggingTable(
					loggingTableModel), "���샍�O");

			tabbedPane.addChangeListener(new ChangeListener() {
				public void stateChanged(ChangeEvent e) {
					JTabbedPane tab = (JTabbedPane) e.getSource();
					if (tab.getSelectedIndex() == 3) {
						panelAlarmList.remove(alarmConditions);
						panelAlarmList.add(loggingFinder, BorderLayout.NORTH);
					} else {
						panelAlarmList.remove(loggingFinder);
						panelAlarmList.add(alarmConditions, BorderLayout.NORTH);
					}
					validate();
					repaint();
				}
			});
		}
	}

	/**
	 * �����������𐶐����܂��B
	 */
	protected Box createAlarmConditions() {
		Box panelRet = Box.createVerticalBox();
		// �����{�^��
		JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER));
		JButton findButton = new JButton("���������ݒ�");
		findButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					final AttributeRecord[] attris = wifeApplet.alarmListFinder
							.getAttributeRecords();
					final List priorityList = wifeApplet.alarmListFinder
							.getPriorityTable();
					FindAlarmCondition cond_new = FindAlarmDialog
							.showFindAlarmDialog(
									WifeUtilities.getParentFrame(wifeApplet),
									condition,
									attris,
									priorityList);
					if (condition != cond_new) {
						condition = cond_new;
						s_order.setSelectedIndex(0);
						h_order.setSelectedIndex(0);
						c_order.setSelectedIndex(0);
						updateAlarmConditionLabels();
					}
				} catch (Exception ex) {
					ex.printStackTrace();
					JOptionPane.showMessageDialog(
							WifeUtilities.getDialogParent(wifeApplet),
							alarmDefine.getAlarmConfig()
									.getServerErrorMessage().getMessage(),
							"F-11 server error",
							JOptionPane.ERROR_MESSAGE);
				}
			}
		});
		panel.add(findButton);
		// �J�n����
		JPanel panel1 = new JPanel();
		panel1.setBorder(BorderFactory.createLineBorder(Color.black));
		panel1.add(new JLabel("�J�n�����F"));
		labelStDate = new JLabel();
		panel1.add(labelStDate);
		panel.add(panel1);
		// �I������
		panel1 = new JPanel();
		panel1.setBorder(BorderFactory.createLineBorder(Color.black));
		panel1.add(new JLabel("�I�������F"));
		labelEdDate = new JLabel();
		panel1.add(labelEdDate);
		panel.add(panel1);
		panelRet.add(panel);
		// ���
		panel = new JPanel(new FlowLayout(FlowLayout.CENTER));
		panel1 = new JPanel();
		panel1.setBorder(BorderFactory.createLineBorder(Color.black));
		panel1.add(new JLabel("�����F"));
		labelKind = new JLabel();
		panel1.add(labelKind);
		panel.add(panel1);
		panelRet.add(panel);
		if (isShowSortColumn) {
			// ���
			panel = new JPanel(new FlowLayout(FlowLayout.CENTER));
			panel1 = new JPanel();
			panel1.setBorder(BorderFactory.createLineBorder(Color.black));
			panel1.add(new JLabel("��ʁF"));
			labelPriority = new JLabel();
			panel1.add(labelPriority);
			panel.add(panel1);
			panelRet.add(panel);
		}
		// ����
		panel = new JPanel(new FlowLayout(FlowLayout.CENTER));
		panel1 = new JPanel();
		panel1.setBorder(BorderFactory.createLineBorder(Color.black));
		panel1.add(new JLabel("�����F"));
		labelOnOff = new JLabel();
		panel1.add(labelOnOff);
		panel.add(panel1);
		panel1 = new JPanel();
		panel1.setBorder(BorderFactory.createLineBorder(Color.black));
		panel1.add(new JLabel("�m�F�i�q�X�g���̂݁j�F"));
		labelHistCheck = new JLabel();
		panel1.add(labelHistCheck);
		panel.add(panel1);
		panelRet.add(panel);
		panel1 = new JPanel();
		panel1.setBorder(BorderFactory.createLineBorder(Color.black));
		panel1.add(new JLabel("�L���F"));
		unit = new JLabel();
		panel1.add(unit);
		panel.add(panel1);
		panelRet.add(panel);
		panel1 = new JPanel();
		panel1.setBorder(BorderFactory.createLineBorder(Color.black));
		panel1.add(new JLabel("���́F"));
		name = new JLabel();
		panel1.add(name);
		panel.add(panel1);
		panelRet.add(panel);

		condition = new FindAlarmCondition();
		updateAlarmConditionLabels();
		panelRet.setBorder(BorderFactory.createTitledBorder("��������"));
		return panelRet;
	}

	protected void updateAlarmConditionLabels() {
		if (condition.isSt_enable()) {
			labelStDate.setText(formatDate.format(condition.getSt_calendar()
					.getTime()));
		} else {
			labelStDate.setText("�w��Ȃ�");
		}
		if (condition.isEd_enable()) {
			labelEdDate.setText(formatDate.format(condition.getEd_calendar()
					.getTime()));
		} else {
			labelEdDate.setText("�w��Ȃ�");
		}
		AttributeRecord[] attris = condition.getAttributeRecord();
		if (0 >= attris.length) {
			labelKind.setText("�S��");
		} else {
			StringBuffer sb = new StringBuffer();
			for (int j = 0; j < attris.length; j++) {
				sb.append(attris[j].getName()).append(",  ");
			}
			labelKind.setText(sb.toString());
		}
		if (isShowSortColumn) {
			List prioList = condition.getPriorities();
			if (prioList.isEmpty()) {
				labelPriority.setText("�S��");
			} else {
				StringBuffer sb = new StringBuffer();
				for (Iterator i = prioList.iterator(); i.hasNext();) {
					Priority priority = (Priority) i.next();
					sb.append(priority.getName()).append(", ");
				}
				labelPriority.setText(sb.toString());
			}
		}
		if (condition.getBitvalSelect() == RadioStat.SELECTTRUE) {
			labelOnOff.setText("�����E�^�]�̂�");
		} else if (condition.getBitvalSelect() == RadioStat.SELECTFALSE) {
			labelOnOff.setText("�����E��~�̂�");
		} else if (condition.getBitvalSelect() == RadioStat.SELECTALL) {
			labelOnOff.setText("�S��");
		}
		if (condition.getHistckSelect() == RadioStat.SELECTTRUE) {
			labelHistCheck.setText("�m�F�ς�");
		} else if (condition.getHistckSelect() == RadioStat.SELECTFALSE) {
			labelHistCheck.setText("���m�F");
		} else if (condition.getHistckSelect() == RadioStat.SELECTALL) {
			labelHistCheck.setText("�S��");
		}
		unit.setText(condition.getUnit());
		name.setText(condition.getName());
	}

	/**
	 * �T�}���A�q�X�g���A�����e�[�u���𐶐����܂��B
	 */
	private JTabbedPane createAlarmTables() {
		JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);

		AlarmTableConfig alarmTableConfig = alarmDefine.getAlarmConfig()
				.getAlarmTableConfig();

		createSummaryTab(tabbedPane, alarmTableConfig);
		createHistoryTab(tabbedPane, alarmTableConfig);
		createCareerTab(tabbedPane, alarmTableConfig);
		setAlarmTableCellRenderer(summary);
		setAlarmTableCellRenderer(history);
		setAlarmTableCellRenderer(career);

		return tabbedPane;
	}

	private void createCareerTab(
			JTabbedPane tabbedPane,
			AlarmTableConfig alarmTableConfig) {
		TableModel c_table = new DefaultTableModel(new String[] { "�W�����v�p�X",
				"�����W�����v", "�D�揇��", "�\���F", "point", "provider", "holder",
				"�T�E���h�^�C�v", "�T�E���h�p�X", "Email�O���[�v", "Email���[�h", "����", "�L��", "����",
				"�x��E���", "���" }, 0) {
			private static final long serialVersionUID = -984023000372115422L;

			public boolean isCellEditable(int row, int column) {
				return false;
			}
		};
		career = new AlarmTable(c_table, wifeApplet, alarmTableConfig);
		career.setAutoCreateColumnsFromModel(false);
		removeColumns(career, 11);
		setTableColor(career, alarmTableConfig);

		JPanel panel = createCareerComboBox();

		JPanel panel1 = createCareerButton();
		panel.add(panel1);

		JPanel tabBase = new JPanel(new BorderLayout());
		tabBase.add(panel, BorderLayout.NORTH);

		setTableWidth(career, 0, DATE_FIELD_WIDTH);
		setTableWidth(career, 1, UNIT_FIELD_WIDTH);
		setTableWidth(career, 3, STATS_FIELD_WIDTH);
		SortColumnUtil.removeSortColumn(
				career,
				4,
				wifeApplet,
				STATS_FIELD_WIDTH);

		JScrollPane sp = new JScrollPane(career);
		tabBase.add(sp, BorderLayout.CENTER);
		tabbedPane.addTab("����", null, tabBase, "����");
	}

	private JPanel createPrevAndNextButton(
			JLabel label,
			ActionListener prev,
			ActionListener next) {
		JPanel panel1 = new JPanel(new FlowLayout(FlowLayout.CENTER));
		JButton button = new JButton("�O�y�[�W");
		button.addActionListener(prev);
		panel1.add(button);
		panel1.add(label);
		button = new JButton("���y�[�W");
		button.addActionListener(next);
		panel1.add(button);
		return panel1;
	}

	private JPanel createCareerButton() {
		career_page = new JLabel();
		return createPrevAndNextButton(career_page, new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				logger.debug("pushed prev-button.");
				updateCareer(fac_c.getPrev());
			}
		}, new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				logger.debug("pushed next-button.");
				updateCareer(fac_c.getNext());
			}
		});
	}

	private JPanel createComboBox(JComboBox box, ActionListener l) {
		JPanel panel = new JPanel(new GridLayout(0, 3));
		// ���я�
		JPanel panel1 = new JPanel(new FlowLayout(FlowLayout.LEFT));
		panel1.add(new JLabel("����"));
		box.addActionListener(l);
		panel1.add(box);
		panel.add(panel1);
		return panel;
	}

	private JPanel createCareerComboBox() {
		c_order = new JComboBox(new String[] { "�W��", "�t��", "�L�� ����", "�L�� �~��",
				"���� ����", "���� �~��" });
		return createComboBox(c_order, new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				logger.debug("selected order.");
				int viewrec = alarmDefine.getAlarmConfig()
						.getAlarmTableConfig().getLineCountConfig().getValue();
				updateCareer(FindAlarmPosition.createFindAlarmPosition(viewrec));
			}
		});
	}

	private void setTableWidth(AlarmTable career, int column, int width) {
		TableColumn tc = career.getColumn(career.getColumnName(column));
		tc.setPreferredWidth(width);
		tc.setMaxWidth(tc.getPreferredWidth());
	}

	private void setTableColor(
			AlarmTable table,
			AlarmTableConfig alarmTableConfig) {
		table.setBackground(alarmTableConfig.getBackGroundColor());
		JTableHeader tableHeader = table.getTableHeader();
		tableHeader.setBackground(alarmTableConfig.getHeaderBackGroundColor());
		tableHeader.setForeground(alarmTableConfig.getHeaderForeGroundColor());
	}

	private void createHistoryTab(
			JTabbedPane tabbedPane,
			AlarmTableConfig alarmTableConfig) {
		TableModel h_table = new DefaultTableModel(new String[] { "�W�����v�p�X",
				"�����W�����v", "�D�揇��", "�\���F", "point", "provider", "holder",
				"�����E�^�]", "�����E��~", "�L��", "����", "���", "�m�F" }, 0) {
			private static final long serialVersionUID = -46305358609411425L;

			public boolean isCellEditable(int row, int column) {
				return false;
			}
		};
		history = new AlarmTable(h_table, wifeApplet, alarmTableConfig);
		history.setAutoCreateColumnsFromModel(false);
		if (!wifeApplet.isAppletTypeC()) {
			history.addMouseListener(new HistoryTableListener(
					wifeApplet,
					alarmDefine.getAlarmConfig()));
		}
		removeColumns(history, 7);
		setTableColor(history, alarmTableConfig);

		JPanel tabBase = new JPanel(new BorderLayout());
		JPanel panel = createHistoryComboBox();

		JPanel panel1 = createHistoryButton();
		panel.add(panel1);

		if (!wifeApplet.isAppletTypeC()) {
			JPanel bPanel_b = createAllCheckedButton();
			panel.add(bPanel_b);
		}
		tabBase.add(panel, BorderLayout.NORTH);

		setTableWidth(history, 0, DATE_FIELD_WIDTH);
		setTableWidth(history, 1, DATE_FIELD_WIDTH);
		setTableWidth(history, 2, UNIT_FIELD_WIDTH);
		setTableWidth(history, 5, STATS_FIELD_WIDTH);
		SortColumnUtil.removeSortColumn(
				history,
				4,
				wifeApplet,
				STATS_FIELD_WIDTH);

		JScrollPane sp = new JScrollPane(history);
		tabBase.add(sp, BorderLayout.CENTER);
		tabbedPane.addTab("�q�X�g��", null, tabBase, "�q�X�g��");
	}

	private JPanel createAllCheckedButton() {
		JPanel bPanel_b = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		JButton button = new JButton("�S�m�F");
		button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (Subject.getNullSubject() == wifeApplet.subject) {
					return;
				}

				try {
					int ret = JOptionPane.showConfirmDialog(
							null,
							"���m�F����S�Ċm�F��Ԃɂ��܂��B��낵���ł����H",
							"F-11 Client",
							JOptionPane.YES_NO_OPTION);
					if (ret == JOptionPane.YES_OPTION) {
						logger.debug("all check.");
						wifeApplet.alarmListFinder.setHistoryCheckAll();
						updateHistory(fac_h);
					}
				} catch (Exception ex) {
					ex.printStackTrace();
					JOptionPane.showMessageDialog(
							WifeUtilities.getDialogParent(wifeApplet),
							alarmDefine.getAlarmConfig()
									.getServerErrorMessage().getMessage(),
							"F-11 server error",
							JOptionPane.ERROR_MESSAGE);
				}
			}
		});
		bPanel_b.add(button);
		return bPanel_b;
	}

	private JPanel createHistoryButton() {
		history_page = new JLabel();
		return createPrevAndNextButton(history_page, new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				logger.debug("pushed prev-button.");
				updateHistory(fac_h.getPrev());
			}
		}, new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				logger.debug("pushed next-button.");
				updateHistory(fac_h.getNext());
			}
		});
	}

	private JPanel createHistoryComboBox() {
		h_order = new JComboBox(new String[] { "�W��", "�t��", "�����E��~���� ����",
				"�����E��~���� �~��", "�L�� ����", "�L�� �~��", "���� ����", "���� �~��", "�m�F ����",
				"�m�F �~��" });
		return createComboBox(h_order, new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				logger.debug("selected order.");
				int viewrec = alarmDefine.getAlarmConfig()
						.getAlarmTableConfig().getLineCountConfig().getValue();
				updateHistory(FindAlarmPosition
						.createFindAlarmPosition(viewrec));
			}
		});
	}

	private void createSummaryTab(
			JTabbedPane tabbedPane,
			AlarmTableConfig alarmTableConfig) {
		TableModel s_table = new DefaultTableModel(new String[] { "�W�����v�p�X",
				"�����W�����v", "�D�揇��", "�\���F", "point", "provider", "holder",
				"�����E�^�]", "�����E��~", "�L��", "����", "�x��E���", "���" }, 0) {
			private static final long serialVersionUID = 4104647228256893197L;

			public boolean isCellEditable(int row, int column) {
				return false;
			}
		};
		summary = new AlarmTable(s_table, wifeApplet, alarmTableConfig);
		summary.setAutoCreateColumnsFromModel(false);
		removeColumns(summary, 7);
		setTableColor(summary, alarmTableConfig);

		FontMetrics metrics = summary.getFontMetrics(summary.getFont());
		DATE_FIELD_WIDTH = metrics.stringWidth("8888/88/88 88:88:88") + 8;
		STATS_FIELD_WIDTH = metrics.stringWidth("�x��E���") + 8;

		JPanel tabBase = new JPanel(new BorderLayout());
		JPanel panel = createSummaryComboBox();

		JPanel panel1 = createSummaryButton();
		panel.add(panel1);
		tabBase.add(panel, BorderLayout.NORTH);

		setTableWidth(summary, 0, DATE_FIELD_WIDTH);
		setTableWidth(summary, 1, DATE_FIELD_WIDTH);
		setTableWidth(summary, 2, UNIT_FIELD_WIDTH);
		setTableWidth(summary, 4, STATS_FIELD_WIDTH);
		SortColumnUtil.removeSortColumn(
				summary,
				5,
				wifeApplet,
				STATS_FIELD_WIDTH);

		JScrollPane sp = new JScrollPane(summary);
		tabBase.add(sp, BorderLayout.CENTER);
		tabbedPane.addTab("�T�}��", null, tabBase, "�T�}��");
	}

	private JPanel createSummaryButton() {
		summary_page = new JLabel();
		return createPrevAndNextButton(summary_page, new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				logger.debug("pushed prev-button.");
				updateSummary(fac_s.getPrev());
			}
		}, new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				logger.debug("pushed next-button.");
				updateSummary(fac_s.getNext());
			}
		});
	}

	private JPanel createSummaryComboBox() {
		s_order = new JComboBox(new String[] { "�W��", "�t��", "�����E�^�]���� ����",
				"�����E�^�]���� �~��", "�����E��~���� ����", "�����E��~���� �~��", "���� ����", "���� �~��" });
		return createComboBox(s_order, new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				logger.debug("selected order.");
				int viewrec = alarmDefine.getAlarmConfig()
						.getAlarmTableConfig().getLineCountConfig().getValue();
				updateSummary(FindAlarmPosition
						.createFindAlarmPosition(viewrec));
			}
		});
	}

	/**
	 * �擪�J�������� n �J�������폜���܂��B
	 * 
	 * @param table �Ώۂ̃e�[�u��
	 * @param removeColumnCount �폜����J������
	 */
	private void removeColumns(JTable table, int removeColumnCount) {
		for (int i = removeColumnCount - 1; i >= 0; i--) {
			table.removeColumn(table.getColumn(table.getColumnName(0)));
		}
	}

	/**
	 * �Ώۂ̃e�[�u���� AlarmTableCellRenderer ��ݒ肵�܂��B
	 * 
	 * @param table �Ώۂ̃e�[�u��
	 */
	private void setAlarmTableCellRenderer(JTable table) {
		AlarmTableCellRenderer cellRecderer = new AlarmTableCellRenderer();
		for (int i = table.getColumnCount(); i > 0; i--) {
			DefaultTableColumnModel cmodel = (DefaultTableColumnModel) table
					.getColumnModel();
			TableColumn column = cmodel.getColumn(i - 1);
			column.setCellRenderer(cellRecderer);
		}
	}

	/**
	 * AlarmTabbedPane �̉�ʃ��b�N�A�N�V�����𐶐����܂��B
	 * 
	 * @return LockAction �̃C���X�^���X�B
	 */
	public LockAction createLockAction(PageChanger pageChanger) {
		if (isCreateTables()) {
			return new LockAction(
					"��ʌŒ�",
					GraphicManager
							.get("/toolbarButtonGraphics/general/Stop24.gif"),
					pageChanger);
		}
		return null;
	}

	/**
	 * �e�e�[�u���̐�����Ԃ𒲍����܂��B
	 * 
	 * @return �T�}���A�q�X�g���A�����e�[�u������������Ă���ꍇ true�B�łȂ��ꍇ false ��Ԃ��܂��B
	 */
	private boolean isCreateTables() {
		return summary != null && history != null && career != null;
	}

	protected abstract void changeAlarmPanel(final ActionEvent e);

	private void updateSummary(FindAlarmPosition newfac) {
		logger.debug("updateSummary(" + newfac.toString() + ")");
		try {
			DefaultTableModel model = (DefaultTableModel) summary.getModel();
			while (0 < model.getRowCount()) {
				model.removeRow(0);
			}
			FindAlarmTable table = wifeApplet.alarmListFinder.getSummaryList(
					condition,
					newfac,
					s_order.getSelectedIndex());
			Object[][] rows = table.getRecords();
			for (int row = 0; row < rows.length; row++) {
				model.addRow(rows[row]);
			}
			model.fireTableDataChanged();
			final StringBuffer sb = new StringBuffer();
			sb.append(newfac.getOffset() / newfac.getLimit() + 1);
			sb.append("/");
			sb.append((table.getMaxrec() - 1) / newfac.getLimit() + 1);
			summary_page.setText(sb.toString());
			fac_s = newfac.setMaxrec(table.getMaxrec());
		} catch (Exception e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(
					WifeUtilities.getDialogParent(wifeApplet),
					alarmDefine.getAlarmConfig().getServerErrorMessage()
							.getMessage(),
					"F-11 server error",
					JOptionPane.ERROR_MESSAGE);
		}
	}

	private void updateHistory(FindAlarmPosition newfac) {
		logger.debug("updateHistory(" + newfac.toString() + ")");
		try {

			DefaultTableModel model = (DefaultTableModel) history.getModel();
			while (0 < model.getRowCount()) {
				model.removeRow(0);
			}

			FindAlarmTable table = wifeApplet.alarmListFinder.getHistoryList(
					condition,
					newfac,
					h_order.getSelectedIndex());
			Object[][] rows = table.getRecords();
			for (int row = 0; row < rows.length; row++) {
				model.addRow(rows[row]);
			}
			model.fireTableDataChanged();
			final StringBuffer sb = new StringBuffer();
			sb.append(newfac.getOffset() / newfac.getLimit() + 1);
			sb.append("/");
			sb.append((table.getMaxrec() - 1) / newfac.getLimit() + 1);
			history_page.setText(sb.toString());
			fac_h = newfac.setMaxrec(table.getMaxrec());
		} catch (Exception e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(
					WifeUtilities.getDialogParent(wifeApplet),
					alarmDefine.getAlarmConfig().getServerErrorMessage()
							.getMessage(),
					"F-11 server error",
					JOptionPane.ERROR_MESSAGE);
		}
	}

	private void updateCareer(FindAlarmPosition newfac) {
		logger.debug("updateHistory(" + newfac.toString() + ")");
		try {
			DefaultTableModel model = (DefaultTableModel) career.getModel();
			while (0 < model.getRowCount()) {
				model.removeRow(0);
			}
			FindAlarmTable table = wifeApplet.alarmListFinder.getCareerList(
					condition,
					newfac,
					c_order.getSelectedIndex());
			Object[][] rows = table.getRecords();
			for (int row = 0; row < rows.length; row++) {
				model.addRow(rows[row]);
			}
			model.fireTableDataChanged();
			final StringBuffer sb = new StringBuffer();
			sb.append(newfac.getOffset() / newfac.getLimit() + 1);
			sb.append("/");
			sb.append((table.getMaxrec() - 1) / newfac.getLimit() + 1);
			career_page.setText(sb.toString());
			fac_c = newfac.setMaxrec(table.getMaxrec());
		} catch (Exception e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(
					WifeUtilities.getDialogParent(wifeApplet),
					alarmDefine.getAlarmConfig().getServerErrorMessage()
							.getMessage(),
					"F-11 server error",
					JOptionPane.ERROR_MESSAGE);
		}
	}

	/**
	 * �T�}���[Table�N���X�ł��B
	 */
	private static class AlarmTable extends JTable {
		private static final long serialVersionUID = 4906686373708580963L;

		/**
		 * �R���X�g���N�^
		 * 
		 * @param dataHolder �f�[�^�z���_�[
		 * @param wifeApplet ���C���A�v���b�g�̎Q��
		 */
		AlarmTable(
				TableModel model,
				AbstractWifeApplet wifeApplet,
				AlarmTableConfig alarmTableConfig) {

			super(model);
			new PageJump(this, wifeApplet);

			FontConfig fc = alarmTableConfig.getFontConfig();

			Font font = fc.getFont();

			setFont(font);
			FontMetrics metrics = getFontMetrics(font);
			int height = metrics.getHeight();
			setRowHeight(height);

			JTableHeader header = getTableHeader();
			header.setFont(font);
		}
	}

	/**
	 * �e�[�u�����f����4�J�����ڂ̐F�𔽉f����Z�������_���[�N���X�B
	 */
	private static class AlarmTableCellRenderer extends
			DefaultTableCellRenderer {
		private static final long serialVersionUID = 457590410332187554L;
		private static final FastDateFormat format = FastDateFormat
				.getInstance("yyyy/MM/dd HH:mm:ss");

		public Component getTableCellRendererComponent(
				JTable table,
				Object value,
				boolean isSelected,
				boolean hasFocus,
				int row,
				int column) {
			super.getTableCellRendererComponent(
					table,
					value,
					isSelected,
					hasFocus,
					row,
					column);

			if (value instanceof Date) {
				Date d = (Date) value;
				if (d.after(Globals.EPOCH)) {
					super.setText(format.format(value));
				} else {
					super.setText(null);
				}
			}

			TableModel tm = (TableModel) table.getModel();
			String colorName = (String) tm.getValueAt(row, 3);
			super.setForeground(ColorFactory.getColor(colorName));
			return this;
		}
	}

	/**
	 * �q�X�g���ꗗ�\�̃}�E�X�C�x���g���Ď�����A�N�V�������X�i�[�ł��B
	 */
	private static class HistoryTableListener extends MouseAdapter {
		private static final int CHECK_COLUMN = 11;
		private final AbstractNewApplet wifeApplet;
		private final AlarmConfig config;

		public HistoryTableListener(
				AbstractNewApplet wifeApplet,
				AlarmConfig config) {
			super();
			this.wifeApplet = wifeApplet;
			this.config = config;
		}

		public void mousePressed(MouseEvent e) {
			// �����O�C�����͊m�F�s����
			if (wifeApplet.subject == Subject.getNullSubject())
				return;

			JTable table = (JTable) e.getSource();
			int row = table.rowAtPoint(e.getPoint());
			int column = table.columnAtPoint(e.getPoint());
			logger.debug("row : " + row + " column : " + column);
			Object o = table.getValueAt(row, column);
			if (TableUtil.getModelColumn(e) == CHECK_COLUMN && o == null) {
				TableModel tm = table.getModel();
				Integer point = (Integer) tm.getValueAt(row, 4);
				String provider = (String) tm.getValueAt(row, 5);
				String holder = (String) tm.getValueAt(row, 6);
				Timestamp on_date = (Timestamp) tm.getValueAt(row, 7);
				try {
					wifeApplet.alarmListFinder.setHistoryCheck(
							point,
							provider,
							holder,
							on_date);
					table.setValueAt("��������", row, column);
				} catch (Exception ex) {
					ex.printStackTrace();
					JOptionPane.showMessageDialog(
							WifeUtilities.getDialogParent(wifeApplet),
							config.getServerErrorMessage().getMessage(),
							"F-11 server error",
							JOptionPane.ERROR_MESSAGE);
				}
			}
		}
	}

	/**
	 * ��ʃ��b�N��\���A�N�V�����N���X�ł��B
	 */
	private static class LockAction extends AbstractAction {
		private static final long serialVersionUID = 6592672649949107589L;
		private PageChanger pageChanger;

		LockAction(String name, Icon icon, PageChanger pageChanger) {
			super(name, icon);
			this.pageChanger = pageChanger;
		}

		public void actionPerformed(ActionEvent actionEvent) {
			if (pageChanger.isDisplayLock())
				pageChanger.setDisplayLock(false);
			else
				pageChanger.setDisplayLock(true);
		}
	}

}
