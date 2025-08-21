package com.kd.basic.member;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class MemberService {

	private final MemberMapper memberMapper;
	
	public void join(MemberDTO dto) {
		memberMapper.join(dto);
	}
	
	public String idCheck(String mbsp_id) {
		return memberMapper.idCheck(mbsp_id);
	}
	
	public MemberDTO login(String mbsp_id) {
		return memberMapper.login(mbsp_id);
	}
	
	public MemberDTO modify(String mbsp_id) {
		return memberMapper.modify(mbsp_id);
	}
}
