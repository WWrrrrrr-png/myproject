package com.ezen.shop.admin.review;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import com.ezen.shop.admin.AdminDto;
import com.ezen.shop.common.utils.FileUtils;
import com.ezen.shop.common.utils.PageMaker;
import com.ezen.shop.common.utils.SearchCriteria;
import com.ezen.shop.member.MemberVO;
import com.ezen.shop.review.ReviewReply;
import com.ezen.shop.review.ReviewService;
import com.ezen.shop.review.ReviewVO;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@RequestMapping("/admin/review/*")
@Controller
public class AdReviewController {

	private final AdReviewService adReviewService;
	private final ReviewService reviewService;
	
	// 상품이미지 관련작업기능
	private final FileUtils fileUtils;
	
	@Value("${com.ezen.upload.path}")
	private String uploadPath;
	
	
	@GetMapping("/review_list")
	public void review_list(@ModelAttribute("cri") SearchCriteria cri, @ModelAttribute("rev_rate") String rev_rate, @ModelAttribute("rev_content") String rev_content,  Model model) throws Exception {
		
		log.info("검색정보" + cri.toString());
		
		List<ReviewVO> review_list = adReviewService.review_list(cri, rev_rate, rev_content);
		
		// 날짜폴더의 역슬래시를 슬래시로 변환하는 작업.
		/*
		review_list.forEach(reivew_Info -> {
			reivew_Info.get   ("pro_up_folder", reivew_Info.get("pro_up_folder").toString().replace("\\", "/"));			
		});
		*/
		review_list.forEach(reivew_Info -> {
			reivew_Info.getProduct().setPro_up_folder((reivew_Info.getProduct().getPro_up_folder().replace("\\", "/")));
		});
		
		
		PageMaker pageMaker = new PageMaker();
		pageMaker.setCri(cri);
		pageMaker.setTotalCount(adReviewService.review_count(cri, rev_rate, rev_content));
		
		model.addAttribute("review_list", review_list);
		model.addAttribute("pageMaker", pageMaker);
		
	}
	
	// 상품후기코드를 통한 상품후기정보.
	@GetMapping("/review_info/{rev_code}")
	public ResponseEntity<ReviewVO> review_info(@PathVariable("rev_code") Long rev_code) throws Exception {
		ResponseEntity<ReviewVO> entity = null;
		
		ReviewVO review_info = reviewService.review_info(rev_code);
		
		log.info("상품후기정보: " + review_info);
		
		entity = new ResponseEntity<ReviewVO>(review_info, HttpStatus.OK);
		
		return entity;
	}
	
	// http://localhost:8888/admin/review/reply_insert. 상품후기 답변저장.
	@PostMapping(value = "/reply_insert", consumes = "application/json", produces = {MediaType.TEXT_PLAIN_VALUE})
	public ResponseEntity<String> reply_insert(@RequestBody ReviewReply vo, HttpSession session) throws Exception {
	
		ResponseEntity<String> entity = null;
		
		log.info("상품후기답변: " + vo);
		
		// 관리자 아이디 저장.  AdminController 에서 참조.
		AdminDto adminDto = ((AdminDto) session.getAttribute("admin_auth"));
		vo.setManager_id(adminDto.getAd_userid());
		
	    reviewService.reply_insert(vo);
		
		entity = new ResponseEntity<String>("success", HttpStatus.OK);
			
		return entity;
	}
	
	// 답변하기 수정정보.  리턴되는 ReviewReply 객체가 JSON으로 변환하여, 클라이언트(fetch()함수)로 응답
	@GetMapping("/reply_info/{reply_id}")
	public ResponseEntity<ReviewReply> reply_info(@PathVariable("reply_id") Long reply_id) throws Exception {
		ResponseEntity<ReviewReply> entity = null;
		
		entity = new ResponseEntity<ReviewReply>(adReviewService.reply_info(reply_id), HttpStatus.OK);
		
		return entity;
	}
	
	// 답변 수정정보 저장
	@PostMapping(value = "/reply_modify", consumes = "application/json", produces = {MediaType.TEXT_PLAIN_VALUE})
	public ResponseEntity<String> reply_modify(@RequestBody ReviewReply vo) throws Exception {
		
		
		log.info("답변정보: " + vo);
//		log.info("답변정보: " + reply_text);
		
		ResponseEntity<String> entity = null;
		
		adReviewService.reply_modify(vo.getReply_id(), vo.getReply_text());
		
		entity = new ResponseEntity<String>("success", HttpStatus.OK);
		
		return entity;
	}
	
	// 답변 삭제
	@DeleteMapping("/reply_delete/{reply_id}")
	public ResponseEntity<String> reply_delete(@PathVariable("reply_id") Long reply_id) throws Exception {
		
		ResponseEntity<String> entity = null;
		
		adReviewService.reply_delete(reply_id);
		
		entity = new ResponseEntity<String>("success", HttpStatus.OK);
		
		return entity;
	}
	
	
	@GetMapping("/image_display")
	public ResponseEntity<byte[]> image_display(String dateFolderName, String fileName) throws Exception {
		
		return fileUtils.getFile(uploadPath + "\\" + dateFolderName, fileName);
	}
}
