package com.ezen.shop.member;

import org.apache.ibatis.annotations.Param;

// @Mapper
public interface MemberMapper {

	// 아이디 체크
	String idCheck(String mbsp_id);
	
	// 회원정보저장
	void join(MemberVO vo);
	
	// 로그인 처리
	MemberVO login(String mbsp_id);
	
	// 회원수정 폼
	MemberVO modify(String mbsp_id);
	
	// 회원수정
	void modify_save(MemberVO vo);
	
	// mapper인터페이스에서 파라미터가 2개이상이면, @Param 어노테이션을 사용해야 한다.(규칙)
	// 비밀번호 변경
	void pwchange(@Param("mbsp_id") String mbsp_id, @Param("mbsp_password") String mbsp_password);
	
	// 아아디찾기
	String idsearch(@Param("mbsp_name") String mbsp_name, @Param("mbsp_email") String mbsp_email);
	
	// 임시비밀번호 발급하기위한 아이디와메일주소 존재여부체크
	String pwtemp_confirm(@Param("mbsp_id") String mbsp_id, @Param("mbsp_email") String mbsp_email);
}
