package org.F11.scada.xwife.explorer.timeset;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.Date;

public interface TimeSetUtil extends Remote {
	public void setSystemDate(Date date) throws RemoteException;
}
