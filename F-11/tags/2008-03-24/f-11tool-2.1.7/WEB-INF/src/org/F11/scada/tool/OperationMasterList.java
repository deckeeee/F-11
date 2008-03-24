/*
 * �쐬��: 2004/03/25
 *
 * ���̐������ꂽ�R�����g�̑}�������e���v���[�g��ύX���邽��
 * �E�B���h�E > �ݒ� > Java > �R�[�h���� > �R�[�h�ƃR�����g
 */
package org.F11.scada.tool;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/** 
 * @author hori
 *
 * ���̐������ꂽ�R�����g�̑}�������e���v���[�g��ύX���邽��
 * �E�B���h�E > �ݒ� > Java > �R�[�h���� > �R�[�h�ƃR�����g
 */
public class OperationMasterList {

	private final List operationList = new ArrayList();
	private final Map nameMap = new HashMap();

	public OperationMasterList() {
		initAdd("user", "���[�U�[�Ǘ�");
		initAdd("group", "�O���[�v�Ǘ�");
		initAdd("autoprint", "�����󎚐ݒ�");
		initAdd("name", "�|�C���g���̊Ǘ�");
		initAdd("policy", "�|�C���g�F�؊Ǘ�");
		initAdd("item", "�|�C���g�A�C�e���Ǘ�");
		initAdd("email", "�|�C���g�d�q���[�����M��Ǘ�");
		initAdd("career", "�|�C���g�����Ǘ�");
		initAdd("device", "�f�o�C�X�Ǘ�");
		initAdd("menu", "���j���[�Ǘ�");
		initAdd("page", "��ʊǗ�");
		initAdd("maintenance", "�����e�i���X�Ǘ�");
		initAdd("ffugroupname", "FFU�O���[�v���̊Ǘ�");
		initAdd("email_group_master", "�d�q���[�����M�O���[�v �}�X�^�[�Ǘ�");
		initAdd("email_attribute_setting", "�d�q���[�����M�O���[�v �����ʊǗ�");
		initAdd("email_individual_setting", "�d�q���[�����M�O���[�v �ʊǗ�");
		initAdd("logdata", "���M���O�f�[�^ �_�E�����[�h");
		initAdd("sound_attribute_setting", "�x�񉹐ݒ�@�����ʊǗ�");
		initAdd("sound_individual_setting", "�x�񉹐ݒ�@�ʊǗ�");
		initAdd("sentmail", "�x�񃁁[���@���M�ꗗ");
		initAdd("opelog", "���샍�O�@�_�E�����[�h");
	}

	private void initAdd(String key, String message) {
		operationList.add(key);
		nameMap.put(key, message);
	}

	public List getKeys() {
		return new ArrayList(operationList);
	}

	public String getName(String key) {
		return (String) nameMap.get(key);
	}

}
