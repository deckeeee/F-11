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
 *
 */

package org.F11.scada.server.register;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.net.URL;
import java.util.Set;

import org.apache.commons.digester.Digester;
import org.apache.commons.digester.RuleSet;
import org.xml.sax.SAXException;

/**
 * �y�[�W��`����z���_��`�𒊏o���郆�[�e�B���e�B�[�N���X�ł��B
 * 
 * @author maekawa
 *
 */
abstract public class PageXmlUtil {
	/**
	 * �Ώۂ�xml����z���_��`�𒊏o���܂��B
	 * 
	 * @param xml �y�[�W��`
	 * @return HolderString�̃Z�b�g
	 */
	public static Set<HolderString> getHolderStrings(String xml) {
		return getHolderStrings(xml, new PageXmlRuleSet());
	}

	public static Set<HolderString> getHolderStrings(String xml, RuleSet ruleSet) {
		StringReader in = new StringReader(xml);
		try {
			return getHolderStrings(in, ruleSet);
		} finally {
			if (in != null) {
				in.close();
			}
		}
	}

	public static Set<HolderString> getHolderStrings(Reader xml) {
		return getHolderStrings(xml, new PageXmlRuleSet());
	}

	public static Set<HolderString> getHolderStrings(Reader xml, RuleSet ruleSet) {
		HolderStringSet holderStringSet = new HolderStringSet();

		Digester digester = new Digester();
		URL url = PageXmlUtil.class.getResource("/resources/pagemap10.dtd");
		if (null == url) {
			throw new IllegalStateException(
				"/resources/pagemap10.dtd ���N���X�p�X��ɑ��݂��܂���");
		}
		digester.register("-//F-11 2.0//DTD F11 Page Configuration//EN", url
			.toString());
		digester.setValidating(true);
		digester.setNamespaceAware(true);
		digester.addRuleSet(ruleSet);
		digester.push(holderStringSet);

		try {
			digester.parse(xml);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		}

		return holderStringSet;
	}

	public static boolean isCache(String xml) {
		StringReader in = new StringReader(xml);
		try {
			return isCache(in);
		} finally {
			if (in != null) {
				in.close();
			}
		}
	}

	public static boolean isCache(Reader in) {
		PageCache pageCache = new PageCache();

		Digester digester = new Digester();
		digester.setNamespaceAware(true);
		digester.addRuleSet(new PageXmlCacheRuleSet());
		digester.push(pageCache);

		try {
			digester.parse(in);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		}

		return pageCache.isCache();
	}
}