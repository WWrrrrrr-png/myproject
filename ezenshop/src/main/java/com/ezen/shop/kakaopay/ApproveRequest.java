package com.ezen.shop.kakaopay;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

// 결제승인요청 파라미터
@Getter // private 필드를 대상으로 getter메서드 생성
@AllArgsConstructor // 모든 필드를 대상으로 생성자메서드 생성
@Builder
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class ApproveRequest {

	private String cid;
	private String tid;
	private String partner_order_id;
	private String partner_user_id;
	private String pg_token;
}
