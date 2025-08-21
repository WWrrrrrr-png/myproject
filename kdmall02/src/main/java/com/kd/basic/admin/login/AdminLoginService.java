package com.kd.basic.admin.login;

import org.springframework.stereotype.Service;


import lombok.RequiredArgsConstructor;

@Service 
@RequiredArgsConstructor
public class AdminLoginService {

	//final:값변경 x     
	//administerMapper 인터페이스 주입 받음  
	//Mapper는 DB와 직접 통신(SQL 실행)
	private final AdminLoginMapper adminLoginMapper;
	
	public AdminLoginVO findById(String ad_userid) {
	    return adminLoginMapper.adin_ok(ad_userid);
	}

	public AdminLoginVO login(String ad_userid) {
		// TODO Auto-generated method stub
		return null;
	}

	
}
