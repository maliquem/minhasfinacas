package com.jael.minhasfinacas.service;

import com.jael.minhasfinacas.model.entity.Lancamento;
import com.jael.minhasfinacas.model.enums.StatusLancamento;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface LancamentoService {
	
	Lancamento salvar( Lancamento lancamento );
	
	Lancamento atualizar( Lancamento lancamento );
	
	Optional< Lancamento > obterPorId( Long id );
	
	List< Lancamento > buscar( Lancamento lancamentoFiltro );
	
	BigDecimal obterSaldoPorUsuario( Long id );
	
	void deletar( Lancamento lancamento );
	
	void atualizarStatus( Lancamento lancamento, StatusLancamento status );
	
	void validar( Lancamento lancamento );
	
}
