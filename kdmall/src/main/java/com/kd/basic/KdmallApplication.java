package com.kd.basic;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;


@MapperScan(basePackages = {"com.kd.basic.**"})
@SpringBootApplication(exclude = SecurityAutoConfiguration.class) // 시큐리티 기능해제.
public class KdmallApplication {

	public static void main(String[] args) {
		SpringApplication.run(KdmallApplication.class, args);
	}

}
