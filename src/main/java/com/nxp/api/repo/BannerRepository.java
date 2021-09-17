package com.nxp.api.repo;

import java.util.List;

import com.nxp.api.entity.Banner;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface BannerRepository extends JpaRepository<Banner, Long> {

	List<Banner> findAllByOrderByPriorityAscCreatedDateDesc();

	List<Banner> findAllByStatusAndOperatorIdOrderByPriorityAscCreatedDateDesc(int status, Long operatorId);

	@Query(value = "SELECT a FROM Banner a WHERE UPPER(a.title) like UPPER(CONCAT('%', :keysearch,'%')) ORDER BY a.createdDate")
	List<Banner> searchBanner(@Param("keysearch") String keysearch, Pageable pageable);
	
	@Query(value = "SELECT count(a) FROM Banner a WHERE UPPER(a.title) like UPPER(CONCAT('%', :keysearch,'%')) ORDER BY a.createdDate")
	Long countBanner(@Param("keysearch") String keysearch);
}
