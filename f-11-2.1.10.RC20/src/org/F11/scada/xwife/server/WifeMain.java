/*
 * $Header: /cvsroot/f-11/F-11/src/org/F11/scada/xwife/server/WifeMain.java,v
 * 1.32.2.35 2007/10/26 02:26:26 frdm Exp $ $Revision: 1.32.2.35 $ $Date:
 * 2007/10/26 02:26:26 $
 * =============================================================================
 * Projrct F-11 - Web SCADA for Java Copyright (C) 2002 Project F-11
 * <http://www.F-11.org/>. All Rights Reserved. This program is free software;
 * you can redistribute it and/or modify it under the terms of the GNU General
 * Public License as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version. This program is
 * distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNU General Public License for more details. You
 * should have received a copy of the GNU General Public License along with this
 * program; if not, write to the Free Software Foundation, Inc., 59 Temple Place -
 * Suite 330, Boston, MA 02111-1307, USA.
 */

package org.F11.scada.xwife.server;

import java.awt.BorderLayout;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.rmi.RMISecurityManager;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.util.Iterator;
import java.util.TimeZone;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.plaf.metal.MetalLookAndFeel;
import javax.xml.parsers.FactoryConfigurationError;

import org.F11.scada.EnvironmentManager;
import org.F11.scada.Service;
import org.F11.scada.WifeUtilities;
import org.F11.scada.security.AccessControl;
import org.F11.scada.server.ManagerDelegator;
import org.F11.scada.server.alarm.AlarmReferencer;
import org.F11.scada.server.alarm.table.AlarmListFinder;
import org.F11.scada.server.alarm.table.AlarmListFinderDelegator;
import org.F11.scada.server.autoprint.AutoPrintEditor;
import org.F11.scada.server.autoprint.AutoPrintEditorFactory;
import org.F11.scada.server.command.CommandProvider;
import org.F11.scada.server.comment.PointCommentService;
import org.F11.scada.server.dao.ItemDao;
import org.F11.scada.server.deploy.PageFileDeployer;
import org.F11.scada.server.deploy.PageFileDeploymentScanner;
import org.F11.scada.server.deploy.TreeFileDeployer;
import org.F11.scada.server.edit.ServerEditHandler;
import org.F11.scada.server.edit.ServerEditManager;
import org.F11.scada.server.formula.FormulaDataProviderImpl;
import org.F11.scada.server.formula.ItemFormulaService;
import org.F11.scada.server.frame.FrameDefineManager;
import org.F11.scada.server.frame.FrameEditHandlerFactory;
import org.F11.scada.server.frame.editor.FrameEditHandler;
import org.F11.scada.server.io.BarGraph2ValueListHandlerManager;
import org.F11.scada.server.io.SelectiveAllDataValueListHandlerManager;
import org.F11.scada.server.io.SelectiveValueListHandlerManager;
import org.F11.scada.server.io.ValueListHandlerManager;
import org.F11.scada.server.io.ValueListHandlerManagerImpl;
import org.F11.scada.server.io.postgresql.S2ContainerUtil;
import org.F11.scada.server.logging.BarGraph2LoggingHandler;
import org.F11.scada.server.logging.F11LoggingHandler;
import org.F11.scada.server.logging.LoggingManager;
import org.F11.scada.server.logging.SelectiveAllDataLoggingHandler;
import org.F11.scada.server.logging.SelectiveLoggingHandler;
import org.F11.scada.server.operationlog.OperationLoggingFinderService;
import org.F11.scada.server.register.HolderRegisterBuilder;
import org.F11.scada.server.schedule.SchedulePointService;
import org.F11.scada.server.timeset.TimeSetManager;
import org.F11.scada.theme.DefaultWifeTheme;
import org.F11.scada.util.JavaVersion;
import org.F11.scada.xwife.WifeWindowAdapter;
import org.F11.scada.xwife.explorer.Explorer;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.apache.log4j.xml.DOMConfigurator;
import org.seasar.framework.container.S2Container;

/**
 * @author maekawa
 * @version
 */
public class WifeMain extends JPanel {
	private static final long serialVersionUID = -8067023058031469062L;

	private static Logger logger;

	public static final int RMI_RECV_PORT_SERVER = 50001;

	private FrameDefineManager frameDef;
	private ValueListHandlerManager valueListHandlerManager;
	private SelectiveValueListHandlerManager selectiveValueListHandler;
	private SelectiveAllDataValueListHandlerManager selectiveAllDataValueListHandlerManager;
	private BarGraph2ValueListHandlerManager bargraph2ValueListHandlerManager;
	private FrameEditHandler frameEditHandler;
	private ServerEditHandler serverEditHandler;
	private ManagerDelegator managerDelegator;
	private AlarmListFinder alarmListFinder;
	private OperationLoggingFinderService operationLoggingFinderService;
	private PointCommentService pointCommentService;
	private SchedulePointService schedulePointService;
	private AccessControl accessControl;

	/** Creates new WifeMain */
	public WifeMain(int rmiReceivePort) throws Exception {
		super();

		startupWait();

		displayInfo();

		setLayout(new BorderLayout());

		Class.forName(WifeUtilities.getJdbcDriver());

		S2Container container = S2ContainerUtil.getS2Container();

		DataProviderFactory dataProviderFactory = (DataProviderFactory) container
				.getComponent(DataProviderFactory.class);
		frameDef = (FrameDefineManager) container.getComponent("frameManager");

		for (Iterator it = dataProviderFactory.create().iterator(); it
				.hasNext();) {
			WifeDataProvider dp = (WifeDataProvider) it.next();
			dp.setSendRequestSupport(frameDef);
			dp.start();
		}

		boolean isUseFormula = Boolean.valueOf(
				EnvironmentManager.get(
						"/server/formula/isUseFormula",
						"false")).booleanValue();
		if (isUseFormula) {
			ItemDao itemDao = (ItemDao) container.getComponent(ItemDao.class);
			HolderRegisterBuilder builder = (HolderRegisterBuilder) container
					.getComponent(HolderRegisterBuilder.class);
			AlarmReferencer alarm = (AlarmReferencer) container
					.getComponent(WifeDataProvider.PARA_NAME_ALARM);
			AlarmReferencer demand = (AlarmReferencer) container
					.getComponent(WifeDataProvider.PARA_NAME_DEMAND);
			ItemFormulaService service = (ItemFormulaService) container
					.getComponent(ItemFormulaService.class);
			WifeDataProvider dp = new FormulaDataProviderImpl(
					30000,
					itemDao,
					builder,
					alarm,
					demand,
					service);
			dp.setSendRequestSupport(frameDef);
			dp.start();
		}

		valueListHandlerManager = new ValueListHandlerManagerImpl(
				rmiReceivePort);
		LoggingManager logManager = new LoggingManager(new F11LoggingHandler(
				valueListHandlerManager));
//		 LoggingManager logManager = new LoggingManager(new F11LoggingHandler(
//		 new DummyValueListHandlerManager()));
		selectiveValueListHandler = new SelectiveValueListHandlerManager(
				rmiReceivePort);
		new LoggingManager(new SelectiveLoggingHandler(
				selectiveValueListHandler));
		selectiveAllDataValueListHandlerManager = new SelectiveAllDataValueListHandlerManager(
				rmiReceivePort);
		new LoggingManager(new SelectiveAllDataLoggingHandler(
				selectiveAllDataValueListHandlerManager));
		bargraph2ValueListHandlerManager = new BarGraph2ValueListHandlerManager(rmiReceivePort);
		new LoggingManager(new BarGraph2LoggingHandler(
				bargraph2ValueListHandlerManager));

		new TimeSetManager();

		FrameEditHandlerFactory factory = new FrameEditHandlerFactory(
				rmiReceivePort,
				frameDef,
				logManager.getTaskMap());
		frameEditHandler = factory.createFrameEditHandler();
		AutoPrintEditorFactory autoPrintEditorFactory = (AutoPrintEditorFactory) container
				.getComponent(AutoPrintEditorFactory.class);
		AutoPrintEditor autoPrintEditor = autoPrintEditorFactory
				.getAutoPrintEditor();
		serverEditHandler = new ServerEditManager(
				rmiReceivePort,
				autoPrintEditor);

		managerDelegator = (ManagerDelegator) container
				.getComponent(ManagerDelegator.class);

		setTreeDeployer();

		setPageDeployer();

		new CommandProvider();

		alarmListFinder = new AlarmListFinderDelegator(rmiReceivePort);

		operationLoggingFinderService = (OperationLoggingFinderService) container
				.getComponent("finderservice");
		pointCommentService = (PointCommentService) container
				.getComponent("commentService");
		if (WifeUtilities.isSchedulePoint()) {
			schedulePointService = (SchedulePointService) container
					.getComponent("scheduleService");
			schedulePointService.init();
		}

		accessControl = new AccessControl(rmiReceivePort);

		startService();

		addExplorer(container);

		Runtime runtime = Runtime.getRuntime();
		runtime.addShutdownHook(new Thread() {
			public void run() {
				logger.info("JavaVMが停止しました。");
			}
		});
	}

	private void setPageDeployer() {
		Lock lock = new ReentrantLock();
		Condition condition = lock.newCondition();
		frameDef.setLock(lock);
		frameDef.setCondition(condition);
		PageFileDeploymentScanner pageScanner = new PageFileDeploymentScanner(
				new PageFileDeployer(frameDef),
				lock,
				condition);
		File page = new File("pagedefine");
		logger.info("Page Define root : " + page.getAbsolutePath());
		pageScanner.addFile(page);
	}

	private void setTreeDeployer() {
		PageFileDeploymentScanner treeScanner = new PageFileDeploymentScanner(
				new TreeFileDeployer(frameDef.getTreeDefineManager()),
				5000L);
		File menu = new File("treedefine");
		logger.info("Tree Define root : " + menu.getAbsolutePath());
		treeScanner.addFile(menu);
	}

	private void addExplorer(S2Container container) {
		Explorer explorer = (Explorer) container.getComponent(Explorer.class);
		add(explorer, BorderLayout.CENTER);
	}

	private void startupWait() throws InterruptedException {
		String waitTimeStr = EnvironmentManager
				.get("/server/startup/wait", "0");
		long waitTime = Long.parseLong(waitTimeStr);
		Thread.sleep(waitTime * 1000);
	}

	/**
	 * 
	 */
	private void displayInfo() {
		logger.info(System.getProperty("java.vendor"));
		logger.info(System.getProperty("java.version"));
		logger.info(System.getProperty("java.home"));
		logger.info(System.getProperty("java.vm.specification.version"));
		logger.info(System.getProperty("java.vm.specification.vendor"));
		logger.info(System.getProperty("java.vm.specification.name"));
		logger.info(System.getProperty("java.vm.version"));
		logger.info(System.getProperty("java.vm.vendor"));
		logger.info(System.getProperty("java.vm.name"));
		logger.info(System.getProperty("os.name"));
		logger.info(System.getProperty("os.version"));
	}

	/**
	 * @throws FactoryConfigurationError
	 */
	private static void createLog() throws FactoryConfigurationError {
		File file = new File("./log");
		file.mkdir();
		Class clazz = WifeMain.class;
		logger = Logger.getLogger(clazz.getName());

		URL url = clazz.getResource("/resources/server_log4j.xml");
		if (url != null) {
			DOMConfigurator.configure(url);
		} else {
			url = clazz
					.getResource("/resources/xwife_server_main_log4j.properties");
			PropertyConfigurator.configure(url);
		}
	}

	private void startService() {
		S2Container container = S2ContainerUtil.getS2Container();
		Service service = (Service) container.getComponent("serviceExecutor");
		service.start();
	}

	/**
	 * @param args the command line arguments
	 */
	public static void main(String args[]) {
		boolean nolock = false;
		boolean timezone = false;
		if (args != null) {
			for (int i = 0; i < args.length; i++) {
				if ("-nolock".equalsIgnoreCase(args[i])) {
					nolock = true;
				} else if ("-timezone".equalsIgnoreCase(args[i])) {
					timezone = true;
				}
			}
		}

		if (timezone) {
			TimeZone zone = TimeZone.getTimeZone("Asia/Tokyo");
			TimeZone.setDefault(zone);
			System.out.println("Set TimeZone = " + zone.getDisplayName());
		}

		createLog();

		if (!nolock) {
			File lockDir = new File("./lock");
			lockDir.mkdir();
			File lock = new File("./lock/lock");
			try {
				if (!lock.createNewFile()) {
					logger.info("F-11 Data Server Already start up.");
					showDialog(
							"F-11 サーバーは既に起動しています。\n起動していないのにこのダイアログが表示される場合は、\n../lock/lock ファイルを削除して再起動して下さい。",
							"F-11 Data Server Already start up.",
							JOptionPane.ERROR_MESSAGE);
					System.exit(1);
					return;
				}
			} catch (IOException e) {
				e.printStackTrace();
				return;
			}
			lock.deleteOnExit();
		}

		MetalLookAndFeel.setCurrentTheme(new DefaultWifeTheme());
		// セキュリティマネージャを作成およびインストールします
		if (System.getSecurityManager() == null) {
			System.setSecurityManager(new RMISecurityManager());
		}

		int port = Integer.parseInt(EnvironmentManager.get(
				"/server/rmi/managerdelegator/port",
				"1099"));
		try {
			LocateRegistry.createRegistry(port);
		} catch (RemoteException e) {
			logger.warn("既にRmiRegistryが起動しています", e);
		}

		int rmiReceivePort = Integer.parseInt(EnvironmentManager.get(
				"/server/rmi/managerdelegator/rmiReceivePort",
				"" + RMI_RECV_PORT_SERVER));
		JFrame frame = new JFrame(EnvironmentManager.get(
				"/server/title",
				"F-11 Server"));
		try {
			WifeMain main = new WifeMain(rmiReceivePort);

			frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
			frame.addWindowListener(new WifeWindowAdapter(frame));
			frame.getContentPane().add(main);
			frame.setSize(600, 400);
			frame.setVisible(true);
		} catch (Exception e) {
			logger.fatal("サーバー起動時にエラーが発生しました。", e);
			showDialog(
					"サーバー起動時にエラーが発生しました。\nサーバー起動を終了します。",
					"サーバー起動時にエラーが発生しました。",
					JOptionPane.ERROR_MESSAGE);
			System.exit(1);
			return;
		}
	}

	private static void showDialog(Object message, String title, int option) {
		JavaVersion javaVersion = new JavaVersion();
		if (javaVersion.compareTo(new JavaVersion(1, 5, 0, 0)) < 0) {
			JOptionPane.showMessageDialog(null, message, title, option);
		} else {
			JOptionPane pane = new JOptionPane(message, option);
			JDialog dialog = pane.createDialog(null, title);
			dialog.setAlwaysOnTop(true);
			dialog.setVisible(true);
		}
	}
}
