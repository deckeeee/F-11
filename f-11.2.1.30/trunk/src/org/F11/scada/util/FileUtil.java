package org.F11.scada.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Iterator;

public abstract class FileUtil {
	/**
	 * �w�茏�����c���A�ҏW���t�̌Â����Ƀt�@�C�����폜����B0�ȉ����w�肳�ꂽ�ꍇ�͍폜�������Ȃ��B
	 *
	 * @param files �t�@�C���̈ꗗ
	 * @param cnt �c�������B0�ȉ����w�肳�ꂽ�ꍇ�͍폜�������Ȃ��B
	 */
	public static void removeOldFile(File[] files, int cnt) {
		if (cnt <= 0 || null == files || files.length <= cnt) {
			return;
		}

		ArrayList<File> fileList = new ArrayList<File>(files.length);
		for (int i = 0; i < files.length; i++) {
			fileList.add(files[i]);
		}
		while (cnt < fileList.size()) {
			long first = System.currentTimeMillis();
			File firstFile = null;
			for (Iterator<File> it = fileList.iterator(); it.hasNext();) {
				File file = it.next();
				if (file.lastModified() < first) {
					first = file.lastModified();
					firstFile = file;
				}
			}
			firstFile.delete();
			fileList.remove(firstFile);
		}
	}

	/**
	 * �o�b�t�@�[�ς݂Ŏ����t���b�V������PrintWrite��Ԃ��܂��B
	 *
	 * @param path �o�͂���p�X��
	 * @param append �ǋL���[�h
	 * @param csn �����G���R�[�h
	 * @return �o�b�t�@�[�ς݂�PrintWrite��Ԃ��܂��B
	 * @throws UnsupportedEncodingException OS�ŃT�|�[�g����Ă��Ȃ��G���R�[�h���w�肳�ꂽ�ꍇ�X���[���܂��B
	 * @throws FileNotFoundException �o�͂���p�X���쐬�ł��Ȃ��ꍇ�X���[���܂��B
	 */
	public static PrintWriter getPrintWriter(String path,
			boolean append,
			String csn) throws UnsupportedEncodingException,
			FileNotFoundException {
		return new PrintWriter(new BufferedWriter(new OutputStreamWriter(
			new FileOutputStream(new File(path), append),
			csn)), true);
	}

	/**
	 * �o�b�t�@�[�ς݂̃e�L�X�g���[�_�[��Ԃ��܂��B
	 *
	 * @param path ���͂���t�@�C����
	 * @param csn �����G���R�[�h
	 * @return �o�b�t�@�[�ς݂̃e�L�X�g���[�_�[��Ԃ��܂��B
	 * @throws UnsupportedEncodingException OS�ŃT�|�[�g����Ă��Ȃ��G���R�[�h���w�肳�ꂽ�ꍇ�X���[���܂��B
	 * @throws FileNotFoundException ���͂���p�X���쐬�ł��Ȃ��ꍇ�X���[���܂��B
	 */
	public static BufferedReader getReader(String path, String csn)
			throws UnsupportedEncodingException, FileNotFoundException {
		return new BufferedReader(new InputStreamReader(new FileInputStream(
			new File(path)), csn));
	}
}
