/** 
 * When the database is created, add a default user for testing purposes. 
 * The encrypted password is "Password1", and the ID is set to 1001 so that new users do not overlap due to @@GeneratedValue starting at 1.
 */

insert into users
values(1001, 'andre.janino@gmail.com', 'André', 'Armstrong Janino Cizotto', '$2a$10$heirHA89ULwxENiWxaj25O1S9oRafpyvLQw21shSNWhV7i/VuZNJ6');