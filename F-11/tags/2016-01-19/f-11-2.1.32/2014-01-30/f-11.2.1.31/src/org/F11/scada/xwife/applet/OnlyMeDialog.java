package org.F11.scada.xwife.applet;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import org.F11.scada.util.FontUtil;

/**
 * クライアント二重起動時に表示する、自動で閉じるダイアログです。
 *
 * @author maekawa
 *
 */
public class OnlyMeDialog extends JDialog {
	private static final long serialVersionUID = -3271636626461992386L;
	private static final long PRIOD_TIME = 1000L;
	private static final String END_PREFIX = "秒後にこのウィンドウを閉じます。";
	private Timer timer;

	/**
	 * ダイアログのコンストラクタ
	 *
	 * @param max 閉じるまでの時間をミリ秒で指定
	 * @param title 表題文字列を指定
	 * @param notes ダイアログの文言を指定
	 */
	public OnlyMeDialog(long max, String title, String notes) {
		super();
		timer = new Timer(getClass().getName(), true);
		timer.schedule(new DisposeTimerTask(this), max);
		init(max, title, notes);
	}

	private void init(long max, String title, String notes) {
		setTitle(title);
		setModal(true);
		setAlwaysOnTop(true);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		layoutComponent(max, notes);
	}

	private void layoutComponent(long max, String notes) {
		Container container = getContentPane();
		container.add(getCenter(max, notes), BorderLayout.CENTER);
		container.add(getSouth(), BorderLayout.SOUTH);
		pack();
		setLocationRelativeTo(null);
	}

	private Component getCenter(long max, String notes) {
		JPanel p = new JPanel(new BorderLayout());
		p.setBorder(BorderFactory.createEmptyBorder(25, 55, 25, 55));
		p.add(getNotes(max, notes));
		return p;
	}

	private Component getNotes(long max, String notes) {
		JPanel p = new JPanel(new GridLayout(2, 1));
		JLabel mainLabel = new JLabel(notes);
		mainLabel.setFont(FontUtil.getFont("Monospaced-PLAIN-30"));
		p.add(mainLabel);
		long secTime = max / PRIOD_TIME;
		JLabel secLabel = new JLabel(secTime + END_PREFIX);
		secLabel.setFont(FontUtil.getFont("Monospaced-PLAIN-30"));
		timer.scheduleAtFixedRate(
			new SecTimerTask(secLabel, secTime),
			0,
			PRIOD_TIME);
		p.add(secLabel);
		return p;
	}

	private Component getSouth() {
		Box box = Box.createHorizontalBox();
		box.setBorder(BorderFactory.createEmptyBorder(5, 10, 10, 10));
		box.add(Box.createHorizontalGlue());
		box.add(getOkButton());
		return box;
	}

	private Component getOkButton() {
		JButton button = new JButton("終了");
		button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				dispose();
			}
		});
		return button;
	}

	@Override
	public void dispose() {
		super.dispose();
		timer.cancel();
	}

	/**
	 * 一定時間が経つとダイアログを閉じるタスク
	 *
	 * @author maekawa
	 *
	 */
	private static class DisposeTimerTask extends TimerTask {
		private final JDialog dialog;

		public DisposeTimerTask(JDialog dialog) {
			this.dialog = dialog;
		}

		@Override
		public void run() {
			dialog.dispose();
		}
	}

	/**
	 * 1秒ごとにラベルを変更するタスク
	 *
	 * @author maekawa
	 *
	 */
	private static class SecTimerTask extends TimerTask {
		private final JLabel label;
		private long secTime;

		public SecTimerTask(JLabel label, long secTime) {
			this.label = label;
			this.secTime = secTime;
		}

		@Override
		public void run() {
			label.setText(secTime + END_PREFIX);
			--secTime;
		}
	}

	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				new OnlyMeDialog(10000L, "既にクライアントが起動しています", "既にクライアントが起動しています")
					.setVisible(true);
			}
		});
	}
}
