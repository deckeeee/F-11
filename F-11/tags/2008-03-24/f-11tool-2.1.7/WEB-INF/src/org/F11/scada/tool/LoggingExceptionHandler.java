/*
 * Created on 2003/07/28
 *
 * To change this generated comment go to 
 * Window>Preferences>Java>Code Generation>Code Template
 */
package org.F11.scada.tool;

import java.io.PrintWriter;
import java.io.StringWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ExceptionHandler;
import org.apache.struts.config.ExceptionConfig;

/**
 * @author hori
 */
public class LoggingExceptionHandler extends ExceptionHandler {
	protected Log log = LogFactory.getLog(this.getClass());

	/* (non-Javadoc)
	 * @see org.apache.struts.action.ExceptionHandler#execute(java.lang.Exception, org.apache.struts.config.ExceptionConfig, org.apache.struts.action.ActionMapping, org.apache.struts.action.ActionForm, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */
	public ActionForward execute(
		Exception ex,
		ExceptionConfig config,
		ActionMapping mapping,
		ActionForm form,
		HttpServletRequest request,
		HttpServletResponse responce)
		throws ServletException {

		ActionForward af = super.execute(ex, config, mapping, form, request, responce);

		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);
		ex.printStackTrace(pw);
		log.fatal(sw.toString());

		//System.out.println(sw.toString());

		return af;
	}

}
