package org.F11.scada.xwife.explorer.timeset;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import jp.gr.javacons.jim.Manager;

import org.F11.scada.server.register.HolderString;
import org.F11.scada.util.RmiUtil;
import org.F11.scada.util.SystemTimeUtil;
import org.apache.commons.digester.Digester;
import org.apache.log4j.Logger;
import org.xml.sax.SAXException;

public class TimeSetUtilImpl implements TimeSetUtil {
	private final Logger log = Logger.getLogger(TimeSetUtilImpl.class);

	private List<HolderString> holderStrings;

	public TimeSetUtilImpl() {
		RmiUtil.registryServer(this, TimeSetUtil.class);
		loadTimeSet("/resources/TimeSet.xml");
	}

	private void loadTimeSet(String file) {
		URL xml = getClass().getResource(file);
		if (xml == null) {
			log.info("Not Found " + file);
		} else {
			parse(xml);
		}
	}

	private void parse(URL xml) {
		Digester digester = createDigester();
		InputStream is = null;
		try {
			is = xml.openStream();
			digester.parse(is);
			is.close();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		} finally {
			if (is != null) {
				try {
					is.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	private Digester createDigester() {
		Digester digester = new Digester();
		digester.setNamespaceAware(true);
		digester.push(this);
		digester
			.addObjectCreate("timeset/timesettask/read", HolderString.class);
		digester.addSetNext("timeset/timesettask/read", "addHolderString");
		digester.addSetProperties("timeset/timesettask/read");
		digester.addObjectCreate(
			"timeset/timesettask/write",
			HolderString.class);
		digester.addSetNext("timeset/timesettask/write", "addHolderString");
		digester.addSetProperties("timeset/timesettask/write");
		return digester;
	}

	public void addHolderString(HolderString holderString) {
		if (null == holderStrings) {
			holderStrings = new ArrayList<HolderString>();
		}
		holderStrings.add(holderString);
	}

	public void setSystemDate(Date date) {
		log.info("set datetime=" + date);
		SystemTimeUtil util = new SystemTimeUtil();
		if (null != holderStrings) {
			for (HolderString holderString : holderStrings) {
				log.info("set holder=" + holderString);
				util.setPlcTime(
					Manager.getInstance().findDataHolder(
						holderString.getProvider(),
						holderString.getHolder()),
					date);
			}
		}
		util.setSystemTime(date);
	}

	public static void main(String[] args) {
		TimeSetUtilImpl util = new TimeSetUtilImpl();
		util.setSystemDate(new Date());
	}
}
