/**
 * Copyright (2020, ) Institute of Software, Chinese Academy of Sciences
 */
package io.github.doslab.wukong.utils;

import java.net.URI;

import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.config.RequestConfig;
import org.apache.hc.client5.http.impl.DefaultClientConnectionReuseStrategy;
import org.apache.hc.client5.http.impl.DefaultConnectionKeepAliveStrategy;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.HttpEntity;
import org.apache.hc.core5.util.Timeout;

/**
 * 
 * @author tangting18@otcaix.iscas.ac.cn
 * @since 2019.12.10
 * 
 * This code comes from Internet, it works well in our case.
 */
public class HttpUtils {

	public static CloseableHttpClient createDefaultHttpClient() {

		RequestConfig requestConfig = RequestConfig.custom()
							.setConnectTimeout(Timeout.ZERO_MILLISECONDS)
							.build();

		return HttpClients.custom()
				.setDefaultRequestConfig(requestConfig)
				.setKeepAliveStrategy(new DefaultConnectionKeepAliveStrategy())
				.setConnectionReuseStrategy(new DefaultClientConnectionReuseStrategy())
				.build();
	}
	
	
	public static HttpEntity getResponse(String url) throws Exception {
		CloseableHttpClient client = HttpUtils.createDefaultHttpClient();
		HttpGet request = new HttpGet(new URI(url));
		return client.execute(request).getEntity();
	}
	
}
