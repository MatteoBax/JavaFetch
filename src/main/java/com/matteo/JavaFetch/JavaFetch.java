package com.matteo.JavaFetch;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpClient.Redirect;
import java.net.http.HttpClient.Version;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.time.Duration;
import java.util.Arrays;
import java.util.Collection;
import java.util.function.Consumer;

import com.matteo.JavaFetch.Tools.Buffer;
import com.matteo.JavaFetch.Tools.Tools;

public class JavaFetch {
	private String url;
	private String method;
	private String formData;
	private Buffer<Exception> exceptionBuffer;
	private boolean canRead = false;
	private File tempFetchFile;

	public JavaFetch(String url, String method, Collection<RequestParam> data) {
		this.url = url;
		this.method = method;
		if (data == null || data.size() == 0) {
			this.formData = null;
		} else {
			this.formData = Tools.jsonToFormData(Tools.convertRequestParamsToJSON(data));
		}
		this.exceptionBuffer = new Buffer<Exception>();
	}

	public JavaFetch(String url, String method, RequestParam[] data) {
		this(url, method, Arrays.asList(data));
	}

	public JavaFetch(String url, String method, RequestParam data) {
		this(url, method, new RequestParam[] { data });
	}

	public JavaFetch(String url, String method) {
		this(url, method, new RequestParam[0]);
	}

	private HttpClient createHttpClient() {
		HttpClient.Builder clientBuilder = HttpClient.newBuilder();
		clientBuilder.version(Version.HTTP_1_1);
		clientBuilder.followRedirects(Redirect.NORMAL);
		clientBuilder.connectTimeout(Duration.ofSeconds(20));
		return clientBuilder.build();
	}

	private HttpRequest createHttpRequest() {
		HttpRequest.Builder builder = HttpRequest.newBuilder();
		String tmpUrl = url;
		if(method.toUpperCase().equals("GET")) {
			tmpUrl = url + "?" + formData;
		}
		
		builder.uri(URI.create(tmpUrl));
		
		if (formData != null && !method.toUpperCase().equals("GET")) {
			builder.header("Content-Type", "application/x-www-form-urlencoded");
			builder.method(method, BodyPublishers.ofString(formData));
		} else {
			builder.method(method, BodyPublishers.ofString(""));
		}
		return builder.build();
	}

	private HTTPResponse send() throws IOException, InterruptedException {
		HttpClient client = createHttpClient();
		HttpResponse<byte[]> response = client.send(createHttpRequest(), BodyHandlers.ofByteArray());
		return new HTTPResponseImpl(response);
	}

	public JavaFetch then(Consumer<HTTPResponse> handler) {
		HTTPResponse response = null;
		try {
			response = send();
		} catch (Exception e) {
			exceptionBuffer.write(e);
			canRead = true;
		}

		if (response != null) {
			handler.accept(response);
			File tempFile = response.getTempFileReference();
			if (tempFile != null && tempFile.exists()) {
				tempFile.delete();
			}
		}

		return this;
	}

	public void onException(Consumer<Exception> handler) {

		while (canRead) {
			handler.accept(exceptionBuffer.read());
			canRead = false;
		}
	}
}
