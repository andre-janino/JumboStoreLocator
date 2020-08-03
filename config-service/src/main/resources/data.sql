/** 
 * Insert the default key/values for Spring Cloud Config properties on the PROPERTIES table.
 * e.g.: INSERT INTO PROPERTIES VALUES ('app-service', 'default', 'master', 'prop', 'val');
 * 
 * Values which would not change depending on the profile (such as zuul routes) are still defined in application.properties.
 */

/** 
 * default profile
 */

/** JWT properties */
INSERT INTO PROPERTIES VALUES ('auth-service', 'default', 'master', 'header', 'Authorization');
INSERT INTO PROPERTIES VALUES ('auth-service', 'default', 'master', 'prefix', 'Bearer');
INSERT INTO PROPERTIES VALUES ('auth-service', 'default', 'master', 'secretKey', 'SuperSecretJumboKey');
INSERT INTO PROPERTIES VALUES ('auth-service', 'default', 'master', 'jwtExpiration', '3600');
INSERT INTO PROPERTIES VALUES ('api-gateway', 'default', 'master', 'header', 'Authorization');
INSERT INTO PROPERTIES VALUES ('api-gateway', 'default', 'master', 'prefix', 'Bearer');
INSERT INTO PROPERTIES VALUES ('api-gateway', 'default', 'master', 'secretKey', 'SuperSecretJumboKey');

/** discovery-service application properties */
INSERT INTO PROPERTIES VALUES ('discovery-service', 'default', 'master', 'server.port', '9000');
INSERT INTO PROPERTIES VALUES ('discovery-service', 'default', 'master', 'eureka.client.register-with-eureka', 'false');
INSERT INTO PROPERTIES VALUES ('discovery-service', 'default', 'master', 'eureka.client.fetch-registry', 'false');
INSERT INTO PROPERTIES VALUES ('discovery-service', 'default', 'master', 'eureka.instance.hostname', 'localhost');
INSERT INTO PROPERTIES VALUES ('discovery-service', 'default', 'master', 'eureka.client.serviceUrl.defaultZone', 'http://${eureka.instance.hostname}:${server.port}/eureka/');

/** api-gateway application properties */
INSERT INTO PROPERTIES VALUES ('api-gateway', 'default', 'master', 'server.port', '3000');
INSERT INTO PROPERTIES VALUES ('api-gateway', 'default', 'master', 'eureka.client.serviceUrl.defaultZone', 'http://localhost:9000/eureka/');
INSERT INTO PROPERTIES VALUES ('api-gateway', 'default', 'master', 'eureka.client.registerWithEureka', 'false');
INSERT INTO PROPERTIES VALUES ('api-gateway', 'default', 'master', 'eureka.client.fetchRegistry', 'true');
INSERT INTO PROPERTIES VALUES ('api-gateway', 'default', 'master', 'zuul.sensitiveHeaders', 'Cookie,Set-Cookie');
INSERT INTO PROPERTIES VALUES ('api-gateway', 'default', 'master', 'ribbon.ConnectTimeout', '1500');
INSERT INTO PROPERTIES VALUES ('api-gateway', 'default', 'master', 'ribbon.ReadTimeout', '3000');
INSERT INTO PROPERTIES VALUES ('api-gateway', 'default', 'master', 'zuul.host.connect-timeout-millis', '3000');

/** auth-service application properties */
INSERT INTO PROPERTIES VALUES ('auth-service', 'default', 'master', 'server.port', '4000');
INSERT INTO PROPERTIES VALUES ('auth-service', 'default', 'master', 'eureka.client.serviceUrl.defaultZone', 'http://localhost:9000/eureka/');
/** define hystrix threasholds: if 10 out of 20 requests have failed in the past 5 seconds, call the fallback method */
INSERT INTO PROPERTIES VALUES ('auth-service', 'default', 'master', 'hystrix.command.default.circuitBreaker.requestVolumeThreshold', '20');
INSERT INTO PROPERTIES VALUES ('auth-service', 'default', 'master', 'hystrix.command.default.circuitBreaker.errorThresholdPercentage', '50');
INSERT INTO PROPERTIES VALUES ('auth-service', 'default', 'master', 'hystrix.command.default.circuitBreaker.sleepWindowInMilliseconds', '5000');

/** user-service application properties */
INSERT INTO PROPERTIES VALUES ('user-service', 'default', 'master', 'server.port', '8082');
INSERT INTO PROPERTIES VALUES ('user-service', 'default', 'master', 'spring.datasource.url', 'jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1');
INSERT INTO PROPERTIES VALUES ('user-service', 'default', 'master', 'spring.datasource.username', 'sa');
INSERT INTO PROPERTIES VALUES ('user-service', 'default', 'master', 'spring.datasource.password', '');
INSERT INTO PROPERTIES VALUES ('user-service', 'default', 'master', 'spring.cloud.config.fail-fast', 'true');
INSERT INTO PROPERTIES VALUES ('user-service', 'default', 'master', 'eureka.client.serviceUrl.defaultZone', 'http://localhost:9000/eureka/');

/** 
 * TODO: production profile
 */
 
 