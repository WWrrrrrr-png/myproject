package com.ezen.shop.common.interceptor;

import java.io.BufferedReader;
import java.io.IOException;
import java.net.URLEncoder;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import com.ezen.shop.member.MemberVO;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@Component
public class LoginInterceptor implements HandlerInterceptor {

	// 인터셉터 클래스가 관리하는 URI 요청이 발생되면, 인터셉터가 가로채서 preHandle()메서드가 먼저 동작(실행)
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {
		
		// HttpServletRequest request : 클라이언트가 요청한(보내온) 모든 정보(데이타)를 서버에서 관리하는 객체.
		// HttpServletResponse response : 서버에서 클라이언트로 보낼 응답정보를 관리하는 객체.
	
		boolean result = false;
	
		// 로그인 session.setAttribute("login_auth", userInfo);
		// 인증된 상태인지 체크하는 작업
		HttpSession session = request.getSession();
		MemberVO memberVO = (MemberVO) session.getAttribute("login_auth");
		
		if(memberVO == null) { // 현재 주소를 요청한 사용자는(브라우저) 로그인을 하지않은 의미.(인증을 안한 상태)
			result = false;
			
			// ajax요청주소인지 구분하는 작업.
			if(isAjaxRequest(request)) {
				
				String originalUrl = request.getRequestURI();
				String postData = getPostData(request);
				
				System.out.println("데이타: " + postData);
				
				session.setAttribute("targetUrl", originalUrl);
				session.setAttribute("postData", postData);
				
				// response.sendRedirect("/member/login?targetUrl=" + URLEncoder.encode(originalUrl, "UTF-8"));
				
				
				response.sendError(400); // 400 Http상태코드. ajax로 제어가 400번 클라이언트 에러정보를 가지고 넘어간다.
			}else {
				getTargetUrl(request); // 원래요청된 주소를 세션형태로 저장  "targetUrl"
				
				response.sendRedirect("/member/login"); // 로그인주소
			}
		}else { // 로그인을 한 의미.(인증을 한 상태)
			result = true; // true이면, 컨트롤러로 실행이 넘어간다.
		}
	
		return result;
	}

	// 인증되지 않은 상태에서 ajax방식으로 post 요청시 사용한 데이타 
	private String getPostData(HttpServletRequest request) throws IOException {

		StringBuilder postData = new StringBuilder();
		BufferedReader reader = request.getReader();
		String line;
		while((line = reader.readLine()) != null) {
			postData.append(line);
		}
		return postData.toString();
	}

	// 클라이언트의 요청이 ajax인지 체크하는 기능
	private boolean isAjaxRequest(HttpServletRequest request) {
		
		boolean isAjax = false;
		
		String header = request.getHeader("AJAX"); // "true"
		if(header != null && header.equals("true")) {
			isAjax = true;
		}
		
		
		return isAjax;
	}

	// 인증되지 않은 상태에서 원래요청한 주소(URI)의 정보를 저장하는 기능
	private void getTargetUrl(HttpServletRequest request) {
		
		// http://localhost:8888/userinfo/modify?userid=user01 주소요청
		String uri = request.getRequestURI();  // /userinfo/modify
		String query = request.getQueryString(); // ?물음표 뒤의 문자열   ?userid=user01
		
		if(query == null || query.equals("null")) { // 쿼리스트링이 없을 경우
			query = "";
		}else { // 쿼리스트링이 있을 경우
			query = "?" + query;   // ?userid=user01
		}
		
		String targetUrl = uri + query; // /userinfo/modify?userid=user01
		
		// 클라이언트가 요청한 방식이 get방식일 경우.
		if(request.getMethod().equals("GET")) {
			// 인증되지 않은 사용자가 원래요청한 주소및 쿼리스트링을 세션에 저장하고, 로그인에서 참조하고 자 할 경우 목적
			request.getSession().setAttribute("targetUrl", targetUrl);
		}
		
	}

	
}
