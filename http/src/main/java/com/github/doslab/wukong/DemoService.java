/**
 * Copyright (2020, ) Institute of Software, Chinese Academy of Sciences
 */
package com.github.doslab.wukong;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.net.ssl.SSLContext;

import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.entity.DecompressingEntity;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManager;
import org.apache.hc.client5.http.socket.ConnectionSocketFactory;
import org.apache.hc.client5.http.socket.PlainConnectionSocketFactory;
import org.apache.hc.client5.http.ssl.NoopHostnameVerifier;
import org.apache.hc.client5.http.ssl.SSLConnectionSocketFactory;
import org.apache.hc.client5.http.ssl.TrustAllStrategy;
import org.apache.hc.core5.http.HttpEntity;
import org.apache.hc.core5.http.URIScheme;
import org.apache.hc.core5.http.config.RegistryBuilder;
import org.apache.hc.core5.ssl.SSLContexts;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.github.doslab.wukong.utils.ReqUtil;

import io.github.kubesys.devfrk.spring.cores.AbstractHttpHandler;
import io.github.kubesys.devfrk.tools.annotations.ServiceDefinition;

/**
 * @author wuheng@otcaix.iscas.ac.cn
 * 
 * @version 2.3.0
 * @since 2020.10.15
 * 
 **/
@ServiceDefinition
public class DemoService extends AbstractHttpHandler {

	/**
	 * clients
	 */
	protected Map<String, String> urls = new HashMap<>();

	protected Map<String, String> cookies = new HashMap<>();

	protected Map<String, String> tokens = new HashMap<>();

	protected static String JSON = "{\r\n" + "	\"listCluster\": {\r\n"
			+ "		\"url\": \"/kapis/resources.kubesphere.io/v1alpha3/clusters/{name}\",\r\n"
			+ "		\"type\": \"get\"\r\n" + "	}\r\n" + "}";

	protected static JsonNode config = null;

	public DemoService() throws Exception {
		config = new ObjectMapper().readTree(JSON);
	}

	/**
	 * @param id
	 * @param metadata
	 * @param map
	 * @return
	 * @throws Exception
	 */
	public void createClient(String id, String url, String type, String value) throws Exception {

		urls.put(id, url);
		if (type.equals("cookie")) {
			cookies.put(id, value);
		} else {
			tokens.put(id, value);
		}

	}

	public JsonNode getClients() throws Exception {

		ArrayNode list = new ObjectMapper().createArrayNode();

		for (String key : urls.keySet()) {
			ObjectNode node = new ObjectMapper().createObjectNode();
			node.put("id", key);
			node.put("url", urls.get(key));
			node.put("cookie", cookies.get(key));
			node.put("token", tokens.get(key));
			list.add(node);
		}

		return list;
	}

//	@ApiOperation(value = "根据分析的接口进行访问")
	public Object execRequest(
			// @ApiParam(value = "公有云账户ID", required = true, example = "asasd")
			String id,
			// @ApiParam(value = "生命周期管理Json，每个Json不一样", required = true, example =
			// "见createClient的输出")
			JsonNode data) throws Exception {

		String lifecycle = data.get("lifecycle").asText();
		if (!config.has(lifecycle)) {
			throw new Exception("unsupport lifecycle, please using getLifecycles for more detail");
		}

		JsonNode json = config.get(lifecycle);

		String path = json.get("url").asText();

		String type = json.get("type").asText();

		if (type.equals("get")) {

			if (data.has("params")) {

				Iterator<String> fieldNames = data.get("params").fieldNames();
				while (fieldNames.hasNext()) {
					String fieldName = fieldNames.next();
					String fieldValue = data.get("params").get(fieldName).asText();
					path = path.replace("{" + fieldName + "}", fieldValue);
				}

			}

			SSLContext sslContext = SSLContexts.custom().loadTrustMaterial(new TrustAllStrategy()).build();

			SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(sslContext, new NoopHostnameVerifier());

			PoolingHttpClientConnectionManager connManager = new PoolingHttpClientConnectionManager(
					RegistryBuilder.<ConnectionSocketFactory>create()
							.register(URIScheme.HTTP.id, PlainConnectionSocketFactory.getSocketFactory())
							.register(URIScheme.HTTPS.id, sslsf).build());

			CloseableHttpClient httpClient = HttpClients.custom().setConnectionManager(connManager).build();

			HttpGet httpget = ReqUtil.get(tokens.get(id), cookies.get(id), urls.get(id) + path);
			CloseableHttpResponse resp = httpClient.execute(httpget);
			
			HttpEntity entity = resp.getEntity();
			
			if (entity instanceof DecompressingEntity) {
				InputStream content = entity.getContent();

				StringBuilder sb = new StringBuilder();
				try (BufferedReader reader = new BufferedReader(
						new InputStreamReader(content, StandardCharsets.UTF_8))) {
					String line;
					while ((line = reader.readLine()) != null) {
						sb.append(line);
					}
				}

//				String jsonString = sb.toString();
//				System.out.println(jsonString);
//				return new ObjectMapper().readTree(jsonString);
				return sb.toString();

			}
			return new ObjectMapper().readTree(entity.getContent());
		}

		return null;
	}

	public void readFile(
			// @ApiParam(value = "公有云账户ID", required = true, example = "asasd")
			String path) throws Exception {

		JsonNode newOne = new ObjectMapper().readTree(new File(path));
		merge(config, newOne);

	}

	public JsonNode listAll() {
		return config;
	}

	private static JsonNode merge(JsonNode mainNode, JsonNode updateNode) {

		Iterator<String> fieldNames = updateNode.fieldNames();
		while (fieldNames.hasNext()) {

			String fieldName = fieldNames.next();
			System.out.println(fieldName);
			JsonNode jsonNode = mainNode.get(fieldName);

			if (jsonNode != null && jsonNode.isObject()) {
				merge(jsonNode, updateNode.get(fieldName));
			} else {
				if (mainNode instanceof ObjectNode) {

					JsonNode value = updateNode.get(fieldName);
					((ObjectNode) mainNode).put(fieldName, value);
				}
			}

		}

		return mainNode;
	}
}
