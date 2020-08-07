/** 
 * When the database is created, add:
 * - Three hard-coded user roles.
 * - A default user for testing purposes. 
 * 
 * Regarding the default user: the encrypted password is "Password1", and the ID is set to 1001 so that new users do not overlap due to @@GeneratedValue starting at 1.
 * A guest user is also created to evaluate limited permissions (specially useful if user-service is unavailable and circuit-breaker is activated at auth-service)
 */

insert into role (id, name) values(1, 'GUEST');
insert into role (id, name) values(2, 'USER');
insert into role (id, name) values(3, 'ADMIN');

insert into users (id, email, first_name, last_name, password, role_id)
values(1001, 'andre.janino@gmail.com', 'André', 'Armstrong Janino Cizotto', '$2a$10$heirHA89ULwxENiWxaj25O1S9oRafpyvLQw21shSNWhV7i/VuZNJ6', 3);

insert into users (id, email, first_name, last_name, password, role_id)
values(1002, 'marijn.deromph@jumbo.com', 'Marijn', 'de Römph', '$2a$10$heirHA89ULwxENiWxaj25O1S9oRafpyvLQw21shSNWhV7i/VuZNJ6', 3);

insert into users (id, email, first_name, last_name, password, role_id)
values(1003, 'marijn.deromph@jumbo.com', 'Marijn', 'de Römph', '$2a$10$heirHA89ULwxENiWxaj25O1S9oRafpyvLQw21shSNWhV7i/VuZNJ6', 3);

insert into users (id, email, first_name, last_name, password, role_id)
values(1004, 'tamara.duric@jumbo.com', 'Tamara', 'Duric', '$2a$10$heirHA89ULwxENiWxaj25O1S9oRafpyvLQw21shSNWhV7i/VuZNJ6', 3);

insert into users (id, email, first_name, last_name, password, role_id)
values(1005, 'gustavo.henriquesmartins@jumbo.com', 'Gustavo', 'Henrique Martins', '$2a$10$heirHA89ULwxENiWxaj25O1S9oRafpyvLQw21shSNWhV7i/VuZNJ6', 3);