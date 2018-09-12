package com.couchbase.day.config;

import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import com.couchbase.client.core.tracing.ThresholdLogReporter;
import com.couchbase.client.core.tracing.ThresholdLogTracer;
import com.couchbase.client.java.Bucket;
import com.couchbase.client.java.Cluster;
import com.couchbase.client.java.CouchbaseCluster;
import com.couchbase.client.java.env.CouchbaseEnvironment;
import com.couchbase.client.java.env.DefaultCouchbaseEnvironment;

import io.opentracing.Tracer;

import com.uber.jaeger.Configuration;
import com.uber.jaeger.samplers.ProbabilisticSampler;

@SpringBootApplication
public class Database {

	@Value("${storage.host}")
	private String host;

	@Value("${storage.bucket}")
	private String bucket;

	@Value("${storage.username}")
	private String username;

	@Value("${storage.password}")
	private String password;

	public @Bean Cluster couchbaseCluster() {
				
/*		
		Tracer tracer = new Configuration("couchbase-beer", 
				new Configuration.SamplerConfiguration(ProbabilisticSampler.TYPE, 1),
				new Configuration.ReporterConfiguration(
						true, 
						"localhost", 
						5775, 
						1000, 
						10000))
				.getTracer();
*/		

		
		//CouchbaseEnvironment env = DefaultCouchbaseEnvironment.builder().queryEndpoints(1).build();

		
		Tracer tracer = ThresholdLogTracer.create(ThresholdLogReporter.builder()
				.kvThreshold(500, TimeUnit.MICROSECONDS)
				.n1qlThreshold(1, TimeUnit.SECONDS)
				.logInterval(1, TimeUnit.SECONDS)   // log every 10 seconds
				.sampleSize(Integer.MAX_VALUE)
				.pretty(true)
				.build());

		CouchbaseEnvironment env = DefaultCouchbaseEnvironment.builder()
				.tracer(tracer)
				.build();
		
		CouchbaseCluster cluster = CouchbaseCluster.create(env, host);
		
		
		//CouchbaseCluster cluster = CouchbaseCluster.create(host);
		cluster.authenticate(username, password);
		return cluster;
	}

	public @Bean Bucket loginBucket() {
		return couchbaseCluster().openBucket(bucket);
	}
}
