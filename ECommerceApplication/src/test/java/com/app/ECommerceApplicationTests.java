package com.app;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

@SpringBootTest
class ECommerceApplicationTests {

	@Test
	void contextLoads() {
	}

	@Test
	void mainMethod_ShouldRunWithoutExceptions() {
		assertDoesNotThrow(() ->
				ECommerceApplication.main(new String[] {})
		);
	}

}
