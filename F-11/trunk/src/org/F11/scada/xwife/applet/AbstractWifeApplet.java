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
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Rectangle;
import java.awt.Robot;
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
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Timer;
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
import javax.swing.Box;
import javax.swing.JApplet;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.JTree;
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

import jp.gr.javacons.jim.DataProvider;
import jp.gr.javacons.jim.DataProviderDoesNotSupportException;
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
import org.F11.scada.applet.symbol.WifeTimer;
import org.F11.scada.parser.AppletFrameDefine;
import org.F11.scada.parser.BasePaneNotFoundException;
import org.F11.scada.parser.alarm.AlarmDefine;
import org.F11.scada.parser.alarm.TitleConfig;
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
import org.F11.scada.xwife.applet.alarm.ActionTimerTask;
import org.F11.scada.xwife.applet.alarm.LimitedAction;
import org.apache.commons.configuration.Configuration;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.apache.log4j.xml.DOMConfigurator;
import org.xml.sax.SAXException;

/**
 * クライアントアプレットの基底クラスです。
 */
public abstract class AbstractWifeApplet extends JApplet implements
		Authenticationable, PageChanger, AlarmPlayer, LimitedAction {
	private static final long serialVersionUID = 1465754515897744531L;
	/** Log4j Logging オブジェクトのインスタンスです */
	protected Logger logger = Logger.getLogger(AbstractWifeApplet.class);

	/** 画面定義オブジェクトの参照です。定義 xml ファイルをパースした結果が保持されています */
	protected transient AppletFrameDefine frameDef;
	/** 単体起動されたときのフラグです。Web Browser 起動とは別内容の画面にする場合に使用します */
	private transient boolean isStandalone;
	/** メイン画面の JSplitPane オブジェクトです */
	protected transient JSplitPane mainSplit;
	/** メイン画面の JSplitPane オブジェクトです */
	protected transient JSplitPane spane;
	/** 選択ツリーです */
	protected transient PageTree tree;
	/** ログアウトユーザー名です。 */
	protected transient String logoutuser;
	/** ユーザー主体情報 Subject オブジェクトです */
	protected transient Subject subject;
	/** ユーザー認証・承認オブジェクトのリモート参照です */
	protected transient AccessControlable accessControl;
	/** 編集可能シンボルのセットです */
	private transient Set editables;
	/** 画面ロックフラグです。 */
	private boolean isDisplayLock;
	/** 再生中の音源です。 */
	private Clip clip;
	/** 再生中の音声ファイル名 */
	private String soundName;
	/** 再生禁止フラグです。 */
	private boolean isAlarmSoundLock;
	/** アプレットの参照カウンターです */
	private static volatile int referenceCount;
	/** サーバーエラー例外 */
	private Exception serverError;
	/** セッション情報 */
	protected Session session;
	/** 参照頁履歴 */
	protected PageHistory history;
	/** クライアント設定 */
	protected Configuration configuration;
	/** 操作ログサービス */
	private OperationLoggingService loggingService;
	/** ツリークリックデバッグの有無 */
	private boolean isTreeClick;
	/** タイプCの場合は true でない場合は false */
	private boolean isAppletTypeC;
	/** スプラッシュスクリーン */
	protected final SplashScreen splashScreen;
	/** 起動時にサウンドオフ */
	private final boolean soundoffAtStarted;
	/** スクリーンセーバー解除のタイマー */
	private Timer screenSaverTimer = new Timer(true);
	/** スクリーンセーバー解除の受付フラグ */
	private boolean isCheck = true;
	/** スクリーンセーバー解除の受付間隔(1分) */
	private static final long SCREEN_SAVER_TIME = 60000L;

	static {
		MetalLookAndFeel.setCurrentTheme(new DefaultWifeTheme());
	}

	abstract protected void lookup()
		throws MalformedURLException,
		RemoteException,
		NotBoundException;

	abstract protected void layoutContainer() throws IOException, SAXException;

	/**
	 * アプレットを初期化します。ユーザー主体情報もここで初期化されます。
	 */
	public AbstractWifeApplet(boolean isStandalone, boolean soundoffAtStarted)
			throws RemoteException {
		this.isStandalone = isStandalone;
		this.soundoffAtStarted = soundoffAtStarted;
		try {
			clip = (Clip) AudioSystem.getLine(new Line.Info(Clip.class));
		} catch (LineUnavailableException e) {
			logger.error("音源デバイスが見つかりません。", e);
		} catch (IllegalArgumentException e) {
			logger.error("音源デバイスが見つかりません。", e);
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
		logger.info("SessionID : " + session.toString());
		history = new PageHistoryImpl();
		configuration = new ClientConfiguration();
		splashScreen = new SplashScreen(this, configuration);
		boolean splashOn =
			configuration.getBoolean("org.F11.scada.xwife.applet.splash.on",
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
					logger.info("JavaVMが停止しました。 SessionID:"
						+ session.toString());
				}
			});
		}

		splashScreen.incrementValue();
	}

	private void lookupCollector() {
		int maxRetry =
			Integer.parseInt(EnvironmentManager.get(
					"/server/rmi/collectorserver/retry/count", "-1"));
		if (maxRetry < 0) {
			for (int i = 1;; i++) {
				try {
					lookup();
					serverError = null;
					break;
				} catch (Exception e1) {
					logger.info(WifeUtilities.createRmiActionControl()
						+ " retry rmi lookup. ("
						+ i
						+ ")");
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
					logger.info(WifeUtilities.createRmiActionControl()
						+ " retry rmi lookup. ("
						+ i
						+ "/"
						+ maxRetry
						+ ")");
					serverError = e1;
					logger.debug(e1);
					ThreadUtil.sleep(Globals.RMI_CONNECTION_RETRY_WAIT_TIME);
					continue;
				}
			}
		}
	}

	private void lookupOperationLogging() {
		loggingService =
			(OperationLoggingService) RmiUtil
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
				url =
					getClass().getResource(
							"/resources/xwife_applet_log4j.properties");
				PropertyConfigurator.configure(url);
			}
		} else {
			URL url =
				getClass().getResource(
						"/resources/xwife_applet_log4j.properties");
			PropertyConfigurator.configure(url);
		}
	}

	/**
	 * アプレットの初期化処理です。L&F、画面定義の初期化処理です。
	 */
	public void init() {
		logger.debug("session : " + session);
		ToolTipManager manager = ToolTipManager.sharedInstance();
		manager.setInitialDelay(0);
		manager.setDismissDelay(configuration.getInt(
				"xwife.applet.Applet.dismissDelay", 10000));
		manager.setReshowDelay(0);

		try {
			logoutuser =
				accessControl.getLogoutUser(InetAddress.getLocalHost());
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
	 * タイマーインスタンス、代理データプロバイダスレッドの初期化を処理します。
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
	 * 代理データプロバイダスレッドの停止処理します。
	 */
	public void stop() {
		referenceCount--;
	}

	/** アプレットの破棄 */
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

			screenSaverTimer.cancel();
		}
	}

	/**
	 * ユーザー認証ダイアログを表示します。認証が正常に完了した場合、このインスタンスに保持している Subject を切り替えられた Subject
	 * に更新します。
	 */
	public void showAuthenticationDialog() {
		try {
			AuthenticationDialog dlg =
				new AuthenticationDialog(WifeUtilities.getParentFrame(this));
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
	 * ログアウト処理をします。 WIFE では Subject 内の、プリンシパルをクリアして、初期状態に戻します。
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
	 * メニューツリーをユーザー用に切り替えます。 現在表示中ページが表示可能ならば保持します。
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
			tree.scrollPathToVisible(path);
			tree.requestFocusInWindow();
		}
	}

	/**
	 * 編集可能シンボルをリスナー登録します。リスナー登録されたシンボルは、ユーザー認証
	 * が行われてユーザーが変更された場合に、自分自身の編集可能フラグを更新します。
	 *
	 * @param symbol 編集可能シンボルオブジェクト
	 */
	public void addEditable(Editable symbol) {
		if (editables == null) {
			editables = new HashSet();
		}
		editables.add(symbol);
	}

	/**
	 * 編集可能シンボルをリスナーから削除します。
	 *
	 * @param symbol 編集可能シンボルオブジェクト
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
	 * 指定キーのページを表示します。
	 */
	public void changePage(final PageChangeEvent pageChange) {
		// 自動切換え且つ画面ロックモードなら処理しない。
		if (pageChange.isAuto() && isDisplayLock()) {
			return;
		}

		final TreePath path =
			searchTreePath(pageChange.getKey(), pageChange.getArgv());
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
			tree.scrollPathToVisible(path);
			tree.requestFocusInWindow();
		}
	}

	/**
	 * 設定されている画面ロックモードの状態を返します。
	 *
	 * @return ロックモードなら true、そうでないなら false
	 */
	public boolean isDisplayLock() {
		return isDisplayLock;
	}

	/**
	 * 画面ロックモードを設定します。
	 *
	 * @param isDisplayLock 画面ロックする場合は true、そうでない場合は false
	 */
	public void setDisplayLock(boolean isDisplayLock) {
		this.isDisplayLock = isDisplayLock;
	}

	/**
	 * ユーザー認証の結果、ユーザーの変更されたことをリスナーに通知します。
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
		DataProviderProxyDefine proxyDefine =
			new DataProviderProxyDefine(session, this);
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
			new TreeClicker(tree);
		}
	}

	private boolean isReceiveCache() {
		return configuration.getBoolean(
				"parser.AppletFrameDefine.receiveCache", false);
	}

	/**
	 * @return
	 */
	protected Box createBandFButton() {
		JButton treeBack =
			new JButton(GraphicManager.get(configuration.getString(
					"BandFButton.backIcon",
					"/toolbarButtonGraphics/navigation/Back16.gif")));
		Dimension preferredSize =
			new Dimension(configuration.getInt("BandFButton.width", 20),
					configuration.getInt("BandFButton.height", 20));
		treeBack.setPreferredSize(preferredSize);
		treeBack.setToolTipText("戻る");
		JButton treeForward =
			new JButton(GraphicManager.get(configuration.getString(
					"BandFButton.forwardIcon",
					"/toolbarButtonGraphics/navigation/Forward16.gif")));
		treeForward.setPreferredSize(preferredSize);
		treeForward.setToolTipText("進む");

		treeBack.addActionListener(PageHistoryActionFactory.createBackAction(
				history, this));
		treeForward.addActionListener(PageHistoryActionFactory
				.createForwardAction(history, this));

		Box treeBox = Box.createHorizontalBox();
		treeBox.add(treeBack);
		treeBox.add(Box.createGlue());
		treeBox.add(treeForward);
		return treeBox;
	}

	/**
	 * 操作ツリーからkeyを検索し、存在すればTreePathを返します。
	 *
	 * @param key キー文字列
	 * @return 検索結果のTreePath。検索文字列が存在しなければnullを返します。
	 */
	protected TreePath searchTreePath(String key) {
		return searchTreePath(key, null);
	}

	protected TreePath searchTreePath(String key, Object argv) {
		synchronized (tree) {
			DefaultMutableTreeNode root =
				(DefaultMutableTreeNode) tree.getModel().getRoot();
			for (Enumeration e = root.depthFirstEnumeration(); e
					.hasMoreElements();) {
				DefaultMutableTreeNode node =
					(DefaultMutableTreeNode) e.nextElement();
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
	 * メニューとツールバーを管理するクラス。 選択されたツリーより、pageMap からメニュー、ツールバーオブジェクトを取り出し、
	 * それぞれのコンポーネントを表示します。
	 */
	protected static class WifeToolBar extends JPanel implements
			TreeSelectionListener {
		private static final long serialVersionUID = 8077802722148750798L;
		/** pageMap オブジェクトの参照 */
		private final AbstractWifeApplet applet;
		/** スレッドプール */
		private final ExecutorService executorService =
			Executors.newSingleThreadExecutor();
		private final JLabel label = new JLabel("Now Loading...");
		private Future future = new FutureTask(new Runnable() {
			public void run() {
			}
		}, null);

		private BasePane currentBase = BasePane.DUMMY_PAGE;

		private int currentDividerLocation;

		private final Logger logger = Logger.getLogger(WifeToolBar.class);

		/**
		 * コンストラクタ
		 *
		 * @param pageMap ページマップオブジェクト
		 * @param session セッション情報
		 */
		WifeToolBar(AbstractWifeApplet applet) {
			super(new BorderLayout());
			this.applet = applet;
			FontUtil.setFont("serif", "plain", "36", label);
		}

		/**
		 * ツリーのクリックを受け取り、メニューとツールバーを設定し直す。
		 */
		public void valueChanged(TreeSelectionEvent e) {
			JTree tree = (JTree) e.getSource();
			DefaultMutableTreeNode selNode =
				(DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
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
					future =
						executorService
								.submit(new PageChangeTask(selNode, argv));
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
		 * 初期ページを表示します。
		 */
		private void showInitPage() {
			synchronized (currentBase) {
				currentBase = BasePane.DUMMY_PAGE;
			}
			TreeDefine treeDefine =
				applet.frameDef.getMenuTreeRoot(applet.subject.getUserName());
			final TreePath path =
				applet.searchTreePath(treeDefine.getInitPage());
			if (path != null) {
				SwingUtilities.invokeLater(new Runnable() {
					public void run() {
						synchronized (applet.tree) {
							applet.tree.setSelectionPath(path);
							applet.tree.expandPath(path);
							applet.tree.scrollPathToVisible(path);
							applet.tree.requestFocusInWindow();
						}
					}
				});
			}
		}

		private class PageChangeTask implements Runnable {
			private final Logger logger =
				Logger.getLogger(PageChangeTask.class);
			private final PageTreeNode pgnode;
			private final Object argv;

			PageChangeTask(DefaultMutableTreeNode selNode, Object argv) {
				pgnode = (PageTreeNode) selNode.getUserObject();
				this.argv = argv;
			}

			public void run() {
				try {
					// logger.info("session : " + applet.session);
					if (null != argv) {
						final Map pageMap =
							applet.frameDef.getPage(pgnode.getKey(),
									applet.session, argv);
						setMain(pageMap);
					} else {
						final Map pageMap =
							applet.frameDef.getPage(pgnode.getKey(),
									applet.session);
						setMain(pageMap);
					}
				} catch (BasePaneNotFoundException bex) {
					logger.error("page not found from server.", bex);
					JOptionPane.showMessageDialog(applet, pgnode
						+ "ページがサーバーにありませんでした。\n初期表示ページを表示します。",
							"Page not found from server",
							JOptionPane.ERROR_MESSAGE);
					showInitPage();
				}
			}

			private void setMain(final Map pageMap) {
				SwingUtilities.invokeLater(new Runnable() {
					public void run() {
						removeAll();
						add((JComponent) pageMap
								.get(AppletFrameDefine.ITEM_KEY_TOOLBAR),
								BorderLayout.SOUTH);
						revalidate();
						repaint();

						synchronized (currentBase) {
							if (isDestroy()) {
								currentBase.destroyPage();
							}
							currentBase =
								(BasePane) pageMap
										.get(AppletFrameDefine.ITEM_KEY_PANE);
							currentBase.revalidate();
							applet.spane.setRightComponent(currentBase);
						}
						applet.spane.setDividerLocation(currentDividerLocation);
						applet.spane.revalidate();
						applet.spane.repaint();
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
	 * 指定されたURLの音源をループ再生します
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
			logger.error("再生できないサウンドファイル形式です。", e);
		} catch (IOException e) {
			logger.error("サウンドファイルの読み込みに失敗しました。", e);
		} catch (LineUnavailableException e) {
			logger.error("再生できないサウンドファイル形式です。", e);
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
	 * 再生中の音を停止します
	 */
	public void stopAlarm() {
		if (null != clip && clip.isRunning()) {
			clip.stop();
			clip.setFramePosition(0);
		} else {
			if (null == clip) {
				logger.error("サウンドクリップオブジェクトが null です。");
			}
		}
	}

	/**
	 * 設定されている再生禁止モードの状態を返します。
	 *
	 * @return 再生禁止モードなら true、そうでないなら false
	 */
	public boolean isAlarmSoundLock() {
		return isAlarmSoundLock;
	}

	/**
	 * 再生禁止モードを設定します。
	 *
	 * @param isDisplayLock 再生禁止する場合は true、そうでない場合は false
	 */
	public void setAlarmSoundLock(boolean isAlarmSoundLock) {
		this.isAlarmSoundLock = isAlarmSoundLock;
	}

	/**
	 * このオブジェクトがアプリケーションとして、起動されている かどうかを返します。
	 *
	 * @return アプリケーションとして起動されている場合は true を そうでない場合は false を返します。
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
	 * 閉じるボタンの挙動を設定します。
	 *
	 * @param frame 親フレーム
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
	 * ClientConfiguration.xmlの設定より、閉じるボタンの挙動を返します。
	 *
	 * @return ClientConfiguration.xmlの設定より、閉じるボタンの挙動を返します。
	 */
	protected static boolean isClose(AbstractWifeApplet applet) {
		ClientConfiguration configuration = new ClientConfiguration();
		return !applet.isStandalone
			|| configuration.getBoolean("xwife.applet.Applet.isClose", true);
	}

	/**
	 * {@link Robot Robot}クラスを使用して、シフトキーを押下します。スクリーンセーバーの 解除に使用します。
	 */
	public void pressShiftKey() {
		if (isStandalone) {
			if (configuration.getBoolean("xwife.applet.Applet.screenSaver",
					false)
				&& isCheck) {
				RobotUtil.getInstance().keyClick(KeyEvent.VK_SHIFT);
				isCheck = false;
				screenSaverTimer.schedule(new ActionTimerTask(this),
						SCREEN_SAVER_TIME);
			}
		}
	}

	public void setCheck(boolean b) {
		isCheck = b;
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
		int width =
			configuration.getInt("xwife.applet.Applet.frame.size.width", 1152);
		int height =
			configuration.getInt("xwife.applet.Applet.frame.size.height", 864);
		Rectangle r = new Rectangle(x, y, width, height);
		frame.setBounds(r);
	}

	public boolean isAppletTypeC() {
		return isAppletTypeC;
	}

	public void setAppletTypeC(boolean isAppletTypeC) {
		this.isAppletTypeC = isAppletTypeC;
	}

	public String getLogoutUser() {
		return logoutuser;
	}

	public boolean isSoundoffAtStarted() {
		return soundoffAtStarted;
	}
}
