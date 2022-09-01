package com.jael.minhasfinacas.model.repository;

import com.jael.minhasfinacas.model.entity.Lancamento;
import com.jael.minhasfinacas.model.enums.TipoLancamento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;

public interface LancamentoRepository extends JpaRepository< Lancamento, Long > {
	
	@Query( value = " SELECT sum(l.valor) FROM Lancamento l JOIN l.usuario u WHERE u.id = :idUsuario and l.tipo = :tipo GROUP BY u " )
	BigDecimal obterSaldoPorTipoLancamentoEUsuario( @Param( "idUsuario" ) Long idUsuario,
	                                                @Param( "tipo" ) TipoLancamento tipo );
	
	@Query( value = " SELECT l.descricao FROM Lancamento l JOIN l.usuario u WHERE u.id = :idUsuario " )
	List< String > obterDescricaoPorIdUsuario( @Param( "idUsuario" ) Long idUsuario );
	
}
