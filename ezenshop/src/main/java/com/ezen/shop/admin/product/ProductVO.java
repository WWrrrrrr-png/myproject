package com.ezen.shop.admin.product;

import java.util.Date;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class ProductVO {
	
	private Integer pro_num;
	private Integer cate_code; // 2차카테고리
	private String pro_name;
	private int pro_price;
	private int pro_discount; // 할인율
	private String pro_publisher;
	private String pro_content;
	private String pro_up_folder; // 상품 이미지파일이 저장되는 날짜폴더명
	private String pro_img; // 상품 이미지이름. 업로드된 파일에서 파일이름을 이용하여 저장.(유일한 이름)
	private int pro_amount;
	private String pro_buy;
	private int pro_review;
	private Date pro_date;
	private Date pro_updatedate;
}
