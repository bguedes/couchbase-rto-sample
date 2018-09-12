package com.couchbase.day.service;

import static com.couchbase.client.java.query.Select.select;
import static com.couchbase.client.java.query.dsl.Expression.s;
import static com.couchbase.client.java.query.dsl.Expression.x;
import static com.couchbase.client.java.query.dsl.Expression.i;

import com.couchbase.client.java.document.JsonDocument;
import com.couchbase.client.java.document.json.JsonObject;

import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.couchbase.client.core.time.Delay;
import com.couchbase.client.java.Bucket;
import com.couchbase.client.java.query.AsyncN1qlQueryResult;
import com.couchbase.client.java.query.N1qlQuery;
import com.couchbase.client.java.query.N1qlQueryResult;
import com.couchbase.client.java.query.N1qlQueryRow;
import com.couchbase.client.java.query.Statement;
import com.couchbase.client.java.query.dsl.Sort;
import com.couchbase.client.java.util.retry.RetryBuilder;
import com.couchbase.day.model.Result;

@Service
public class AirportServiceImpl implements AirportService {

	private static final Logger LOGGER = LoggerFactory.getLogger(AirportServiceImpl.class);

	private Bucket bucket;

	@Autowired
	public AirportServiceImpl(Bucket bucket) {
		this.bucket = bucket;
	}
	
	@Override
	@SuppressWarnings("rawtypes")
	public Result findAirportById(String id) {
		
		JsonDocument doc = bucket.get("airport_"+ id);
		
        JsonObject responseContent;
		if(doc == null) {
			responseContent = JsonObject.create().put("success", false).put("failure", "No Airport document for id :" + id);
		} else {
			responseContent = JsonObject.create().put("success", true).put("data", doc.content());
		}

		return Result.of(new ResponseEntity<String>(responseContent.toString(), HttpStatus.OK));
	}	
	
	@Override
	@SuppressWarnings("rawtypes")
	public Result findAirportByName(String name) {
		String nameN1QLValue = "'" + name + "'";
		String bucketN1QLValue = "`" + this.bucket.name() + "`";

		Statement selectAirportByName = select("*").from(bucketN1QLValue).where(x("airportname").eq(nameN1QLValue));

		LOGGER.info("Executing Query: {}", selectAirportByName.toString());

		return Result.of(executeQuery(selectAirportByName), selectAirportByName.toString());
	}

	@Override
	@SuppressWarnings("rawtypes")
	public Result findFlighPaths(String from, String to) {
		
		Statement selectFaa = select(x("faa").as("fromAirport"))
				.from(i(this.bucket.name()))
				.where(x("airportname").eq(s(from)))
				.union(
					select(x("faa").as("toAirport")).
					from(i(this.bucket.name()))
					.where(x("airportname").eq(s(to)))
				);


		N1qlQueryResult result = bucket.query(N1qlQuery.simple(selectFaa));

		String fromAirportResult = null;
		String toAirportResult = null;
		
		for (N1qlQueryRow row : result) { 			
            if (row.value().containsKey("fromAirport")) {
            		fromAirportResult = row.value().getString("fromAirport");
            }
            if (row.value().containsKey("toAirport")) {
            		toAirportResult = row.value().getString("toAirport");
            }
		}		
		

		Statement selectFlightPath = select("a.name, s.flight, s.utc, r.sourceairport, r.destinationairport, r.equipment")
				.from(i(this.bucket.name())).as("r")
				.unnest("r.schedule").as("s")
				.join(i(this.bucket.name())).as("a").onKeys("r.airlineid")
				.where(x("r.sourceairport").eq(s(fromAirportResult)).and(x("r.destinationairport").eq(s(toAirportResult))).and(x("s.day").eq(6)))
						.orderBy(Sort.asc("a.name"));
		
		return Result.of(executeQuery(selectFlightPath), selectFlightPath.toString());
	}

	@Override
	@SuppressWarnings("rawtypes")
	public Result findHotelsByDescriptionAndLocation(String description, String location) {
		// TODO Auto-generated method stub
		return null;
	}

	@SuppressWarnings("unchecked")
	private List<Map<String, Object>> executeQuery(Statement statementQuery) {
		
		return bucket.async().query(N1qlQuery.simple(statementQuery))
				.flatMap(AsyncN1qlQueryResult::rows)
				.map(result -> result.value().toMap())
				.timeout(10, TimeUnit.SECONDS)					
				.retryWhen(RetryBuilder.anyOf(java.net.ConnectException.class)
						.delay(Delay.exponential(TimeUnit.MILLISECONDS, 1000))
						.max(5)
						.build())
				.toList()
				.toBlocking()
				.single();
	}
}
