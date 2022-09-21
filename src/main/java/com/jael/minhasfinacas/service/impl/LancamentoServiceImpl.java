package com.jael.minhasfinacas.service.impl;

import com.jael.minhasfinacas.exception.RegraNegocioException;
import com.jael.minhasfinacas.model.entity.Lancamento;
import com.jael.minhasfinacas.model.enums.StatusLancamento;
import com.jael.minhasfinacas.model.enums.TipoLancamento;
import com.jael.minhasfinacas.model.repository.LancamentoRepository;
import com.jael.minhasfinacas.service.LancamentoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static org.springframework.data.domain.ExampleMatcher.matching;

@Service
public class LancamentoServiceImpl implements LancamentoService {
	
	@Autowired
	private final LancamentoRepository repository;
	
	public LancamentoServiceImpl( LancamentoRepository repository ) {
		
		this.repository = repository;
	}
	
	@Override
	@Transactional
	public Lancamento salvar( Lancamento lancamento ) {
		
		validar( lancamento );
		return repository.save( lancamento );
	}
	
	@Override
	@Transactional
	public Lancamento atualizar( Lancamento lancamento ) {
		
		Objects.requireNonNull( lancamento.getId() );
		validar( lancamento );
		return repository.save( lancamento );
	}
	
	@Override
	@Transactional
	public Optional< Lancamento > obterPorId( Long id ) {
		
		Optional< Lancamento > lancamento = repository.findById( id );
		
		if ( lancamento.isEmpty() ) {
			throw new RegraNegocioException( "Lançamento não encontrado para o ID informado." );
		}
		
		return lancamento;
	}
	
	@Override
	@Transactional
	public List< Lancamento > buscar( Lancamento lancamentoFiltro ) {
		
		Example< Lancamento > example = Example.of( lancamentoFiltro, matching().withIgnoreCase()
		                                                                        .withStringMatcher(
				                                                                        ExampleMatcher.StringMatcher.CONTAINING ) );
		return repository.findAll( example );
	}
	@Override
	@Transactional
	public List< String > buscarTodasAsDescricoesPorUsuario( Long id ) {
		
		List< String > listaDescricao = repository.obterDescricaoPorIdUsuario( id );
		
		if ( listaDescricao.isEmpty() ) {
			throw new RegraNegocioException( "Nenhuma descrição de lançamento encontrada." );
		}
		
		return listaDescricao;
	}
	
	@Override
	@Transactional( readOnly = true )
	public BigDecimal obterSaldoPorUsuario( Long id ) {
		
		BigDecimal receitas = repository.obterSaldoPorTipoLancamentoEUsuario( id, TipoLancamento.RECEITA );
		BigDecimal despesas = repository.obterSaldoPorTipoLancamentoEUsuario( id, TipoLancamento.DESPESA );
		
		if ( receitas == null ) {
			receitas = BigDecimal.ZERO;
		}
		
		if ( despesas == null ) {
			despesas = BigDecimal.ZERO;
		}
		return receitas.subtract( despesas );
	}
	
	@Override
	public void deletar( Lancamento lancamento ) {
		
		Objects.requireNonNull( lancamento.getId() );
		repository.delete( lancamento );
	}
	
	@Override
	@Transactional
	public void atualizarStatus( Lancamento lancamento, StatusLancamento status ) {
		
		lancamento.setStatus( status );
		atualizar( lancamento );
	}
	
	@Override
	public void validar( Lancamento lancamento ) {
		
		if ( lancamento.getDescricao() == null || lancamento.getDescricao().trim().equals( "" ) ) {
			throw new RegraNegocioException( "Informe uma Descrição válida." );
		}
		
		if ( lancamento.getMes() == null || lancamento.getMes() < 1 || lancamento.getMes() > 12 ) {
			throw new RegraNegocioException( "Informe um Mês entre 1 e 12." );
		}
		
		if ( lancamento.getAno() == null || lancamento.getAno().toString().length() != 4 ) {
			throw new RegraNegocioException( "Informe um Ano válido." );
		}
		
		if ( lancamento.getUsuario() == null || lancamento.getUsuario().getId() == null ) {
			throw new RegraNegocioException( "Informe um Usuário válido." );
		}
		
		if ( lancamento.getValor() == null || lancamento.getValor().compareTo( BigDecimal.ZERO ) < 1 ) {
			throw new RegraNegocioException( "Informe um Valor válido." );
		}
		
		if ( lancamento.getTipo() == null ) {
			throw new RegraNegocioException( "Informe um Tipo de Lançamento." );
		}
		
		if ( lancamento.getStatus() == null ) {
			throw new RegraNegocioException( "Informe um Status de Lançamento." );
		}
	}
	
}
