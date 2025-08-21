package com.ezen.shop.admin.order;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.ezen.shop.common.utils.FileUtils;
import com.ezen.shop.common.utils.PageMaker;
import com.ezen.shop.common.utils.SearchCriteria;
import com.ezen.shop.member.MemberService;
import com.ezen.shop.member.MemberVO;
import com.ezen.shop.order.OrderVO;
import com.ezen.shop.payment.PaymentService;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Slf4j
@RequestMapping("/admin/order/*")
@Controller
public class AdOrderController {

	
	private final AdOrderService adOrderService;
	private final MemberService memberService;
	private final PaymentService paymentService;
	
	// 상품이미지 관련작업기능
	private final FileUtils fileUtils;
	
	@Value("${com.ezen.upload.path}")
	private String uploadPath;
	
	// 주문목록
	@GetMapping("/order_list")
	public void order_list(@ModelAttribute("cri") SearchCriteria cri, @ModelAttribute("period") String period, 
			@ModelAttribute("start_date") String start_date, @ModelAttribute("end_date") String end_date, @ModelAttribute("payment_method") String payment_method, @ModelAttribute("ord_status") String ord_status, Model model) throws Exception {
		
		
		log.info("검색정보: " + cri);
		log.info("날짜검색: " + period);
		log.info("날짜검색: " + start_date);
		log.info("날짜검색: " + end_date);
		
		log.info("cri: " + cri.getPageStart());
		
		
		cri.setPerPageNum(2);
		
		List<Map<String, Object>> order_list = adOrderService.order_list(cri, period, start_date, end_date, payment_method, ord_status);
		
		PageMaker pageMaker = new PageMaker();
		pageMaker.setCri(cri);
		pageMaker.setTotalCount(adOrderService.getTotalCount(cri, period, start_date, end_date, payment_method, ord_status));
		
		model.addAttribute("order_list", order_list);
		model.addAttribute("pageMaker", pageMaker);
		
		
	}
	
	@GetMapping("/orderdetail_info")
	public void orderdetail_info(Integer ord_code, Model model) throws Exception {
		
		// 1)주문상세목록
		List<Map<String, Object>> order_product_info = adOrderService.orderdetail_info(ord_code);
		
		// 날짜폴더의 역슬래시를 슬래시로 변환하는 작업.
		order_product_info.forEach(o_Info -> {
			o_Info.put("pro_up_folder", o_Info.get("pro_up_folder").toString().replace("\\", "/"));			
		});
		
		model.addAttribute("order_product_info", order_product_info);
		
		// 2)주문결제내역
		model.addAttribute("payment_info", adOrderService.payment_info(ord_code));
		
		
		
		// 4)관리자메모내용, 배송지정보
		OrderVO order_info = adOrderService.order_info(ord_code);
		model.addAttribute("order_info", order_info);
		
		// 3)주문자정보
		String mbsp_id = order_info.getMbsp_id();
		model.addAttribute("member_info", memberService.modify(mbsp_id));
		
		
	}
	
	// 상품목록 이미지출력하기.. 클라이언트에서 보낸 파라미터명 스프링의 컨트롤러에서 받는 파라미터명이 일치해야 한다.
	@GetMapping("/image_display")
	public ResponseEntity<byte[]> image_display(String dateFolderName, String fileName) throws Exception {
		
		return fileUtils.getFile(uploadPath + "\\" + dateFolderName, fileName);
	}
	
	
	// 관리자 메모저장
	@PostMapping("/admin_ord_message")
	public ResponseEntity<String> admin_ord_message(Integer ord_code, String ord_message) throws Exception {
		ResponseEntity<String> entity = null;
		
		adOrderService.admin_ord_message(ord_code, ord_message);
		
		entity = new ResponseEntity<String>("success", HttpStatus.OK);
		
		return entity;
	}
	
	// 배송지정보 수정
	@PostMapping("/order_info_edit")
	public ResponseEntity<String> order_info_edit(OrderVO vo) throws Exception {
		ResponseEntity<String> entity = null;
		
		adOrderService.order_info_edit(vo);
		
		entity = new ResponseEntity<String>("success", HttpStatus.OK);
		
		return entity;
	}
	
	// 주문 개별상품 삭제.
	@PostMapping("/order_product_del")
	public ResponseEntity<String> order_product_del(Integer ord_code, Integer pro_num) throws Exception {
		ResponseEntity<String> entity = null;
		
		adOrderService.order_product_del(ord_code, pro_num);
				
		entity = new ResponseEntity<String>("success", HttpStatus.OK);
		
		return entity;
	}
	
	// 결제 상태변경  payment_status
	@PostMapping("/payment_status")
	public ResponseEntity<String> payment_status(Integer payment_id, String payment_status) throws Exception {
		ResponseEntity<String> entity = null;
		
		paymentService.payment_status(payment_id, payment_status);
				
		entity = new ResponseEntity<String>("success", HttpStatus.OK);
		
		return entity;
	}
	
	// 주문상태변경
	@PostMapping("/order_status")
	public ResponseEntity<String> order_status(Integer ord_code, String ord_status) throws Exception {
		
		ResponseEntity<String> entity = null;
		
		adOrderService.order_status(ord_code, ord_status);
				
		entity = new ResponseEntity<String>("success", HttpStatus.OK);
		
		return entity;
	}
	
	
	
	
}
