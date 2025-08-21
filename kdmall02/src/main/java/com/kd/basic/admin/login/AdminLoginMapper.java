package com.kd.basic.admin.login;

import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface AdminLoginMapper {
     //AdminVo에서 주입    
	//insert (회원관리자 계정 등록)
	int join(AdminLoginVO vo); // 관리자 계정 등록 
	
	//AdminVo에서 주입  
	// Select(로그인용 조회)
	AdminLoginVO adin_ok(String ad_userid);
}
