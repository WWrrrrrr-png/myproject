package com.ezen.shop;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.ezen.shop.admin.category.AdCategoryService;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Controller
public class HomeController {

	
	private final AdCategoryService adCategoryService;
	
	// 기본주소 - 메인페이지
	@GetMapping("/")
	public String home(Model model) {
		
		//1차 카테고리목록
		model.addAttribute("cate_list", adCategoryService.getFirstCategoryList());
		
		
		return "index"; // index.html
	}
}
