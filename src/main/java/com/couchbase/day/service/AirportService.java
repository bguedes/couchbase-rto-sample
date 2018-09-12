package com.couchbase.day.service;

import com.couchbase.day.model.Result;

public interface AirportService {

	@SuppressWarnings("rawtypes")
	public Result findAirportById(String id);
	
	@SuppressWarnings("rawtypes")
	public Result findAirportByName(String name);
	
	@SuppressWarnings("rawtypes")
	public Result findFlighPaths(String from, String to);
	
	@SuppressWarnings("rawtypes")
	public Result findHotelsByDescriptionAndLocation(String description, String location);
}
