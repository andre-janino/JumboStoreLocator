package com.jumbo.userservice.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.test.context.junit4.SpringRunner;

import com.jumbo.userservice.entity.User;

@RunWith(SpringRunner.class)
@DataJpaTest
public class UserRepositoryIntegrationTest {

	private static final String USER_NAME_1 = "John";
	private static final String USER_NAME_2 = "Mary";
	
	private static final String USER_EMAIL_1 = "email_user_1@gmail.com";
	private static final String USER_EMAIL_2 = "email_user_2@gmail.com";
	
	@Autowired
    private TestEntityManager entityManager;
 
    @Autowired
    private UserRepository repository;
    
    @Test
    public void userRepositoryInjected_NotNull() throws Exception {
        assertThat(repository).isNotNull();
    }
    
    @Test
	public void findAll_ExistingUsers_ReturnUsers() {
		// declare a valid user
	    entityManager.persist(new User(USER_NAME_1, USER_EMAIL_1));
	    entityManager.persist(new User(USER_NAME_2, USER_EMAIL_2));
	    entityManager.flush();
	 
	    // execute the query
	    List<User> foundUsers = repository.findAll();
	 
	    // assert the user is present
	    assertThat(foundUsers.size()).isEqualTo(2);
	    assertThat(foundUsers.get(0).getFirstName()).isEqualTo(USER_NAME_1);
	    assertThat(foundUsers.get(1).getFirstName()).isEqualTo(USER_NAME_2);
	}
    
    @Test
   	public void findAll_NoUsers_ReturnEmpty() {
   	    // execute the query
   	    List<User> foundUsers = repository.findAll();
   	 
   	    // assert the user is present
   	    assertThat(foundUsers).isEmpty();
   	}
    
	@Test
	public void findByEmail_ExistingUser_ReturnUser() {
		// declare a valid user
	    entityManager.persist(new User(USER_NAME_1, USER_EMAIL_1));
	    entityManager.flush();
	 
	    // execute the query
	    User found = repository.findByEmail(USER_EMAIL_1);
	 
	    // assert the user is present
	    assertThat(found.getEmail()).isEqualTo(USER_EMAIL_1);
	}
	
	@Test
	public void findByEmail_InvalidEmail_ReturnNull() { 
	    // execute the query
	    User found = repository.findByEmail(USER_EMAIL_1);
	 
	    // assert the no user was found
	    assertThat(found).isNull();
	}
	
	@Test
	public void findByFirstName_ExistingUser_ReturnUser() {
		// declare a valid user
	    entityManager.persist(new User(USER_NAME_1, USER_EMAIL_1));
	    entityManager.flush();
	 
	    // execute the query
	    User found = repository.findByFirstName(USER_NAME_1);
	 
	    // assert the user is present
	    assertThat(found.getFirstName()).isEqualTo(USER_NAME_1);
	}
	
	@Test
	public void findByFirstName_InvalidName_ReturnNull() { 
	    // execute the query
	    User found = repository.findByFirstName(USER_NAME_1);
	 
	    // assert the no user was found
	    assertThat(found).isNull();
	}
	
	@Test
	public void saave_NewUser_ReturnCreatedUser() {
		// declare a valid user
	    User user = new User(USER_NAME_1, USER_EMAIL_1);
	 
	    // execute the query
	    User saved = repository.save(user);
	 
	    // assert the no user was found
	    assertThat(saved).isEqualTo(user);
	}
	
	@Test
	public void save_ExistingUser_ReturnUpdatedUser() {
		// declare a valid user
	    User user = new User(USER_NAME_1, USER_EMAIL_1);
		entityManager.persist(user);
	    entityManager.flush();
	    
	    // modify the user object
	    user.setFirstName("GALACTUS");
	    
	    // execute the save command passing the modified user
	    User result = repository.save(user);
	    
	    // assert that the user was modified
	    assertThat(result.getFirstName()).isEqualTo("GALACTUS");
	}
	
	@Test
	public void save_UnchangedUser_ReturnUpdatedUser() {
		// declare a valid user
	    User user = new User(USER_NAME_1, USER_EMAIL_1);
		entityManager.persist(user);
	    entityManager.flush();
	    
	    // execute the save command passing the unmodified user
	    User result = repository.save(user);
	    
	    // assert that no changes were made
	    assertThat(result).isEqualTo(user);
	}
	
	@Test
	public void delete_ExistingUser_UserRemoved() {
		// declare a valid user
	    User user = new User(USER_NAME_1, USER_EMAIL_1);
		entityManager.persist(user);
	    entityManager.flush();
	 
	    // execute the delete command and try to find the deleted user
	    repository.delete(user);
	    User found = repository.findByFirstName(USER_NAME_1);
	 
	    // assert the user was not found
	    assertThat(found).isNull();
	}
	
	@Test(expected = EmptyResultDataAccessException.class)
	public void delete_InvalidId_ThrowEmptyResultDataAccessException() {
	    // execute the delete command without passing a valid user ID
	    repository.deleteById(0l);
	}
	
	@Test(expected = InvalidDataAccessApiUsageException.class)
	public void delete_NullUser_ThrowInvalidDataAccessApiUsageException() {
	    // execute the delete command without passing a user 
	    repository.delete(null);
	}
}