package com.ezen.shop.admin.statistics;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class StatisticsService {

	private final StatisticsMapper statisticsMapper;
	
	public List<OrderAmount> monthlysales_statistics(int year) {
		return statisticsMapper.monthlysales_statistics(year);
	}
	
	public List<Map<String, Object>> monthlysales_statistics2(int year) {
		return statisticsMapper.monthlysales_statistics2(year);
	}
	
	public List<Map<String, Object>> getDailyStatistics(String date) {
		return statisticsMapper.getDailyStatistics(date);
	}
	
	public List<Map<String, Object>> getHourlyStatistics(String start_date, String end_date) {
		return statisticsMapper.getHourlyStatistics(start_date, end_date);
	}
	
	public List<Map<String, Object>> getWeeklyStatistics(String start_date, String end_date) {
		return statisticsMapper.getWeeklyStatistics(start_date, end_date);
	}
	
	public List<Map<String, Object>> getMonthlyStatistics(String year) {
		return statisticsMapper.getMonthlyStatistics(year);
	}
}
