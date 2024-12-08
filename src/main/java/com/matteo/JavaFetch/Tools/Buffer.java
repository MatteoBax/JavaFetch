package com.matteo.JavaFetch.Tools;

public class Buffer<T> {
	private T dato;
	private int turno = 0;

	public synchronized void write(T el) {
		while (turno != 0) {
			try {
				wait();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		dato = el;
		turno++;
		notifyAll();
	}

	public synchronized T read() {
		while (turno != 1) {
			try {
				wait();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		turno--;
		notifyAll();
		return dato;
	}
	
	public synchronized boolean hasData() {
		return turno == 1;
	}
}
