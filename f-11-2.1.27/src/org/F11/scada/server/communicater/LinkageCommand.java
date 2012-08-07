/*
 * Projrct F-11 - Web SCADA for Java Copyright (C) 2002 Freedom, Inc. All Rights
 * Reserved. This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or (at your
 * option) any later version. This program is distributed in the hope that it
 * will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty
 * of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General
 * Public License for more details. You should have received a copy of the GNU
 * General Public License along with this program; if not, write to the Free
 * Software Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA
 * 02111-1307, USA.
 */

package org.F11.scada.server.communicater;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.Timer;
import java.util.TimerTask;
import java.util.TreeMap;
import java.util.TreeSet;

import org.F11.scada.server.converter.Converter;
import org.F11.scada.server.event.WifeCommand;
import org.apache.log4j.Logger;

/**
 * @author hori WifeCommandの集合を管理するクラスです。
 */
public final class LinkageCommand {
	private static Logger log = Logger.getLogger(LinkageCommand.class);

	/**
	 * 集合用コンパレータ 他方の通信エリアを完全に包括する場合に同一と見なします。 通信定義集合の検索を高速化する為に使用します。
	 */
	private final Comparator<WifeCommand> comp = new Comparator<WifeCommand>() {
		public int compare(WifeCommand wc1, WifeCommand wc2) {
			if (wc1.getDeviceID().compareTo(wc2.getDeviceID()) < 0)
				return -1;
			else if (wc1.getDeviceID().compareTo(wc2.getDeviceID()) > 0)
				return 1;

			if (wc1.getCycleMode() < wc2.getCycleMode())
				return -1;
			else if (wc1.getCycleMode() > wc2.getCycleMode())
				return 1;

			if (wc1.getMemoryMode() < wc2.getMemoryMode())
				return -1;
			else if (wc1.getMemoryMode() > wc2.getMemoryMode())
				return 1;

			if (wc1.getMemoryAddress() < wc2.getMemoryAddress()
					&& wc1.getMemoryAddress() + wc1.getWordLength() < wc2.getMemoryAddress()
							+ wc2.getWordLength())
				return -1;
			if (wc2.getMemoryAddress() < wc1.getMemoryAddress()
					&& wc2.getMemoryAddress() + wc2.getWordLength() < wc1.getMemoryAddress()
							+ wc1.getWordLength())
				return 1;

			return 0;
		}
	};

	/* 通信定義の集合 */
	protected final SortedMap<WifeCommand, DefineData> lk_commands = new TreeMap<WifeCommand, DefineData>(
			comp);
	/* コンバータ */
	private final Converter converter;

	/* 通信回数収集中フラグ */
	private boolean countGo = false;

	public LinkageCommand(Converter converter) {
		this.converter = converter;

		if (log.isDebugEnabled()) {
			Timer timer = new Timer();
			timer.schedule(new TimerTask() {
				public void run() {
					startCount();
				}
			}, 10L * 60L * 1000L);
			timer.schedule(new TimerTask() {
				public void run() {
					stopCount();
				}
			}, 20L * 60L * 1000L);
			timer.schedule(new TimerTask() {
				public void run() {
					log.debug(getCountString());
				}
			}, 21L * 60L * 1000L);
		}
	}

	/**
	 * ホルダ通信定義のコレクションを集合に追加します。
	 * @param defines 未ソート通信定義コレクション
	 */
	public void addDefine(Collection<WifeCommand> dh_commands) {
		// 参照元でリストを利用していることがある為、コピーしたリストをソートして使用する。
		ArrayList<WifeCommand> commands = new ArrayList<WifeCommand>(
				dh_commands);
		Collections.sort(commands, comp);
		for (Iterator<WifeCommand> i = commands.iterator(); i.hasNext();) {
			WifeCommand command = i.next();
			addDefine(command);
		}
	}

	/**
	 * ホルダ通信定義を集合に追加します。
	 * @param define 追加する通信定義
	 */
	public synchronized void addDefine(WifeCommand dh_command) {
		// 既存の集合から一致する要素を検索
		if (lk_commands.keySet().contains(dh_command)) {
			WifeCommand wc = lk_commands.tailMap(dh_command).firstKey();
			DefineData df = lk_commands.get(wc);
			df.addCommand(dh_command);
			// 一致した要素のWordLengthが小さい場合がある
			if (wc.getWordLength() < dh_command.getWordLength()) {
				lk_commands.remove(wc);
				lk_commands.put(dh_command, df);
			}
			return;
		}

		// 既存の集合から拡張可能な要素を検索
		WifeCommand wc1 = null;
		WifeCommand wc2 = null;
		WifeCommand wc1e = null;
		WifeCommand wc2e = null;
		SortedMap<WifeCommand, DefineData> head = lk_commands.headMap(dh_command);
		if (!head.isEmpty()) {
			// キーより小さい要素を作成する。拡張に失敗した場合 null値。
			wc1 = (WifeCommand) head.lastKey();
			wc1e = createExpandedCommand(wc1, dh_command);
		}
		SortedMap<WifeCommand, DefineData> tail = lk_commands.tailMap(dh_command);
		if (!tail.isEmpty()) {
			// キーより大きい要素を作成する。拡張に失敗した場合 null値。
			wc2 = (WifeCommand) tail.firstKey();
			wc2e = createExpandedCommand(wc2, dh_command);
		}
		if (wc1e != null && wc2e != null) {
			// 拡張後のWordLengthが小さい方を採用
			if (wc1e.getWordLength() <= wc2e.getWordLength()) {
				DefineData df = (DefineData) lk_commands.remove(wc1);
				df.addCommand(dh_command);
				lk_commands.put(wc1e, df);
			} else {
				DefineData df = (DefineData) lk_commands.remove(wc2);
				df.addCommand(dh_command);
				lk_commands.put(wc2e, df);
			}
			return;
		} else if (wc1e != null) {
			DefineData df = (DefineData) lk_commands.remove(wc1);
			df.addCommand(dh_command);
			lk_commands.put(wc1e, df);
			return;
		} else if (wc2e != null) {
			DefineData df = (DefineData) lk_commands.remove(wc2);
			df.addCommand(dh_command);
			lk_commands.put(wc2e, df);
			return;
		}

		// 拡張が不可能だったので追加
		DefineData df = new DefineData();
		df.addCommand(dh_command);
		lk_commands.put(dh_command, df);
	}

	public void removeDefine(Collection<WifeCommand> dh_commands) {
		// ArrayList commands = new ArrayList(dh_commands);
		// Collections.sort(commands, comp);
		// for (Iterator i = commands.iterator(); i.hasNext();) {
		// WifeCommand command = (WifeCommand) i.next();
		// removeDefine(command);
		// }
	}

	public void removeDefine(WifeCommand dh_command) {
		if (lk_commands.containsKey(dh_command)) {
			DefineData df = (DefineData) lk_commands.get(dh_command);
			df.removeDefine(dh_command);
		}
	}

	/**
	 * ホルダ通信定義のコレクションを含む通信要求リストを抽出します。 既存の集合から指定の通信定義を含む通信定義を検索し、コレクションします。
	 * 集合にない通信定義は、そのまま追加します。
	 * @param commands 抽出キーになる通信定義
	 * @return 抽出した通信定義のコレクション
	 */
	public synchronized Collection<WifeCommand> getDefines(
			Collection<WifeCommand> dh_commands) {
		SortedSet<WifeCommand> reqtbl = new TreeSet<WifeCommand>(comp);
		for (Iterator<WifeCommand> it = dh_commands.iterator(); it.hasNext();) {
			WifeCommand dh_comm = it.next();

			// 作成済みの通信要求リストを検索
			if (reqtbl.contains(dh_comm)) {
				WifeCommand wc = (WifeCommand) reqtbl.tailSet(dh_comm).first();
				if (dh_comm.getWordLength() <= wc.getWordLength()) {
					continue;
				}
			}
			// 既存の集合を検索
			if (lk_commands.keySet().contains(dh_comm)) {
				WifeCommand wc = lk_commands.tailMap(dh_comm).firstKey();
				if (dh_comm.getWordLength() <= wc.getWordLength()) {
					reqtbl.add(lk_commands.tailMap(dh_comm).firstKey());
					continue;
				}
			}
			// 集合にない通信定義
			throw new IllegalArgumentException("not added command. " + dh_comm);
		}
		return reqtbl;
	}

	/**
	 * 通信結果で前回値を更新します。
	 * @param command 通信定義のキー
	 * @param readData 通信結果
	 * @return ホルダ値を更新する必要がある場合は true
	 */
	public synchronized boolean updateDefine(WifeCommand command,
			ByteBuffer readData) {
		// 集合にないコマンドの通信結果は常にホルダ値更新
		DefineData df = lk_commands.get(command);
		if (df == null)
			return true;

		if (countGo) {
			// 通信回数収集中
			df.countup();
		}

		byte[] data = new byte[readData.remaining()];
		readData.get(data);
		return df.updateData(data);
	}

	/**
	 * 集合にリンクされたホルダ通信定義のコレクションを返します。
	 * @param command 集合のキー
	 * @return ホルダ通信定義のコレクション。
	 */
	public synchronized Collection<WifeCommand> getHolderCommands(
			WifeCommand command) {
		DefineData df = lk_commands.get(command);
		if (df == null) {
			throw new IllegalArgumentException("not find LinkageCommand."
					+ command.toString());
		}
		return df.getDefines();
	}

	/**
	 * 通信回数カウントを開始します。
	 */
	public synchronized void startCount() {
		countGo = true;
	}

	/**
	 * 通信回数カウントを停止します。
	 */
	public synchronized void stopCount() {
		countGo = false;
	}

	/**
	 * 通信回数の文字列表現を返します。
	 * @return
	 */
	public synchronized String getCountString() {
		StringBuffer sb = new StringBuffer();
		sb.append("Communication counter\n");
		for (Iterator<WifeCommand> it = lk_commands.keySet().iterator(); it.hasNext();) {
			WifeCommand wc = it.next();
			DefineData df = lk_commands.get(wc);
			sb.append(wc.toString()).append("\n");
			sb.append(df.getCountString());
			sb.append("------------------------------\n");
		}
		return sb.toString();
	}

	/*
	 * ２つの通信エリアを包括する通信定義を新しく作成します。 @param src 通信定義 @param dest 通信定義 @return
	 * 作成された通信定義。包括不可能な場合は null。
	 */
	private WifeCommand createExpandedCommand(WifeCommand src, WifeCommand dest) {
		if (!src.getDeviceID().equals(dest.getDeviceID())
				|| src.getCycleMode() != dest.getCycleMode()
				|| src.getMemoryMode() != dest.getMemoryMode()) {
			return null;
		}
		if (dest.getMemoryAddress() < src.getMemoryAddress()
				&& src.getMemoryAddress() + src.getWordLength() <= dest.getMemoryAddress()
						+ converter.getPacketMaxSize(src)) {
			long len = src.getMemoryAddress() + src.getWordLength()
					- dest.getMemoryAddress();
			if (len < dest.getWordLength()) {
				len = dest.getWordLength();
			}

			return src.createCommand(dest.getMemoryAddress(), (int) len);
		} else if (src.getMemoryAddress() <= dest.getMemoryAddress()
				&& dest.getMemoryAddress() + dest.getWordLength() <= src.getMemoryAddress()
						+ converter.getPacketMaxSize(src)) {
			long len = dest.getMemoryAddress() + dest.getWordLength()
					- src.getMemoryAddress();
			if (len < src.getWordLength()) {
				len = src.getWordLength();
			}

			return src.createCommand(src.getMemoryAddress(), (int) len);
		}
		return null;
	}

	/*
	 * 集合の要素に付随する内容を表すクラス。
	 */
	private final class DefineData {
		/* ホルダ通信定義への参照 */
		private final Collection<WifeCommand> commands = new HashSet<WifeCommand>();
		/* 通信定義の前回値 */
		private byte[] oldData = new byte[0];

		/* 通信回数 */
		private int commCnt = 0;
		/* 前回通信時刻 */
		private long commTime = Long.MAX_VALUE;
		/* 最短通信間隔 */
		private long minCommSpan = Long.MAX_VALUE;
		/* 最長通信間隔 */
		private long maxCommSpan = Long.MIN_VALUE;

		public DefineData() {
		}

		public void addCommand(WifeCommand command) {
			commands.add(command);
			oldData = new byte[0]; // 次回読込みデータに必ず差異を作る。
		}

		public void removeDefine(WifeCommand command) {
			commands.remove(command);
		}

		/*
		 * 
		 */
		public Collection<WifeCommand> getDefines() {
			return new HashSet<WifeCommand>(commands);
		}

		/*
		 * 前回値と違いがあれば true
		 */
		public boolean updateData(byte[] data) {
			if (Arrays.equals(oldData, data))
				return false;
			oldData = data;
			return true;
		}

		/*
		 * 通信回数及び通信間隔更新
		 */
		public void countup() {
			commCnt++;

			long nowTime = System.currentTimeMillis();
			if (commTime < nowTime) {
				long span = nowTime - commTime;
				if (span < minCommSpan) {
					minCommSpan = span;
				}
				if (maxCommSpan < span) {
					maxCommSpan = span;
				}
			}
			commTime = nowTime;
		}

		public String getCountString() {
			StringBuffer sb = new StringBuffer();
			sb.append("Comm Count:").append(commCnt).append("\n");
			sb.append("CommTimeSpan min:").append(minCommSpan / 1000.0);
			sb.append(" max:").append(maxCommSpan / 1000.0).append("\n");
			return sb.toString();
		}
	}
}
