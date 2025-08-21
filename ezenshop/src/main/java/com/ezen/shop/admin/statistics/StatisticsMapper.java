package com.ezen.shop.admin.statistics;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

public interface StatisticsMapper {

	List<OrderAmount> monthlysales_statistics(int year);
	
	List<Map<String, Object>> monthlysales_statistics2(int year); // 권장
	
	List<Map<String, Object>> getDailyStatistics(String date);
	
	List<Map<String, Object>> getHourlyStatistics(@Param("start_date") String start_date, @Param("end_date") String end_date);
	
	List<Map<String, Object>> getWeeklyStatistics(@Param("start_date") String start_date, @Param("end_date") String end_date);
	
	List<Map<String, Object>> getMonthlyStatistics(@Param("year") String year);
	
}
