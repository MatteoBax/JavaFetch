package com.matteo.JavaFetch;

public class App {
	public static void main(String[] args) throws NoSuchFieldException, SecurityException {
		new JavaFetch("https://www.google.com", "GET").then((response) -> {
			System.out.println("RESP");
		}).onException((err) -> {
			err.printStackTrace();
		});

	}
}
