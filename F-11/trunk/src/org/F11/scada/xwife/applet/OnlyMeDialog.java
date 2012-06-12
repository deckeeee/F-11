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

public class OnlyMeDialog extends JDialog {
	private static final long serialVersionUID = -3271636626461992386L;
	private static final long PRIOD_TIME = 1000L;
	private static final String TITLE = "既にクライアントが起動しています";
	private Timer timer;

	public OnlyMeDialog(long max) {
		super();
		timer = new Timer(getClass().getName(), true);
		timer.schedule(new DisposeTimerTask(this), max);
		init(max);
	}

	private void init(long max) {
		setTitle(TITLE);
		setModal(true);
		setAlwaysOnTop(true);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		layoutComponent(max);
	}

	private void layoutComponent(long max) {
		Container container = getContentPane();
		container.add(getCenter(max), BorderLayout.CENTER);
		container.add(getSouth(), BorderLayout.SOUTH);
		pack();
		setLocationRelativeTo(null);
	}

	private Component getCenter(long max) {
		JPanel p = new JPanel(new BorderLayout());
		p.setBorder(BorderFactory.createEmptyBorder(25, 25, 25, 25));
		p.add(getNotes(max));
		return p;
	}

	private Component getNotes(long max) {
		JPanel p = new JPanel(new GridLayout(2, 1));
		JLabel mainLabel = new JLabel("既にクライアントが起動しています");
		mainLabel.setFont(FontUtil.getFont("Monospaced-PLAIN-24"));
		p.add(mainLabel);
		long secTime = max / PRIOD_TIME;
		JLabel secLabel = new JLabel(secTime + "秒後に終了します。");
		secLabel.setFont(FontUtil.getFont("Monospaced-PLAIN-24"));
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
		JButton button = new JButton("OK");
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
			label.setText(secTime + "秒後に終了します。");
			--secTime;
		}
	}

	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				new OnlyMeDialog(10000L).setVisible(true);
			}
		});
	}
}
