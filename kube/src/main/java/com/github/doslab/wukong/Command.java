/**
 * Copyright (2019, ) Institute of Software, Chinese Academy of Sciences
 */
package com.github.doslab.wukong;

import java.io.File;
import java.util.Map;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import io.github.doslab.wukong.CloudAPIAnalyzer;
import io.github.doslab.wukong.CloudClassloader;
import io.github.doslab.wukong.CloudGenerator;
import io.github.doslab.wukong.CloudMetadata;
import io.github.kubesys.client.KubernetesClient;

public class Command  {

	protected static KubernetesClient client = new KubernetesClient(
									System.getenv("kubeUrl"), 
									System.getenv("token"));
	
	public static void generate(CloudMetadata ccm) throws Exception {
		CloudGenerator cmd  = new CloudGenerator(ccm, new CloudClassloader(ccm));
		cmd.generateJar();
	}
	
	public static void register(CloudMetadata ccm) throws Exception {
		
		CloudAPIAnalyzer cmd = new CloudAPIAnalyzer(ccm, new CloudClassloader(ccm));
		Map<String, JsonNode> nodes = cmd.extractCloudAPIs();
		for (String key: nodes.keySet()) {
			ObjectNode json = new ObjectMapper().createObjectNode();
			json.put("apiVersion", "doslab.io/v1");
			json.put("kind", "Api");
			
			ObjectNode meta = new ObjectMapper().createObjectNode();
			meta.put("name", ccm.getKind().toLowerCase() + "-" + key.toLowerCase());
			json.set("metadata", meta);
			json.put("type", ccm.getKind());
			
			ObjectNode spec = new ObjectMapper().createObjectNode();
			spec.set(key, nodes.get(key));
			json.set("spec", spec);
			
			System.out.println(json.toPrettyString());
			client.createResource(json);
		}
	}
	
	public static void main(String[] args) throws Exception {
		CloudMetadata ccm = new ObjectMapper().readValue(
				new File("conf/vm/aliyun-ecs.json"), 
				CloudMetadata.class);
		
		register(ccm);
	}

}
