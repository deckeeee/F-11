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
		// 唯一の列
		TableColumn tc = new TableColumn(0, width);

		// 中央揃え・背景灰色のレンダラーを登録する
		DefaultTableCellRenderer r = new DefaultTableCellRenderer();
		r.setHorizontalAlignment(SwingConstants.CENTER);
		r.setBackground(dataTable.getTableHeader().getBackground());
		tc.setCellRenderer(r);

		tc.setHeaderValue(name); // 列見出し(項目名)
		tc.setResizable(false); // サイズ変更禁止
		JTableHeader h = super.getTableHeader();
		h.setReorderingAllowed(false); // 列の入れ替え(ドラッグ)を禁止

		addColumn(tc);
		setEnabled(false);
	}

	private static class RowHeaderDataModel extends DefaultTableModel implements
			TableModelListener {
		protected JTable dataTable;

		public RowHeaderDataModel(JTable dataTable) {
			this.dataTable = dataTable; // データ用テーブル

			TableModel dataModel = dataTable.getModel();
			dataModel.removeTableModelListener(this);
			dataModel.addTableModelListener(this);
		}

		@Override
		public Object getValueAt(int row, int column) {
			// データ用テーブルの行番号を返す
			// RowHeaderTable自身はソートやフィルタリングは行われない為、引数rowは表示用行番号と一致している
			return row + 1;
		}

		@Override
		public Class<?> getColumnClass(int columnIndex) {
			return Integer.class;
		}

		@Override
		public void setValueAt(Object aValue, int row, int column) {
			// 値の登録は行わない
		}

		@Override
		public boolean isCellEditable(int row, int column) {
			return false; // 編集不可
		}

		@Override
		public int getRowCount() {
			if (dataTable != null) {
				return dataTable.getRowCount(); // データテーブルの表示行数を返す
			}
			return 0;
		}

		/*
		 * TableModelListenerのメソッドであり、データ用TableModelに変更があったときに呼ばれる。
		 */
		public void tableChanged(TableModelEvent e) {
			switch (e.getType()) {
			case TableModelEvent.INSERT: // 行追加
			case TableModelEvent.DELETE: // 行削除
				super.fireTableChanged(e);
				break;
			default:
				// System.out.println("tableChanged:" + e.getType());
				break;
			}
		}
	}
}
