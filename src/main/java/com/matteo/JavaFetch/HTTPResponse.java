package com.matteo.JavaFetch;

import java.lang.reflect.Type;
import java.io.File;
import java.io.IOException;
import java.net.http.HttpResponse;
import com.google.gson.Gson;

public interface HTTPResponse extends HttpResponse<byte[]> {
	public default Object bodyAsObject(Class<?> objectClass) {
		final String body = bodyAsString();
		Gson gson = new Gson();
		Object gsonO = null;
		try {
			gsonO = gson.fromJson(body, objectClass);
		} catch (Exception e) {}

		return gsonO;
	}

	public default <T> T bodyAsObject(Type typeOfT) {
		final String body = bodyAsString();
		Gson gson = new Gson();
		T gsonObject = null;
		
		try {
			gsonObject = gson.fromJson(body, typeOfT);
		} catch (Exception e) {}
		
		return gsonObject;
	}

	public default String bodyAsString() {
		return new String(body());
	}

	public File getTempFileReference();

	public File getFile() throws IOException;
}
