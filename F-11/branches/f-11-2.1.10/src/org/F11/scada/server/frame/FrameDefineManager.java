/*
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
package org.F11.scada.server.frame;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.WeakHashMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

import jp.gr.javacons.jim.DataHolder;
import jp.gr.javacons.jim.DataProvider;
import jp.gr.javacons.jim.DataProviderDoesNotSupportException;
import jp.gr.javacons.jim.Manager;

import org.F11.scada.EnvironmentManager;
import org.F11.scada.Globals;
import org.F11.scada.WifeUtilities;
import org.F11.scada.parser.tree.TreeDefine;
import org.F11.scada.server.ScheduleHolderOwner;
import org.F11.scada.server.alarm.table.PointTableBean;
import org.F11.scada.server.dao.ItemDao;
import org.F11.scada.server.entity.Item;
import org.F11.scada.server.io.ItemUtil;
import org.F11.scada.server.io.StrategyUtility;
import org.F11.scada.server.register.HolderRegisterBuilder;
import org.F11.scada.server.register.HolderString;
import org.F11.scada.util.ConcurrentHashSet;
import org.F11.scada.util.ConnectionUtil;
import org.F11.scada.util.SingletonSortedMap;
import org.F11.scada.xwife.applet.Session;
import org.F11.scada.xwife.server.WifeDataProvider;
import org.apache.log4j.Logger;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

/**
 * �y�[�W��`���Ǘ�����N���X�ł��B
 * 
 * @author hori
 */
public class FrameDefineManager extends UnicastRemoteObject implements
		FrameDefineHandler, FramePageEditTimeSupport, JimRegister, FrameDefine,
		SendRequestSupport {

	private static final long serialVersionUID = 9182991988246291052L;
	private static final int SCHEDULE_DATA_TYPE = 16;
	/** �|�C���g���̕�����ϊ� �J�n������ */
	private static final String POINT_NAME_BRA = "$(";
	/** �|�C���g���̕�����ϊ� �I�������� */
	private static final String POINT_NAME_CKET = ")";
	/** �|�C���g���� �Z�p���[�^������ */
	private static final int POINT_NAME_SEPA = '_';
	/** SQL�����񃆁[�e�B���e�B�[ */
	private final StrategyUtility utility;
	/** �y�[�W���ƃy�[�W�P�ʂ�XML������̃}�b�v�ł� */
	private final Map pageMap;
	/** �X�e�[�^�X�o�[�I�u�W�F�N�g�̎Q�Ƃł� */
	private PageDefine statusbar;
	// TODO �N���C�A���g�ڑ����ɐ�����݂��邱�ƁB
	/** ���M�v�������L�[�ɂ��āA�Z�b�V�������i�[�����}�b�v */
	private final SortedMap sendRequestDateMap;
	/** �Z�b�V�����ƃN���C�A���g���ŏI�v������PageID�̃}�b�v */
	private final Map clientPageMap;
	/** DIcon */
	private final HolderRegisterBuilder builder;
	/** ���j���[�c���[��`�̊Ǘ��N���X�ł��B */
	private TreeDefineManager treeDefineManager;
	private static Logger log = Logger.getLogger(FrameDefineManager.class);
	private List cachePageNames = new ArrayList();
	private Lock lock;
	private Condition condition;
	private final Map pointNameCache;
	private ScheduleHolderOwner scheduleHolderOwner;
	private ItemUtil itemUtil;
	private final Set cacheHolderSet = new ConcurrentHashSet();
	private ItemDao itemDao;
	private Map noSystemItems;

	/**
	 * �R���X�g���N�^
	 * 
	 * @param recvPort RMI�V���A���C�Y�I�u�W�F�N�g����M�̃|�[�g�ԍ�
	 */
	public FrameDefineManager(String recvPort, HolderRegisterBuilder builder)
			throws RemoteException, MalformedURLException, IOException {
		this(
				Integer.parseInt(recvPort),
				"/resources/XWifeAppletDefine.xml",
				builder);
	}

	/**
	 * �R���X�g���N�^
	 * 
	 * @param recvPort RMI�V���A���C�Y�I�u�W�F�N�g����M�̃|�[�g�ԍ�
	 */
	public FrameDefineManager(int recvPort, HolderRegisterBuilder builder)
			throws RemoteException, MalformedURLException, IOException {
		this(recvPort, "/resources/XWifeAppletDefine.xml", builder);
	}

	public FrameDefineManager(
			int port,
			String path,
			HolderRegisterBuilder builder) throws RemoteException,
			MalformedURLException, IOException {

		super(port);

		treeDefineManager = new TreeDefineManager();

		utility = new StrategyUtility();
		Naming.rebind(WifeUtilities.createRmiFrameDefineManager(), this);

		pageMap = new ConcurrentHashMap();
		sendRequestDateMap = Collections
				.synchronizedSortedMap(new SingletonSortedMap());
		clientPageMap = new ConcurrentHashMap();
		this.builder = builder;
		pointNameCache = new WeakHashMap();

		InputStream stream = null;
		try {
			// �p�[�X
			XMLReader parser = XMLReaderFactory
					.createXMLReader(EnvironmentManager.get(
							"/org.xml.sax.driver",
							""));
			parser.setContentHandler(new F11PageHandler(this));
			stream = getClass().getResourceAsStream(path);
			InputSource is = new InputSource(stream);
			parser.parse(is);
			stream.close();
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (stream != null) {
				stream.close();
			}
		}
		long timeout = Long.parseLong(EnvironmentManager.get(
				"/server/pagetimeout",
				"600000"));
		PageTimeout pt = new PageTimeout(timeout);
		pt.schedule(this);
	}

	public void setScheduleHolderOwner(ScheduleHolderOwner scheduleHolderOwner) {
		this.scheduleHolderOwner = scheduleHolderOwner;
	}

	public void setItemUtil(ItemUtil itemUtil) {
		this.itemUtil = itemUtil;
	}

	public void setItemDao(ItemDao itemDao) {
		this.itemDao = itemDao;
	}

	public void init() {
		log.info("Item Map �����J�n");
		Item[] items = itemDao.getNoSystemItems();
		noSystemItems = new HashMap(items.length);
		for (int i = 0; i < items.length; i++) {
			setNoSystemItems(items[i]);
		}
		log.info("Item Map �����I��");
	}

	private void setNoSystemItems(Item item) {
		noSystemItems.put(getHolderString(item), item);
	}

	private HolderString getHolderString(Item item) {
		return new HolderString(item.getProvider(), item.getHolder());
	}

	/**
	 * �y�[�W��`��o�^���܂��B
	 * 
	 * @param name �y�[�W��
	 * @param xml ��`
	 */
	public void setPageString(String name, String xml) {
		PageDefine page = new PageDefine(System.currentTimeMillis(), xml);
		pageMap.put(name, page);
	}

	/**
	 * �X�e�[�^�X�o�[�o�^��ǉ����܂��B
	 * 
	 * @param xml ��`
	 */
	public void setStatusbar(String xml) {
		statusbar = new PageDefine(System.currentTimeMillis(), xml);
	}

	public void setCondition(Condition condition) {
		this.condition = condition;
	}

	public void setLock(Lock lock) {
		this.lock = lock;
	}

	/**
	 * �y�[�W��`��xml������ŕԂ��܂��B
	 * 
	 * @param name �y�[�W��
	 * @return xml��`
	 */
	protected String getPageString(String name) {
		PageDefine page = (PageDefine) pageMap.get(name);
		return page.getSrcXml();
	}

	/**
	 * �X�e�[�^�X�o�[��`��xml������ŕԂ��܂��B
	 * 
	 * @return
	 */
	protected String getStatusbarString() {
		return statusbar.getSrcXml();
	}

	/**
	 * �y�[�W���̃Z�b�g��Ԃ��܂��B
	 * 
	 * @return
	 */
	protected Set getPageNameSet() {
		return pageMap.keySet();
	}

	/**
	 * key�Ŏw�肳�ꂽ�����ȍ~�Ƀy�[�W��`���ύX����Ă���΁AXML�Œ�`��Ԃ��܂��B
	 * 
	 * @param name �y�[�W��
	 * @param key �X�V����
	 * @return String �y�[�W��`��XML�\���B�ύX�������̓y�[�W�������̏ꍇnull
	 */
	private PageDefine getPage(String name, long key) {
		PageDefine page = (PageDefine) pageMap.get(name);
		if (page != null && key < page.getEditTime()) {
			return new PageDefine(page.getEditTime(), replaceAllPointName(page
					.getSrcXml()));
		}
		return null;
	}

	/*
	 * (Javadoc �Ȃ�)
	 * 
	 * @see org.F11.scada.server.frame.FrameDefineHandler#getPage(java.lang.String,
	 *      long, java.net.InetAddress)
	 */
	public synchronized PageDefine getPage(
			String name,
			long key,
			Session session) {
		log.info("getPage 1");
		PageDefine pd = getPage(name, key);
		log.info("getPage 2");
		List wdps = getWifeDataProviders(pd, session);
		log.info("getPage 3");
		providerLock(wdps);
		log.info("getPage 4");
		try {
			checkUnregisterJim(session);
			log.info("getPage 5");
			sendRequestDateMap.put(new Long(key), session);
			if (log.isDebugEnabled()) {
				log.debug("key=" + getKeyString(key) + " value="
						+ sendRequestDateMap.get(new Long(key)));
			}
			clientPageMap.put(session, name);
			log.info("getPage 6");
			registerJim(pd);
			log.info("getPage 7");
		} finally {
			providerUnlock(wdps, pd);
			log.info("getPage 8");
		}
		return pd;
	}

	private List<DataProvider> getWifeDataProviders(PageDefine pd, Session session) {
		HashSet<String> set = new HashSet<String>();
		getRemoveProvider(session, set);
		getNextProvider(pd, set);
		ArrayList<DataProvider> wdps = new ArrayList<DataProvider>();
		Manager manager = Manager.getInstance();
		for (Iterator<String> i = set.iterator(); i.hasNext();) {
			String dpname = i.next();
			DataProvider dataProvider = manager.getDataProvider(dpname);
			if (null != dataProvider) {
				wdps.add(dataProvider);
			} else {
				log.error("�T�[�o�[�ɑ��݂��Ȃ��v���o�C�_���y�[�W�ɒ�`����Ă��܂� = " + dpname);
			}
		}
		return wdps;
	}

	private void getRemoveProvider(Session session, HashSet set) {
		if (clientPageMap.containsKey(session)) {
			String pname = (String) clientPageMap.get(session);
			if (pageMap.containsKey(pname)) {
				PageDefine define = (PageDefine) pageMap.get(pname);
				if (!define.isCache()) {
					getNextProvider(define, set);
				}
			}
		}
	}

	private void getNextProvider(PageDefine pd, HashSet set) {
		if (null != pd) {
			Set nextPageHolders = pd.getDataHolders();
			for (Iterator i = nextPageHolders.iterator(); i.hasNext();) {
				HolderString hs = (HolderString) i.next();
				if (!set.contains(hs.getProvider())) {
					set.add(hs.getProvider());
				}
			}
		}
	}

	private void providerLock(List wdps) {
		for (Iterator i = wdps.iterator(); i.hasNext();) {
			WifeDataProvider wdp = (WifeDataProvider) i.next();
			wdp.lock();
		}
	}

	private void providerUnlock(List wdps, PageDefine define) {
		for (Iterator i = wdps.iterator(); i.hasNext();) {
			WifeDataProvider wdp = (WifeDataProvider) i.next();
			wdp.unlock();
		}
	}

	private void checkUnregisterJim(Session session) {
		if (clientPageMap.containsKey(session)) {
			String pname = (String) clientPageMap.get(session);
			if (pageMap.containsKey(pname)) {
				PageDefine clientPage = (PageDefine) pageMap.get(pname);
				if (!clientPage.isCache()) {
					log.debug("unregisterJim");
					unregisterJim(session);
				} else {
					log.debug("not unregisterJim");
				}
			}
		}
	}

	/**
	 * ���̃N���C�A���g���J���Ă���y�[�W�̃z���_�[��NOR���Ƃ�JIM����폜�B
	 * 
	 * @param address ���M���N���C�A���g��IP�A�h���X
	 * @throws RemoteException
	 */
	private void unregisterJim(Session session) {
		if (!clientPageMap.containsKey(session)) {
			return;
		}
		String pname = (String) clientPageMap.remove(session);
		unregisterJim(pname);
	}

	private void unregisterJim(String pname) {
		if (pageMap.containsKey(pname)) {
			PageDefine define = (PageDefine) pageMap.get(pname);
			unregisterJim(define);
		}
	}

	private void unregisterJim(PageDefine define) {
		unregisterJim(define, false);
	}

	private void unregisterJim(PageDefine define, boolean isDeploy) {
		Set deleteHolders = define.getDataHolders();
		unregisterJim(deleteHolders, isDeploy);
	}

	public synchronized void unregisterJim(Set holders, boolean isDeploy) {
		if (!isDeploy) {
			removeClientPage(holders);
			removeCachePage(holders);
		}

		Manager manager = Manager.getInstance();
		Item[] items = removeSystem(holders, true);
		for (int i = 0; i < items.length; i++) {
			Item item = items[i];
			DataProvider dp = manager.getDataProvider(item.getProvider());
			// logger.debug("remove holder : " + item.getHolder());
			DataHolder dh = dp.getDataHolder(item.getHolder());
			if (isRemoveHolder(dp, dh)) {
				scheduleHolderOwner.removeScheduleHolder(item.getPoint());
			}
		}
		builder.unregister(items);
	}

	private void removeClientPage(Set deleteHolders) {
		for (Iterator it = clientPageMap.values().iterator(); it.hasNext();) {
			String name = (String) it.next();
			PageDefine pd = getPage(name, 0L);
			deleteHolders.removeAll(pd.getDataHolders());
		}
	}

	private void removeCachePage(Set deleteHolders) {
		deleteHolders.removeAll(cacheHolderSet);
	}

	private boolean isRemoveHolder(DataProvider dp, DataHolder dh) {
		return dh != null && dp.canRemoveDataHolderByUser(dh)
				&& !Globals.ERR_HOLDER.equals(dh.getDataHolderName());
	}

	private Item[] removeSystem(Collection col, boolean removeMode) {
		if (col.size() <= 0) {
			return new Item[0];
		}
		ArrayList holders = new ArrayList(col.size());
		for (Iterator it = col.iterator(); it.hasNext();) {
			HolderString h = (HolderString) it.next();
			if (removeMode || hasNotDataHolder(h)) {
				holders.add(h);
			}
		}

		if (holders.size() <= 0) {
			return new Item[0];
		}

		return getItems(holders);
	}

	private Item[] getItems(ArrayList holders) {
		ArrayList newItems = new ArrayList(holders.size());
		for (Iterator i = holders.iterator(); i.hasNext();) {
			HolderString hs = (HolderString) i.next();
			if (noSystemItems.containsKey(hs)) {
				Item item = (Item) noSystemItems.get(hs);
				if (item.getDataType() == SCHEDULE_DATA_TYPE) {
					item = itemDao.getItem(new HolderString(item
							.getProvider(), item.getHolder()));
				}
				newItems.add(item);
			}
		}
		return (Item[]) newItems.toArray(new Item[0]);
	}

	private boolean hasNotDataHolder(HolderString hs) {
		DataHolder dh = Manager.getInstance().findDataHolder(
				hs.getProvider(),
				hs.getHolder());
		if (null == dh) {
			return true;
		}
		return false;
	}

	/**
	 * PageDefine#getDataHolders��Set(HolderString)�I�u�W�F�N�g�Ńf�[�^�z���_�[�𐶐���JIM�ɓo�^����B
	 * 
	 * @param define
	 */
	private synchronized void registerJim(PageDefine define) {
		if (define != null) {
			if (define.isCache()) {
				cacheHolderSet.addAll(define.getDataHolders());
			}
			addDataHolders(define.getDataHolders());
		}
	}

	public synchronized void addDataHolders(Set holderSet) {
		// logger.info("Add holders : " + holderSet);
		log.info("addDataHolders 1");
		Item[] items = removeSystem(holderSet, false);
		log.info("addDataHolders 2");
		builder.register(items);
		log.info("addDataHolders 3");
		getInitialData(items);
		log.info("addDataHolders 4");
	}

	private void getInitialData(Item[] items) {
		Manager manager = Manager.getInstance();
		Map itemMap = itemUtil.getItemMap(items);
		try {
			for (Iterator i = itemMap.entrySet().iterator(); i.hasNext();) {
				Map.Entry entry = (Map.Entry) i.next();
				String provider = (String) entry.getKey();
				List holders = (List) entry.getValue();
				DataProvider dp = manager.getDataProvider(provider);
				dp.syncRead(getDataHolders(manager, holders));
			}
		} catch (DataProviderDoesNotSupportException e) {
			log.error("���B���Ȃ��͂��̃G���[", e);
		}
	}

	private DataHolder[] getDataHolders(Manager manager, List holders) {
		DataHolder[] holderArray = new DataHolder[holders.size()];
		int index = 0;
		for (Iterator i = holders.iterator(); i.hasNext(); index++) {
			Item item = (Item) i.next();
			holderArray[index] = manager.findDataHolder(
					item.getProvider(),
					item.getHolder());
		}
		return holderArray;
	}

	/**
	 * key�Ŏw�肳�ꂽ�����ȍ~�ɃX�e�[�^�X�o�[��`���ύX����Ă���΁AXML�Œ�`��Ԃ��܂��B
	 * 
	 * @param key �X�V����
	 * @return String �X�e�[�^�X�o�[��`��XML�\���B�ύX�����̏ꍇnull
	 */
	public PageDefine getStatusbar(long key) throws RemoteException {
		if (statusbar != null && key < statusbar.getEditTime()) {
			return statusbar;
		}
		return null;
	}

	/**
	 * ���[�U�[���̃��j���[�c���[��Ԃ��܂��B �w�胆�[�U�[�Ƀ��j���[��`��������΁A�f�t�H���g�̃��j���[�c���[��Ԃ��܂��B
	 * 
	 * @param user ���[�U�[��
	 * @return ���j���[�c���[�̒�`
	 * @throws RemoteException
	 */
	public TreeDefine getMenuTreeRoot(String user) throws RemoteException {
		return treeDefineManager.getMenuTreeRoot(user);
	}

	public TreeDefineManager getTreeDefineManager() {
		return treeDefineManager;
	}

	private String replaceAllPointName(String src) {
		StringBuffer sb = new StringBuffer();
		int sp = src.indexOf(POINT_NAME_BRA);
		if (sp < 0)
			return src;

		int cp = 0;
		while (0 <= sp) {
			sb.append(src.substring(cp, sp));
			sp += 2;
			int ep = src.indexOf(POINT_NAME_CKET, sp);
			if (ep < 0)
				break;

			sb.append(findPointName(src.substring(sp, ep)));
			cp = ep + 1;
			sp = src.indexOf(POINT_NAME_BRA, cp);
		}
		sb.append(src.substring(cp));
		return sb.toString();
	}

	private String findPointName(String tag) {
		if (pointNameCache.containsKey(tag)) {
			return (String) pointNameCache.get(tag);
		} else {
			String pointName = replacePointName(tag);
			pointNameCache.put(tag, pointName);
			return pointName;
		}
	}

	private String replacePointName(String tag) {
		int p = tag.indexOf(POINT_NAME_SEPA);
		if (p < 0)
			throw new NoSuchElementException(tag);

		int no = Integer.parseInt(tag.substring(0, p));
		String key = tag.substring(p + 1);

		Connection con = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		try {
			con = ConnectionUtil.getConnection();
			stmt = con.prepareStatement(utility
					.getPrepareStatement("/pointtable/read"));
			stmt.setInt(1, no);
			rs = stmt.executeQuery();
			if (!rs.next()) {
				throw new NoSuchElementException(tag);
			}

			String ret = rs.getString(key);

			return ret;
		} catch (SQLException e) {
			log.error("�|�C���g�ϊ��G���[ : " + tag, e);
			return POINT_NAME_BRA + tag + POINT_NAME_CKET;
		} finally {
			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException e) {
				}
			}
			if (stmt != null) {
				try {
					stmt.close();
				} catch (SQLException e) {
				}
			}
			if (con != null) {
				try {
					con.close();
				} catch (SQLException e) {
				}
			}
		}
	}

	/**
	 * �����̃}�b�v�I�u�W�F�N�g��S��put���܂�
	 * 
	 * @param map �y�[�W���̂ƃy�[�W��`�I�u�W�F�N�g�̃}�b�v
	 * @since 1.0.3
	 */
	public void putAll(Map map) {
		unregisterPages(map);
		pageMap.putAll(map);
		registerPages(map);
	}

	private void unregisterPages(Map map) {
		for (Iterator i = map.keySet().iterator(); i.hasNext();) {
			String pageName = (String) i.next();
			if (pageMap.containsKey(pageName)) {
				PageDefine pageDefine = (PageDefine) pageMap.get(pageName);
				if (pageDefine.isCache()) {
					unregisterJim(pageDefine);
				}
			}
		}
	}

	private void registerPages(Map map) {
		synchronized (cachePageNames) {
			for (Iterator i = map.entrySet().iterator(); i.hasNext();) {
				Map.Entry entry = (Map.Entry) i.next();
				PageDefine pageDefine = (PageDefine) entry.getValue();
				if (pageDefine.isCache()) {
					registerJim(pageDefine);
					cachePageNames.add(entry.getKey());
				}
			}
		}
	}

	/**
	 * �w�肵�����̂̃y�[�W��`���폜���܂�
	 * 
	 * @param pageName �폜����y�[�W����
	 * @return �폜����y�[�W�I�u�W�F�N�g
	 * @since 1.0.3
	 */
	public Object removePageString(String pageName) {
		PageDefine pageDefine = (PageDefine) pageMap.get(pageName);
		unregisterJim(pageDefine, true);
		return (PageDefine) pageMap.remove(pageName);
	}

	/**
	 * �����̃y�[�W���̂����ɓo�^����Ă���� true �������łȂ���� false ��Ԃ��܂�
	 * 
	 * @param pageName ���肷��y�[�W����
	 * @return �����̃y�[�W���̂����ɓo�^����Ă���� true �������łȂ���� false ��Ԃ��܂�
	 * @since 1.0.3
	 */
	public boolean containsKey(String pageName) {
		return pageMap.containsKey(pageName);
	}

	/**
	 * ���̃|�C���g���܂܂��I�u�W�F�N�g��ݒ肵�܂��B
	 * 
	 * @param nb �V�|�C���g���
	 * @param ob ���|�C���g���
	 * @since 1.0.3
	 */
	public void setPageEditTime(PointTableBean nb, PointTableBean ob)
			throws RemoteException {
		if (nb == null) {
			throw new IllegalArgumentException("Point is null.");
		}

		for (Iterator it = pageMap.entrySet().iterator(); it.hasNext();) {
			Map.Entry entry = (Map.Entry) it.next();
			String key = (String) entry.getKey();
			PageDefine value = (PageDefine) entry.getValue();
			String xml = value.getSrcXml();
			if (isChange(nb, xml)) {
				pageMap.put(
						key,
						new PageDefine(System.currentTimeMillis(), xml));
				pointNameCache.remove(nb.getPoint() + "_" + nb.getUnit());
				pointNameCache.remove(nb.getPoint() + "_" + nb.getName());
				pointNameCache.remove(nb.getPoint() + "_" + nb.getMark());
				// logger.info("Page Timestamp modified. page = " + key);
			}
		}
	}

	private boolean isChange(PointTableBean nb, String xml) {
		return xml.indexOf("$(" + nb.getPoint()) > 0;
	}

	public synchronized void setSendRequestDateMap(Session session, long time) {
		sendRequestDateMap.put(new Long(time), session);
		if (log.isDebugEnabled()) {
			log.debug("key=" + getKeyString(time) + " value="
					+ sendRequestDateMap.get(new Long(time)));
		}
	}

	public List getCachePages() {
		if (lock == null || condition == null) {
			throw new IllegalStateException("lock or condition is null");
		}
		lock.lock();
		List pages = Collections.EMPTY_LIST;
		try {
			condition.await();
			synchronized (cachePageNames) {
				pages = new ArrayList(cachePageNames);
			}
		} catch (InterruptedException e) {
		} finally {
			lock.unlock();
		}
		return pages;
	}

	public synchronized void removePages(long currentTime) {
		SortedMap removeMap = new TreeMap(sendRequestDateMap.headMap(new Long(
				currentTime)));
		for (Iterator i = removeMap.entrySet().iterator(); i.hasNext();) {
			Map.Entry entry = (Map.Entry) i.next();
			Long key = (Long) entry.getKey();
			if (0 != key.longValue()) {
				Session session = (Session) entry.getValue();
				if (log.isInfoEnabled()) {
					log.info(" -- Page Removed : "
							+ new Timestamp(key.longValue()) + " session : "
							+ session);
				}
				unregisterJim(session);
				sendRequestDateMap.remove(key);
			}
		}
	}

	private String getKeyString(long t) {
		SimpleDateFormat f = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		return f.format(new Date(t));
	}

	synchronized boolean isSendRequestDateMap(Object key) {
		return sendRequestDateMap.containsKey(key);
	}

	Map getPointNameCache() {
		return pointNameCache;
	}
}
