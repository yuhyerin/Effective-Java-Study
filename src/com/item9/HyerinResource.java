package com.item9;

public class HyerinResource implements AutoCloseable {

	@Override
	public void close() throws RuntimeException { // RuntimeException을 던지도록 한다.
		System.out.println("close~");
		throw new IllegalStateException();
		
	}
	
	public void hello() {
		System.out.println("hello~");
//		throw new IllegalStateException();
	}

}
