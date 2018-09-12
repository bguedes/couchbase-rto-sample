# couchbase-beer

mvn clean package

### To run the web proxy :

mvn spring-boot:run

### Web proxy parameters :

### Jaeger for  :

docker run -d --name jaeger -e COLLECTOR_ZIPKIN_HTTP_PORT=9411 -p 5775:5775/udp -p 6831:6831/udp -p 6832:6832/udp -p 5778:5778 -p 16686:16686 -p 14268:14268 -p 9411:9411 jaegertracing/all-in-one:latest

For using Jaeger UI http://localhost:16686

By default Spring Boot use the port 8080

For Couchbase configuration, you can modify the file src/main/java/resources/applications.properties

* storage.host=10.112.163.101 (couchbase ip adress)
* storage.bucket=beer-sample
* storage.username=Administrator
* storage.password=couchbase
