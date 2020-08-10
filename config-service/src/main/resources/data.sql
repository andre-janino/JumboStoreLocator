/** 
 * Insert the default key/values for Spring Cloud Config properties on the PROPERTIES table.
 * e.g.: INSERT INTO PROPERTIES VALUES ('app-service', 'default', 'master', 'prop', 'val');
 */

/** 
 * default profile
 */

/** JWT properties */
INSERT INTO PROPERTIES VALUES ('auth-service', 'default', 'master', 'header', 'Authorization');
INSERT INTO PROPERTIES VALUES ('auth-service', 'default', 'master', 'prefix', 'Bearer');
INSERT INTO PROPERTIES VALUES ('auth-service', 'default', 'master', 'secretKey', 'SuperSecretJumboKey');
/** token hardcoded to last a year, as this is a dev environment. */
INSERT INTO PROPERTIES VALUES ('auth-service', 'default', 'master', 'jwtExpiration', '2592000');
INSERT INTO PROPERTIES VALUES ('api-gateway', 'default', 'master', 'header', 'Authorization');
INSERT INTO PROPERTIES VALUES ('api-gateway', 'default', 'master', 'prefix', 'Bearer');
INSERT INTO PROPERTIES VALUES ('api-gateway', 'default', 'master', 'secretKey', 'SuperSecretJumboKey');

/** discovery-service application properties */
INSERT INTO PROPERTIES VALUES ('discovery-service', 'default', 'master', 'spring.cloud.config.fail-fast', 'true');
INSERT INTO PROPERTIES VALUES ('discovery-service', 'default', 'master', 'server.port', '9999');
INSERT INTO PROPERTIES VALUES ('discovery-service', 'default', 'master', 'eureka.client.register-with-eureka', 'false');
INSERT INTO PROPERTIES VALUES ('discovery-service', 'default', 'master', 'eureka.client.fetch-registry', 'false');
INSERT INTO PROPERTIES VALUES ('discovery-service', 'default', 'master', 'eureka.instance.hostname', 'localhost');
INSERT INTO PROPERTIES VALUES ('discovery-service', 'default', 'master', 'eureka.client.serviceUrl.defaultZone', 'http://${eureka.instance.hostname}:${server.port}/eureka/');

/** api-gateway application properties */
INSERT INTO PROPERTIES VALUES ('api-gateway', 'default', 'master', 'spring.cloud.config.fail-fast', 'true');
INSERT INTO PROPERTIES VALUES ('api-gateway', 'default', 'master', 'server.port', '3000');
INSERT INTO PROPERTIES VALUES ('api-gateway', 'default', 'master', 'eureka.client.serviceUrl.defaultZone', 'http://localhost:9999/eureka/');
INSERT INTO PROPERTIES VALUES ('api-gateway', 'default', 'master', 'eureka.client.registerWithEureka', 'false');
INSERT INTO PROPERTIES VALUES ('api-gateway', 'default', 'master', 'eureka.client.fetchRegistry', 'true');
INSERT INTO PROPERTIES VALUES ('api-gateway', 'default', 'master', 'zuul.sensitiveHeaders', 'Cookie,Set-Cookie');
INSERT INTO PROPERTIES VALUES ('api-gateway', 'default', 'master', 'ribbon.ConnectTimeout', '6000');
INSERT INTO PROPERTIES VALUES ('api-gateway', 'default', 'master', 'ribbon.ReadTimeout', '8000');
INSERT INTO PROPERTIES VALUES ('api-gateway', 'default', 'master', 'zuul.host.connect-timeout-millis', '8000');
INSERT INTO PROPERTIES VALUES ('api-gateway', 'default', 'master', 'zuul.ignored-headers','cache-control');

/** auth-service application properties */
INSERT INTO PROPERTIES VALUES ('auth-service', 'default', 'master', 'spring.cloud.config.fail-fast', 'true');
INSERT INTO PROPERTIES VALUES ('auth-service', 'default', 'master', 'server.port', '4000');
INSERT INTO PROPERTIES VALUES ('auth-service', 'default', 'master', 'eureka.client.serviceUrl.defaultZone', 'http://localhost:9999/eureka/');

/** store-service application properties */
INSERT INTO PROPERTIES VALUES ('store-service', 'default', 'master', 'spring.cloud.config.fail-fast', 'true');
INSERT INTO PROPERTIES VALUES ('store-service', 'default', 'master', 'server.port', '5555');
INSERT INTO PROPERTIES VALUES ('store-service', 'default', 'master', 'eureka.client.serviceUrl.defaultZone', 'http://localhost:9999/eureka/');
/** mongodb atlas connection info */
INSERT INTO PROPERTIES VALUES ('store-service', 'default', 'master', 'spring.data.mongodb.uri', 'mongodb+srv://jumbo:jumbodb@storescluster.ssuzl.mongodb.net/storeData?retryWrites=true&w=majority');
INSERT INTO PROPERTIES VALUES ('store-service', 'default', 'master', 'spring.data.mongodb.database', 'storeData');

/** user-service application properties */
INSERT INTO PROPERTIES VALUES ('user-service', 'default', 'master', 'spring.cloud.config.fail-fast', 'true');
INSERT INTO PROPERTIES VALUES ('user-service', 'default', 'master', 'server.port', '8082');
INSERT INTO PROPERTIES VALUES ('user-service', 'default', 'master', 'spring.datasource.url', 'jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1');
INSERT INTO PROPERTIES VALUES ('user-service', 'default', 'master', 'spring.datasource.username', 'sa');
INSERT INTO PROPERTIES VALUES ('user-service', 'default', 'master', 'spring.datasource.password', '');
INSERT INTO PROPERTIES VALUES ('user-service', 'default', 'master', 'spring.cloud.config.fail-fast', 'true');
INSERT INTO PROPERTIES VALUES ('user-service', 'default', 'master', 'eureka.client.serviceUrl.defaultZone', 'http://localhost:9999/eureka/');

/** 
 * TODO: production profile
 */