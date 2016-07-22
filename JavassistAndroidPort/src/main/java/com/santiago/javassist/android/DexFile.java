package com.santiago.javassist.android;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import com.santiago.dexer.dex.DexOptions;
import com.santiago.dexer.dex.cf.CfOptions;
import com.santiago.dexer.dex.cf.CfTranslator;
import com.santiago.dexer.dex.file.ClassDefItem;
import com.santiago.dexer.util.FileUtils;

public class DexFile {
	private final com.santiago.dexer.dex.file.DexFile file;
	private final DexOptions dex_options = new DexOptions();
	
	public DexFile() {
		this.file = new com.santiago.dexer.dex.file.DexFile(dex_options);
	}
	
	public void addClass(File classFile) {
		final CfOptions cf_options = new CfOptions();
		final ClassDefItem cdi = CfTranslator.translate(classFile.getName(), FileUtils.readFile(classFile), cf_options, dex_options);
		file.add(cdi);
	}
	
	public void writeFile(String filePath) throws IOException {
		final FileOutputStream fos = new FileOutputStream(filePath);
		Throwable error = null;
		try {
			file.writeTo(fos, null, false);
		} catch (IOException e) {
			error = e;
		} finally {
			fos.close();
		}
		if (null != error) {
			new File(filePath).delete();
		}
	}
}
