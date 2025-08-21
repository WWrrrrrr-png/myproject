package com.ezen.shop.cart;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.ezen.shop.common.utils.FileUtils;
import com.ezen.shop.member.MemberVO;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequestMapping("/cart/*")
@RequiredArgsConstructor
@Controller
public class CartController {

	private final CartService cartService;
	
	// 상품이미지 관련작업기능
	private final FileUtils fileUtils;
	
	@Value("${com.ezen.upload.path}")
	private String uploadPath;
	
	
	// 인증되지 않은 상태에서 ajax요청 일 경우 호출
	@GetMapping("/cart_add")
	public String ajax_cart_add(CartVO vo, HttpSession session ) throws Exception {
			
		log.info("장바구니: " + vo);
		
		ResponseEntity<String> entity = null;
		
		String mbsp_id = ((MemberVO)session.getAttribute("login_auth")).getMbsp_id();
		vo.setMbsp_id(mbsp_id);
		
		cartService.cart_add(vo);
	
		return "redirect:/cart/cart_list";
	}
	
	// HttpSession session : 인증된 사용자만 사용하는 기능.
	@PostMapping("/cart_add")
	public ResponseEntity<String> cart_add(CartVO vo, HttpSession session ) throws Exception {
		
		log.info("장바구니: " + vo);
		
		ResponseEntity<String> entity = null;
		
		String mbsp_id = ((MemberVO)session.getAttribute("login_auth")).getMbsp_id();
		vo.setMbsp_id(mbsp_id);
		
		cartService.cart_add(vo);
		
		entity = new ResponseEntity<String>("success", HttpStatus.OK);
		
		return entity;
	}
	
	@GetMapping("/cart_list")
	public void cart_list(HttpSession session, Model model) throws Exception {
		
		String mbsp_id = ((MemberVO)session.getAttribute("login_auth")).getMbsp_id();
		
		List<Map<String, Object>> cart_list = cartService.cart_list(mbsp_id);
		
		// 날짜폴더의 역슬래쉬 \ 를 / 로 변환작업
		cart_list.forEach(cartVO -> {
			cartVO.put("pro_up_folder", cartVO.get("pro_up_folder").toString().replace("\\", File.separator));
			
		});
		
		model.addAttribute("cart_list", cart_list);
		
		// 장바구니 비우기작업에서 총금액이 null 로 발생이된다. 타임리프에서 null 체크작업 필요.
		model.addAttribute("cart_total_price", cartService.getCartTotalPriceByUserId(mbsp_id));
	}
	
	@GetMapping("/cart_empty")
	public String cart_empty(HttpSession session) throws Exception {
		
		String mbsp_id = ((MemberVO)session.getAttribute("login_auth")).getMbsp_id();
		
		cartService.cart_empty(mbsp_id);
		
		return "redirect:/cart/cart_list";
	}
	
	@GetMapping("/cart_change")
	public String cart_change(CartVO vo, HttpSession session) throws Exception {
		
		String mbsp_id = ((MemberVO)session.getAttribute("login_auth")).getMbsp_id();
		vo.setMbsp_id(mbsp_id);
		
		cartService.cart_change(vo);
		
		
		return "redirect:/cart/cart_list";
	}
	
	@PostMapping("/cart_sel_delete")
	public String cart_sel_delete(int[] check, HttpSession session) throws Exception {
		
		String mbsp_id = ((MemberVO)session.getAttribute("login_auth")).getMbsp_id();
		
//		log.info("상품코드: " + Arrays.toString(check));
		
		cartService.cart_sel_delete(check, mbsp_id);
		
		return "redirect:/cart/cart_list";
	}
	
	@GetMapping("/image_display")
	public ResponseEntity<byte[]> image_display(String dateFolderName, String fileName) throws Exception {
		
		return fileUtils.getFile(uploadPath + File.separator + dateFolderName, fileName);
	}
}
