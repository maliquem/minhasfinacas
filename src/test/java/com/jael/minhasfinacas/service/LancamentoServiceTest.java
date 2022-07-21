package com.jael.minhasfinacas.service;

import com.jael.minhasfinacas.exception.RegraNegocioException;
import com.jael.minhasfinacas.model.entity.Lancamento;
import com.jael.minhasfinacas.model.enums.TipoLancamento;
import com.jael.minhasfinacas.model.repository.LancamentoRepository;
import com.jael.minhasfinacas.model.repository.LancamentoRepositoryTest;
import com.jael.minhasfinacas.service.impl.LancamentoServiceImpl;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.data.domain.Example;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static com.jael.minhasfinacas.model.enums.StatusLancamento.EFETIVADO;
import static com.jael.minhasfinacas.model.enums.StatusLancamento.PENDENTE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowableOfType;
import static org.mockito.Mockito.*;

@RunWith( SpringRunner.class )
@ActiveProfiles( "test" )
public class LancamentoServiceTest {
	
	@SpyBean
	LancamentoServiceImpl service;
	
	@MockBean
	LancamentoRepository repository;
	
	@Test
	public void deveSalvarUmLancamento() {
		//CENÁRIO
		Lancamento lancamento = LancamentoRepositoryTest.criarLancamento();
		doNothing().when( service ).validar( lancamento );
		Lancamento lancamentoRetorno = LancamentoRepositoryTest.criarLancamento();
		lancamentoRetorno.setId( 1L );
		lancamentoRetorno.setStatus( PENDENTE );
		when( repository.save( lancamento ) ).thenReturn( lancamentoRetorno );
		
		//AÇÃO
		Lancamento lancamentoSalvado = service.salvar( lancamento );
		
		//VERIFICAÇÃO
		assertThat( lancamentoSalvado.getId() ).isEqualTo( lancamentoRetorno.getId() );
		assertThat( lancamentoSalvado.getStatus() ).isEqualTo( lancamentoRetorno.getStatus() );
	}
	
	@Test
	public void naoDeveSalvarUmLancamentoQuandoHouverErroDeValidacao() {
		//CENÁRIO
		Lancamento lancamento = LancamentoRepositoryTest.criarLancamento();
		doThrow( RegraNegocioException.class ).when( service ).validar( lancamento );
		
		//AÇÃO
		catchThrowableOfType( () -> service.salvar( lancamento ), RegraNegocioException.class );
		
		//VERIFICAÇÃO
		verify( repository, never() ).save( lancamento );
	}
	
	@Test
	public void deveAtualizarUmLancamento() {
		//CENÁRIO
		Lancamento lancamento = LancamentoRepositoryTest.criarLancamento();
		lancamento.setId( 1L );
		lancamento.setStatus( PENDENTE );
		doNothing().when( service ).validar( lancamento );
		when( repository.save( lancamento ) ).thenReturn( lancamento );
		
		//AÇÃO
		service.atualizar( lancamento );
		
		//VERIFICAÇÃO
		verify( repository, times( 1 ) ).save( lancamento );
	}
	
	@Test
	public void naoDeveAtualizarUmLancamentoQueAindaNaoFoiSalvo() {
		//CENÁRIO
		Lancamento lancamento = LancamentoRepositoryTest.criarLancamento();
		
		//AÇÃO
		catchThrowableOfType( () -> service.atualizar( lancamento ), NullPointerException.class );
		
		//VERIFICAÇÃO
		verify( repository, never() ).save( lancamento );
	}
	
	@Test
	public void deveDeletarUmLancamento() {
		//CENÁRIO
		Lancamento lancamento = LancamentoRepositoryTest.criarLancamento();
		lancamento.setId( 1L );
		
		//AÇÃO
		service.deletar( lancamento );
		
		//VERIFICAÇÃO
		verify( repository, times( 1 ) ).delete( lancamento );
	}
	
	@Test
	public void naoDeveDeletarUmLancamentoQueAindaNaoFoiSalvo() {
		//CENÁRIO
		Lancamento lancamento = LancamentoRepositoryTest.criarLancamento();
		
		//AÇÃO
		catchThrowableOfType( () -> service.deletar( lancamento ), NullPointerException.class );
		
		//VERIFICAÇÃO
		verify( repository, never() ).delete( lancamento );
	}
	
	@Test
	public void deveFiltrarUmLancamento() {
		//CENÁRIO
		Lancamento lancamento = LancamentoRepositoryTest.criarLancamento();
		lancamento.setId( 1L );
		List< Lancamento > lista = List.of( lancamento );
		when( repository.findAll( any( Example.class ) ) ).thenReturn( lista );
		
		//AÇÃO
		List< Lancamento > buscar = service.buscar( lancamento );
		
		//VERIFICAÇÃO
		assertThat( buscar ).isNotEmpty().hasSize( 1 ).contains( lancamento );
	}
	
	@Test
	public void deveAtualizarUmStatusDeLancamento() {
		//CENÁRIO
		Lancamento lancamento = LancamentoRepositoryTest.criarLancamento();
		lancamento.setId( 1L );
		lancamento.setStatus( PENDENTE );
		doReturn( lancamento ).when( service ).atualizar( lancamento );
		
		//AÇÃO
		service.atualizarStatus( lancamento, EFETIVADO );
		
		//VERIFICAÇÃO
		assertThat( lancamento.getStatus() ).isEqualTo( EFETIVADO );
		verify( service, times( 1 ) ).atualizar( lancamento );
	}
	
	@Test
	public void deveObterUmLancamentoPorId() {
		//CENÁRIO
		Lancamento lancamento = LancamentoRepositoryTest.criarLancamento();
		lancamento.setId( 1L );
		when( repository.findById( anyLong() ) ).thenReturn( Optional.of( lancamento ) );
		
		//AÇÃO
		Optional< Lancamento > lancamentoObtido = service.obterPorId( 1L );
		
		//VERIFICAÇÃO
		assertThat( lancamentoObtido ).isPresent();
		assertThat( lancamentoObtido.get().getId() ).isEqualTo( lancamento.getId() );
	}
	
	@Test
	public void deveLancarErroAoValidarLancamento() {
		
		Lancamento lancamento = new Lancamento();
		
		Throwable regraNegocioException = catchThrowableOfType( () -> service.validar( lancamento ),
		                                                        RegraNegocioException.class );
		
		assertThat( regraNegocioException ).isInstanceOf( RegraNegocioException.class )
		                                   .hasMessageContaining( "Informe uma Descrição válida." );
		
		lancamento.setDescricao( "" );
		
		regraNegocioException = catchThrowableOfType( () -> service.validar( lancamento ),
		                                              RegraNegocioException.class );
		
		assertThat( regraNegocioException ).isInstanceOf( RegraNegocioException.class )
		                                   .hasMessageContaining( "Informe uma Descrição válida." );
		
		lancamento.setDescricao( "Salario" );
		
		regraNegocioException = catchThrowableOfType( () -> service.validar( lancamento ),
		                                              RegraNegocioException.class );
		
		assertThat( regraNegocioException ).isInstanceOf( RegraNegocioException.class )
		                                   .hasMessageContaining( "Informe um Mês entre 1 e 12." );
		
		lancamento.setMes( 13 );
		
		regraNegocioException = catchThrowableOfType( () -> service.validar( lancamento ),
		                                              RegraNegocioException.class );
		
		assertThat( regraNegocioException ).isInstanceOf( RegraNegocioException.class )
		                                   .hasMessageContaining( "Informe um Mês entre 1 e 12." );
		
		lancamento.setMes( 1 );
		
		regraNegocioException = catchThrowableOfType( () -> service.validar( lancamento ),
		                                              RegraNegocioException.class );
		
		assertThat( regraNegocioException ).isInstanceOf( RegraNegocioException.class )
		                                   .hasMessageContaining( "Informe um Ano válido." );
		
		lancamento.setAno( 20222 );
		
		regraNegocioException = catchThrowableOfType( () -> service.validar( lancamento ),
		                                              RegraNegocioException.class );
		
		assertThat( regraNegocioException ).isInstanceOf( RegraNegocioException.class )
		                                   .hasMessageContaining( "Informe um Ano válido." );
		
		lancamento.setAno( 2022 );
		
		regraNegocioException = catchThrowableOfType( () -> service.validar( lancamento ),
		                                              RegraNegocioException.class );
		
		assertThat( regraNegocioException ).isInstanceOf( RegraNegocioException.class )
		                                   .hasMessageContaining( "Informe um Usuário válido." );
		
		lancamento.setUsuario( UsuarioServiceTest.criarUsuario() );
		
		regraNegocioException = catchThrowableOfType( () -> service.validar( lancamento ),
		                                              RegraNegocioException.class );
		
		assertThat( regraNegocioException ).isInstanceOf( RegraNegocioException.class )
		                                   .hasMessageContaining( "Informe um Valor válido." );
		
		lancamento.setValor( BigDecimal.ZERO );
		
		regraNegocioException = catchThrowableOfType( () -> service.validar( lancamento ),
		                                              RegraNegocioException.class );
		
		assertThat( regraNegocioException ).isInstanceOf( RegraNegocioException.class )
		                                   .hasMessageContaining( "Informe um Valor válido." );
		
		lancamento.setValor( BigDecimal.ONE );
		
		regraNegocioException = catchThrowableOfType( () -> service.validar( lancamento ),
		                                              RegraNegocioException.class );
		
		assertThat( regraNegocioException ).isInstanceOf( RegraNegocioException.class )
		                                   .hasMessageContaining( "Informe um Tipo de Lançamento." );
		
	}
	
	@Test
	public void deveObterSaldoDoUsuario() {
		//CENÁRIO
		Lancamento lancamentoReceita = LancamentoRepositoryTest.criarLancamento();
		Lancamento lancamentoDespesa = LancamentoRepositoryTest.criarLancamento();
		lancamentoDespesa.setId( 2L );
		lancamentoDespesa.setTipo( TipoLancamento.DESPESA );
		lancamentoDespesa.setValor( BigDecimal.valueOf( 200 ) );
		when( repository.obterSaldoPorTipoLancamentoEUsuario( 1L, TipoLancamento.RECEITA ) ).thenReturn(
				lancamentoReceita.getValor() );
		when( repository.obterSaldoPorTipoLancamentoEUsuario( 1L, TipoLancamento.DESPESA ) ).thenReturn(
				lancamentoDespesa.getValor() );
		
		//AÇÃO
		BigDecimal saldoPorUsuario = service.obterSaldoPorUsuario( lancamentoDespesa.getUsuario().getId() );
		
		//VERIFICAÇÃO
		verify( repository, times( 1 ) ).obterSaldoPorTipoLancamentoEUsuario( 1L, TipoLancamento.RECEITA );
		verify( repository, times( 1 ) ).obterSaldoPorTipoLancamentoEUsuario( 1L, TipoLancamento.DESPESA );
		assertThat( saldoPorUsuario ).isEqualTo( BigDecimal.valueOf( 200 ) );
	}
	
}
