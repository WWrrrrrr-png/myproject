package com.ezen.shop.admin.category;

import java.util.List;

//import org.apache.ibatis.annotations.Mapper;

//@Mapper
public interface AdCategoryMapper {

	// 1차 카테고리목록
	List<CategoryVO> getFirstCategoryList();
	
	// 2차 카테고리목록
	List<CategoryVO> getSecondCategoryList(Integer cate_prt_code);
	
	CategoryVO getFirstCategoryBySecondCategory(int secondCategory);
}
