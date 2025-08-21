package com.ezen.shop.admin.category;

import java.util.List;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class AdCategoryService {

	private final AdCategoryMapper adCategoryMapper;
	
	public List<CategoryVO> getFirstCategoryList() {
		return adCategoryMapper.getFirstCategoryList();
	}
	
	public List<CategoryVO> getSecondCategoryList(Integer cate_prt_code) {
		return adCategoryMapper.getSecondCategoryList(cate_prt_code);
	}
	
	// 상품수정 폼에서 사용할 선택한 1차카테고리 정보
	public CategoryVO getFirstCategoryBySecondCategory(int secondCategory) {
		return adCategoryMapper.getFirstCategoryBySecondCategory(secondCategory);
	}
}
