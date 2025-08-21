package com.ezen.shop.common.config;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.ezen.shop.common.interceptor.LoginInterceptor;

import lombok.RequiredArgsConstructor;

// 인터셉터 클래스가 관리되는 URI주소 설정작업
@RequiredArgsConstructor
@Component
public class WebMvcConfig implements WebMvcConfigurer{

	private final LoginInterceptor loginInterceptor;
	
	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		
		registry.addInterceptor(loginInterceptor)
			.addPathPatterns("/cart/**", "/order/**")
			.excludePathPatterns("/", "/member/login", "/member/join") // 제외주소
			.excludePathPatterns("/order/image_display"); // 웹메일에서 사용
	}
}
