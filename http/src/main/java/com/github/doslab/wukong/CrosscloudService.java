/**
 * Copyright (2020, ) Institute of Software, Chinese Academy of Sciences
 */
package com.github.doslab.wukong;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.kubesys.httpfrk.core.HttpBodyHandler;
import com.github.kubesys.tools.annotations.ServiceDefinition;
import com.github.kubesys.tools.annotations.api.CatalogDescriber;
import com.github.kubesys.tools.annotations.api.ParamDescriber;
import com.github.kubesys.tools.annotations.api.ServiceDescriber;

/**
 * @author wuheng@otcaix.iscas.ac.cn
 * 
 * @version 2.3.0
 * @since 2020.10.15
 * 
 **/
@ServiceDefinition
@CatalogDescriber(desc = "���ƹ������")
public class CrosscloudService extends HttpBodyHandler {

	/**
	 * clients
	 */
	protected Map<String, Object> clients = new HashMap<>();
	
	/**
	 * analyzers
	 */
	protected Map<String, CloudAPIAnalyzer> analyzers = new HashMap<>();
	
	/**
	 * @param id
	 * @param metadata
	 * @param map
	 * @return
	 * @throws Exception
	 */
	@ServiceDescriber(shortName = "����Client", desc = "", prereq = "��")
	public boolean createClient(
			@ParamDescriber(required = true, 
			desc = "client��Id",
			regexp = "Сд��ĸ�����ֺ��л���",
			example = "id001")
			String id, 
			@ParamDescriber(required = true, 
			desc = "�����������ɵ�һЩ��Ϣ",
			regexp = "JSON",
			example = "�����������ɵ�һЩ��Ϣ")
			CloudMetadata metadata, 
			@ParamDescriber(required = true, 
			desc = "�˻���Ϣ",
			regexp = "���忴�����Ƶ�API����",
			example = "���簢������zone, accessKey, secretKey")
			Map<String, String> map) throws Exception {
		
		// init clients
		CloudClassloader loader = new CloudClassloader(metadata);
		Class<?> clz = loader.loadClass("com.github.kubesys.crosscloud.Client");
		Method method = clz.getMethod("getClient", Map.class);
		clients.put(id, method.invoke(null, map));
		
		// init analyser
		if (!analyzers.containsKey(metadata.getKind())) {
			CloudAPIAnalyzer analyser = new CloudAPIAnalyzer(metadata, loader);
			analyser.extractCloudAPIs();
			analyzers.put(metadata.getKind(), analyser);
		}
		analyzers.put(id, analyzers.get(metadata.getKind()));
		
		
		return true;
	}
	
	public Object execRequest(
			@ParamDescriber(required = true, 
			desc = "client��Id",
			regexp = "Сд��ĸ�����ֺ��л���",
			example = "id001")
			String id, 
			@ParamDescriber(required = true, 
			desc = "���������",
			regexp = "JSON",
			example = "�����������ɵ�һЩ��Ϣ")
			JsonNode lifecycle) throws Exception  {
		
		
		String key = lifecycle.fields().next().getKey();
		
		CloudAPIAnalyzer analyser = analyzers.get(id);
		
		Method method = getMethod(id, key, analyser);
		
		return method.invoke(clients.get(id), new ObjectMapper().readValue(
								lifecycle.get(key).toPrettyString(), analyser.getData(key)));
	}

	protected Method getMethod(String id, String key, CloudAPIAnalyzer analyser) {
		
		Method method = null;
		
		for (Method m : clients.get(id).getClass().getMethods()) {
			if (m.getName().equals(analyser.getAPI(key)) && m.getParameterCount() == 1) {
				method = m;
				break;
			}
		}
		return method;
	}

}
