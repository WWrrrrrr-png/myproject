package com.kd.basic;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Controller
public class HomeController {

	private final TestMapper testMapper;
	
	@ResponseBody
	@GetMapping("/getTime")
	public String getTime() {
		
		return testMapper.getTime();
	}
	
	// 기본페이지
	@GetMapping("/")
	public String home() {
		
		return "index";
	}
	
	@GetMapping("/sub1")
	public String sub1() {
		
		return "sub1";
	}
	
	@GetMapping("/sub2")
	public String sub2() {
		
		return "sub2";
	}
}
