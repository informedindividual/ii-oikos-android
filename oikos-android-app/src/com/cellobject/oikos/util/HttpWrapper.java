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
import java.util.List;
import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import android.util.Log;

public class HttpWrapper {
	private final HttpClient client = new DefaultHttpClient();//reuse this object to ensure that sessions are working!

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
}
