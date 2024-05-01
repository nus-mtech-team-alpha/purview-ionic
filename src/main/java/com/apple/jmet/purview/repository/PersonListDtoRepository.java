package com.apple.jmet.purview.repository;


import java.util.List;

import org.springframework.data.repository.Repository;

import com.apple.jmet.purview.domain.Person;
import com.apple.jmet.purview.dto.PersonListDto;

public interface PersonListDtoRepository extends Repository<Person, Long> {
    List<PersonListDto> findAllBy();
}
