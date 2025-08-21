package com.ezen.shop.common.utils;

// 페이징기능을 위한 클래스.
public class Criteria {

	private int page;	//	1	2	3	4	5  사용자가 선택한 페이지번호
	private int perPageNum;  // 페이지별로 출력한 게시물 개수
	
	public Criteria() {
		this.page = 1;
		this.perPageNum = 10;
	}

	public int getPage() {
		return page;
	}

	public void setPage(int page) {
		
		if(page <= 0) {
			this.page = 1;
			return;
		}
		
		this.page = page;
	}

	public void setPerPageNum(int perPageNum) {
		
		if(perPageNum <= 0 || perPageNum > 100) {
			this.perPageNum = 10;
			return;
		}
		
		this.perPageNum = perPageNum;
	}
	
	// mybatis mapper파일에서 참조가 될려면, getter()메서드 문법형식을 갖추고 있어야 한다.(중요)
	// 수동. mybatis mapper파일에서 참조.
	// page 1	2	3	4	5  perPageNum = 10
	public int getPageStart() {
		return (this.page - 1) * perPageNum;
	}
	
	// mybatis mapper파일에서 참조.  perPageNum = 10
	public int getPerPageNum() {
		return perPageNum;
	}

	@Override
	public String toString() {
		return "Criteria [page=" + page + ", perPageNum=" + perPageNum + "]";
	}
	
	
	
	
}
