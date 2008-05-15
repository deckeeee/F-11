/*
 * 作成日: 2008/02/19 TODO この生成されたファイルのテンプレートを変更するには次へジャンプ: ウィンドウ - 設定 - Java -
 * コード・スタイル - コード・テンプレート
 */
package org.F11.scada.server.formula;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.SortedSet;

import javax.swing.event.TableModelListener;

import jp.gr.javacons.jim.DataHolder;
import jp.gr.javacons.jim.DataProvider;
import jp.gr.javacons.jim.DataProviderDoesNotSupportException;
import jp.gr.javacons.jim.DataReferencer;
import jp.gr.javacons.jim.Manager;
import junit.framework.TestCase;

import org.F11.scada.WifeUtilities;
import org.F11.scada.data.HolderData;
import org.F11.scada.data.WifeData;
import org.F11.scada.data.WifeDataDigital;
import org.F11.scada.data.WifeQualityFlag;
import org.F11.scada.server.alarm.AlarmDataStore;
import org.F11.scada.server.alarm.AlarmReferencer;
import org.F11.scada.server.dao.ItemDao;
import org.F11.scada.server.entity.Item;
import org.F11.scada.server.formula.dto.ItemFormulaDto;
import org.F11.scada.server.frame.SendRequestSupport;
import org.F11.scada.server.register.HolderRegister;
import org.F11.scada.server.register.HolderRegisterBuilder;
import org.F11.scada.server.register.HolderString;
import org.F11.scada.server.register.impl.DigitalHolderRegister;
import org.F11.scada.test.util.TestUtil;
import org.F11.scada.xwife.applet.Session;
import org.F11.scada.xwife.server.WifeDataProvider;

public class FormulaDataProviderImplTest extends TestCase {
	private FormulaDataProviderImpl dp;
	private DataProvider dummy_dp;

	protected void setUp() throws Exception {
		dummy_dp = TestUtil.createDigitalDataProvider();
		Manager.getInstance().addDataProvider(dummy_dp);
		dp = new FormulaDataProviderImpl(new TestItemDao(),
				new TestHolderRegisterBuilder(), new TestAlarmReferencer(),
				new TestAlarmReferencer(), new TestItemFormulaService());
		dp.setSendRequestSupport(new TestSendRequestSupport());
		dp.start();
	}

	protected void tearDown() throws Exception {
		TestUtil.crearJIM();
	}

	public void testGetDataHolder() throws Exception {
		DataHolder dh = dp.getDataHolder("D_1900000_Digital");
		assertNotNull(dh);
	}

	public void testAddDataHolder() throws Exception {
		DataHolder dh = new DataHolder();
		dh.setValueClass(WifeData.class);
		dh.setDataHolderName("D_1900001_Digital");
		dh.setValue(WifeDataDigital.valueOfFalse(1), new Date(),
				WifeQualityFlag.INITIAL);
		dh.setParameter(WifeDataProvider.PARA_NAME_CYCLEREAD, Boolean.TRUE);
		dp.addDataHolder(dh);
		assertNotNull(dp.getDataHolder("D_1900001_Digital"));
	}

	public void testNotSupport() throws Exception {
		DataHolder dh = dp.getDataHolder("D_1900000_Digital");
		assertNotNull(dh);
		try {
			dh.syncRead();
			fail();
		} catch (DataProviderDoesNotSupportException e) {
		}
		try {
			dh.syncWrite();
			fail();
		} catch (DataProviderDoesNotSupportException e) {
		}
		try {
			dh.asyncRead();
			fail();
		} catch (DataProviderDoesNotSupportException e) {
		}
		try {
			dh.asyncWrite();
			fail();
		} catch (DataProviderDoesNotSupportException e) {
		}
	}

	public void testFormulaDataHolder() throws Exception {
		DataHolder dh = dp.getDataHolder("D_1900000_Digital");
		WifeDataDigital d = (WifeDataDigital) dh.getValue();
		assertEquals(WifeDataDigital.valueOfFalse(0), d);
		Session session = new Session();
		List list = dp.getHoldersData(0, session);
		assertNotNull(list);
		assertEquals(0, list.size());

		DataHolder dummy10_dh = dummy_dp.getDataHolder("D_1900010_Digital");
		DataHolder dummy11_dh = dummy_dp.getDataHolder("D_1900011_Digital");
		// bit10=off,bit11=off
		dummy10_dh.setValue(WifeDataDigital.valueOfFalse(10), new Date(),
				WifeQualityFlag.GOOD);
		dummy11_dh.setValue(WifeDataDigital.valueOfFalse(11), new Date(),
				WifeQualityFlag.GOOD);
		list = dp.getHoldersData(0, session);
		assertNotNull(list);
		assertEquals(0, list.size());

		// bit10=on,bit11=off
		dummy10_dh.setValue(WifeDataDigital.valueOfTrue(10), new Date(),
				WifeQualityFlag.GOOD);
		list = dp.getHoldersData(0, session);
		assertNotNull(list);
		assertEquals(1, list.size());
		HolderData hd = (HolderData) list.get(0);
		assertNull(hd.getDemandData());
		assertEquals("D_1900000_Digital", hd.getHolder());
		assertEquals("0001", WifeUtilities.toString(hd.getValue()));

		// bit10=off,bit11=off
		dummy10_dh.setValue(WifeDataDigital.valueOfFalse(10), new Date(),
				WifeQualityFlag.GOOD);
		list = dp.getHoldersData(0, session);
		assertNotNull(list);
		assertEquals(1, list.size());
		hd = (HolderData) list.get(0);
		assertNull(hd.getDemandData());
		assertEquals("D_1900000_Digital", hd.getHolder());
		assertEquals("0000", WifeUtilities.toString(hd.getValue()));

		// bit10=off,bit11=on
		dummy11_dh.setValue(WifeDataDigital.valueOfTrue(11), new Date(),
				WifeQualityFlag.GOOD);
		list = dp.getHoldersData(0, session);
		assertNotNull(list);
		assertEquals(1, list.size());
		hd = (HolderData) list.get(0);
		assertNull(hd.getDemandData());
		assertEquals("D_1900000_Digital", hd.getHolder());
		assertEquals("0001", WifeUtilities.toString(hd.getValue()));

		// bit10=off,bit11=off
		dummy11_dh.setValue(WifeDataDigital.valueOfFalse(11), new Date(),
				WifeQualityFlag.GOOD);
		list = dp.getHoldersData(0, session);
		assertNotNull(list);
		assertEquals(1, list.size());
		hd = (HolderData) list.get(0);
		assertNull(hd.getDemandData());
		assertEquals("D_1900000_Digital", hd.getHolder());
		assertEquals("0000", WifeUtilities.toString(hd.getValue()));
	}

	// ///////////////////////////////////////////////////////
	class TestItemDao implements ItemDao {

		public Item getItem(HolderString holderString) {
			return getItem();
		}

		public Item[] getSystemItems(String provider, boolean system) {
			return new Item[]{getItem()};
		}

		private Item getItem() {
			Item item = new Item();
			item.setPoint(new Integer(0));
			item.setProvider("FORMULA");
			item.setHolder("D_1900000_Digital");
			item.setDataArgv("00");
			item.setComCycle(0);
			item.setComCycleMode(true);
			item.setComMemoryAddress(100);
			item.setComMemoryKinds(0);
			item.setOffDelay(new Integer(10));
			return item;
		}

		public Item selectItem(String provider, String holder) {
			return getItem();
		}

		public int updateItem(Item item) {
			return 1;
		}

		public Item[] getNoSystemItems() {
			return null;
		}

	}

	class TestHolderRegisterBuilder implements HolderRegisterBuilder {
		private HolderRegister hr = new DigitalHolderRegister();

		public void register(Item[] items) {
			for (int i = 0; i < items.length; i++) {
				hr.register(items[i]);
			}
		}

		public void unregister(Item[] items) {
			for (int i = 0; i < items.length; i++) {
				hr.unregister(items[i]);
			}
		}
	}

	class TestAlarmReferencer implements AlarmReferencer {

		public boolean addDataStore(AlarmDataStore store) {
			return false;
		}

		public void addReferencer(DataReferencer rf) {
		}

		public void removeReferencer(DataReferencer dr) {
		}

		public SortedSet getReferencers() {
			return null;
		}

		public void addTableModelListener(TableModelListener l) {
		}

		public Class<String> getColumnClass(int columnIndex) {
			return null;
		}

		public int getColumnCount() {
			return 0;
		}

		public String getColumnName(int columnIndex) {
			return null;
		}

		public int getRowCount() {
			return 0;
		}

		public Object getValueAt(int rowIndex, int columnIndex) {
			return null;
		}

		public boolean isCellEditable(int rowIndex, int columnIndex) {
			return false;
		}

		public void removeTableModelListener(TableModelListener l) {
		}

		public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
		}

	}

	class TestSendRequestSupport implements SendRequestSupport {
		public void setSendRequestDateMap(Session session, long time) {
		}
	}

	class TestItemFormulaService implements ItemFormulaService {
		public List<ItemFormulaDto> findAll() {
			List<ItemFormulaDto> ret = new ArrayList<ItemFormulaDto>();
			ItemFormulaDto formula = new ItemFormulaDto();
			formula.setHolder("D_1900000_Digital");
			formula.setFormula("P1_D_1900010_Digital || P1_D_1900011_Digital");
			ret.add(formula);
			return ret;
		}
	}
}
