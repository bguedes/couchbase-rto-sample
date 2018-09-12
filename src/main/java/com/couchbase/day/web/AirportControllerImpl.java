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
import com.couchbase.day.service.AirportService;

@RestController
@RequestMapping("/api/airports")
public class AirportControllerImpl {

	private static final Logger LOGGER = LoggerFactory.getLogger(AirportControllerImpl.class);

    private final AirportService airportService;

    @Autowired
    public AirportControllerImpl(AirportService airportService) {
        this.airportService = airportService;
    }
    
    @RequestMapping(value="/airport/{id}/", method=RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<? extends IValue> findAirportById(@PathVariable("id") String id) {

        try {
            return ResponseEntity.ok(airportService.findAirportById(id));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new Error(e.getMessage()));
        }
    }      
    
    @RequestMapping(value="/airport/name/{name}/", method=RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<? extends IValue> findAirportByState(@PathVariable("name") String name) {
        try {
            return ResponseEntity.ok(airportService.findAirportByName(name));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new Error(e.getMessage()));
        }
    }  
    
    @RequestMapping(value="/flypath/{from}/{to}/", method=RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<? extends IValue> findAirportFlighPaths(@PathVariable("from") String from, @PathVariable("to") String to) {
        try {
            return ResponseEntity.ok(airportService.findFlighPaths(from, to));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new Error(e.getMessage()));
        }
    }     
}
