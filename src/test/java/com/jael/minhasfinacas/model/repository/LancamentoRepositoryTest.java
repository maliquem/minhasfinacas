package com.jael.minhasfinacas.model.repository;

import com.jael.minhasfinacas.model.entity.Lancamento;
import com.jael.minhasfinacas.model.enums.StatusLancamento;
import com.jael.minhasfinacas.model.enums.TipoLancamento;
import com.jael.minhasfinacas.service.UsuarioServiceTest;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith( SpringRunner.class )
@DataJpaTest
@AutoConfigureTestDatabase( replace = AutoConfigureTestDatabase.Replace.NONE )
@ActiveProfiles( "test" )
public class LancamentoRepositoryTest {
	
	@Autowired
	LancamentoRepository repository;
	
	@Autowired
	TestEntityManager entityManager;
	
	@Test
	public void deveSalvarUmLancamento() {
		//CENÁRIO
		Lancamento lancamento = criarLancamento();
		
		//AÇÃO
		Lancamento lancamentoSalvo = repository.save( lancamento );
		
		//VERIFICAÇÃO
		assertThat( lancamentoSalvo.getId() ).isNotNull();
	}
	
	@Test
	public void deveDeletarUmLancamento() {
		//CENÁRIO
		Lancamento lancamento = criarEPersistirUmLancamento();
		
		//AÇÃO
		Lancamento lancamentoSalvo = entityManager.find( Lancamento.class, lancamento.getId() );
		repository.delete( lancamentoSalvo );
		
		//VERIFICAÇÃO
		Lancamento lancamentoDeletado = entityManager.find( Lancamento.class, lancamento.getId() );
		assertThat( lancamentoDeletado ).isNull();
	}
	
	@Test
	public void deveAtualizarUmLancamento() {
		//CENÁRIO
		Lancamento lancamento = criarEPersistirUmLancamento();
		
		//AÇÃO
		lancamento.setAno( 2020 );
		lancamento.setMes( 3 );
		repository.save( lancamento );
		
		//VERIFICAÇÃO
		Lancamento lancamentoAtualizado = entityManager.find( Lancamento.class, lancamento.getId() );
		assertThat( lancamentoAtualizado.getAno() ).isEqualTo( 2020 );
		assertThat( lancamentoAtualizado.getMes() ).isEqualTo( 3 );
	}
	
	@Test
	public void deveBuscarUmLancamentoPorId() {
		//CENÁRIO
		Lancamento lancamento = criarEPersistirUmLancamento();
		
		//AÇÃO
		Optional< Lancamento > lancamentoAchado = repository.findById( lancamento.getId() );
		
		//VERIFICAÇÃO
		assertThat( lancamentoAchado ).isPresent();
	}
	private Lancamento criarEPersistirUmLancamento() {
		
		Lancamento lancamento = criarLancamento();
		entityManager.persist( lancamento );
		return lancamento;
	}
	
	public static Lancamento criarLancamento() {
		
		return Lancamento.builder()
		                 .ano( 2022 )
		                 .mes( 7 )
		                 .descricao( "lançamento teste" )
		                 .valor( BigDecimal.valueOf( 400 ) )
		                 .tipo( TipoLancamento.RECEITA )
		                 .status( StatusLancamento.PENDENTE )
		                 .usuario( UsuarioServiceTest.criarUsuario() )
		                 .dataCadastro( LocalDate.now() )
		                 .build();
	}
	
}
