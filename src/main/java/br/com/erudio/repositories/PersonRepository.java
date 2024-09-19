package br.com.erudio.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.erudio.model.Person;

//@Repository não é mais necessário para se tornar um JpaRepository
public interface PersonRepository extends JpaRepository<Person, Long> {}
