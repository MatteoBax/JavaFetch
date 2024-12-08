package com.matteo.JavaFetch.Tools;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Collection;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.matteo.JavaFetch.RequestParam;

public class Tools {
	public static String jsonToFormData(JsonObject json) {
		StringBuilder formData = new StringBuilder();

		for (String key : json.keySet()) {
			if (formData.length() > 0) {
				formData.append("&");
			}
			String encodedKey = URLEncoder.encode(key, StandardCharsets.UTF_8);
			JsonElement valueElement = json.get(key);
			String encodedValue;

			if (valueElement.isJsonPrimitive() && valueElement.getAsJsonPrimitive().isString()) {
				// mi rimuove i doppi apici se è una stringa
				encodedValue = URLEncoder.encode(valueElement.getAsString(), StandardCharsets.UTF_8);
			} else {
				encodedValue = URLEncoder.encode(valueElement.toString(), StandardCharsets.UTF_8);
			}
			formData.append(encodedKey).append("=").append(encodedValue);
		}

		return formData.toString();
	}

	// controlla se il JSON deserializzato è uguale al JSON originale
	public static boolean matchesJson(JsonObject jsonObject, Object obj) {
		JsonObject objJson = JsonParser.parseString(new Gson().toJson(obj)).getAsJsonObject();
		for (String key : jsonObject.keySet()) {
			JsonElement jsonValue = jsonObject.get(key);
			JsonElement objValue = objJson.get(key);
			if (!jsonValue.equals(objValue)) {
				return false;
			}
		}
		return true;
	}

	public static JsonObject convertRequestParamsToJSON(Collection<RequestParam> requestParams) {
		JsonObject o = new JsonObject();
		Gson gson = new Gson();
		for (RequestParam param : requestParams) {
			Object value = param.getValue();
			if (value instanceof Number) {
				o.addProperty(param.getName(), (Number) value);
			} else if (value instanceof Boolean) {
				o.addProperty(param.getName(), (Boolean) value);
			} else if (value instanceof Character) {
				o.addProperty(param.getName(), (Character) value);
			} else if (value instanceof String) {
				o.addProperty(param.getName(), (String) value);
			} else {
				JsonElement el = gson.toJsonTree(value);
				o.add(param.getName(), el);
			}
		}
		return o;

	}

	public static boolean isCommonType(Object o) {
		return (o != null && (o instanceof Short || o instanceof Integer || o instanceof Long || o instanceof BigInteger
				|| o instanceof Float || o instanceof Double || o instanceof BigDecimal || o instanceof String));
	}
}
