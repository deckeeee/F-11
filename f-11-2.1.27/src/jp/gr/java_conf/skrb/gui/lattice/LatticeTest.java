/*
 * 大日本製薬株式会社　GLP温湿度データ収集システム
 * Copyright (c) 2003 Freedom, Inc.
 */

package jp.gr.java_conf.skrb.gui.lattice;

import java.awt.Color;
import java.awt.Container;

import javax.swing.JFrame;
import javax.swing.JLabel;

/**
 * 
 * @author Hideaki Maekawa <maekawa@frdm.co.jp>
 */
public class LatticeTest {
	public static void main(String[] args) {
		JFrame f = new JFrame();
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		Container c = f.getContentPane();
		c.setLayout(new LatticeLayout(20, 2));
		
		LatticeConstraints cons = new LatticeConstraints();
		cons.top = 3;
		cons.bottom = 1;
		cons.left = 3;
		cons.right = 1;
		
		cons.adjust = LatticeConstraints.BOTH;
		cons.fill = LatticeConstraints.BOTH;
		cons.x = 0;
		cons.y = 0;
		cons.width = 1;
		JLabel l1 = new JLabel("色");
		l1.setBackground(Color.RED);
		l1.setOpaque(true);
		c.add(l1, cons);

		cons.adjust = LatticeConstraints.BOTH;
		cons.fill = LatticeConstraints.BOTH;
		cons.x = 1;
		cons.y = 0;
		cons.width = 13;
		c.add(new JLabel("記号＋名称"), cons);

		cons.adjust = LatticeConstraints.BOTH;
		cons.fill = LatticeConstraints.BOTH;
		cons.x = 14;
		cons.y = 0;
		cons.width = 2;
		c.add(new JLabel("参照値"), cons);

		cons.adjust = LatticeConstraints.BOTH;
		cons.fill = LatticeConstraints.BOTH;
		cons.x = 16;
		cons.y = 0;
		cons.width = 2;
		c.add(new JLabel("参照値"), cons);

		cons.adjust = LatticeConstraints.BOTH;
		cons.fill = LatticeConstraints.BOTH;
		cons.x = 18;
		cons.y = 0;
		cons.width = 2;
		c.add(new JLabel("単位"), cons);

		cons.adjust = LatticeConstraints.BOTH;
		cons.fill = LatticeConstraints.BOTH;
		cons.x = 14;
		cons.y = 1;
		cons.width = 2;
		c.add(new JLabel("現在値"), cons);

		cons.adjust = LatticeConstraints.BOTH;
		cons.fill = LatticeConstraints.BOTH;
		cons.x = 16;
		cons.y = 1;
		cons.width = 2;
		JLabel l2 = new JLabel("現在値");
		l2.setBackground(Color.BLUE);
		l2.setForeground(Color.WHITE);
		l2.setOpaque(true);
		c.add(l2, cons);
		
		f.setSize(510, 80);
		f.setVisible(true);
	}
}
