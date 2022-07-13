package com.jael.minhasfinacas.service;

import com.jael.minhasfinacas.exception.ErroAutenticacao;
import com.jael.minhasfinacas.exception.RegraNegocioException;
import com.jael.minhasfinacas.model.entity.Usuario;
import com.jael.minhasfinacas.model.repository.UsuarioRepository;
import com.jael.minhasfinacas.service.impl.UsuarioServiceImpl;
import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Optional;

@RunWith( SpringRunner.class )
@ActiveProfiles( "test" )
public class UsuarioServiceTest {
	UsuarioService service;
	@MockBean
	UsuarioRepository repository;
	
	@Before
	public void setUp() {
		service = new UsuarioServiceImpl( repository );
	}
	
	@Test( expected = Test.None.class )
	public void deveAutenticarUmUsuarioComSucesso() {
		//CENÁRIO
		Usuario usuario = criarUsuario();
		Mockito.when( repository.findByEmail( usuario.getEmail() ) ).thenReturn( Optional.of( usuario ) );
		
		//AÇÃO
		Usuario result = service.autenticar( usuario.getEmail(), usuario.getSenha() );
		
		//VERIFICAÇÃO
		Assertions.assertThat( result ).isNotNull();
	}
	
	@Test
	public void deveNaoAutenticarUmUsuarioDevidoAEmailIncorreto() {
		//CENÁRIO
		Usuario usuario = criarUsuario();
		Mockito.when( repository.findByEmail( usuario.getEmail() ) ).thenReturn( Optional.of( usuario ) );
		
		//AÇÃO
		Throwable exception = Assertions.catchThrowable( () -> service.autenticar( "email@email.com", usuario.getSenha() ) );
		Assertions.assertThat( exception ).isInstanceOf( ErroAutenticacao.class ).hasMessage( "Usuário não encontrado para o email informado." );
	}
	
	@Test
	public void deveNaoAutenticarUmUsuarioDevidoASenhaInvalida() {
		//CENÁRIO
		Usuario usuario = criarUsuario();
		Mockito.when( repository.findByEmail( usuario.getEmail() ) ).thenReturn( Optional.of( usuario ) );
		
		//AÇÃO
		Throwable exception = Assertions.catchThrowable( () -> service.autenticar( usuario.getEmail(), "password" ) );
		Assertions.assertThat( exception ).isInstanceOf( ErroAutenticacao.class ).hasMessage( "Senha inválida." );
	}
	
	@Test( expected = Test.None.class )
	public void deveValidarEmail() {
		//CENÁRIO
		Mockito.when( repository.existsByEmail( Mockito.anyString() ) ).thenReturn( false );
		
		//AÇÃO
		service.validarEmail( "usuario@email.com" );
	}
	
	@Test( expected = RegraNegocioException.class )
	public void deveLancarErroAoValidarEmailQuandoExistirEmailCadastrado() {
		//CENÁRIO
		Mockito.when( repository.existsByEmail( Mockito.anyString() ) ).thenReturn( true );
		
		//AÇÃO
		service.validarEmail( "usuario@email.com" );
	}
	
	public static Usuario criarUsuario() {
		return Usuario
				.builder()
				.nome( "usuário" )
				.email( "usuario@email.com" )
				.senha( "senha" )
				.build();
	}
}

