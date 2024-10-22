/**
 * Copyright (2024, ) Institute of Software, Chinese Academy of Sciences
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */
package io.github.doslab.wukong;

import java.io.File;
import java.io.IOException;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

import io.github.doslab.wukong.utils.OpenAPIUtils;

/**
 * @author wuheng@iscas.ac.cn
 * @since  2024.03.27
 */
public class OpenAPIAnalyzer {

	public static void main(String[] args) {
		
		ObjectNode allNodes = new ObjectMapper().createObjectNode();
		
		try {
			
            // 读取OpenAPI规范文件（YAML格式）
            File file = new File("openapi/daocloud/容器管理 OpenAPIv0.25.0.json");
            ObjectMapper objectMapper = new ObjectMapper(new YAMLFactory());
            JsonNode rootNode = objectMapper.readTree(file);

            // 提取路径、HTTP方法和参数模型
            JsonNode pathsNode = rootNode.get("paths");
            pathsNode.fields().forEachRemaining(entry -> {
            	
            	ObjectNode valueNode = new ObjectMapper().createObjectNode();
            	
                String path = entry.getKey();
                JsonNode methodsNode = entry.getValue();
                methodsNode.fields().forEachRemaining(methodEntry -> {
                    String httpMethod = methodEntry.getKey();
                    String operationId = methodEntry.getValue().get("operationId").asText();
                    JsonNode parametersNode = methodEntry.getValue().get("parameters");
                    
                    valueNode.put("url", path);
                    valueNode.put("type", httpMethod);
                    
                    System.out.println("Path: " + path);
                    System.out.println("HTTP Method: " + httpMethod);
                    System.out.println("Operation ID: " + operationId);
                    
                    ArrayNode plist = new ObjectMapper().createArrayNode();
                    if (parametersNode != null) {
                        System.out.println("Parameters:");
                        parametersNode.forEach(parameter -> {
                        	
                        	ObjectNode param = new ObjectMapper().createObjectNode();
                        	
                            String name = parameter.get("name").asText();
                            String type = parameter.has("schema")  
                            		? OpenAPIUtils.getType(rootNode, parameter.get("schema")) 
                            		: parameter.get("type").asText();
                            System.out.println("  Name: " + name);
                            System.out.println("  Type: " + type);
                            
                            param.put("name", name);
                            param.put("type", type);
                            
                            plist.add(param);
                        });
                    }
                    
                    valueNode.set("params", plist);
                    allNodes.set(operationId, valueNode);
                    System.out.println();
                });
            });

        } catch (IOException e) {
            e.printStackTrace();
        }
		
		System.out.println(allNodes.toPrettyString());
	}
	
	
}
