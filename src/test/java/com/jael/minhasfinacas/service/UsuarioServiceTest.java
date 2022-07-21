package com.jael.minhasfinacas.service;

import com.jael.minhasfinacas.exception.ErroAutenticacao;
import com.jael.minhasfinacas.exception.RegraNegocioException;
import com.jael.minhasfinacas.model.entity.Usuario;
import com.jael.minhasfinacas.model.repository.UsuarioRepository;
import com.jael.minhasfinacas.service.impl.UsuarioServiceImpl;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.Mockito.*;

@RunWith( SpringRunner.class )
@ActiveProfiles( "test" )
public class UsuarioServiceTest {
	
	@SpyBean
	UsuarioServiceImpl service;
	
	@MockBean
	UsuarioRepository repository;
	
	@Test( expected = Test.None.class )
	public void deveSalvarUmUsuario() {
		//CENÁRIO
		Usuario usuario = criarUsuario();
		doNothing().when( service ).validarEmail( anyString() );
		when( repository.save( any( Usuario.class ) ) ).thenReturn( usuario );
		
		//AÇÃO
		Usuario usuarioSalvo = service.salvarUsuario( new Usuario() );
		
		//VERIFICAÇÃO
		assertThat( usuarioSalvo ).isNotNull();
		assertThat( usuarioSalvo.getId() ).isEqualTo( 1L );
		assertThat( usuarioSalvo.getEmail() ).isEqualTo( "usuario@email.com" );
		assertThat( usuarioSalvo.getNome() ).isEqualTo( "usuário" );
		assertThat( usuarioSalvo.getSenha() ).isEqualTo( "senha" );
	}
	
	@Test( expected = RegraNegocioException.class )
	public void napDeveSalvarUmUsuarioComEmailJaCadastrado() {
		//CENÁRIO
		Usuario usuario = criarUsuario();
		doThrow( RegraNegocioException.class ).when( service ).validarEmail( usuario.getEmail() );
		
		//AÇÃO
		service.salvarUsuario( usuario );
		
		//VERIFICAÇÃO
		verify( repository, never() ).save( usuario );
	}
	
	@Test( expected = Test.None.class )
	public void deveAutenticarUmUsuarioComSucesso() {
		//CENÁRIO
		Usuario usuario = criarUsuario();
		when( repository.findByEmail( usuario.getEmail() ) ).thenReturn( Optional.of( usuario ) );
		
		//AÇÃO
		Usuario result = service.autenticar( usuario.getEmail(), usuario.getSenha() );
		
		//VERIFICAÇÃO
		assertThat( result ).isNotNull();
	}
	
	@Test
	public void deveNaoAutenticarUmUsuarioDevidoAEmailIncorreto() {
		//CENÁRIO
		Usuario usuario = criarUsuario();
		when( repository.findByEmail( usuario.getEmail() ) ).thenReturn( Optional.of( usuario ) );
		
		//AÇÃO
		Throwable exception = catchThrowable( () -> service.autenticar( "email@email.com", usuario.getSenha() ) );
		assertThat( exception ).isInstanceOf( ErroAutenticacao.class )
		                       .hasMessage( "Usuário não encontrado para o email informado." );
	}
	
	@Test
	public void deveNaoAutenticarUmUsuarioDevidoASenhaInvalida() {
		//CENÁRIO
		Usuario usuario = criarUsuario();
		when( repository.findByEmail( usuario.getEmail() ) ).thenReturn( Optional.of( usuario ) );
		
		//AÇÃO
		Throwable exception = catchThrowable( () -> service.autenticar( usuario.getEmail(), "password" ) );
		assertThat( exception ).isInstanceOf( ErroAutenticacao.class ).hasMessage( "Senha inválida." );
	}
	
	@Test( expected = Test.None.class )
	public void deveValidarEmail() {
		//CENÁRIO
		when( repository.existsByEmail( anyString() ) ).thenReturn( false );
		
		//AÇÃO
		service.validarEmail( "usuario@email.com" );
	}
	
	@Test( expected = RegraNegocioException.class )
	public void deveLancarErroAoValidarEmailQuandoExistirEmailCadastrado() {
		//CENÁRIO
		when( repository.existsByEmail( anyString() ) ).thenReturn( true );
		
		//AÇÃO
		service.validarEmail( "usuario@email.com" );
	}
	
	public static Usuario criarUsuario() {
		
		return Usuario.builder().id( 1L ).nome( "usuário" ).email( "usuario@email.com" ).senha( "senha" ).build();
	}
	
}

