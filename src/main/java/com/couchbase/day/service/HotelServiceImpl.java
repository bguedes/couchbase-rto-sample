package com.couchbase.day.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataRetrievalFailureException;
import org.springframework.stereotype.Service;

import com.couchbase.client.java.Bucket;
import com.couchbase.client.java.document.json.JsonObject;
import com.couchbase.client.java.search.SearchQuery;
import com.couchbase.client.java.search.queries.MatchQuery;
import com.couchbase.client.java.search.result.SearchQueryResult;
import com.couchbase.client.java.search.result.SearchQueryRow;
import com.couchbase.day.model.Result;


@Service
public class HotelServiceImpl implements HotelService{

	private static final Logger LOGGER = LoggerFactory.getLogger(HotelServiceImpl.class);

	private Bucket bucket;

	@Autowired
	public HotelServiceImpl(Bucket bucket) {
		this.bucket = bucket;
	}

	@Override
	@SuppressWarnings("rawtypes")
	public Result findHotelsByDescription(String description) {
		
		MatchQuery fts = SearchQuery.match("description:" + description);
		SearchQuery query = new SearchQuery("airport-search", fts).limit(30);

		return Result.of(extractResultOrThrow(bucket.query(query)));
	}


    private List<Map<String, Object>> extractResultOrThrow(SearchQueryResult results) {
    	
    		List<Map<String, Object>> resultList = new ArrayList<Map<String, Object>>();
    		  	
        if (!results.status().isSuccess()) {
            LOGGER.warn("Query returned with errors: " + results.errors());
            throw new DataRetrievalFailureException("Query error: " + results.errors());
        }

        for (SearchQueryRow hit : results.hits()) {
        	
        		JsonObject jsonObject = bucket.get(hit.id()).content();
        	
        		Map<String, Object> map = new HashMap<String, Object>();
            	map.put(hit.id(), jsonObject.toString());
            resultList.add(map);        	    
        }
	
        return resultList ;
    }
}
