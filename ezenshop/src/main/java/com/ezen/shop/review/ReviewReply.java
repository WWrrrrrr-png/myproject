package com.ezen.shop.review;

import java.time.LocalDateTime;
import java.util.List;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class ReviewReply {

	private Long reply_id;
    private Long rev_code;
    private String manager_id;
    private String reply_text;
    private LocalDateTime reply_date;
    

}
