/** 
 * Insert the default key/values for Spring Cloud Config properties on the PROPERTIES table.
 */

INSERT INTO PROPERTIES VALUES ('auth-service', 'default', 'master', 'header', 'Authorization');
INSERT INTO PROPERTIES VALUES ('auth-service', 'default', 'master', 'prefix', 'Bearer');
INSERT INTO PROPERTIES VALUES ('auth-service', 'default', 'master', 'secretKey', 'SuperSecretJumboKey');
INSERT INTO PROPERTIES VALUES ('auth-service', 'default', 'master', 'jwtExpiration', '3600');

INSERT INTO PROPERTIES VALUES ('api-gateway', 'default', 'master', 'header', 'Authorization');
INSERT INTO PROPERTIES VALUES ('api-gateway', 'default', 'master', 'prefix', 'Bearer');
INSERT INTO PROPERTIES VALUES ('api-gateway', 'default', 'master', 'secretKey', 'SuperSecretJumboKey');