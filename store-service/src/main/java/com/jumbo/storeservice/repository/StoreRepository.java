package com.jumbo.storeservice.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.jumbo.storeservice.entity.Store;

public interface StoreRepository extends PagingAndSortingRepository<Store, String> {

	@Query("{locationType:'?0'}")
    List<Store> findByLocationType(String locationType);
	
	Iterable<Store> findAll(Sort sort);

	Page<Store> findAll(Pageable pageable);
}

