package com.kd.basic.mail;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@AllArgsConstructor
public class EmailDTO {

	private String senderName; // 발신자이름
	private String senderMail; // 발신자 메일주소
	private String receiverMail;  // 수신자 메일주소
	private String subject;  // 제목
	private String message;  // 내용
	
	public EmailDTO() {
		this.senderMail = "EzenMall";
		this.senderName = "Ezen Mall";
		this.subject = "Ezen Mall 회원가입 메일인증코드입니다.";
		this.message = "메일 인증코드를 확인하시고, 회원가입시 인증코드 입력란에 입력바랍니다.";
	}
}
