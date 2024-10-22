/**
 * Copyright (2020, ) Institute of Software, Chinese Academy of Sciences
 */
package io.github.doslab.wukong.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;

import org.apache.hc.core5.http.HttpEntity;

import io.github.doslab.wukong.CloudVersionGenerator;

/**
 * 
 * @author tangting18@otcaix.iscas.ac.cn
 * @since 2019.12.10
 * 
 * This code comes from Internet, it works well in our case.
 */
public class FileUtils {

	public static BufferedReader read(String file) throws Exception {
		return new BufferedReader(new FileReader(new File(file)));
	}
	
	public static BufferedReader read(HttpEntity entity) throws Exception {
		return new BufferedReader(new InputStreamReader(entity.getContent()));
	}
	
	public static BufferedReader read(String groupId, String artifactId) throws IOException, Exception {
		String pageUrl = CloudVersionGenerator.MAVEN_URL_PREFIX + groupId.replace(".", "/") + "/" + artifactId;
		return FileUtils.read(HttpUtils.getResponse(pageUrl));
	}
	
	
	public static void write(File file, String content) throws Exception {
		if (!file.getParentFile().exists()) {
			file.getParentFile().mkdirs();
		}
		FileWriter fw = new FileWriter(file);
		fw.write(content);
		fw.close();
	}
	
}
