package com.couchbase.day.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.couchbase.day.model.Error;
import com.couchbase.day.model.IValue;
import com.couchbase.day.service.HotelService;

@RestController
@RequestMapping("/api/hotels")
public class HotelControllerImpl {

	private static final Logger LOGGER = LoggerFactory.getLogger(HotelControllerImpl.class);

    private final HotelService hotelService;

    @Autowired
    public HotelControllerImpl(HotelService hotelService) {
        this.hotelService = hotelService;
    } 
    
    @RequestMapping(value="/description/{description}/", method=RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<? extends IValue> findHotelsByDescription(@PathVariable("description") String description) {

        try {
            return ResponseEntity.ok(hotelService.findHotelsByDescription(description));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new Error(e.getMessage()));
        }
    }  
    
}
