# couchbase-beer

mvn clean package

### To run the web proxy :

mvn spring-boot:run

By default Spring Boot use the port 8080

For Couchbase configuration, you can modify the file src/main/java/resources/applications.properties

* storage.host=10.112.163.101 (couchbase ip adress)
* storage.bucket=beer-sample
* storage.username=Administrator
* storage.password=couchbase

REST apis example for test :

KV -> GET http://127.0.0.1:8080/api/airports/airport/airport_1254/

N1QL -> GET http://127.0.0.1:8080/api/airports/airport/name/Peronne St Quentin/