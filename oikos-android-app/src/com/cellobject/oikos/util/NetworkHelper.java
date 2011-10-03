/**
 * This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as
 * published by the Free Software Foundation; version 2 of the License. This program is distributed in the hope that it will be
 * useful, but WITHOUT ANY WARRANTY. See the GNU General Public License for more details. Copyright (C) 2011 Oikos.no, developed
 * by InformedIndividual.org & CellObject.com
 */
package com.cellobject.oikos.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.security.KeyStore;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.List;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HTTP;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

public class NetworkHelper {
	private final Context context;

	private final HttpClient client;

	/*
	 * Always verify the host - don't check for certificate.
	 */
	public static final HostnameVerifier DO_NOT_VERIFY = new HostnameVerifier() {
		@Override
		public boolean verify(final String hostname, final SSLSession session) {
			return true;
		}
	};

	public NetworkHelper(final Context context) {
		super();
		this.context = context;
		client = createHttpClient();
	}

	public boolean isOnline() {
		final ConnectivityManager connectivityManager = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
		final NetworkInfo netInfo = connectivityManager.getActiveNetworkInfo();
		if ( (netInfo != null) && netInfo.isConnectedOrConnecting()) {
			return true;
		}
		return false;
	}

	/*
	 * reuse this object to ensure that sessions are working. Trusting all certificates using HttpClient over HTTPS.
	 */
	public HttpClient createHttpClient() {
		try {
			final KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
			trustStore.load(null, null);
			final SSLSocketFactory sf = new IISSLSocketFactory(trustStore);
			sf.setHostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
			final HttpParams params = new BasicHttpParams();
			HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
			HttpProtocolParams.setContentCharset(params, HTTP.UTF_8);
			final SchemeRegistry registry = new SchemeRegistry();
			registry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
			registry.register(new Scheme("https", sf, 443));
			final ClientConnectionManager ccm = new ThreadSafeClientConnManager(params, registry);
			return new DefaultHttpClient(ccm, params);
		} catch (final Exception e) {
			return new DefaultHttpClient();
		}
	}

	/**
	 * Sends HTTP GET request and returns body of response from server as String
	 */
	public String httpGet(final String urlToServer) throws IOException {
		final HttpGet request = new HttpGet(urlToServer);
		final ResponseHandler<String> responseHandler = new BasicResponseHandler();
		final String responseBody = client.execute(request, responseHandler);
		return responseBody;
	}

	/**
	 * Sends HTTP GET request and returns body of response from server as String
	 */
	public String httpGet(final String urlToServer, final List<BasicNameValuePair> parameterList) throws IOException {
		final String url = urlToServer + "?" + URLEncodedUtils.format(parameterList, null);
		final HttpGet request = new HttpGet(url);
		final ResponseHandler<String> responseHandler = new BasicResponseHandler();
		final String responseBody = client.execute(request, responseHandler);
		return responseBody;
	}

	/**
	 * Sends HTTP POST request and returns body of response from server as String
	 */
	public String httpPost(final String urlToServer, final List<BasicNameValuePair> parameterList) throws IOException {
		final HttpPost request = new HttpPost(urlToServer);
		final ResponseHandler<String> responseHandler = new BasicResponseHandler();
		final UrlEncodedFormEntity formParameters = new UrlEncodedFormEntity(parameterList);
		request.setEntity(formParameters);
		final String responseBody = client.execute(request, responseHandler);
		return responseBody;
	}

	/**
	 * Sends HTTP GET request and returns body of response from server as HTTPResponse object. The response object contains HTTP
	 * headers and body. This method also writes both header and body to log. Use LogCat to view output. Also notice that in this
	 * case the body is read line by line from a stream (in.readLine()). In most cases one of the above methods should suffice.
	 */
	public HttpResponse httpGetWithHeader(final String urlToServer, final List<BasicNameValuePair> parameterList) throws IOException {
		final String url = urlToServer + "?" + URLEncodedUtils.format(parameterList, null);
		BufferedReader in = null;
		try {
			final HttpGet request = new HttpGet(url);
			final HttpResponse response = client.execute(request);
			/* Let's get/log the HTTP-header * */
			final Header[] headers = response.getAllHeaders();
			for (final Header h : headers) {
				Log.i("HTTPHEADER", "Header names: " + h.getName());
				Log.i("HTTPHEADER", "Header Value: " + h.getValue());
			}
			/* Read body of HTTP-message from server */
			in = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
			final StringBuffer body = new StringBuffer("");
			String line = null;
			while ( (line = in.readLine()) != null) {
				body.append(line + "\n");
				Log.i("HTTPBODY", line);
			}
			return response;
		} finally {
			try {
				in.close();
			} catch (final IOException ie) {
				Log.e("HTTP", ie.toString());
			}
		}
	}

	/**
	 * Trust every server - don't check for any certificate.
	 */
	public static void trustAllHosts() {
		// Create a trust manager that does not validate certificate chains
		final TrustManager[] trustAllCerts = new TrustManager[]{new X509TrustManager() {
			@Override
			public X509Certificate[] getAcceptedIssuers() {
				return new X509Certificate[]{};
			}

			@Override
			public void checkClientTrusted(final X509Certificate[] chain, final String authType) throws CertificateException {
			}

			@Override
			public void checkServerTrusted(final X509Certificate[] chain, final String authType) throws CertificateException {
			}
		}};
		// Install the all-trusting trust manager
		try {
			final SSLContext sc = SSLContext.getInstance("TLS");
			sc.init(null, trustAllCerts, new SecureRandom());
			HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
		} catch (final Exception e) {
			e.printStackTrace();
		}
	}
}
