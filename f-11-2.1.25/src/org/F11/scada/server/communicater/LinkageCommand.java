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
 * @author hori WifeCommand�̏W�����Ǘ�����N���X�ł��B
 */
public final class LinkageCommand {
	private static Logger log = Logger.getLogger(LinkageCommand.class);

	/**
	 * �W���p�R���p���[�^ �����̒ʐM�G���A�����S�ɕ����ꍇ�ɓ���ƌ��Ȃ��܂��B �ʐM��`�W���̌���������������ׂɎg�p���܂��B
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

	/* �ʐM��`�̏W�� */
	protected final SortedMap<WifeCommand, DefineData> lk_commands = new TreeMap<WifeCommand, DefineData>(
			comp);
	/* �R���o�[�^ */
	private final Converter converter;

	/* �ʐM�񐔎��W���t���O */
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
	 * �z���_�ʐM��`�̃R���N�V�������W���ɒǉ����܂��B
	 * @param defines ���\�[�g�ʐM��`�R���N�V����
	 */
	public void addDefine(Collection<WifeCommand> dh_commands) {
		// �Q�ƌ��Ń��X�g�𗘗p���Ă��邱�Ƃ�����ׁA�R�s�[�������X�g���\�[�g���Ďg�p����B
		ArrayList<WifeCommand> commands = new ArrayList<WifeCommand>(
				dh_commands);
		Collections.sort(commands, comp);
		for (Iterator<WifeCommand> i = commands.iterator(); i.hasNext();) {
			WifeCommand command = i.next();
			addDefine(command);
		}
	}

	/**
	 * �z���_�ʐM��`���W���ɒǉ����܂��B
	 * @param define �ǉ�����ʐM��`
	 */
	public synchronized void addDefine(WifeCommand dh_command) {
		// �����̏W�������v����v�f������
		if (lk_commands.keySet().contains(dh_command)) {
			WifeCommand wc = lk_commands.tailMap(dh_command).firstKey();
			DefineData df = lk_commands.get(wc);
			df.addCommand(dh_command);
			// ��v�����v�f��WordLength���������ꍇ������
			if (wc.getWordLength() < dh_command.getWordLength()) {
				lk_commands.remove(wc);
				lk_commands.put(dh_command, df);
			}
			return;
		}

		// �����̏W������g���\�ȗv�f������
		WifeCommand wc1 = null;
		WifeCommand wc2 = null;
		WifeCommand wc1e = null;
		WifeCommand wc2e = null;
		SortedMap<WifeCommand, DefineData> head = lk_commands.headMap(dh_command);
		if (!head.isEmpty()) {
			// �L�[��菬�����v�f���쐬����B�g���Ɏ��s�����ꍇ null�l�B
			wc1 = (WifeCommand) head.lastKey();
			wc1e = createExpandedCommand(wc1, dh_command);
		}
		SortedMap<WifeCommand, DefineData> tail = lk_commands.tailMap(dh_command);
		if (!tail.isEmpty()) {
			// �L�[���傫���v�f���쐬����B�g���Ɏ��s�����ꍇ null�l�B
			wc2 = (WifeCommand) tail.firstKey();
			wc2e = createExpandedCommand(wc2, dh_command);
		}
		if (wc1e != null && wc2e != null) {
			// �g�����WordLength�������������̗p
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

		// �g�����s�\�������̂Œǉ�
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
	 * �z���_�ʐM��`�̃R���N�V�������܂ޒʐM�v�����X�g�𒊏o���܂��B �����̏W������w��̒ʐM��`���܂ޒʐM��`���������A�R���N�V�������܂��B
	 * �W���ɂȂ��ʐM��`�́A���̂܂ܒǉ����܂��B
	 * @param commands ���o�L�[�ɂȂ�ʐM��`
	 * @return ���o�����ʐM��`�̃R���N�V����
	 */
	public synchronized Collection<WifeCommand> getDefines(
			Collection<WifeCommand> dh_commands) {
		SortedSet<WifeCommand> reqtbl = new TreeSet<WifeCommand>(comp);
		for (Iterator<WifeCommand> it = dh_commands.iterator(); it.hasNext();) {
			WifeCommand dh_comm = it.next();

			// �쐬�ς݂̒ʐM�v�����X�g������
			if (reqtbl.contains(dh_comm)) {
				WifeCommand wc = (WifeCommand) reqtbl.tailSet(dh_comm).first();
				if (dh_comm.getWordLength() <= wc.getWordLength()) {
					continue;
				}
			}
			// �����̏W��������
			if (lk_commands.keySet().contains(dh_comm)) {
				WifeCommand wc = lk_commands.tailMap(dh_comm).firstKey();
				if (dh_comm.getWordLength() <= wc.getWordLength()) {
					reqtbl.add(lk_commands.tailMap(dh_comm).firstKey());
					continue;
				}
			}
			// �W���ɂȂ��ʐM��`
			throw new IllegalArgumentException("not added command. " + dh_comm);
		}
		return reqtbl;
	}

	/**
	 * �ʐM���ʂőO��l���X�V���܂��B
	 * @param command �ʐM��`�̃L�[
	 * @param readData �ʐM����
	 * @return �z���_�l���X�V����K�v������ꍇ�� true
	 */
	public synchronized boolean updateDefine(WifeCommand command,
			ByteBuffer readData) {
		// �W���ɂȂ��R�}���h�̒ʐM���ʂ͏�Ƀz���_�l�X�V
		DefineData df = lk_commands.get(command);
		if (df == null)
			return true;

		if (countGo) {
			// �ʐM�񐔎��W��
			df.countup();
		}

		byte[] data = new byte[readData.remaining()];
		readData.get(data);
		return df.updateData(data);
	}

	/**
	 * �W���Ƀ����N���ꂽ�z���_�ʐM��`�̃R���N�V������Ԃ��܂��B
	 * @param command �W���̃L�[
	 * @return �z���_�ʐM��`�̃R���N�V�����B
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
	 * �ʐM�񐔃J�E���g���J�n���܂��B
	 */
	public synchronized void startCount() {
		countGo = true;
	}

	/**
	 * �ʐM�񐔃J�E���g���~���܂��B
	 */
	public synchronized void stopCount() {
		countGo = false;
	}

	/**
	 * �ʐM�񐔂̕�����\����Ԃ��܂��B
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
	 * �Q�̒ʐM�G���A������ʐM��`��V�����쐬���܂��B @param src �ʐM��` @param dest �ʐM��` @return
	 * �쐬���ꂽ�ʐM��`�B��s�\�ȏꍇ�� null�B
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
	 * �W���̗v�f�ɕt��������e��\���N���X�B
	 */
	private final class DefineData {
		/* �z���_�ʐM��`�ւ̎Q�� */
		private final Collection<WifeCommand> commands = new HashSet<WifeCommand>();
		/* �ʐM��`�̑O��l */
		private byte[] oldData = new byte[0];

		/* �ʐM�� */
		private int commCnt = 0;
		/* �O��ʐM���� */
		private long commTime = Long.MAX_VALUE;
		/* �ŒZ�ʐM�Ԋu */
		private long minCommSpan = Long.MAX_VALUE;
		/* �Œ��ʐM�Ԋu */
		private long maxCommSpan = Long.MIN_VALUE;

		public DefineData() {
		}

		public void addCommand(WifeCommand command) {
			commands.add(command);
			oldData = new byte[0]; // ����Ǎ��݃f�[�^�ɕK�����ق����B
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
		 * �O��l�ƈႢ������� true
		 */
		public boolean updateData(byte[] data) {
			if (Arrays.equals(oldData, data))
				return false;
			oldData = data;
			return true;
		}

		/*
		 * �ʐM�񐔋y�ђʐM�Ԋu�X�V
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
