package com.nxp.api.repo;

import com.nxp.api.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {

	@Query(value = "SELECT b FROM Account a JOIN Role b ON a.roleId = b.id WHERE a.username = :username")
	Role checkRole(@Param(value = "username") String username);

}
