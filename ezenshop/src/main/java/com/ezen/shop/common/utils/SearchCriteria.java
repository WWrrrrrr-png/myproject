package com.ezen.shop.common.utils;

// 페이징기능, 검색기능을 위한 클래스
public class SearchCriteria extends Criteria {
	
	// 디폴트 생성자 자동생성.
	
	private String searchType; // 검색종류(제목, 내용, 작성자 선택)
	private String keyword;    // 검색어
	
	public String getSearchType() {
		return searchType;
	}
	public void setSearchType(String searchType) {
		this.searchType = searchType;
	}
	public String getKeyword() {
		return keyword;
	}
	public void setKeyword(String keyword) {
		this.keyword = keyword;
	}
	
	// 부모클래스인 Criteria의 private 필드정보를 읽어오기위한 getter메서드 사용추가함.
	@Override
	public String toString() {
		return "SearchCriteria [searchType=" + searchType + ", keyword=" + keyword + ", getPage()=" + getPage()
				+ ", getPerPageNum()=" + getPerPageNum() + "]";
	}
	
	
	
	
}
