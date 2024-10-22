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
package io.github.doslab.wukong.utils;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.net.JarURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.util.Enumeration;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import com.fasterxml.jackson.databind.JsonNode;

/**
 * @author wuheng@iscas.ac.cn
 * @since  2024.03.27
 * 
 */
public class OpenAPIUtils {

	public static String getType(JsonNode rootNode, JsonNode schemaNode) {
		
		if (schemaNode.has("$ref")) {
            String ref = schemaNode.get("$ref").asText();
            // 处理引用，获取对应模式定义
            // 这里假设引用格式为 "#/components/schemas/XXX"
            String[] parts = ref.split("/");
            String schemaName = parts[parts.length - 1];
            JsonNode schemaDefinition = rootNode.at("/components/schemas/" + schemaName);
            // 对于获取到的模式定义，你可以进一步解析其内容
            System.out.println("  Schema Ref: " + schemaName);
            System.out.println("  Schema Definition: " + schemaDefinition);
            return schemaDefinition.toPrettyString();
        } else {
            String type = schemaNode.get("type").asText();
            System.out.println("  Type: " + type);
            return type;
        }
		
	}
	
	public static String getLifecycle(String operationID) {
		String lifecycle = operationID.replaceAll("_", "");
		return lifecycle.substring(0, 1).toLowerCase() 
							+ lifecycle.substring(1);
	}

}
