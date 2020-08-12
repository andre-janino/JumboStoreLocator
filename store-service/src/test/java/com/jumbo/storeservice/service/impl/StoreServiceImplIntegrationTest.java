package com.jumbo.storeservice.service.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.BDDMockito.given;

import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.junit4.SpringRunner;

import com.jumbo.storeservice.entity.Store;
import com.jumbo.storeservice.repository.StoreRepository;
import com.jumbo.storeservice.service.StoreService;

/**
 * JUnit tests for the store-service. 
 * 
 * @author André Janino
 */
@RunWith(SpringRunner.class)
public class StoreServiceImplIntegrationTest {

	private static final String CITY_1 = "CITY_1";
	private static final String CITY_3 = "CITY_3";
	private static final String CITY_2 = "CITY_2";
	
	Store store1;
	Store store2;
	Store store3;
	
	private final static String SupermarktPuP = "SupermarktPuP";
	private final static String PuP = "PuP";
	private final static String Supermarkt = "Supermarkt";
	private final static List<String> storeTypesFull = Arrays.asList(SupermarktPuP, PuP, Supermarkt);
	private final static List<String> storeTypesFiltered = Arrays.asList(PuP);
	
	private final static String storeId1 = "1";
	private final static String storeId2 = "2";
	private final static List<String> storeIds = Arrays.asList(storeId1, storeId2);
	
	@Mock
    private Page<Store> storesPage;
	
	
	@TestConfiguration
    static class StoreServiceImplTestContextConfiguration {
 
        @Bean
        public StoreService userService() {
            return new StoreServiceImpl();
        }
    }
	
	@Autowired
    private StoreService service;
 
    @MockBean
    private StoreRepository repository;
    
    /**
     * Setup the repository returns for two users, and initialize a third one, that is not present on the db.
     */
    @Before
    public void setUp() {
    	store1 = new Store();
    	store1.setAddressName(CITY_1);
    	store2 = new Store();
    	store2.setAddressName(CITY_2);
    	store3 = new Store();
    	store3.setAddressName(CITY_3);
    	
    	// prepare the responses
    	List<Store> allStores = Arrays.asList(store1,store2,store3);	
    	given(storesPage.getContent()).willReturn(allStores);
    	List<Store> filteredStores = Arrays.asList(store1);
    	List<Store> allStoresOrdered = Arrays.asList(store2,store3,store1);
    	List<Store> allStoresOrderedFiltered = Arrays.asList(store2,store1);
             
    	// mock the repository responses
        given(repository.findByLocationTypeIn(eq(storeTypesFull), any(Pageable.class))).willReturn(allStores);
        given(repository.findByLocationTypeIn(eq(storeTypesFiltered), any(Pageable.class))).willReturn(filteredStores);
        
        given(repository.findByLocationTypeInAndSapStoreIDIn(anyList(), anyList())).willReturn(allStores);    				 
        given(repository.findNearestStores(anyDouble(), anyDouble(), anyList(), any(Pageable.class))).willReturn(allStoresOrdered);
        given(repository.findNearestStoresById(anyDouble(), anyDouble(), anyList(), anyList())).willReturn(allStoresOrderedFiltered);
    }
    
    @Test
    public void storeServiceInjected_NotNull() throws Exception {
        assertThat(service).isNotNull();
    }
    
    @Test
	public void findAll_ReturnStores() {
		 List<Store> found = service.findAllStores(storeTypesFull, 1000);
		 assertThat(found.size()).isEqualTo(3);
	}
    
    @Test
	public void findAll_Filtered_ReturnStores() {
		 List<Store> found = service.findAllStores(storeTypesFiltered, 1000);
		 assertThat(found.size()).isEqualTo(1);
	}
    
	@Test
	public void findFavoriteStores_ReturnStores() {
		List<Store> found = service.findFavoriteStores(storeIds, storeTypesFull);
		assertThat(found.size()).isEqualTo(3);
		assertThat(found.get(1).getAddressName()).isEqualTo(CITY_2);
	}

    @Test
	public void findNearestFavoriteStores_ReturnStores() {
    	List<Store> found = service.findNearestFavoriteStores(12345d, 54321d, storeIds, storeTypesFull);
		assertThat(found.size()).isEqualTo(2);
		assertThat(found.get(0).getAddressName()).isEqualTo(CITY_2);
    }
}