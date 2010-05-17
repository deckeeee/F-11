/*
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
 */

package org.F11.scada.server.command;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import jp.gr.javacons.jim.DataHolder;
import jp.gr.javacons.jim.DataReferencer;
import jp.gr.javacons.jim.DataReferencerOwner;
import jp.gr.javacons.jim.DataValueChangeEvent;
import jp.gr.javacons.jim.DataValueChangeListener;

import org.F11.scada.WifeException;
import org.F11.scada.applet.expression.Expression;
import org.F11.scada.server.alarm.DataValueChangeEventKey;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.digester.Digester;
import org.apache.log4j.Logger;
import org.xml.sax.SAXException;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * CommandProvider�̒�`�N���X�ł�
 * @author Hideaki Maekawa <frdm@users.sourceforge.jp>
 */
public class CommandProvider
		implements DataReferencerOwner, DataValueChangeListener {

	/** Logging API */
	private static Logger log = Logger.getLogger(CommandProvider.class);
	/** �ێ�����N���X */
	private static final Class[][] TYPE_INFO = {{DataHolder.class}};

	/** �v���o�C�_�� + �z���_���ƃR�}���h��`�̃}�b�v */
	private Map commands;
	/** �f�[�^���t�@�����T�̃��X�g�ł� */
	private List referencers;
	/** Expression�]�����ƃR�}���h��`�̃}�b�v(Set�����ŗ��p) */
	private Map expressionCommands;

	/**
	 * �f�t�H���g�̒�`�t�@�C��(/resources/command.xml)�ŃR�}���h���s�I�u�W�F�N�g�����������܂�
	 * @throws IOException
	 * @throws SAXException
	 */	
	public CommandProvider() throws IOException, SAXException {
		this("/resources/command.xml");
	}

	/**
	 * �w�肳�ꂽ��`�t�@�C���ŃR�}���h���s�I�u�W�F�N�g�����������܂�
	 * @param file
	 * @throws IOException
	 * @throws SAXException
	 */
	public CommandProvider(String file) throws IOException, SAXException {
		URL xml = getClass().getResource(file);

		if (xml == null) {
			log.info("Not Found " + file);
			log.info("CommandProvider not running.");
			return;
		}

		commands = new ConcurrentHashMap();
		referencers = new CopyOnWriteArrayList();
		expressionCommands = new ConcurrentHashMap();

		Digester digester = new Digester();
		URL url = getClass().getResource("/resources/command20.dtd");
		if (null == url) {
			throw new IllegalStateException("/resources/command20.dtd ���N���X�p�X��ɑ��݂��܂���");
		}
		digester.register("-//F-11 2.0//DTD F11 Command Configuration//EN", url.toString());
		digester.setValidating(true);
		digester.push(this);

		addPageRule(digester);

		InputStream is = null;
		try {
			is = xml.openStream();
			digester.parse(is);
			is.close();
		} finally {
			if (is != null) {
				is.close();
			}
		}
	}

	private void addPageRule(Digester digester) {
		digester.addObjectCreate("command-provider/command", CommandConfig.class);
		digester.addSetNext("command-provider/command", "addCommand");
		digester.addSetProperties("command-provider/command");

		digester.addObjectCreate("command-provider/command/class", ClassConfig.class);
		digester.addSetNext("command-provider/command/class", "addClassConfig");
		digester.addSetProperties("command-provider/command/class");

		digester.addCallMethod("command-provider/command/class/property", "addProperty", 2);
		digester.addCallParam("command-provider/command/class/property", 0, "name");
		digester.addCallParam("command-provider/command/class/property", 1, "value");

		digester.addObjectCreate("command-provider/expression", ExpressionConfig.class);
		digester.addSetNext("command-provider/expression", "addCommand");
		digester.addSetProperties("command-provider/expression");

		digester.addObjectCreate("command-provider/expression/class", ClassConfig.class);
		digester.addSetNext("command-provider/expression/class", "addClassConfig");
		digester.addSetProperties("command-provider/expression/class");

		digester.addCallMethod("command-provider/expression/class/property", "addProperty", 2);
		digester.addCallParam("command-provider/expression/class/property", 0, "name");
		digester.addCallParam("command-provider/expression/class/property", 1, "value");
	}

	/**
	 * Command��`�I�u�W�F�N�g��ǉ����܂��B
	 * @param command Command��`�I�u�W�F�N�g
	 */
	public void addCommand(CommandConfig command) {
		log.info("add command=" + command);
		List cmds = createCommandList(command);
		if (commands.containsKey(getKey(command))) {
			throw new IllegalArgumentException("�v���o�C�_�E�z���_���d�����Ă��܂��B : " + getKey(command));
		} else {
			commands.put(getKey(command), cmds);
			DataReferencer ref =
				new DataReferencer(command.getProvider(), command.getHolder());
			ref.connect(this);
			referencers.add(ref);
		}
	}

	private List createCommandList(ClassConfigContainer container) {
		List cfgs = container.getdClassConfigs();
		List cmds = new ArrayList(cfgs.size());
		for (Iterator it = cfgs.iterator(); it.hasNext();) {
			ClassConfig cfg = (ClassConfig) it.next();
			try {
				Class clazz = Class.forName(cfg.getClassName());
				Command cmd = (Command) clazz.newInstance();
				Map param = cfg.getProperties();
				BeanUtils.populate(cmd, param);
				cmds.add(cmd);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return cmds;
	}

	private String getKey(CommandConfig command) {
		return command.getProvider() + "_" + command.getHolder();
	}

	/**
	 * Command��`�I�u�W�F�N�g��ǉ����܂��B
	 * @param command Command��`�I�u�W�F�N�g
	 */
	public void addCommand(ExpressionConfig command) {
		log.info("" + command.isEnableInitialMode());
		Expression expression = new Expression(command.isEnableInitialMode());
		expression.toPostfix(command.getValue());
		if (expressionCommands.containsKey(expression)) {
			throw new IllegalArgumentException("�]�������d�����Ă��܂��B : " + command.getValue());
		} else {
			List commands = createCommandList(command);
			expressionCommands.put(expression, commands);
			connectReferencer(expression, commands);
		}
	}

	private void connectReferencer(Expression expression, List commands) {
		for (Iterator it = expression.getProviderHolderNames().iterator(); it.hasNext();) {
			String tag = (String) it.next();
			int p = tag.indexOf('_');
			if (0 < p) {
				DataReferencer ref = new DataReferencer(tag.substring(0, p), tag.substring(p + 1));
				ref.connect(new ExpressionReferencer(expression, commands));
				referencers.add(ref);
			}
		}
	}

	/**
	 * Command��`�I�u�W�F�N�g�̃��X�g��Ԃ��܂��B
	 * Unit�e�X�g�ׂ̈̃��\�b�h�Ȃ̂ŁA�p�b�P�[�W�v���C�x�[�g
	 * @return Command��`�I�u�W�F�N�g�̃��X�g
	 */
	Map getCommands() {
		return Collections.unmodifiableMap(commands);
	}

	/**
	 * Command��`�I�u�W�F�N�g�̃��X�g��Ԃ��܂��B
	 * Unit�e�X�g�ׂ̈̃��\�b�h�Ȃ̂ŁA�p�b�P�[�W�v���C�x�[�g
	 * @return Command��`�I�u�W�F�N�g�̃��X�g
	 */
	Map getExpressionCommands() {
		return expressionCommands;
	}

	/**
	 * �ێ�����f�[�^�^�C�v��Ԃ��܂�
	 * @param dr �f�[�^���t�@�����T
	 * @return �ێ�����f�[�^�^�C�v��Ԃ��܂�
	 */
	public Class[][] getReferableDataHolderTypeInfo(DataReferencer dr) {
		return TYPE_INFO;
	}
	
	/**
	 * �f�[�^�ύX�C�x���g���������܂�
	 * @param evt �f�[�^�ύX�C�x���g
	 */
	public void dataValueChanged(DataValueChangeEvent evt) {
		DataValueChangeEventKey dvce = new DataValueChangeEventKey(evt);
		String key = getKey(dvce);
		if (commands.containsKey(key)) {
			List cmds = (List) commands.get(key);
			for (Iterator it = cmds.iterator(); it.hasNext();) {
				Command cmd = (Command) it.next();
				cmd.execute(dvce);
			}
		}
	}

	private String getKey(DataValueChangeEventKey dvce) {
		return dvce.getProvider() + "_" + dvce.getHolder();
	}



	private static class ExpressionReferencer 
			implements DataReferencerOwner, DataValueChangeListener {

		/** Expression�]�����ƃR�}���h��`�̃}�b�v */
		private final Expression expression;
		private final List commands;

		ExpressionReferencer(Expression expression, List commands) {
			this.expression = expression;
			this.commands = commands;
		}

		public void dataValueChanged(DataValueChangeEvent evt) {
			DataValueChangeEventKey dvce = new DataValueChangeEventKey(evt, false);
			dataValueChangedForExpression(dvce);
		}

		private void dataValueChangedForExpression(DataValueChangeEventKey dvce) {
			try {
				if (expression.hasDataHolder(getKey(dvce))) {
					for (Iterator it = commands.iterator(); it.hasNext();) {
						Command cmd = (Command) it.next();
						cmd.execute(new DataValueChangeEventKey(
								dvce.getPoint(),
								dvce.getProvider(),
								dvce.getHolder(),
								Boolean.valueOf(expression.booleanValue()),
								dvce.getTimeStamp()));
					}
				}
			} catch (WifeException e) {
				if (e.getDetailCode() != WifeException.WIFE_INITIALDATA_WARNING) {
					log.error("�]������͎��ɃG���[���������܂���", e);
				}
			}
		}

		private String getKey(DataValueChangeEventKey dvce) {
			return dvce.getProvider() + "_" + dvce.getHolder();
		}

		public Class[][] getReferableDataHolderTypeInfo(DataReferencer dr) {
			return TYPE_INFO;
		}
	}
}
