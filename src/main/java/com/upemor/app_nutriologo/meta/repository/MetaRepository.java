package com.upemor.app_nutriologo.meta.repository;

import com.upemor.app_nutriologo.meta.entity.Meta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MetaRepository extends JpaRepository<Meta, Integer> {
}
