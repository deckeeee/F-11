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

import java.awt.AWTException;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Frame;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.URL;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.Line;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.InputMap;
import javax.swing.JApplet;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.JToolBar;
import javax.swing.JTree;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.ToolTipManager;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.plaf.metal.MetalLookAndFeel;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

import jp.gr.javacons.jim.DataHolder;
import jp.gr.javacons.jim.DataProvider;
import jp.gr.javacons.jim.DataProviderDoesNotSupportException;
import jp.gr.javacons.jim.DataReferencer;
import jp.gr.javacons.jim.DataReferencerOwner;
import jp.gr.javacons.jim.DataValueChangeEvent;
import jp.gr.javacons.jim.DataValueChangeListener;
import jp.gr.javacons.jim.Manager;

import org.F11.scada.EnvironmentManager;
import org.F11.scada.Globals;
import org.F11.scada.Service;
import org.F11.scada.WifeUtilities;
import org.F11.scada.applet.ClientConfiguration;
import org.F11.scada.applet.DataProviderProxy;
import org.F11.scada.applet.DataProviderProxyDefine;
import org.F11.scada.applet.ServerErrorUtil;
import org.F11.scada.applet.symbol.AnimeTimer;
import org.F11.scada.applet.symbol.BasePane;
import org.F11.scada.applet.symbol.Editable;
import org.F11.scada.applet.symbol.GraphicManager;
import org.F11.scada.applet.symbol.ReferencerOwnerSymbol;
import org.F11.scada.applet.symbol.WifeTimer;
import org.F11.scada.data.WifeData;
import org.F11.scada.data.WifeDataDigital;
import org.F11.scada.data.WifeQualityFlag;
import org.F11.scada.parser.AppletFrameDefine;
import org.F11.scada.parser.BasePaneNotFoundException;
import org.F11.scada.parser.alarm.AlarmDefine;
import org.F11.scada.parser.alarm.TitleConfig;
import org.F11.scada.parser.alarm.ToolBar;
import org.F11.scada.parser.tree.TreeDefine;
import org.F11.scada.security.AccessControlable;
import org.F11.scada.security.auth.Subject;
import org.F11.scada.security.auth.login.AuthenticationDialog;
import org.F11.scada.security.auth.login.Authenticationable;
import org.F11.scada.server.operationlog.OperationLoggingService;
import org.F11.scada.theme.DefaultWifeTheme;
import org.F11.scada.util.FontUtil;
import org.F11.scada.util.PageHistory;
import org.F11.scada.util.PageHistoryImpl;
import org.F11.scada.util.RmiUtil;
import org.F11.scada.util.ThreadUtil;
import org.F11.scada.xwife.ClientWindowAdapter;
import org.apache.commons.configuration.Configuration;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.apache.log4j.xml.DOMConfigurator;
import org.xml.sax.SAXException;

/**
 * �N���C�A���g�A�v���b�g�̊��N���X�ł��B
 */
public abstract class AbstractWifeApplet extends JApplet implements
		Authenticationable, PageChanger, AlarmPlayer {
	/** Log4j Logging �I�u�W�F�N�g�̃C���X�^���X�ł� */
	protected Logger logger = Logger.getLogger(AbstractWifeApplet.class);

	/** ��ʒ�`�I�u�W�F�N�g�̎Q�Ƃł��B��` xml �t�@�C�����p�[�X�������ʂ��ێ�����Ă��܂� */
	protected transient AppletFrameDefine frameDef;
	/** �P�̋N�����ꂽ�Ƃ��̃t���O�ł��BWeb Browser �N���Ƃ͕ʓ��e�̉�ʂɂ���ꍇ�Ɏg�p���܂� */
	private transient boolean isStandalone;
	/** ���C����ʂ� JSplitPane �I�u�W�F�N�g�ł� */
	protected transient JSplitPane mainSplit;
	/** ���C����ʂ� JSplitPane �I�u�W�F�N�g�ł� */
	protected transient JSplitPane spane;
	/** �I���c���[�ł� */
	protected transient PageTree tree;
	/** ���O�A�E�g���[�U�[���ł��B */
	private transient String logoutuser;
	/** ���[�U�[��̏�� Subject �I�u�W�F�N�g�ł� */
	protected transient Subject subject;
	/** ���[�U�[�F�؁E���F�I�u�W�F�N�g�̃����[�g�Q�Ƃł� */
	protected transient AccessControlable accessControl;
	/** �ҏW�\�V���{���̃Z�b�g�ł� */
	private transient Set editables;
	/** ��ʃ��b�N�t���O�ł��B */
	private boolean isDisplayLock;
	/** �Đ����̉����ł��B */
	private Clip clip;
	/** �Đ����̉����t�@�C���� */
	private String soundName;
	/** �Đ��֎~�t���O�ł��B */
	private boolean isAlarmSoundLock;
	/** �A�v���b�g�̎Q�ƃJ�E���^�[�ł� */
	private static volatile int referenceCount;
	/** �T�[�o�[�G���[��O */
	private Exception serverError;
	/** �Z�b�V������� */
	protected Session session;
	/** �Q�ƕŗ��� */
	protected PageHistory history;
	/** �N���C�A���g�ݒ� */
	protected Configuration configuration;
	/** ���샍�O�T�[�r�X */
	private OperationLoggingService loggingService;
	/** �c���[�N���b�N�f�o�b�O�̗L�� */
	private boolean isTreeClick;
	/** �^�C�vC�̏ꍇ�� true �łȂ��ꍇ�� false */
	private boolean isAppletTypeC;
	/** �X�v���b�V���X�N���[�� */
	protected final SplashScreen splashScreen;

	static {
		MetalLookAndFeel.setCurrentTheme(new DefaultWifeTheme());
	}

	abstract protected void lookup()
			throws MalformedURLException,
			RemoteException,
			NotBoundException;

	abstract protected void layoutContainer() throws IOException, SAXException;

	/**
	 * �A�v���b�g�����������܂��B���[�U�[��̏��������ŏ���������܂��B
	 */
	public AbstractWifeApplet(boolean isStandalone) throws RemoteException {
		this.isStandalone = isStandalone;
		try {
			clip = (Clip) AudioSystem.getLine(new Line.Info(Clip.class));
		} catch (LineUnavailableException e) {
			logger.error("�����f�o�C�X��������܂���B", e);
		} catch (IllegalArgumentException e) {
			logger.error("�����f�o�C�X��������܂���B", e);
		}

		setLoggerConfig();

		logger.info(System.getProperty("java.vendor"));
		System.out.println("");
		logger.info(System.getProperty("java.version"));
		System.out.println("");
		System.out.println("");
		logger.info(System.getProperty("java.vm.specification.version"));
		logger.info(System.getProperty("java.vm.specification.vendor"));
		logger.info(System.getProperty("java.vm.specification.name"));
		logger.info(System.getProperty("java.vm.version"));
		logger.info(System.getProperty("java.vm.vendor"));
		logger.info(System.getProperty("java.vm.name"));
		logger.info(System.getProperty("os.name"));
		logger.info(System.getProperty("os.version"));
		logger.info("Stand Alone Mode : " + this.isStandalone);

		session = new Session();
		history = new PageHistoryImpl();
		configuration = new ClientConfiguration();
		splashScreen = new SplashScreen(this, configuration);
		boolean splashOn = configuration.getBoolean(
				"org.F11.scada.xwife.applet.splash.on",
				false);
		splashScreen.setVisible(splashOn);
		splashScreen.incrementValue();

		lookupCollector();
		splashScreen.incrementValue();

		if (serverError != null) {
			throw ServerErrorUtil.createException(serverError);
		}
		lookupOperationLogging();

		if (isStandalone) {
			Runtime runtime = Runtime.getRuntime();
			runtime.addShutdownHook(new Thread() {
				public void run() {
					logger.info("JavaVM����~���܂����B");
				}
			});
		}

		splashScreen.incrementValue();
	}

	private void lookupCollector() {
		int maxRetry = Integer.parseInt(EnvironmentManager.get(
				"/server/rmi/collectorserver/retry/count",
				"-1"));
		if (maxRetry < 0) {
			for (int i = 1;; i++) {
				try {
					lookup();
					serverError = null;
					break;
				} catch (Exception e1) {
					logger.info(WifeUtilities.createRmiActionControl()
							+ " retry rmi lookup. (" + i + ")");
					serverError = e1;
					logger.debug(e1);
					ThreadUtil.sleep(Globals.RMI_CONNECTION_RETRY_WAIT_TIME);
					continue;
				}
			}
		} else {
			for (int i = 1; i <= maxRetry; i++) {
				try {
					lookup();
					serverError = null;
					break;
				} catch (Exception e1) {
					logger
							.info(WifeUtilities.createRmiActionControl()
									+ " retry rmi lookup. (" + i + "/"
									+ maxRetry + ")");
					serverError = e1;
					logger.debug(e1);
					ThreadUtil.sleep(Globals.RMI_CONNECTION_RETRY_WAIT_TIME);
					continue;
				}
			}
		}
	}

	private void lookupOperationLogging() {
		loggingService = (OperationLoggingService) RmiUtil
				.lookupServer(OperationLoggingService.class);
	}

	private void setLoggerConfig() {
		if (isStandalone) {
			File file = new File("log");
			file.mkdirs();
			URL url = getClass().getResource("/resources/applet_log4j.xml");
			if (url != null) {
				DOMConfigurator.configure(url);
			} else {
				url = getClass().getResource(
						"/resources/xwife_applet_log4j.properties");
				PropertyConfigurator.configure(url);
			}
		} else {
			URL url = getClass().getResource(
					"/resources/xwife_applet_log4j.properties");
			PropertyConfigurator.configure(url);
		}
	}

	/**
	 * �A�v���b�g�̏����������ł��BL&F�A��ʒ�`�̏����������ł��B
	 */
	public void init() {
		logger.debug("session : " + session);
		ToolTipManager manager = ToolTipManager.sharedInstance();
		manager.setInitialDelay(0);
		manager.setDismissDelay(configuration.getInt(
				"xwife.applet.Applet.dismissDelay",
				10000));
		manager.setReshowDelay(0);

		try {
			logoutuser = accessControl
					.getLogoutUser(InetAddress.getLocalHost());
			Subject s = accessControl.checkAuthentication(logoutuser, "");
			if (s == null) {
				subject = Subject.getNullSubject();
			} else {
				subject = s;
			}

			splashScreen.incrementValue();
			createContainer();
			initSubject();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * �^�C�}�[�C���X�^���X�A�㗝�f�[�^�v���o�C�_�X���b�h�̏��������������܂��B
	 */
	public void start() {
		referenceCount++;
		WifeTimer.getInstance().restart();
		AnimeTimer.getInstance().restart();

		DataProvider[] dps = Manager.getInstance().getDataProviders();
		for (int i = 0; i < dps.length; i++) {
			if (dps[i] instanceof DataProviderProxy)
				((DataProviderProxy) dps[i]).start();
		}
		splashScreen.incrementValue();
		splashScreen.dispose();

		try {
			MetalLookAndFeel.setCurrentTheme(new DefaultWifeTheme());
			UIManager.setLookAndFeel(MetalLookAndFeel.class.getName());
			SwingUtilities.updateComponentTreeUI(this);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (UnsupportedLookAndFeelException e) {
			e.printStackTrace();
		}
	}

	/**
	 * �㗝�f�[�^�v���o�C�_�X���b�h�̒�~�������܂��B
	 */
	public void stop() {
		referenceCount--;
	}

	/** �A�v���b�g�̔j�� */
	public void destroy() {
		if (referenceCount <= 0) {
			DataProvider[] dps = Manager.getInstance().getDataProviders();
			for (int i = 0; i < dps.length; i++) {
				if (dps[i] instanceof Service)
					((Service) dps[i]).stop();
			}

			for (int i = 0; i < dps.length; i++) {
				Manager.getInstance().removeDataProvider(dps[i]);
			}
		}
	}

	/**
	 * ���[�U�[�F�؃_�C�A���O��\�����܂��B�F�؂�����Ɋ��������ꍇ�A���̃C���X�^���X�ɕێ����Ă��� Subject ��؂�ւ���ꂽ Subject
	 * �ɍX�V���܂��B
	 */
	public void showAuthenticationDialog() {
		try {
			AuthenticationDialog dlg = new AuthenticationDialog(WifeUtilities
					.getParentFrame(this));
			Subject sub = dlg.getSubject();
			if (sub != null) {
				this.subject = sub;
				fireSubjectChanged();
				changeTree();
				loggingService.login(sub.getUserName(), InetAddress
						.getLocalHost().getHostAddress(), new Timestamp(System
						.currentTimeMillis()));
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	private void initSubject() {
		try {
			Subject s = accessControl.checkAuthentication(logoutuser, "");
			if (s == null) {
				subject = Subject.getNullSubject();
			} else {
				subject = s;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		fireSubjectChanged();
		changeTree();
	}

	/**
	 * ���O�A�E�g���������܂��B WIFE �ł� Subject ���́A�v�����V�p�����N���A���āA������Ԃɖ߂��܂��B
	 */
	public void logout() {
		try {
			Subject oldSubject = this.subject;
			initSubject();
			loggingService.logout(oldSubject.getUserName(), InetAddress
					.getLocalHost().getHostAddress(), new Timestamp(System
					.currentTimeMillis()));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * ���j���[�c���[�����[�U�[�p�ɐ؂�ւ��܂��B ���ݕ\�����y�[�W���\���\�Ȃ�Εێ����܂��B
	 */
	private void changeTree() {
		String lastKey = null;
		TreeDefine treeDefine = null;
		synchronized (tree) {
			lastKey = tree.getLastPageKey();
			treeDefine = frameDef.getMenuTreeRoot(subject.getUserName());
			tree.setRootTreeNode(treeDefine.getRootNode());
		}

		final TreePath lastPath = searchTreePath(lastKey);
		final TreePath path = searchTreePath(treeDefine.getInitPage());
		if (path != null) {
			SwingUtilities.invokeLater(new Runnable() {
				public void run() {
					if (lastPath != null) {
						changeTree(lastPath);
					} else {
						changeTree(path);
					}
				}
			});
		}
	}

	private void changeTree(TreePath path) {
		synchronized (tree) {
			tree.setSelectionPath(path);
			tree.expandPath(path);
			tree.requestFocusInWindow();
		}
	}

	/**
	 * �ҏW�\�V���{�������X�i�[�o�^���܂��B���X�i�[�o�^���ꂽ�V���{���́A���[�U�[�F��
	 * ���s���ă��[�U�[���ύX���ꂽ�ꍇ�ɁA�������g�̕ҏW�\�t���O���X�V���܂��B
	 * 
	 * @param symbol �ҏW�\�V���{���I�u�W�F�N�g
	 */
	public void addEditable(Editable symbol) {
		if (editables == null) {
			editables = new HashSet();
		}
		editables.add(symbol);
	}

	/**
	 * �ҏW�\�V���{�������X�i�[����폜���܂��B
	 * 
	 * @param symbol �ҏW�\�V���{���I�u�W�F�N�g
	 */
	public void removeEditable(Editable symbol) {
		if (editables == null) {
			return;
		}
		editables.remove(symbol);
	}

	public Subject getSubject() {
		return subject;
	}

	/**
	 * �w��L�[�̃y�[�W��\�����܂��B
	 */
	public void changePage(final PageChangeEvent pageChange) {
		// �����؊�������ʃ��b�N���[�h�Ȃ珈�����Ȃ��B
		if (pageChange.isAuto() && isDisplayLock()) {
			return;
		}

		final TreePath path = searchTreePath(pageChange.getKey(), pageChange
				.getArgv());
		if (path != null) {
			if (SwingUtilities.isEventDispatchThread()) {
				changeTree(pageChange, path);
			} else {
				SwingUtilities.invokeLater(new Runnable() {
					public void run() {
						changeTree(pageChange, path);
					}
				});
			}
		}
	}

	private void changeTree(PageChangeEvent pageChange, TreePath path) {
		synchronized (tree) {
			tree.setSelectionPath(path, pageChange);
			tree.expandPath(path);
			tree.requestFocusInWindow();
		}
	}

	/**
	 * �ݒ肳��Ă����ʃ��b�N���[�h�̏�Ԃ�Ԃ��܂��B
	 * 
	 * @return ���b�N���[�h�Ȃ� true�A�����łȂ��Ȃ� false
	 */
	public boolean isDisplayLock() {
		return isDisplayLock;
	}

	/**
	 * ��ʃ��b�N���[�h��ݒ肵�܂��B
	 * 
	 * @param isDisplayLock ��ʃ��b�N����ꍇ�� true�A�����łȂ��ꍇ�� false
	 */
	public void setDisplayLock(boolean isDisplayLock) {
		this.isDisplayLock = isDisplayLock;
	}

	/**
	 * ���[�U�[�F�؂̌��ʁA���[�U�[�̕ύX���ꂽ���Ƃ����X�i�[�ɒʒm���܂��B
	 */
	private void fireSubjectChanged() {
		if (serverError != null) {
			return;
		}

		if (editables == null)
			return;
		List destList = new ArrayList(editables.size());
		for (Iterator it = editables.iterator(); it.hasNext();) {
			String[] destitem = ((Editable) it.next()).getDestinations();
			destList.add(destitem);
		}
		String[][] dests = (String[][]) destList.toArray(new String[0][0]);

		List ret = null;
		for (int i = 1; i <= Globals.RMI_CONNECTION_RETRY_COUNT; i++) {
			try {
				ret = accessControl.checkPermission(subject, dests);
				serverError = null;
				break;
			} catch (RemoteException ex) {
				try {
					lookup();
				} catch (Exception e) {
					serverError = e;
				}
				continue;
			}
		}

		if (serverError != null) {
			ServerErrorUtil.invokeServerError();
			return;
		}

		for (Iterator it = ret.iterator(), eit = editables.iterator(); it
				.hasNext()
				&& eit.hasNext();) {
			Editable ed = (Editable) eit.next();
			Boolean[] permissions = (Boolean[]) it.next();
			setEditable(ed, permissions);
		}
	}

	private void setEditable(Editable editable, Boolean[] booleans) {
		boolean[] b = new boolean[booleans.length];
		for (int i = 0; i < b.length; i++) {
			b[i] = booleans[i].booleanValue();
		}
		editable.setEditable(b);
	}

	private void createContainer()
			throws SAXException,
			IOException,
			MalformedURLException,
			RemoteException,
			DataProviderDoesNotSupportException {
		DataProviderProxyDefine proxyDefine = new DataProviderProxyDefine(
				session,
				this);
		splashScreen.incrementValue();
		frameDef = new AppletFrameDefine(this, this, proxyDefine);
		if (isReceiveCache()) {
			frameDef.receiveCache(session);
		}
		splashScreen.incrementValue();

		AlarmDataProviderProxy adpp = new AlarmDataProviderProxy();
		adpp.start();
		splashScreen.incrementValue();

		layoutContainer();
		splashScreen.incrementValue();

		treeClicker();
	}

	private void treeClicker() {
		if (isTreeClick
				|| configuration.getBoolean("test.enable.treeclicker", false)) {
			new TreeClicker();
		}
	}

	private boolean isReceiveCache() {
		return configuration.getBoolean(
				"parser.AppletFrameDefine.receiveCache",
				false);
	}

	/**
	 * @return
	 */
	protected Box createBandFButton() {
		JButton treeBack = new JButton(GraphicManager.get(configuration
				.getString(
						"BandFButton.backIcon",
						"/toolbarButtonGraphics/navigation/Back16.gif")));
		Dimension preferredSize = new Dimension(configuration.getInt(
				"BandFButton.width",
				20), configuration.getInt("BandFButton.height", 20));
		treeBack.setPreferredSize(preferredSize);
		treeBack.setToolTipText("�߂�");
		JButton treeForward = new JButton(GraphicManager.get(configuration
				.getString(
						"BandFButton.forwardIcon",
						"/toolbarButtonGraphics/navigation/Forward16.gif")));
		treeForward.setPreferredSize(preferredSize);
		treeForward.setToolTipText("�i��");

		treeBack.addActionListener(PageHistoryActionFactory.createBackAction(
				history,
				this));
		treeForward.addActionListener(PageHistoryActionFactory
				.createForwardAction(history, this));

		Box treeBox = Box.createHorizontalBox();
		treeBox.add(treeBack);
		treeBox.add(Box.createGlue());
		treeBox.add(treeForward);
		return treeBox;
	}

	/**
	 * ����c���[����key���������A���݂����TreePath��Ԃ��܂��B
	 * 
	 * @param key �L�[������
	 * @return �������ʂ�TreePath�B���������񂪑��݂��Ȃ����null��Ԃ��܂��B
	 */
	protected TreePath searchTreePath(String key) {
		return searchTreePath(key, null);
	}

	protected TreePath searchTreePath(String key, Object argv) {
		synchronized (tree) {
			DefaultMutableTreeNode root = (DefaultMutableTreeNode) tree
					.getModel().getRoot();
			for (Enumeration e = root.depthFirstEnumeration(); e
					.hasMoreElements();) {
				DefaultMutableTreeNode node = (DefaultMutableTreeNode) e
						.nextElement();
				TreeNode[] tn = node.getPath();
				StringBuffer buffer = new StringBuffer();
				for (int i = 0; i < tn.length; i++) {
					buffer.append("/");
					buffer.append(tn[i].toString());
				}
				PageTreeNode ptn = (PageTreeNode) node.getUserObject();
				if (ptn.getKey().equals(key)) {
					return new PageTreePath(node.getPath(), argv);
				}
			}
		}
		return null;
	}

	/**
	 * ���j���[�ƃc�[���o�[���Ǘ�����N���X�B �I�����ꂽ�c���[���ApageMap ���烁�j���[�A�c�[���o�[�I�u�W�F�N�g�����o���A
	 * ���ꂼ��̃R���|�[�l���g��\�����܂��B
	 */
	protected static class WifeToolBar extends JPanel implements
			TreeSelectionListener {
		private static final long serialVersionUID = 8077802722148750798L;
		/** pageMap �I�u�W�F�N�g�̎Q�� */
		private final AbstractWifeApplet applet;
		/** �X���b�h�v�[�� */
		private final ExecutorService executorService = Executors
				.newSingleThreadExecutor();
		private final JLabel label = new JLabel("Now Loading...");
		private Future future = new FutureTask(new Runnable() {
			public void run() {
			}
		}, null);

		private BasePane currentBase = BasePane.DUMMY_PAGE;

		private int currentDividerLocation;

		private final Logger logger = Logger.getLogger(WifeToolBar.class);

		/**
		 * �R���X�g���N�^
		 * 
		 * @param pageMap �y�[�W�}�b�v�I�u�W�F�N�g
		 * @param session �Z�b�V�������
		 */
		WifeToolBar(AbstractWifeApplet applet) {
			super(new BorderLayout());
			this.applet = applet;
			FontUtil.setFont("serif", "plain", "36", label);
		}

		/**
		 * �c���[�̃N���b�N���󂯎��A���j���[�ƃc�[���o�[��ݒ肵�����B
		 */
		public void valueChanged(TreeSelectionEvent e) {
			JTree tree = (JTree) e.getSource();
			DefaultMutableTreeNode selNode = (DefaultMutableTreeNode) tree
					.getLastSelectedPathComponent();
			Object argv = null;
			if (PageTreePath.class.isInstance(e.getNewLeadSelectionPath())) {
				PageTreePath path = (PageTreePath) e.getNewLeadSelectionPath();
				argv = path.getArgv();
			}

			if (isChangePage(selNode)) {
				currentDividerLocation = applet.spane.getDividerLocation();
				applet.spane.setDividerLocation(currentDividerLocation);
				applet.spane.setRightComponent(label);
				cancelFuture();
				synchronized (future) {
					future = executorService.submit(new PageChangeTask(
							selNode,
							argv));
				}
			}
		}

		private boolean isChangePage(DefaultMutableTreeNode selNode) {
			if (null != selNode) {
				String key = ((PageTreeNode) selNode.getUserObject()).getKey();
				synchronized (currentBase) {
					return (BasePane.DUMMY_PAGE == currentBase || !currentBase
							.getPageName().equals(key))
							&& selNode.isLeaf();
				}
			} else {
				return false;
			}
		}

		private void cancelFuture() {
			synchronized (future) {
				if (!future.isCancelled() || !future.isDone()) {
					future.cancel(false);
				}
			}
		}

		/**
		 * �����y�[�W��\�����܂��B
		 */
		private void showInitPage() {
			synchronized (currentBase) {
				currentBase = BasePane.DUMMY_PAGE;
			}
			TreeDefine treeDefine = applet.frameDef
					.getMenuTreeRoot(applet.subject.getUserName());
			final TreePath path = applet.searchTreePath(treeDefine
					.getInitPage());
			if (path != null) {
				SwingUtilities.invokeLater(new Runnable() {
					public void run() {
						synchronized (applet.tree) {
							applet.tree.setSelectionPath(path);
							applet.tree.expandPath(path);
							applet.tree.requestFocusInWindow();
						}
					}
				});
			}
		}

		private class PageChangeTask implements Runnable {
			private final Logger logger = Logger
					.getLogger(PageChangeTask.class);
			private final PageTreeNode pgnode;
			private final Object argv;

			PageChangeTask(DefaultMutableTreeNode selNode, Object argv) {
				pgnode = (PageTreeNode) selNode.getUserObject();
				this.argv = argv;
			}

			public void run() {
				try {
					logger.info("session : " + applet.session);
					if (null != argv) {
						final Map pageMap = applet.frameDef.getPage(pgnode
								.getKey(), applet.session, argv);
						setMain(pageMap);
					} else {
						final Map pageMap = applet.frameDef.getPage(pgnode
								.getKey(), applet.session);
						setMain(pageMap);
					}
				} catch (BasePaneNotFoundException bex) {
					logger.error("page not found from server.", bex);
					JOptionPane.showMessageDialog(
							applet,
							pgnode + "�y�[�W���T�[�o�[�ɂ���܂���ł����B\n�����\���y�[�W��\�����܂��B",
							"Page not found from server",
							JOptionPane.ERROR_MESSAGE);
					showInitPage();
				}
			}

			private void setMain(final Map pageMap) {
				SwingUtilities.invokeLater(new Runnable() {
					public void run() {
						removeAll();
						add(
								(JComponent) pageMap
										.get(AppletFrameDefine.ITEM_KEY_TOOLBAR),
								BorderLayout.SOUTH);
						revalidate();
						repaint();

						synchronized (currentBase) {
							if (isDestroy()) {
								currentBase.destroyPage();
							}
							currentBase = (BasePane) pageMap
									.get(AppletFrameDefine.ITEM_KEY_PANE);
							currentBase.revalidate();
							applet.spane.setRightComponent(currentBase);
						}
						applet.spane.setDividerLocation(currentDividerLocation);
						applet.spane.revalidate();
						applet.fireSubjectChanged();
					};
				});
			}

			private boolean isDestroy() {
				return (BasePane.DUMMY_PAGE != currentBase)
						&& (!currentBase.isCache());
			}
		}
	}

	/**
	 * �S��ʋ��ʂ̃c�[���o�[���Ǘ�����N���X�B
	 */
	protected static class SystemToolBar extends JToolBar {
		private static final long serialVersionUID = -4914267991421051617L;
		private final Logger logger = Logger.getLogger(SystemToolBar.class);

		/**
		 * �R���X�g���N�^
		 * 
		 * @param pageMap �y�[�W�}�b�v�I�u�W�F�N�g
		 */
		SystemToolBar(AbstractWifeApplet wifeApplet) {
			super();
			setFloatable(false);

			add(new StopAlarmButton(wifeApplet));
			addSeparator();

			JLabel userNameLabel = new JLabel();
			userNameLabel.setFont(new Font("serif", Font.PLAIN, 22));
			userNameLabel
					.setBorder(BorderFactory.createLineBorder(Color.black));
			userNameLabel.setPreferredSize(new Dimension(200, 30));
			userNameLabel.setMinimumSize(new Dimension(200, 30));
			userNameLabel.setMaximumSize(new Dimension(200, 30));

			if (isDisplayToolBar(wifeApplet)) {
				JButton changeUser = new ChangeUser(wifeApplet, userNameLabel);
				add(changeUser);

				add(userNameLabel);
				addSeparator();
			}

			add(new ScreenLockButton(wifeApplet));
			add(new AlarmSoundLockButton(wifeApplet));
			addSeparator();
		}

		private boolean isDisplayToolBar(AbstractWifeApplet wifeApplet) {
			AlarmDefine ad = null;
			try {
				ad = new AlarmDefine();
			} catch (Exception e) {
				e.printStackTrace();
				logger.error("AlarmDefine.xml is invalid.");
				return true;
			}

			if (wifeApplet.isStandalone) {
				return true;
			}

			ToolBar tb = ad.getAlarmConfig().getToolBar();
			return ad == null || tb == null || tb.isDisplayLogin();
		}

		private static class StopAlarmButton extends JButton {
			private static final long serialVersionUID = -7423103742279766042L;
			private final Logger logger = Logger
					.getLogger(StopAlarmButton.class);
			private static byte[] TRUE_DATA = { (byte) 0xFF, (byte) 0xFF };
			private String writeProvider;
			private String writeHolder;

			StopAlarmButton(final AbstractWifeApplet wifeApplet) {
				super(GraphicManager.get("/images/sndstop.png"));
				setToolTipText("�x�񉹒�~");
				getWrtiteHolder(wifeApplet);
				initKeyEvent(wifeApplet);
				createStopAlarmButtonListener(wifeApplet);
			}

			private void initKeyEvent(final AbstractWifeApplet wifeApplet) {
				String stopKey = wifeApplet.configuration.getString(
						"xwife.applet.Applet.alarmStopKey",
						"F12");
				Action key = new AbstractAction(stopKey) {
					private static final long serialVersionUID = -2668128777789593884L;

					public void actionPerformed(ActionEvent e) {
						wifeApplet.stopAlarm();
						writeAlarmButton(wifeApplet);
						logger.info("�x�񉹒�~");
					}
				};

				// associate action with key
				InputMap imap = getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
				imap.put(KeyStroke.getKeyStroke(stopKey), key
						.getValue(Action.NAME));
				ActionMap amap = getActionMap();
				amap.put(key.getValue(Action.NAME), key);
				addActionListener(key);
			}

			private void getWrtiteHolder(final AbstractWifeApplet wifeApplet) {
				String value = wifeApplet.configuration.getString(
						"xwife.applet.Applet.alarmStopKey.write",
						"");
				if (!"".equals(value)) {
					int p = value.indexOf('_');
					if (0 < p) {
						writeProvider = value.substring(0, p);
						writeHolder = value.substring(p + 1);
						logger.info("�x�񉹒�~�{�^���������݃z���_�� " + writeProvider + "_"
								+ writeHolder + " �Ɋ���t���܂���");
					}
				}
			}

			private void writeAlarmButton(AbstractWifeApplet wifeApplet) {
				DataHolder dh = Manager.getInstance().findDataHolder(
						writeProvider,
						writeHolder);
				if (dh != null) {
					WifeData wd = (WifeData) dh.getValue();
					if (wd instanceof WifeDataDigital) {
						writeDigital(dh, wd);
					} else {
						logger.error("�f�W�^���ȊO�̃f�[�^���w�肳��Ă��܂��B : " + writeProvider
								+ "_" + writeHolder);
					}
				} else {
					if (null != writeProvider && null != writeHolder) {
						logger.warn(writeProvider + "_" + writeHolder
								+ " ���o�^����Ă��܂���");
					}
				}
			}

			private void writeDigital(DataHolder dh, WifeData wd) {
				WifeDataDigital dd = (WifeDataDigital) wd;
				dh.setValue(
						(WifeData) dd.valueOf(TRUE_DATA),
						new Date(),
						WifeQualityFlag.GOOD);
				try {
					dh.syncWrite();
				} catch (Exception e) {
					logger.error("�f�W�^���f�[�^�������݃G���[", e);
				}
			}

			private void createStopAlarmButtonListener(
					AbstractWifeApplet wifeApplet) {
				String value = wifeApplet.configuration.getString(
						"xwife.applet.Applet.alarmStopKey.event",
						"");
				if (!"".equals(value)) {
					new StopAlarmButtonListener(value, this);
				}
			}

			private static class StopAlarmButtonListener implements
					DataValueChangeListener, DataReferencerOwner,
					ReferencerOwnerSymbol {
				private final Logger logger = Logger
						.getLogger(StopAlarmButtonListener.class);
				private final JButton button;
				private DataReferencer referencer;

				StopAlarmButtonListener(String value, JButton button) {
					this.button = button;
					connectReferencer(value);
				}

				private void connectReferencer(String value) {
					int p = value.indexOf('_');
					if (0 < p) {
						String provider = value.substring(0, p);
						String holder = value.substring(p + 1);
						referencer = new DataReferencer(provider, holder);
						referencer.connect(this);
						logger.info("�x�񉹒�~�{�^���� " + provider + "_" + holder
								+ " �Ɋ���t���܂���");
					}
				}

				public void disConnect() {
					referencer.disconnect(this);
				}

				public void dataValueChanged(DataValueChangeEvent evt) {
					Object o = evt.getSource();
					if (o instanceof DataHolder) {
						DataHolder dh = (DataHolder) o;
						Object value = dh.getValue();
						if (value instanceof WifeDataDigital) {
							WifeDataDigital dd = (WifeDataDigital) value;
							if (dd.isOnOff(true)) {
								button.doClick();
							}
						}
					}
				}

				public Class[][] getReferableDataHolderTypeInfo(
						DataReferencer dr) {
					return new Class[][] { { DataHolder.class, WifeData.class } };
				}
			}
		}

		private static class ChangeUser extends JButton {
			private static final long serialVersionUID = 837365258467076509L;

			ChangeUser(
					final AbstractWifeApplet wifeApplet,
					final JLabel userNameLabel) {
				super(GraphicManager.get("/images/user.png"));
				setToolTipText("���O�C��");

				addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent evt) {
						if (wifeApplet.subject.getUserName().equals(
								wifeApplet.logoutuser)) {
							wifeApplet.showAuthenticationDialog();
						} else {
							wifeApplet.logout();
						}

						if (wifeApplet.subject.getUserName().equals(
								wifeApplet.logoutuser)) {
							setToolTipText("���O�C��");
							setIcon(GraphicManager.get("/images/user.png"));
							userNameLabel.setText("");
						} else {
							setToolTipText("���O�A�E�g");
							setIcon(GraphicManager
									.get("/images/logoutuser.png"));
							userNameLabel.setText(wifeApplet.subject
									.getUserName());
						}
					}
				});
			}
		}

		private static class ScreenLockButton extends JButton {
			private static final long serialVersionUID = 695040666580116852L;

			ScreenLockButton(final AbstractWifeApplet wifeApplet) {
				super(GraphicManager.get("/images/sclock.png"));
				setToolTipText("������ʐؑ֋֎~");

				addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent evt) {
						if (wifeApplet.isDisplayLock()) {
							wifeApplet.setDisplayLock(false);
							setToolTipText("������ʐؑ֋֎~");
							setIcon(GraphicManager.get("/images/sclock.png"));
						} else {
							wifeApplet.setDisplayLock(true);
							setToolTipText("������ʐؑ֋֎~����");
							setIcon(GraphicManager.get("/images/scunlock.png"));
						}
					}
				});
			}
		}

		private static class AlarmSoundLockButton extends JButton {
			private static final long serialVersionUID = 8692460921260246988L;

			AlarmSoundLockButton(final AbstractWifeApplet wifeApplet) {
				super(GraphicManager.get("/images/sdlock.png"));
				setToolTipText("�x�񉹋֎~");

				addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent evt) {
						if (wifeApplet.isAlarmSoundLock()) {
							wifeApplet.setAlarmSoundLock(false);
							setToolTipText("�x�񉹋֎~");
							setIcon(GraphicManager.get("/images/sdlock.png"));
						} else {
							wifeApplet.setAlarmSoundLock(true);
							setToolTipText("�x�񉹋֎~����");
							setIcon(GraphicManager.get("/images/sdunlock.png"));
						}
					}
				});
			}
		}
	}

	/**
	 * �w�肳�ꂽURL�̉��������[�v�Đ����܂�
	 * 
	 * @param path
	 */
	public void playAlarm(String path) {
		if (isNotStartAlarm(path)) {
			return;
		}

		URL url = getClass().getResource(path);
		if (url == null) {
			logger.error("Sound file nothing! " + path);
			return;
		}
		try {
			if (null != clip && (!isSameSound(path) || !clip.isRunning())) {
				AudioInputStream stream = AudioSystem.getAudioInputStream(url);
				if (clip.isOpen()) {
					clip.close();
				}
				clip.open(stream);
				stream.close();
				clip.loop(Clip.LOOP_CONTINUOUSLY);
			}
		} catch (UnsupportedAudioFileException e) {
			logger.error("�Đ��ł��Ȃ��T�E���h�t�@�C���`���ł��B", e);
		} catch (IOException e) {
			logger.error("�T�E���h�t�@�C���̓ǂݍ��݂Ɏ��s���܂����B", e);
		} catch (LineUnavailableException e) {
			logger.error("�Đ��ł��Ȃ��T�E���h�t�@�C���`���ł��B", e);
		}
	}

	private boolean isNotStartAlarm(String path) {
		return isAlarmSoundLock() || path == null || path.length() <= 0;
	}

	private boolean isSameSound(String path) {
		if (null != soundName && soundName.equals(path)) {
			soundName = path;
			return true;
		} else {
			return false;
		}
	}

	/**
	 * �Đ����̉����~���܂�
	 */
	public void stopAlarm() {
		if (null != clip && clip.isRunning()) {
			clip.stop();
			clip.setFramePosition(0);
		} else {
			logger.error("�T�E���h�N���b�v�I�u�W�F�N�g�� null �ł��B");
		}
	}

	/**
	 * �ݒ肳��Ă���Đ��֎~���[�h�̏�Ԃ�Ԃ��܂��B
	 * 
	 * @return �Đ��֎~���[�h�Ȃ� true�A�����łȂ��Ȃ� false
	 */
	public boolean isAlarmSoundLock() {
		return isAlarmSoundLock;
	}

	/**
	 * �Đ��֎~���[�h��ݒ肵�܂��B
	 * 
	 * @param isDisplayLock �Đ��֎~����ꍇ�� true�A�����łȂ��ꍇ�� false
	 */
	public void setAlarmSoundLock(boolean isAlarmSoundLock) {
		this.isAlarmSoundLock = isAlarmSoundLock;
	}

	/**
	 * ���̃I�u�W�F�N�g���A�v���P�[�V�����Ƃ��āA�N������Ă��� ���ǂ�����Ԃ��܂��B
	 * 
	 * @return �A�v���P�[�V�����Ƃ��ċN������Ă���ꍇ�� true �� �����łȂ��ꍇ�� false ��Ԃ��܂��B
	 */
	public boolean isStandAlone() {
		return isStandalone;
	}

	protected static String getTitle() {
		AlarmDefine alarmDefine = new AlarmDefine();
		TitleConfig config = alarmDefine.getAlarmConfig().getTitleConfig();
		return config != null ? config.getText() : "F-11";
	}

	/**
	 * ����{�^���̋�����ݒ肵�܂��B
	 * 
	 * @param frame �e�t���[��
	 */
	protected static void setCloseAction(JFrame frame, AbstractWifeApplet applet) {
		if (isClose(applet)) {
			frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		} else {
			frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
			frame.addWindowListener(new ClientWindowAdapter(frame));
		}
	}

	/**
	 * ClientConfiguration.xml�̐ݒ���A����{�^���̋�����Ԃ��܂��B
	 * 
	 * @return ClientConfiguration.xml�̐ݒ���A����{�^���̋�����Ԃ��܂��B
	 */
	protected static boolean isClose(AbstractWifeApplet applet) {
		ClientConfiguration configuration = new ClientConfiguration();
		return !applet.isStandalone
				|| configuration
						.getBoolean("xwife.applet.Applet.isClose", true);
	}

	/**
	 * {@link Robot Robot}�N���X���g�p���āA�V�t�g�L�[���������܂��B�X�N���[���Z�[�o�[�� �����Ɏg�p���܂��B
	 */
	public void pressShiftKey() {
		if (isStandalone) {
			try {
				if (configuration.getBoolean(
						"xwife.applet.Applet.screenSaver",
						false)) {
					Robot robot = new Robot();
					robot.keyPress(KeyEvent.VK_SHIFT);
					robot.keyRelease(KeyEvent.VK_SHIFT);
				}
			} catch (AWTException e) {
				logger.error("Not initialized Robot Object.", e);
			}

		}
	}

	public Configuration getConfiguration() {
		return configuration;
	}

	public void setTreeClick(boolean b) {
		isTreeClick = b;
	}

	void setFrameBounds(Frame frame) {
		int x = configuration.getInt("xwife.applet.Applet.frame.location.x", 0);
		int y = configuration.getInt("xwife.applet.Applet.frame.location.y", 0);
		int width = configuration.getInt(
				"xwife.applet.Applet.frame.size.width",
				1152);
		int height = configuration.getInt(
				"xwife.applet.Applet.frame.size.height",
				864);
		Rectangle r = new Rectangle(x, y, width, height);
		frame.setBounds(r);
	}

	public boolean isAppletTypeC() {
		return isAppletTypeC;
	}

	public void setAppletTypeC(boolean isAppletTypeC) {
		this.isAppletTypeC = isAppletTypeC;
	}
}
