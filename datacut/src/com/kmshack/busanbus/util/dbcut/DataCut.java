package com.kmshack.busanbus.util.dbcut;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;

public class DataCut {
	
	private final static String FILE_DIR = "c:/";
	
	public static void main(String[] args) {

		try {
			FileInputStream fis = new FileInputStream(FILE_DIR + "BusData.kms"); //분할 파일
			BufferedInputStream bis = new BufferedInputStream(fis);
			
			FileOutputStream fos = new FileOutputStream(FILE_DIR + "BusDataCut1.kms"); //분할 후 파일
			BufferedOutputStream bos = new BufferedOutputStream(fos);

			byte[] b = new byte[1024];
			int read = -1;
			int count = 1;
			int count2 = 1;

			while ((read = bis.read(b, 0, 1024)) != -1) {
				bos.write(b, 0, read);
				bos.flush();
				if (count2 % 900 == 0){

					count++;

					if (fos != null)
						fos.close();

					if (bos != null)
						bos.close();

					fos = new FileOutputStream(FILE_DIR + "/BusDataCut" + count + ".kms"); //분할 후 파일
					bos = new BufferedOutputStream(fos);
				}
				count2++;
			}

			fis.close();
			bis.close();

			if (fos != null)
				fos.close();

			if (bos != null)
				bos.close();
		}

		catch (Exception e) {
			System.out.println(e.getMessage());
		}

	}

}
