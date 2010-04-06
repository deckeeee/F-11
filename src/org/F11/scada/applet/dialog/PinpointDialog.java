package org.F11.scada.applet.dialog;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dialog;
import java.awt.Frame;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.rmi.Naming;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.ListIterator;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableColumnModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableModel;

import org.F11.scada.Globals;
import org.F11.scada.WifeUtilities;
import org.F11.scada.applet.symbol.ColorFactory;
import org.F11.scada.applet.symbol.ImageSymbolEditable;
import org.F11.scada.data.DataAccessable;
import org.F11.scada.server.dto.CareerDto;
import org.F11.scada.util.FontUtil;
import org.F11.scada.util.TableUtil;
import org.F11.scada.xwife.applet.AbstractWifeApplet;
import org.F11.scada.xwife.applet.PageChanger;
import org.F11.scada.xwife.applet.alarm.RowHeaderScrollPane;
import org.apache.commons.configuration.Configuration;
import org.apache.commons.lang.time.FastDateFormat;
import org.apache.log4j.Logger;

/**
 * �s���|�C���g������\������_�C�A���O�ł��B
 *
 * @author maekawa
 *
 */
public class PinpointDialog extends WifeDialog {
	private static final long serialVersionUID = 8396858132703771911L;
	private final Logger logger = Logger.getLogger(PinpointDialog.class);
	/** �ҏW�ΏۃV���{�� */
	private ImageSymbolEditable symbol;
	/** �e�A�v���b�g(�x�񉹒�~�ɂĎg�p) */
	private final AbstractWifeApplet wifeApplet;
	private JPanel mainPanel;
	private final int rowWidth1;
	private final int rowWidth2;
	private final int rowWidth3;

	public PinpointDialog(
			Dialog dialog,
			PageChanger changer,
			int rowWidth1,
			int rowWidth2,
			int rowWidth3) {
		super(dialog);
		this.wifeApplet = (AbstractWifeApplet) changer;
		this.rowWidth1 = rowWidth1;
		this.rowWidth2 = rowWidth2;
		this.rowWidth3 = rowWidth3;
		init();
	}

	public PinpointDialog(
			Frame frame,
			PageChanger changer,
			int rowWidth1,
			int rowWidth2,
			int rowWidth3) {
		super(frame);
		this.wifeApplet = (AbstractWifeApplet) changer;
		this.rowWidth1 = rowWidth1;
		this.rowWidth2 = rowWidth2;
		this.rowWidth3 = rowWidth3;
		init();
	}

	private void init() {
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		mainPanel = new JPanel(new BorderLayout());
		mainPanel.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 5));
		mainPanel.add(getSouth(), BorderLayout.SOUTH);
		add(mainPanel, BorderLayout.CENTER);
	}

	@Override
	public void show() {
		mainPanel.add(getNorth(), BorderLayout.NORTH);
		mainPanel.add(getTable(), BorderLayout.CENTER);
		Rectangle dialogBounds = getBounds();
		dialogBounds.setLocation(symbol.getPoint());
		setLocation(WifeUtilities.getInScreenPoint(screenSize, dialogBounds));
		setDefaultFocus();
		super.show();
	}

	@Override
	public void dispose() {
		setDefaultFocus();
		super.dispose();
	}

	private Component getNorth() {
		JPanel panel = new JPanel(new BorderLayout());
		panel.setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 0));
		JLabel label = new JLabel(getTitle());
		label.setBorder(BorderFactory.createLoweredBevelBorder());
		label.setFont(FontUtil.getFont("Monospaced-PLAIN-24"));
		panel.add(label, BorderLayout.CENTER);
		return panel;
	}

	private Component getTable() {
		JTable table = new JTable(createTableModel());
		table.setRowSelectionAllowed(false);
		table.setColumnSelectionAllowed(false);
		setTableCellRenderer(table);
		TableUtil.removeColumn(table, 1);
		TableUtil.removeColumn(table, 1);
		TableUtil.removeColumn(table, 4);
		removeSortColumn(table);
		TableUtil.setColumnWidth(table, 0, " 8888/88/88 88:88:88 ");
		setTableWidth(table);
		int limit = symbol.getLimit();
		return new RowHeaderScrollPane(table, limit, getFormat(limit));
	}

	private void setTableWidth(JTable table) {
		if (0 < rowWidth1) {
			TableUtil.setColumnWidth(table, 1, rowWidth1);
		}
		if (0 < rowWidth2) {
			TableUtil.setColumnWidth(table, 2, rowWidth2);
		}
		Configuration configuration = wifeApplet.getConfiguration();
		boolean isShowSortColumn =
			configuration.getBoolean(
					"org.F11.scada.xwife.applet.alarm.showSortColumn", false);
		if (isShowSortColumn && 0 < rowWidth3) {
			TableUtil.setColumnWidth(table, 3, rowWidth3);
		}
	}

	private String getFormat(int limit) {
		String s = String.valueOf(limit);
		StringBuilder b = new StringBuilder(s.length());
		for (int i = 0; i < s.length(); i++) {
			b.append("0");
		}
		return b.toString();
	}

	private void removeSortColumn(JTable table) {
		Configuration configuration = wifeApplet.getConfiguration();
		boolean isShowSortColumn =
			configuration.getBoolean(
					"org.F11.scada.xwife.applet.alarm.showSortColumn", false);
		if (!isShowSortColumn) {
			TableUtil.removeColumn(table, 3);
		}
	}

	private void setTableCellRenderer(JTable table) {
		TableCellRenderer cellRecderer = new PinpointTableCellRenderer();
		for (int i = table.getColumnCount(); i > 0; i--) {
			DefaultTableColumnModel cmodel =
				(DefaultTableColumnModel) table.getColumnModel();
			TableColumn column = cmodel.getColumn(i - 1);
			column.setCellRenderer(cellRecderer);
		}
	}

	private Component getSouth() {
		Box box = Box.createHorizontalBox();
		box.setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 0));
		box.add(Box.createHorizontalGlue());
		box.add(new CancelButton(this, "����", wifeApplet));
		return box;
	}

	private TableModel createTableModel() {
		List<CareerDto> careerDtos = null;
		try {
			String collectorServer = WifeUtilities.createRmiManagerDelegator();
			DataAccessable accessable =
				(DataAccessable) Naming.lookup(collectorServer);
			careerDtos =
				(List<CareerDto>) accessable.invoke("PinpointService",
						new Object[] {
							symbol.getPinpointHolders(),
							"" + symbol.getLimit() });
		} catch (Exception e) {
			logger.error("�T�[�o�[�ڑ��G���[", e);
			JOptionPane.showMessageDialog(this, "�T�[�o�[�ڑ��G���[\n�����F"
				+ e.getMessage(), "�T�[�o�[�ڑ��G���[", JOptionPane.ERROR_MESSAGE);
		}
		return new PinpoinTableModel(careerDtos);
	}

	@Override
	public void setListIterator(ListIterator listIterator) {
		symbol = (ImageSymbolEditable) listIterator.next();
	}

	/**
	 * �s���|�C���g����\���p�e�[�u���̃Z�������_���[�ł��B����������̃t�H�[�}�b�g�ɕϊ����A7��ڂ̐F�𕶎��ɐݒ肵�܂��B
	 *
	 * @author maekawa
	 *
	 */
	private static class PinpointTableCellRenderer extends
			DefaultTableCellRenderer {
		private static final long serialVersionUID = -8832292724030049502L;
		private final FastDateFormat format =
			FastDateFormat.getInstance("yyyy/MM/dd HH:mm:ss");

		public Component getTableCellRendererComponent(
			JTable table,
			Object value,
			boolean isSelected,
			boolean hasFocus,
			int row,
			int column) {
			super.getTableCellRendererComponent(table, value, isSelected,
					hasFocus, row, column);
			if (value instanceof Date) {
				Date d = (Date) value;
				if (d.after(Globals.EPOCH)) {
					setText(format.format(value));
				} else {
					setText(null);
				}
			}
			TableModel tm = (TableModel) table.getModel();
			String colorName = (String) tm.getValueAt(row, 6);
			setForeground(ColorFactory.getColor(colorName));
			return this;
		}
	}

	/**
	 * Cancel�{�^���N���X�ł��B
	 */
	private static class CancelButton extends JButton implements ActionListener {
		private static final long serialVersionUID = -883935224536772811L;
		private final PinpointDialog dialog;

		public CancelButton(
				PinpointDialog dialog,
				String text,
				PageChanger changer) {
			super(text);
			this.dialog = dialog;
			ActionMapUtil.setActionMap(this, changer);
			addActionListener(this);
		}

		public void actionPerformed(ActionEvent e) {
			dialog.dispose();
		}
	}

	/**
	 * �s���|�C���g����\���p�e�[�u�����f���ł��B
	 *
	 * @author maekawa
	 *
	 */
	private static class PinpoinTableModel extends AbstractTableModel {
		private static final long serialVersionUID = 181311200028509975L;
		private final Logger logger = Logger.getLogger(PinpoinTableModel.class);
		/** �\�� */
		private final String[] title =
			{ "����", "�@��ԍ�", "�@�햼��", "����", "�x��E���", "���", "�F", };
		/** �\�����闚���̃��X�g */
		private final List<CareerDto> careerDto;

		/**
		 * �����̗������X�g�Ń��f���𐶐����܂��B���X�g�� null �̏ꍇ�A��̃��f���𐶐����܂��B
		 *
		 * @param careerDto �����̃��X�g
		 */
		public PinpoinTableModel(List<CareerDto> careerDto) {
			if (null != careerDto) {
				this.careerDto = careerDto;
			} else {
				this.careerDto = Collections.emptyList();
			}
		}

		@Override
		public String getColumnName(int column) {
			return title[column];
		}

		public int getColumnCount() {
			return title.length;
		}

		public int getRowCount() {
			return careerDto.size();
		}

		public Object getValueAt(int rowIndex, int columnIndex) {
			CareerDto dto = careerDto.get(rowIndex);
			switch (columnIndex) {
			case 0:
				return dto.getEntrydate();
			case 1:
				return dto.getUnit();
			case 2:
				return dto.getName();
			case 3:
				return dto.getAttributeName();
			case 4:
				return dto.getMessage();
			case 5:
				return dto.getPriorityName();
			case 6:
				return dto.getColor();
			default:
				logger.error("��ԍ����͈͊O�ł��B" + columnIndex);
				throw new IllegalStateException();
			}
		}
	}
}
