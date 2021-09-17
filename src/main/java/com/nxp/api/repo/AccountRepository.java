package com.nxp.api.repo;

import java.util.List;

import com.nxp.api.entity.Account;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {

	Account findAccountByUsernameAndPassword(String username, String password);

	Account findAccountByUsername(String username);

	@Query(value = "SELECT a FROM Account a WHERE a.username = :username AND a.id != :id")
	Account checkUsernameForUpdate(@Param("username") String username, @Param("id") Long id);

	@Modifying
	@Query(value = "UPDATE Account a SET a.password = :newPassword WHERE a.username = :username")
	void changePasswordByUsername(@Param("newPassword") String newPassword, @Param("username") String username);

	@Query(value = "SELECT a FROM Account a WHERE (UPPER(a.username) like UPPER(CONCAT('%', :keysearch,'%')) "
			+ "OR UPPER(a.fullname) like UPPER(CONCAT('%', :keysearch,'%')) OR UPPER(a.email) like UPPER(CONCAT('%', :keysearch,'%')) "
			+ "OR UPPER(a.phone) like UPPER(CONCAT('%', :keysearch,'%')) OR UPPER(a.penname) like UPPER(CONCAT('%', :keysearch,'%'))) and id != :id ORDER BY a.fullname")
	List<Account> searchAccount(@Param("keysearch") String keysearch, @Param("id") Long id, Pageable pageable);
	
	@Query(value = "SELECT count(a) FROM Account a WHERE (UPPER(a.username) like UPPER(CONCAT('%', :keysearch,'%')) "
			+ "OR UPPER(a.fullname) like UPPER(CONCAT('%', :keysearch,'%')) OR UPPER(a.email) like UPPER(CONCAT('%', :keysearch,'%')) "
			+ "OR UPPER(a.phone) like UPPER(CONCAT('%', :keysearch,'%')) OR UPPER(a.penname) like UPPER(CONCAT('%', :keysearch,'%'))) and id != :id ORDER BY a.fullname")
	Long countAccount(@Param("keysearch") String keysearch, @Param("id") Long id);
}
