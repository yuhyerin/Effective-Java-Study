package com.item9;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class TryWithResources {

	public static void main(String[] args) throws Exception {

//		temp1();
//		temp2();
//		temp3();
//		temp4();
		temp5();
//		firstLineOfFile1(fileName);
//		copy(fileName, resultFileName);
//		firstLineOfFile2(fileName, resultFileName);
	}

	/**
	 * try-catch-finally 로 반납하는 경우 
	 * - 상황 : hello, close 에서 둘 다 에외가 발생함 
	 * - 결과 : hello 에러 스택 트레이스 누락 발생
	 */
	static void temp1() {
		HyerinResource resource = null;
		try {
			resource = new HyerinResource();
			resource.hello();
		} finally {
			if (resource != null) {
				resource.close();
			}
		}
	}
	
	/**
	 * try-with-resource 구문으로 변경. 
	 * - 상황 : hello, close 에서 둘 다 예외가 발생함 
	 * - 결과 : 두 경우의 에러 스택 트레이스가 다 보인다.
	 * 단, close 에서 발생한 예외는 스택 추적 내역에 Suppressed(숨겨졌다)는 꼬리표를 달고 출력됨.*/
	static void temp2() {
		try(HyerinResource resource = new HyerinResource()){
			resource.hello();
		}
	}
	
	/**
	 * try-catch-finally 로 반납하는 경우. 
	 * - 상황 : hello에서는 예외 발생하지 않고, close에서만 발생합니다.
	 * - 결과 : 자원 반납 누락
	 * close 1회 호출됨.
	 * */
	static void temp3() {
		HyerinResource resource1 = null;
		HyerinResource resource2 = null;
		try {
			resource1 = new HyerinResource();
			resource2 = new HyerinResource();
			resource1.hello();
			resource2.hello();
		} finally {
			if (resource1 != null) {
				resource1.close(); // 여기서 예외 발생해서 자원2는 반납되지 않음.
			}
			if (resource2 != null) {
				resource2.close();
			}

		}
	}
	
	/** try-with-resources로 반납하는 경우.
	 * 상황 : hello 에서는 예외 발생하지 않고, close 에서만 발생.
	 * 결과 : 모든 자원을 반납함. 
	 * close 2회 호출. */
	static void temp4() {
		try(HyerinResource resource1 = new HyerinResource();
			HyerinResource resource2 = new HyerinResource();){
			resource1.hello();
			resource2.hello();
			
		}
	}
	
	/** try-with-resource 구문을 사용하는데, 
	 * catch, finally도 함께 쓰인다면 어떻게 될까?
	 * 
	 * hello~ hello~ close~ close~ catch 블록~ Finally 블록~ 전부 출력됨...
	 * 자원 반납은 정상적으로 되지만,의도한 대로 동작하지 않을 수 있으므로 주의하자!!! 
	 * 
	 * close에서 예외 발생시에  Exception 던져서 로그찍고 끝내려고 했을 수 있는데, 
	 * 잘 모르고 catch블록을 덧붙이거나 finally 블록을 사용해서 
	 * close에서 예외가 발생했음에도 불구하고 finally 로직까지 쭉 흘러갈 수 있다.
	 * 
	 * 
	 * */
	static void temp5() {
		try(HyerinResource resource1 = new HyerinResource();
			HyerinResource resource2 = new HyerinResource();){
			resource1.hello();
			resource2.hello();
			
		}catch(IllegalStateException e) {
			System.out.println("catch 블록~");
//			e.printStackTrace();
		}finally {
			System.out.println("Finally 블록~");
		}
	}
	
	/** try-with-resource 구문 사용할 때, 
	 * 자원에 대한 선언은 try 밖에다 해도 제대로 반납이 될 까???
	 * No!!! 문법 오류임... (ㅠㅅㅠ) */
	static void temp6() {
//		HyerinResource resource1 = null;
//		HyerinResource resource2 = null;
//		try(resource1 = new HyerinResource(); resource2 = new HyerinResource();){
		try(HyerinResource resource1 = new HyerinResource();
			HyerinResource resource2 = new HyerinResource();){
			resource1.hello();
			resource2.hello();
			
		}
	}
	

	/** 아래는 교제 코드 ... */
	
	/** [코드9-3] try-with-resources 자원을 회수하는 최선책 */
	static String firstLineOfFile1(String path) throws IOException {
		try (BufferedReader br = new BufferedReader(new FileReader(path))) {
			return br.readLine();
		}
	}

	/**
	 * [코드9-4] 복수의 자원을 처리하는 try-with-resources
	 */
	static final int BUFFER_SIZE = 1000;

	static void copy(String src, String dst) throws IOException {
		try (InputStream in = new FileInputStream(src); 
				OutputStream out = new FileOutputStream(dst);) {
			byte[] buf = new byte[BUFFER_SIZE];
			int n;
			while ((n = in.read(buf)) >= 0) {
				out.write(buf, 0, n);
			}
		}
	}

	/**
	 * [코드9-5] try-with-resource 를 catch절과 함께 쓰는 모습.
	 * try문을 중첩하지 않고도 다수의 예외를 처리할 수 있다.
	 */
	static String firstLineOfFile2(String path, String defaultVal) {
		try (BufferedReader br = new BufferedReader(new FileReader(path))) {
			return br.readLine();
		} catch (IOException e) {
			return defaultVal;
		}
	}

	/**
	 * 정리 우리는 다음과 같은 이유로 
	 * try-catch-finally 가 아닌 try-with-resources 구문을 사용해야 한다. 
	 * 1. 코드를 간결하게 만들 수 있다. 
	 * 2. 번거로운 자원 반납 작업을 하지 않아도 된다. 
	 * 3. 실수로 자원을 반납하지 못하는 경우를 방지할 수 있다. 
	 * 4. 에러로 자원을 반납하지 못하는 경우를 방지할 수 있다. 
	 * 5. 모든 에러에 대한 스택 트레이스를 남길 수 있다.
	 */
}
