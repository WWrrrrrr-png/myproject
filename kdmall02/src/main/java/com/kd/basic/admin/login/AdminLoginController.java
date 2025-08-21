package com.kd.basic.admin.login;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;

@Controller 
@RequestMapping("/admin") 
@RequiredArgsConstructor // 필드 자동주입
public class AdminLoginController {
    
	//controller에 service가 주입을 받음
	private final AdminLoginService adminLoginService;  
	//암호화 메서드 
	private final PasswordEncoder passwordEncoder; 

	@GetMapping("/ad_menu")
	public String loginForm() {
		return "admin/ad_menu";
	}
	
	
	//로그인 폼
	@GetMapping("/login") 
	public String loginForm() { 
		return "admin/login";//adim/login.html뷰 호출 
		
	} 
	
	// 로그인 처리
    @PostMapping("/login")
    public String loginProcess(AdminLoginVO vo, HttpSession session) throws Exception {

        // ✔ 아이디로 DB 조회
        AdminLoginVO result = adminLoginService.login(vo.getAd_userid());

        // ✔ 조회 결과 검증
        if(result != null && passwordEncoder.matches(vo.getAd_password(), result.getAd_password())) {
            // 세션에 로그인 정보 저장
            session.setAttribute("adminUser", result);
            return "redirect:/admin/home"; // 성공 시 홈으로 이동
        } else {
            return "admin/login"; // 실패 시 다시 로그인 페이지
        }
    }
}
