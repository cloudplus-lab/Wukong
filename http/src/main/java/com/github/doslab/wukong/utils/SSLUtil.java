/**
 * Copyright (2023, ) Institute of Software, Chinese Academy of Sciences
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
 */
package com.github.doslab.wukong.utils;

import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.logging.Logger;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.KeyManager;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.hc.client5.http.ssl.NoopHostnameVerifier;

/**
 * 
 * it is used for supporting ssl connections
 * 
 * @author wuheng@iscas.ac.cn
 * @since 1.0.5
 **/
public class SSLUtil {

	public static final Logger m_logger = Logger.getLogger(SSLUtil.class.getName());

	private SSLUtil() {
		super();
	}

	/**
	 * @return SocketFactory
	 */
	public static org.apache.hc.client5.http.ssl.SSLConnectionSocketFactory 
				createSocketFactory(KeyManager[] km, TrustManager[] tm) {
		return new org.apache.hc.client5.http.ssl.SSLConnectionSocketFactory(
				createX509SocketFactory(km, tm), new NoopHostnameVerifier());
	}

	/**
	 * @return SocketFactory
	 */
	public static SSLSocketFactory createX509SocketFactory(KeyManager[] km, TrustManager[] tm) {
		try {
			SSLContext sc = SSLContext.getInstance("TLS");
			sc.init(km, tm == null ? createDefaultTrustManager(): tm, new SecureRandom());
			return sc.getSocketFactory();
		} catch (Exception ex) {
			m_logger.severe("unable to create X509 SocketFactory, " + ex);
			return null;
		}
	}

	/**
	 * @return TrustManager[]
	 */
	public static TrustManager[] createDefaultTrustManager() {
		return new TrustManager[] { new X509TrustManager() {

			@Override
			public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
				m_logger.info("client is trusted.");
			}

			@Override
			public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
				m_logger.info("server is trusted.");
			}

			@Override
			public X509Certificate[] getAcceptedIssuers() {
				return new X509Certificate[] {};
			}

		} };
	}

	/**
	 * @return HostnameVerifier
	 */
	public static HostnameVerifier createDefaultHostnameVerifier() {
		return new HostnameVerifier() {

			public String toString() {
				return super.toString();
			}

			public boolean verify(String hostname, SSLSession session) {
				m_logger.info("hostname is trusted.");
				return (hostname != null);
			}

		};
	}
}
