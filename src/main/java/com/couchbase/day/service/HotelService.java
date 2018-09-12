package com.couchbase.day.service;

import com.couchbase.day.model.Result;

public interface HotelService {
	
	@SuppressWarnings("rawtypes")
	public Result findHotelsByDescription(String description);
	
}
