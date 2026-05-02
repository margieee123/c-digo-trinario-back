package com.spa.manager;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@SpringBootTest
class SpaManagerApplicationTests {

	@Test
	void contextLoads() {
	}
	@Test
	void generarHash() {
		BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
		System.out.println("Admin:          " + encoder.encode("admin123"));
		System.out.println("Terapeuta:      " + encoder.encode("terap123"));
		System.out.println("Recepcionista:  " + encoder.encode("recep123"));
	}
}
