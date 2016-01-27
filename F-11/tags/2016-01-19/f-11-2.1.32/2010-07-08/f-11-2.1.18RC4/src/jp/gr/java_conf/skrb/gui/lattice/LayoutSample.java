package jp.gr.java_conf.skrb.gui.lattice;

import java.applet.Applet;
import java.awt.Button;
import java.awt.Frame;
import java.awt.Label;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import jp.gr.java_conf.skrb.gui.lattice.LatticeConstraints;
import jp.gr.java_conf.skrb.gui.lattice.LatticeLayout;

public class LayoutSample extends Applet {
	private static final long serialVersionUID = -5576204839912497822L;

	public void start() {
		Button button1 = new Button("Button 1 12345");
		Button button2 = new Button("Button 2 67890");
		Button button3 = new Button("Button 3 ABCDEFG");
		Button button4 = new Button("Button 4 abcdefg");
		Button button5 = new Button("Button 5 hijklmn");
		Button button6 = new Button("Button 6 ‚ ‚¢‚¤");
		Button button7 = new Button("Button 7 ‚¦‚¨‚©");
		Button button8 = new Button("Button 8 ‚«‚­‚¯");

		LatticeLayout layout = new LatticeLayout(6, 4);
		LatticeConstraints cons = new LatticeConstraints();
		setLayout(layout);

		cons.x = 0;
		cons.y = 0;
		cons.width = 3;
		cons.height = 2;
		cons.fill = LatticeConstraints.BOTH;
		layout.setConstraints(button1, cons);
		add(button1);

		cons.x = 4;
		cons.y = 0;
		cons.width = 1;
		cons.height = 1;
		cons.adjust = LatticeConstraints.BOTH;
		layout.setConstraints(button2, cons);
		add(button2);

		cons.x = 0;
		cons.y = 2;
		cons.width = 1;
		cons.height = 1;
		layout.setConstraints(button3, cons);
		add(button3);

		cons.x = 1;
		Label label = new Label("Label AAAAA");
		layout.setConstraints(label, cons);
		add(label);

		cons.x = 2;
		cons.width = 2;
		cons.fill = LatticeConstraints.HORIZONTAL;
		cons.valign = LatticeConstraints.CENTER;
		layout.setConstraints(button4, cons);
		add(button4);

		cons.x = 4;
		cons.fill = LatticeConstraints.VERTICAL;
		layout.setConstraints(button5, cons);
		add(button5);

		cons.x = 0;
		cons.y = 3;
		cons.fill = LatticeConstraints.NONE;
		cons.valign = LatticeConstraints.BOTTOM;
		layout.setConstraints(button6, cons);
		add(button6);

		cons.x = 2;
		cons.fill = LatticeConstraints.BOTH;
		layout.setConstraints(button7, cons);
		add(button7);

		cons.x = 4;
		cons.left = 10;
		cons.top = 10;
		cons.right = 2;
		cons.bottom = 5;
		layout.setConstraints(button8, cons);
		add(button8);
	}

	static Frame frame;

	public static void main(String[] args) {
		frame = new Frame("LayoutSample");
		frame.setSize(400, 400);
		frame.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent event) {
				frame.dispose();
			}
		});

		LayoutSample sample = new LayoutSample();
		frame.add(sample);
		sample.start();

		frame.setVisible(true);
	}
}
