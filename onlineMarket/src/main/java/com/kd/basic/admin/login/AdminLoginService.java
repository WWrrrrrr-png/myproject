package com.kd.basic.admin.login;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class AdminLoginService {

	private final AdminLoginMapper adminLoginMapper;
	
	public AdminLoginDTO admin_ok(String ad_userid) {
		return adminLoginMapper.admin_ok(ad_userid);
	}
	
}
