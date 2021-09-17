package com.nxp.api.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

@Service
public abstract class BaseService<E, T, R extends JpaRepository<E, T>> {

	@Autowired
	R repo;

	public List<E> getAll() {
		List<E> listAll = repo.findAll();
		return listAll;
	}

	public E getById(T id) {
		Optional<E> optional = repo.findById(id);
		if (optional.isPresent())
			return optional.get();
		return null;
	}

	public E add(E e) {
		return repo.save(e);
	}

	public E edit(E e) {
		return repo.save(e);
	}

	public void remove(T id) {
		repo.deleteById(id);
	}
}
