/*
 * Created on 2005/10/26
 *
 * To change this generated comment go to 
 * Window>Preferences>Java>Code Generation>Code Template
 */
package org.F11.scada.tool.logdata;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.sql.Connection;
import java.util.Iterator;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.F11.scada.tool.login.PermissionCheck;
import org.F11.scada.util.ConnectionUtil;
import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.seasar.framework.container.S2Container;
import org.seasar.framework.container.factory.SingletonS2ContainerFactory;

/**
 * @author hori
 */
public class DownloadCSVAction extends Action {
	public ActionForward execute(
			ActionMapping mapping,
			ActionForm form,
			HttpServletRequest request,
			HttpServletResponse response) throws Exception, OutOfMemoryError {
		if (!PermissionCheck.check("logdata", request))
			return (mapping.getInputForward());

		Connection con = null;
		BufferedWriter out = null;
		try {
			con = ConnectionUtil.getConnection();
			S2Container container = SingletonS2ContainerFactory.getContainer();
			MakeCsvServiceFactory factory = (MakeCsvServiceFactory) container
					.getComponent(MakeCsvServiceFactory.class);
			DataConditionsForm dataCondForm = (DataConditionsForm) form;
			MakeCsvService service = factory.getMakeCsvService(
					con,
					dataCondForm);
			response.setContentType("text/csv");
			response.setHeader(
					"Content-Disposition",
					"attachment;filename=logging.csv");
			out = new BufferedWriter(new OutputStreamWriter(response
					.getOutputStream()));
			writeHeader(out, dataCondForm, service);
			writeCsvData(out, dataCondForm, service);
			return null;
		} finally {
			if (con != null) {
				con.close();
			}
			if (out != null) {
				out.close();
			}
		}
	}

	private void writeCsvData(
			BufferedWriter out,
			DataConditionsForm dataCondForm,
			MakeCsvService service) throws IOException {
		List csvLines = service.getCsvData(dataCondForm);
		for (Iterator it = csvLines.iterator(); it.hasNext();) {
			out.write((String) it.next());
			out.newLine();
		}
	}

	private void writeHeader(
			BufferedWriter out,
			DataConditionsForm dataCondForm,
			MakeCsvService service) throws IOException {
		List header = service.getHeaderData(dataCondForm);
		for (Iterator it = header.iterator(); it.hasNext();) {
			out.write((String) it.next());
			out.newLine();
		}
	}

}