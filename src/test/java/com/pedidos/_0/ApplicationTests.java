package com.pedidos._0;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(properties = {
		"spring.rabbitmq.listener.simple.auto-startup=false"
})
class ApplicationTests {

	@Test
	void contextLoads() {
	}

}
