/**
 * This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as
 * published by the Free Software Foundation; version 2 of the License. This program is distributed in the hope that it will be
 * useful, but WITHOUT ANY WARRANTY. See the GNU General Public License for more details. Copyright (C) 2011 Oikos.no, developed
 * by InformedIndividual.org & CellObject.com
 */
package com.cellobject.oikos.util;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import org.apache.http.conn.ssl.SSLSocketFactory;

public class IISSLSocketFactory extends SSLSocketFactory {
	SSLContext sslContext = SSLContext.getInstance("TLS");

	public IISSLSocketFactory(final KeyStore truststore) throws NoSuchAlgorithmException, KeyManagementException, KeyStoreException, UnrecoverableKeyException {
		super(truststore);
		final TrustManager tm = new X509TrustManager() {
			@Override
			public void checkClientTrusted(final X509Certificate[] certificate, final String authType) throws CertificateException {
			}

			@Override
			public void checkServerTrusted(final X509Certificate[] certificate, final String authType) throws CertificateException {
			}

			@Override
			public X509Certificate[] getAcceptedIssuers() {
				return null;
			}
		};
		sslContext.init(null, new TrustManager[]{tm}, null);
	}

	@Override
	public Socket createSocket(final Socket socket, final String host, final int port, final boolean autoClose) throws IOException, UnknownHostException {
		return sslContext.getSocketFactory().createSocket(socket, host, port, autoClose);
	}

	@Override
	public Socket createSocket() throws IOException {
		return sslContext.getSocketFactory().createSocket();
	}
}
