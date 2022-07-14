package com.jael.minhasfinacas.service;

import com.jael.minhasfinacas.model.entity.Lancamento;
import com.jael.minhasfinacas.model.enums.StatusLancamento;

import java.util.List;

public interface LancamentoService {
	
	Lancamento salvar( Lancamento lancamento );
	
	Lancamento atualizar( Lancamento lancamento );
	
	List< Lancamento > buscar( Lancamento lancamentoFiltro );
	
	void deletar( Lancamento lancamento );
	
	void atualizarStatus( Lancamento lancamento, StatusLancamento status );
	
	void validar( Lancamento lancamento );
	
}
