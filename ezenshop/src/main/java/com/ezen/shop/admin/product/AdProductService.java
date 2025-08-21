package com.ezen.shop.admin.product;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.ezen.shop.admin.category.CategoryVO;
import com.ezen.shop.common.utils.SearchCriteria;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class AdProductService {

	private final AdProductMapper adProductMapper;
	
	public void pro_insert(ProductVO vo) {
		adProductMapper.pro_insert(vo);
	}
	
	public List<ProductVO> pro_list(SearchCriteria cri) {
		return adProductMapper.pro_list(cri);
	}
	
	public int getTotalCount(SearchCriteria cri) {
		return adProductMapper.getTotalCount(cri);
	}
	
	public void pro_sel_delete_2(int[] pro_num_arr) {
		adProductMapper.pro_sel_delete_2(pro_num_arr);
	}
	
	public void pro_sel_delete_3(int[] check, String pro_name) {
		// 위의 2개의 파라미터를 Map으로 작업.
		
		HashMap<String, Object> map = new HashMap<>();
		map.put("pro_num_arr", check);
		map.put("pro_name", pro_name);
		
		adProductMapper.pro_sel_delete_3(map);
	}
	
	public ProductVO pro_edit_form(Integer pro_num) {
		return adProductMapper.pro_edit_form(pro_num);
	}
	
	public void pro_edit_ok(ProductVO vo) {
		adProductMapper.pro_edit_ok(vo);
	}
	
	public void pro_delete(Integer pro_num) {
		adProductMapper.pro_delete(pro_num);
	}
}
