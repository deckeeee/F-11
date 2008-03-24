/*
 * =============================================================================
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

package org.F11.scada.tool.opelog;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.lang.reflect.InvocationTargetException;
import java.util.Iterator;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.F11.scada.server.operationlog.OperationLoggingFinderService;
import org.F11.scada.server.operationlog.dto.FinderConditionDto;
import org.F11.scada.server.operationlog.dto.OperationLoggingFinderDto;
import org.F11.scada.test.util.TimestampUtil;
import org.F11.scada.tool.login.PermissionCheck;
import org.F11.scada.util.RmiUtil;
import org.apache.commons.lang.time.DateFormatUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

/**
 * 操作ログをテキスト形式でダウンロード処理
 * 
 * @author maekawa
 * 
 */
public class GetOpelogAction extends Action {
	/** テキストファイルの区切り記号 */
	private static final String DELIMITER = "\t";
	/** ロギングAPI */
	private final Log log = LogFactory.getLog(GetOpelogAction.class);
	/** F-11サーバーのサービスを使用 */
	private OperationLoggingFinderService service;

	public ActionForward execute(
			ActionMapping mapping,
			ActionForm form,
			HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		if (!PermissionCheck.check("opelog", request))
			return (mapping.getInputForward());

		write(form, response);
		return null;
	}

	private void write(ActionForm form, HttpServletResponse response)
			throws IllegalAccessException,
			InvocationTargetException,
			IOException {
		lookup();
		List data = service.getOperationLogging(getFinder(form));
		writeCsv(data, response);
	}

	private void lookup() {
		if (null == service) {
			service = (OperationLoggingFinderService) RmiUtil
					.lookupServer(OperationLoggingFinderService.class);
		}
	}

	private void writeCsv(List data, HttpServletResponse response)
			throws IOException {
		response.setContentType("text/txt");
		response.setHeader("Content-Disposition", "attachment;filename="
				+ getFileName());
		BufferedWriter out = null;
		try {
			out = new BufferedWriter(new OutputStreamWriter(response
					.getOutputStream()));
			writeHeader(out);
			writeCsvData(out, data);
		} finally {
			if (null != out) {
				out.close();
			}
		}
	}

	private void writeHeader(BufferedWriter out) throws IOException {
		out.write("日時" + DELIMITER + "IP" + DELIMITER + " ユーザー" + DELIMITER
				+ "変更前" + DELIMITER + "変更後" + DELIMITER + "ポイント記号" + DELIMITER
				+ "ポイント名称" + DELIMITER + "ポイント詳細");
		out.newLine();
	}

	private void writeCsvData(BufferedWriter out, List data) throws IOException {
		for (Iterator i = data.iterator(); i.hasNext();) {
			OperationLoggingFinderDto dto = (OperationLoggingFinderDto) i
					.next();
			out.write(getCsv(dto));
			out.newLine();
		}
	}

	private String getCsv(OperationLoggingFinderDto dto) {
		return getOpeDate(dto) + DELIMITER + dto.getOpeIp() + DELIMITER
				+ dto.getOpeUser() + DELIMITER + dto.getOpeBeforeValue()
				+ DELIMITER + dto.getOpeAfterValue() + DELIMITER + getUnit(dto)
				+ DELIMITER + getName(dto) + DELIMITER + getMessage(dto);
	}

	private String getOpeDate(OperationLoggingFinderDto dto) {
		return DateFormatUtils.format(dto.getOpeDate(), "yyyy/MM/dd HH:mm:ss");
	}

	private String getUnit(OperationLoggingFinderDto dto) {
		return null != dto.getUnit() ? dto.getUnit() : "";
	}

	private String getName(OperationLoggingFinderDto dto) {
		return null != dto.getName() ? dto.getName() : "";
	}

	private String getMessage(OperationLoggingFinderDto dto) {
		return null != dto.getMessage() ? dto.getMessage() : "";
	}

	private String getFileName() {
		return "opelog.txt";
	}

	private FinderConditionDto getFinder(ActionForm form)
			throws IllegalAccessException,
			InvocationTargetException {
		DataConditionsForm conditionsForm = (DataConditionsForm) form;
		FinderConditionDto dto = new FinderConditionDto();
		copyFields(dto, conditionsForm);
		log.info("search condition = " + dto);
		return dto;
	}

	private void copyFields(
			FinderConditionDto dto,
			DataConditionsForm conditionsForm) {
		dto.setStartDate(TimestampUtil.parse(conditionsForm.getStYear() + "/"
				+ conditionsForm.getStMonth() + "/" + conditionsForm.getStDay()
				+ " " + conditionsForm.getStHour() + ":"
				+ conditionsForm.getStMinute() + ":"
				+ conditionsForm.getStSecond()));
		dto.setEndDate(TimestampUtil.parse(conditionsForm.getEtYear() + "/"
				+ conditionsForm.getEtMonth() + "/" + conditionsForm.getEtDay()
				+ " " + conditionsForm.getEtHour() + ":"
				+ conditionsForm.getEtMinute() + ":"
				+ conditionsForm.getEtSecond()));
		dto.setOpeUser(getOpeUser(conditionsForm));
		dto.setOpeIp(getOpeIp(conditionsForm));
		dto.setOpeName(getOpeName(conditionsForm));
		dto.setOpeMessage(getOpeMessage(conditionsForm));
	}

	private String getOpeUser(DataConditionsForm conditionsForm) {
		String opeUser = conditionsForm.getOpeUser();
		return "".equals(opeUser) ? null : opeUser;
	}

	private String getOpeIp(DataConditionsForm conditionsForm) {
		String opIp = conditionsForm.getOpeIp();
		return "".equals(opIp) ? null : opIp;
	}

	private String getOpeName(DataConditionsForm conditionsForm) {
		String opeName = conditionsForm.getOpeName();
		return "".equals(opeName) ? null : opeName;
	}

	private String getOpeMessage(DataConditionsForm conditionsForm) {
		String opeMessage = conditionsForm.getOpeMessage();
		return "".equals(opeMessage) ? null : opeMessage;
	}
}
