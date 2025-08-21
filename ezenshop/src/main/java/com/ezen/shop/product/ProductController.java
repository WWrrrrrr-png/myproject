package com.ezen.shop.product;

import java.io.File;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

import com.ezen.shop.admin.category.AdCategoryService;
import com.ezen.shop.admin.product.ProductVO;
import com.ezen.shop.common.utils.Criteria;
import com.ezen.shop.common.utils.FileUtils;
import com.ezen.shop.common.utils.PageMaker;
import com.ezen.shop.common.utils.SearchCriteria;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@RequestMapping("/product/*")
@Controller
public class ProductController {

	private final ProductService productService;
	private final AdCategoryService adCategoryService;
	
	// 상품이미지 관련작업기능
	private final FileUtils fileUtils;
	
	/* 생성자 주입
	public AdProductController(FileUtils fileUtils) {
		this.fileUtils = fileUtils;
	}
	*/
	
	@Value("${com.ezen.upload.path}")
	private String uploadPath;
	
	@Value("${com.ezen.upload.ckeditor.path}")
	private String uploadCKPath;
	
	// 2차카테고리에 따른 상품리스트는 페이징기능 사용, 검색기능 미사용
	@GetMapping("/pro_list")
	public void pro_list(SearchCriteria cri, @ModelAttribute("cate_name") String cate_name, Integer cate_code, Model model) throws Exception {
		
		log.info("카테고리명: " + cate_name); // 맨투맨&후드티 &가 파라미터의 구분자로 사용이되어 "맨투맨" 출력 
		
		//1차 카테고리목록
		model.addAttribute("cate_list", adCategoryService.getFirstCategoryList());
		
		List<ProductVO> pro_list = productService.getProductListBysecondCategory(cri, cate_code);
		
		
//		pro_list.forEach(pro -> pro.setPro_up_folder(pro.getPro_up_folder().replace("\\", File.separator)));
		
		//2차 카테고리의 상품목록
		model.addAttribute("pro_list", pro_list);
		
		// 페이징작업
		PageMaker pageMaker = new PageMaker();
		pageMaker.setCri(cri);
		
		pageMaker.setTotalCount(productService.getCountProductListBysecondCategory(cate_code));
		
		model.addAttribute("pageMaker", pageMaker);
	}
	
	// 상품목록 이미지출력하기.. 클라이언트에서 보낸 파라미터명 스프링의 컨트롤러에서 받는 파라미터명이 일치해야 한다.
	@GetMapping("/image_display")
	public ResponseEntity<byte[]> image_display(String dateFolderName, String fileName) throws Exception {
		
		return fileUtils.getFile(uploadPath + File.separator + dateFolderName, fileName);
	}
	
	//상품상세정보
	@GetMapping("/pro_info")
	public void pro_info(@ModelAttribute("cate_name") String cate_name, Integer pro_num, Model model) throws Exception {
		
		log.info("카테고리명: " + cate_name);
		
		// 상품정보. 
		ProductVO productVO = productService.pro_info(pro_num);
		// 이미지파일의 날짜폴더 \를 /변환하는 작업
		productVO.setPro_up_folder(productVO.getPro_up_folder().replace("\\", File.separator));
		
		model.addAttribute("productVo", productVO);
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
}
