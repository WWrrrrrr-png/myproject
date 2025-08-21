package com.ezen.shop.kakaopay;



import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;


//@Getter
//@RequiredArgsConstructor

@Log4j2
@Service
public class KakaopayService {
	
	/*
	private final KakaoPayProperties kakaoPayProperties;
	
	private HttpHeaders getHeader() {
		HttpHeaders headers = new HttpHeaders();
		String auth = "SECRET_KEY " +  kakaoPayProperties.getSecretKey();
		headers.set("Authorization", auth);
		headers.set("Content-Type", "application/json");
		
		return headers;
	}
	*/
	
	@Value("${readUrl}")
	private String readUrl;
	
	@Value("${approveUrl}")
	private String approveUrl;
	
	@Value("${secretKey}")
	private String secretKey;
	
	@Value("${cid}")
	private String cid;
	
	@Value("${approval}")
	private String approval;
	
	@Value("${cancel}")
	private String cancel;
	
	@Value("${fail}")
	private String fail;
	
	private String tid;
	
	private String partner_order_id;
	
	private String partner_user_id;
	
	// 1차요청(결제준비요청-ready)
	public ReadyResponse ready(String partner_order_id, String partner_user_id, String item_name, 
				Integer quantity, Integer total_amount, Integer tax_free_amount)  {
		
		// 1)Request header
		HttpHeaders headers = getHttpHeaders();
		
		// 2)Request param
		ReadyRequest readyRequest = ReadyRequest.builder()
				.cid(cid)
				.partner_order_id(partner_order_id)
				.partner_user_id(partner_user_id)
				.item_name(item_name)
				.quantity(quantity)
				.total_amount(total_amount)
				.tax_free_amount(tax_free_amount)
				.approval_url(approval)
				.cancel_url(cancel)
				.fail_url(fail)
				.build();
		
		// data request. 결제준비요청에 보낼 헤더와파라미터를 가지고 있는 객체작업.
		HttpEntity<ReadyRequest> entityMap = new HttpEntity<>(readyRequest, headers);
		
		// 결제준비요청. 
		ResponseEntity<ReadyResponse> response = new RestTemplate().postForEntity(
				readUrl, entityMap, ReadyResponse.class);
		
		// 응답데이터
		ReadyResponse readyResponse = response.getBody();
		
		this.tid = readyResponse.getTid();
		this.partner_order_id = partner_order_id;
		this.partner_user_id = partner_user_id;
		
	    return readyResponse;
	}
	
	// 2차요청(결제승인요청-approve)
	public String approve(String pg_token) {
		
		// 1)Request header
		HttpHeaders headers = getHttpHeaders();
		
		// 2)Request param
		ApproveRequest approveRequest = ApproveRequest.builder()
				.cid(cid)
				.tid(tid)
				.partner_order_id(partner_order_id)
				.partner_user_id(partner_user_id)
				.pg_token(pg_token)
				.build();
		
		// data request. 결제승인요청에 보낼 헤더와파라미터를 가지고 있는 객체작업.
		HttpEntity<ApproveRequest> entityMap = new HttpEntity<>(approveRequest, headers);
		
		// 결제승인요청
		// 결제준비요청. 
		ResponseEntity<ApproveResponse> response = new RestTemplate().postForEntity(
				approveUrl, entityMap, ApproveResponse.class);
		
		//log.info("결제승인요청응답: " + response);
		
//		log.info("결제승인상태코드: " + response.getStatusCode());		
//		
//		if(response.toString().contains("aid")) {
//			log.info("주문관련작업");
//		}
		
//		if(response.getStatusCode() == HttpStatus.OK) {
//			log.info("주문관련작업");
//		}
		
		return response.toString();
	}
	
	public HttpHeaders getHttpHeaders() {
		
		HttpHeaders headers = new HttpHeaders();
		headers.set("Authorization", "SECRET_KEY " + secretKey);
		headers.set("Content-Type", "application/json;charset=utf-8");
		
		return headers;
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
}
