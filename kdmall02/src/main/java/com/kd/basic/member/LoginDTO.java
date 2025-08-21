package com.kd.basic.member;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@ToString
@Setter
@Getter
public class LoginDTO {

	private String mbsp_id;
	private String mbsp_password;
}
