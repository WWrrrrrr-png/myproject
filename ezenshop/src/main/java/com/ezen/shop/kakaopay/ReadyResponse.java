package com.ezen.shop.kakaopay;

import java.util.Date;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

// 결제준비(ready) :카카오페이 결제를 시작하기 위해 결제정보를 카카오페이 서버에 전달하고 결제 고유번호(TID)와 URL을 응답
// ajax 호출한 쪽으로 사용
@Getter
@Setter
@ToString
public class ReadyResponse {
	
	private String tid;
	private String next_redirect_pc_url;
	private Date created_at;
}
