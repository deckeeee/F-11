package org.F11.scada.xwife.applet.alarm;

import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumn;
import javax.swing.table.TableModel;

public class RowHeaderTable extends JTable {
	public RowHeaderTable(JTable dataTable, String name, int width) {
		super(new RowHeaderDataModel(dataTable));
		// �B��̗�
		TableColumn tc = new TableColumn(0, width);

		// ���������E�w�i�D�F�̃����_���[��o�^����
		DefaultTableCellRenderer r = new DefaultTableCellRenderer();
		r.setHorizontalAlignment(SwingConstants.CENTER);
		r.setBackground(dataTable.getTableHeader().getBackground());
		tc.setCellRenderer(r);

		tc.setHeaderValue(name); // �񌩏o��(���ږ�)
		tc.setResizable(false); // �T�C�Y�ύX�֎~
		JTableHeader h = super.getTableHeader();
		h.setReorderingAllowed(false); // ��̓���ւ�(�h���b�O)���֎~

		addColumn(tc);
		setEnabled(false);
	}

	private static class RowHeaderDataModel extends DefaultTableModel implements
			TableModelListener {
		protected JTable dataTable;

		public RowHeaderDataModel(JTable dataTable) {
			this.dataTable = dataTable; // �f�[�^�p�e�[�u��

			TableModel dataModel = dataTable.getModel();
			dataModel.removeTableModelListener(this);
			dataModel.addTableModelListener(this);
		}

		@Override
		public Object getValueAt(int row, int column) {
			// �f�[�^�p�e�[�u���̍s�ԍ���Ԃ�
			// RowHeaderTable���g�̓\�[�g��t�B���^�����O�͍s���Ȃ��ׁA����row�͕\���p�s�ԍ��ƈ�v���Ă���
			return row + 1;
		}

		@Override
		public Class<?> getColumnClass(int columnIndex) {
			return Integer.class;
		}

		@Override
		public void setValueAt(Object aValue, int row, int column) {
			// �l�̓o�^�͍s��Ȃ�
		}

		@Override
		public boolean isCellEditable(int row, int column) {
			return false; // �ҏW�s��
		}

		@Override
		public int getRowCount() {
			if (dataTable != null) {
				return dataTable.getRowCount(); // �f�[�^�e�[�u���̕\���s����Ԃ�
			}
			return 0;
		}

		/*
		 * TableModelListener�̃��\�b�h�ł���A�f�[�^�pTableModel�ɕύX���������Ƃ��ɌĂ΂��B
		 */
		public void tableChanged(TableModelEvent e) {
			switch (e.getType()) {
			case TableModelEvent.INSERT: // �s�ǉ�
			case TableModelEvent.DELETE: // �s�폜
				super.fireTableChanged(e);
				break;
			default:
				// System.out.println("tableChanged:" + e.getType());
				break;
			}
		}
	}
}
