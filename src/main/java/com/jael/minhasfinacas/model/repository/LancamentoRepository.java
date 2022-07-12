package com.jael.minhasfinacas.model.repository;

import com.jael.minhasfinacas.model.entity.Lancamento;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LancamentoRepository extends JpaRepository<Lancamento, Long> {
}
