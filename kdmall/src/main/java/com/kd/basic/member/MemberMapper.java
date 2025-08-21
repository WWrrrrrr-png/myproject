package com.kd.basic.member;


public interface MemberMapper {

	void join(MemberDTO dto);
	
	String idCheck(String mbsp_id);
	
	MemberDTO login(String mbsp_id);
	
	MemberDTO modify(String mbsp_id);
}
