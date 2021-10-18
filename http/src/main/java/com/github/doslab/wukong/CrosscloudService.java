/**
 * Copyright (2020, ) Institute of Software, Chinese Academy of Sciences
 */
package com.github.doslab.wukong;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.github.kubesys.httpfrk.core.HttpBodyHandler;
import com.github.kubesys.tools.annotations.ServiceDefinition;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

/**
 * @author wuheng@otcaix.iscas.ac.cn
 * 
 * @version 2.3.0
 * @since 2020.10.15
 * 
 **/
@ServiceDefinition
@Api(value = "���ƹ�����ݻ��ӿ�")
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
	@ApiOperation(value = "�������˻�")
	public Map<String, JsonNode> createClient(
			@ApiParam(value = "������ID", required = true, example = "asasd") String id, 
			@ApiParam(value = "������Ԫģ��", required = true, example = "�鿴com.github.doslab.wukong.CloudMetadata") CloudMetadata metadata, 
			@ApiParam(value = "������Ԫģ�Ͷ�Ӧ��ֵ", required = true, example = "����accesskey�ȣ������Ʋ�̫һ��")  Map<String, String> map) throws Exception {
		
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
		
		
		return analyzers.get(id).extractCloudAPIs();
	}
	
	@ApiOperation(value = "���ݷ����Ľӿڽ��з���")
	public Object execRequest(
			@ApiParam(value = "�������˻�ID", required = true, example = "asasd") String id, 
			@ApiParam(value = "�������ڹ���Json��ÿ��Json��һ��", required = true, example = "��createClient�����") JsonNode lifecycle) throws Exception  {
		
		
		String key = lifecycle.fields().next().getKey();
		
		CloudAPIAnalyzer analyser = analyzers.get(id);
		
		Method method = getMethod(id, key, analyser);
		
		Class<?> obj = analyser.getData(key);
		Object params = new ObjectMapper().readValue(lifecycle.get(key).toPrettyString(), obj);
		return method.invoke(clients.get(id), params);
	}

	public JsonNode execDiff(
			CloudMetadata v1, 
			CloudMetadata v2) throws Exception {
		
		CloudClassloader lv1 = new CloudClassloader(v1);
		CloudAPIAnalyzer av1 = new CloudAPIAnalyzer(v1, lv1);
		Map<String, JsonNode> mv1 =  av1.extractCloudAPIs();
		
		CloudClassloader lv2 = new CloudClassloader(v2);
		CloudAPIAnalyzer av2 = new CloudAPIAnalyzer(v2, lv2);
		Map<String, JsonNode> mv2 =  av2.extractCloudAPIs();
		
		ObjectNode json = new ObjectMapper().createObjectNode();
		
		ArrayNode newAPI = new ObjectMapper().createArrayNode();
		for (String api : mv2.keySet()) {
			if (!mv1.containsKey(api)) {
				newAPI.add(api);
			}
		}
		json.set("newAPI", newAPI);
		
		ArrayNode depreactedAPI = new ObjectMapper().createArrayNode();
		for (String api : mv1.keySet()) {
			if (!mv2.containsKey(api)) {
				depreactedAPI.add(api);
			}
		}
		json.set("depreactedAPI", depreactedAPI);
		
		ObjectNode changedAPI = new ObjectMapper().createObjectNode();
		for (String api : mv1.keySet()) {
			if (!mv1.get(api).equals(mv2.get(api))) {
				ObjectNode node = new ObjectMapper().createObjectNode();
				node.set(v1.getVersion(), mv1.get(api));
				node.set(v2.getVersion(), mv2.get(api));
				changedAPI.set(api, node);
			}
		}
		json.set("changedAPI", changedAPI);
		
		return json;
	}
	
	public String getStatus() {
		return "Ready";
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
