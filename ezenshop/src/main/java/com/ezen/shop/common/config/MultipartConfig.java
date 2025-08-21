package com.ezen.shop.common.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.multipart.MultipartResolver;
import org.springframework.web.multipart.support.StandardServletMultipartResolver;

// 파일업로드 작업을 위한 bean생성작업
@Configuration
public class MultipartConfig {

	// 스프링부트 3.0 파일업로드시 영향을 받지않음.
//	@Bean
//	public MultipartResolver multipartResolver() {
//		return new StandardServletMultipartResolver();
//	}
}
