/**
 * Copyright (2019, ) Institute of Software, Chinese Academy of Sciences
 */
package com.github.doslab.wukong;


import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.github.doslab.wukong.CloudMetadata;
import io.github.kubesys.client.KubernetesClient;

public class CommandTest  {

//	protected static KubernetesClient client = new KubernetesClient(
//									System.getenv("kubeUrl"), 
//									System.getenv("token"));
//	
//	
//	public static void main(String[] args) throws Exception {
//		JsonNode json = client.getResourceByNamespaceAndName("Account", "default", "jdcloud-vm-001");
//		String asText = json.get("spec").toPrettyString();
//		System.out.println(asText);
//		CloudMetadata ccm = new ObjectMapper().readValue(asText, CloudMetadata.class);
//		System.out.println(ccm);
//	}

}
