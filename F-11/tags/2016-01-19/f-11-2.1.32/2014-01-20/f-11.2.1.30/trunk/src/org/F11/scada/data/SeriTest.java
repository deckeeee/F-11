package org.F11.scada.data;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import junit.framework.TestCase;

public class SeriTest extends TestCase {
	public void testSeri() throws Exception {
		//デシリアライズのテスト。
		File temp = new File("y:/digital.ser");
		WifeDataDigital d = WifeDataDigital.valueOfFalse(0);
		try {
			ObjectOutputStream outs = new ObjectOutputStream(new FileOutputStream(temp));
			outs.writeObject(d);
			outs.flush();
			outs.close();
		} catch (Exception ex) {
			ex.printStackTrace();
			fail();
		}

		//デシリアライズのテスト。
		temp = new File("y:/analog.ser");
		WifeDataAnalog an0 = WifeDataAnalog.valueOfBcdDouble(0);
		try {
			ObjectOutputStream outs =
				new ObjectOutputStream(new FileOutputStream(temp));
			outs.writeObject(an0);
			outs.flush();
			outs.close();
		} catch (Exception ex) {
			ex.printStackTrace();
			fail();
		}


		temp = new File("y:/schedule.ser");
		WifeDataSchedule sc1 = WifeDataSchedule.valueOf(1, 2, "group1");
		try {
			ObjectOutputStream outs =
				new ObjectOutputStream(new FileOutputStream(temp));
			outs.writeObject(sc1);
			outs.flush();
			outs.close();
		} catch (Exception ex) {
			ex.printStackTrace();
			fail();
		}

	}
}
