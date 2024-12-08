package com.matteo.JavaFetch;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient.Version;
import java.net.http.HttpHeaders;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Optional;
import javax.net.ssl.SSLSession;

public class HTTPResponseImpl implements HTTPResponse {
	private File tempFile = null;

	private final HttpResponse<byte[]> response;

	public HTTPResponseImpl(HttpResponse<byte[]> response) {
		this.response = response;
	}

	@Override
	public int statusCode() {
		return response.statusCode();
	}

	@Override
	public HttpRequest request() {
		return response.request();
	}

	@Override
	public Optional<HttpResponse<byte[]>> previousResponse() {
		return response.previousResponse();
	}

	@Override
	public HttpHeaders headers() {
		return response.headers();
	}

	@Override
	public byte[] body() {
		return response.body();
	}

	@Override
	public File getFile() throws IOException {
		final byte[] fileContent = body();
		tempFile = File.createTempFile("tmpFile", ".tmp");
		DataInputStream reader = new DataInputStream(new ByteArrayInputStream(fileContent));
		DataOutputStream writer = new DataOutputStream(new FileOutputStream(tempFile));
		byte[] buffer = new byte[4096];
		int bytesRead;
		while ((bytesRead = reader.read(buffer)) != -1) {
			writer.write(buffer, 0, bytesRead);
		}
		writer.close();
		reader.close();
		return tempFile;
	}

	@Override
	public File getTempFileReference() {
		return tempFile;
	}

	@Override
	public Optional<SSLSession> sslSession() {
		return response.sslSession();
	}

	@Override
	public URI uri() {
		return response.uri();
	}

	@Override
	public Version version() {
		return response.version();
	}
}
