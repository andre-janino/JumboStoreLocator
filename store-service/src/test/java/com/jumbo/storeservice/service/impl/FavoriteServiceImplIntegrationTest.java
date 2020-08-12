package com.jumbo.storeservice.service.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;

import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.junit4.SpringRunner;

import com.jumbo.storeservice.entity.Favorite;
import com.jumbo.storeservice.exception.BadRequest;
import com.jumbo.storeservice.repository.FavoriteRepository;
import com.jumbo.storeservice.service.FavoriteService;

/**
 * JUnit tests for the store-service. 
 * 
 * @author André Janino
 */
@RunWith(SpringRunner.class)
public class FavoriteServiceImplIntegrationTest {
	
	private static final String USER_1 = "John";
	private static final String USER_2 = "Claudia";
	private static final String USER_3 = "Andre";
	
	Favorite fav1;
	Favorite fav2;
	Favorite fav3;
	Favorite favNew;
	
	@TestConfiguration
    static class FavoriteServiceImplTestContextConfiguration {
 
        @Bean
        public FavoriteService userService() {
            return new FavoriteServiceImpl();
        }
    }
	
	@Autowired
    private FavoriteService service;
 
    @MockBean
    private FavoriteRepository repository;
    
    /**
     * Setup the repository returns for two users, and initialize a third one, that is not present on the db.
     */
    @Before
    public void setUp() {
    	fav1 = new Favorite();
    	fav1.setUserName(USER_1);
    	fav1.setStoreId("1234");
    	fav2 = new Favorite();
    	fav2.setUserName(USER_1);
    	fav2.setStoreId("5678");
    	fav3 = new Favorite();
    	fav3.setUserName(USER_2);
    	fav3.setStoreId("3131");
    	favNew = new Favorite();
    	favNew.setUserName(USER_2);
    	favNew.setStoreId("9999");
    	
    	// prepare the responses
    	List<Favorite> allFavoritesUser1 = Arrays.asList(fav1,fav2);
    	List<Favorite> allFavoritesUser2 = Arrays.asList(fav3);	
             
    	// mock the repository responses
        given(repository.findByUserName(eq(USER_1))).willReturn(allFavoritesUser1);
        given(repository.findByUserName(eq(USER_2))).willReturn(allFavoritesUser2);
       
        given(repository.findByUserNameAndId(eq(USER_1), eq("1234"))).willReturn(fav1);
        given(repository.findByUserNameAndId(eq(USER_1), eq("5678"))).willReturn(fav2);
        given(repository.findByUserNameAndId(eq(USER_2), eq("3131"))).willReturn(fav3);
    }
    
    @Test
    public void favoriteServiceInjected_NotNull() throws Exception {
        assertThat(service).isNotNull();
    }
    
    @Test
	public void findAllFavorites_User1_ReturnFavorites() {
		 List<Favorite> found = service.findAllFavorites(USER_1);
		 assertThat(found.size()).isEqualTo(2);
		 assertThat(found.get(1).getStoreId()).isEqualTo(fav2.getStoreId());
	}
    
    @Test
   	public void findAllFavorites_User2_ReturnFavorites() {
   		 List<Favorite> found = service.findAllFavorites(USER_2);
   		 assertThat(found.size()).isEqualTo(1);
   		 assertThat(found.get(0).getStoreId()).isEqualTo(fav3.getStoreId());
   	}
    
    @Test
   	public void findAllFavorites_InvalidUser_ReturnEmtpy() {
   		 List<Favorite> found = service.findAllFavorites(USER_3);
   		 assertThat(found.size()).isEqualTo(0);
   	}
       
    @Test(expected = BadRequest.class)
   	public void addFavorite_ExistingParams_ReturnsBadRequest() {
   		 service.addFavorite(fav1.getUserName(), fav1.getStoreId());
   	}
    
    @Test
   	public void addFavorite_ValidParams_DoesNotThrowBadRequest() {
   		 service.addFavorite(favNew.getUserName(), favNew.getStoreId());
   	}
    
    @Test
	public void delete_ValidId_ReturnTrue() {
		 boolean result = service.removeFavorite(fav1.getUserName(), fav1.getStoreId());
		 assertThat(result).isEqualTo(true);
	}
}