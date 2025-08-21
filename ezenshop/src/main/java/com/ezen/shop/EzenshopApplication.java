package com.ezen.shop;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;

@MapperScan(basePackages = {"com.ezen.shop.**"}) // mapper 인터페이스가 존재하는 패키지설정
@SpringBootApplication(exclude = SecurityAutoConfiguration.class) // 시큐리티 기능 해제.
public class EzenshopApplication {

	public static void main(String[] args) {
		SpringApplication.run(EzenshopApplication.class, args);
	}

}
