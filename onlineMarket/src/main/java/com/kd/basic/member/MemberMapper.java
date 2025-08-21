package com.kd.basic.member;


public interface MemberMapper {

	/*
	 Mapper인터페이스에서 기능이 insert,update,delete로 사용하는
	 메소드는 리턴타입을 보통 void로 한다. 
	 */
	
	void join(MemberDTO dto);
	
	String idCheck(String mbsp_id);
	
	MemberDTO login(String mbsp_id);
	
	MemberDTO modify(String mbsp_id);
	
	void modify_save(MemberDTO dto);
}
