package com.ezen.shop.order;

import java.io.File;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.ezen.shop.cart.CartService;
import com.ezen.shop.cart.CartVO;
import com.ezen.shop.common.utils.Criteria;
import com.ezen.shop.common.utils.FileUtils;
import com.ezen.shop.common.utils.PageMaker;
import com.ezen.shop.common.utils.SearchCriteria;
import com.ezen.shop.mail.EmailDTO;
import com.ezen.shop.mail.EmailService;
import com.ezen.shop.member.MemberService;
import com.ezen.shop.member.MemberVO;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequestMapping("/order/*")
@RequiredArgsConstructor
@Controller
public class OrderController {

	private final OrderService orderService;
	private final CartService cartService;
	private final MemberService memberService;
	private final EmailService emailService;
	
	// 상품이미지 관련작업기능
	private final FileUtils fileUtils;
	
	@Value("${com.ezen.upload.path}")
	private String uploadPath;
	
	// 사용자 주문목록에서 이미지 또는 상품명 클릭시 상품상세설명으로 진행을 할 때 필요한 서브카테고리 준비작업 때문에
	@GetMapping("/pro_preinfo")
	public String pro_preinfo(Integer pro_num, RedirectAttributes rttr) throws Exception {
		
		
		String subCategoryName = orderService.getCategoryNameByPro_num(pro_num);
		
		rttr.addAttribute("cate_name", subCategoryName);
		rttr.addAttribute("pro_num", pro_num);
		
		// //product/pro_info?cate_name=카테고리이름&pro_num=13
		return "redirect:/product/pro_info";
	}
	
	
	// 폼(양식), 내용출력 등 get방식
	// 1)장바구니에서 주문클릭  2) 상품리스트, 상품상세에서 Buy(구매) 버튼클릭
	@GetMapping("/order_info")
	public void order_info(CartVO vo, String type, HttpSession session, Model model) throws Exception {
		
		// 장바구니추가
		// 로그인 사용자아이디
		String mbsp_id = ((MemberVO)session.getAttribute("login_auth")).getMbsp_id();
		vo.setMbsp_id(mbsp_id);
		
		if(type.equals("buy")) cartService.cart_add(vo);
		
		// 장바구니(구매정보) 내역출력
		List<Map<String, Object>> cartDetails = cartService.getCartDetailsByUserId(mbsp_id);
		
		
		
		// 날짜폴더의 역슬래쉬 \ 를 / 로 변환작업
		cartDetails.forEach(cartVO -> {
			cartVO.put("pro_up_folder", cartVO.get("pro_up_folder").toString().replace("\\", File.separator));
			
		});
		
		
		String item_name = "";
		
		if(cartDetails.size() == 1) {
			item_name = (String) cartDetails.get(0).get("pro_name");
		}else {
			item_name = (String) cartDetails.get(0).get("pro_name") + " 외" + cartDetails.size();
		}
		/*
		for(int i=0; i < cartDetails.size(); i++) {
			cartDetails.get(i).put("pro_up_folder", cartDetails.get(i).get("pro_up_folder").toString().replace("\\", "/"));
		}
		*/
		
		model.addAttribute("item_name", item_name);
		model.addAttribute("quantity", cartDetails.size());
		
		model.addAttribute("cartDetails", cartDetails);
		
		// 총주문금액
		model.addAttribute("order_total_price", cartService.getCartTotalPriceByUserId(mbsp_id));
		
				
		// 로그인한 사용자정보
		MemberVO memberVO =  memberService.modify(mbsp_id);
		model.addAttribute("memberVO", memberVO);

		
	}
	
	
	@PostMapping("/order_save")
	public String order_save(OrderVO vo, HttpSession session, String p_method, String account_transfer, String sender, RedirectAttributes rttr) throws Exception {
		
		String mbsp_id = ((MemberVO) session.getAttribute("login_auth")).getMbsp_id();
		vo.setMbsp_id(mbsp_id);
		
		
		String p_method_info = p_method + "/" + account_transfer + "/" + sender;
		
		orderService.order_process(vo, mbsp_id, p_method_info);
		
		rttr.addAttribute("ord_code", vo.getOrd_code());
		rttr.addAttribute("ord_mail", vo.getOrd_mail());
		
		// /order/order_result?ord_code=주문번호
		
		return "redirect:/order/order_result";
	}
	
	int order_total_price;
	
	// 주문결과내역
	@GetMapping("/order_result")
	public void order_result(Integer ord_code, String ord_mail, Model model) throws Exception {
		
		// 반드시 0으로 초기화해야한다. 그렇지 않으면, 세션이 유지된 상태에서 새로운 구매를 진행하면 총금액이 누적됨.
		order_total_price = 0;
		
		// 주문결과내역(주문번호)
		List<Map<String, Object>> order_info = orderService.getOrderInfoByOrd_code(ord_code);
		
		// 날짜폴더의 역슬래쉬 \ 를 / 로 변환작업
		order_info.forEach(o_Info -> {
			o_Info.put("pro_up_folder", o_Info.get("pro_up_folder").toString().replace("\\", File.separator));
			order_total_price += ((int) o_Info.get("dt_amount") * (int) o_Info.get("dt_price"));
		});
		
		EmailDTO dto = new EmailDTO("EZenShop", "EZenShop", "newcomsa@nate.com", "주문내역", "주문내역");
		
		emailService.sendMail("mail/orderConfirmation", dto, order_info, order_total_price);
		
		
		log.info("총주문금액:" + order_total_price);
		
		model.addAttribute("order_info", order_info);
		model.addAttribute("order_total_price", order_total_price);
	}
	
	// order_list : 주문목록  review_manage: 주문목록중 배송완료 대상
	@GetMapping(value =  {"/order_list"})
	public void order_list(SearchCriteria cri, HttpSession session, Model model) throws Exception {
		
		String mbsp_id = ((MemberVO) session.getAttribute("login_auth")).getMbsp_id();
		
		cri.setPerPageNum(2);
		
		List<Map<String, Object>> order_list = orderService.getOrderInfoByUser_id(mbsp_id, cri);
		
		// 날짜폴더의 역슬래쉬 \ 를 / 로 변환작업
		order_list.forEach(o_Info -> {
			o_Info.put("pro_up_folder", o_Info.get("pro_up_folder").toString().replace("\\", File.separator));			
		});
		
		model.addAttribute("order_list", order_list);
		
		
		
		PageMaker pageMaker = new PageMaker();
		pageMaker.setCri(cri);
		pageMaker.setTotalCount(orderService.getOrderCountByUser_id(mbsp_id));
		
		model.addAttribute("pageMaker", pageMaker);
		
		
	}
	
	@GetMapping(value =  {"/review_manage" })
	public void review_manage (SearchCriteria cri, HttpSession session, Model model) throws Exception {
		
		// 상품테이블, 주문, 주문상세테이블, 배송테이블
		// 상품코드, 상품이미지, 상품명, 배송일, 리뷰작성하기 버튼
		String mbsp_id = ((MemberVO) session.getAttribute("login_auth")).getMbsp_id();
		
		cri.setPerPageNum(2);
		
		List<Map<String, Object>> review_list = orderService.review_manage(mbsp_id, cri);
		
		// 날짜폴더의 역슬래쉬 \ 를 / 로 변환작업
		review_list.forEach(r_Info -> {
			r_Info.put("pro_up_folder", r_Info.get("pro_up_folder").toString().replace("\\", File.separator));			
		});
		
		model.addAttribute("review_list", review_list);
		
		
		
		PageMaker pageMaker = new PageMaker();
		pageMaker.setCri(cri);
		pageMaker.setTotalCount(orderService.getReviewCountByUser_id(mbsp_id));
		
		model.addAttribute("pageMaker", pageMaker);
	
	}
		
	// 상품목록 이미지출력하기.. 클라이언트에서 보낸 파라미터명 스프링의 컨트롤러에서 받는 파라미터명이 일치해야 한다.
	@GetMapping("/image_display")
	public ResponseEntity<byte[]> image_display(String dateFolderName, String fileName) throws Exception {
		
		return fileUtils.getFile(uploadPath + File.separator + dateFolderName, fileName);
	}
	
}
