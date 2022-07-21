package com.jael.minhasfinacas.model.repository;

import com.jael.minhasfinacas.model.entity.Usuario;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith( SpringRunner.class )
@ActiveProfiles( "test" )
@DataJpaTest
@AutoConfigureTestDatabase( replace = AutoConfigureTestDatabase.Replace.NONE )
public class UsuarioRepositoryTest {
	
	@Autowired
	UsuarioRepository repository;
	
	@Autowired
	TestEntityManager entityManager;
	
	@Test
	public void deveVerificarAExistenciaDeUmEmail() {
		//CENÁRIO
		Usuario usuario = criarUsuario();
		entityManager.persist( usuario );
		
		//AÇÃO
		boolean result = repository.existsByEmail( "usuario@email.com" );
		
		//VERIFICAÇÃO
		assertThat( result ).isTrue();
	}
	
	@Test
	public void deveRetorarFalsoCasoNaoHouverUsuarioCadastrado() {
		//AÇÃO
		boolean result = repository.existsByEmail( "usuario2@email.com" );
		
		//VERIFICAÇÃO
		assertThat( result ).isFalse();
	}
	
	@Test
	public void devePersistirUmUsuarioNoBancoDeDados() {
		//CENÁRIO
		Usuario usuario = criarUsuario();
		
		//AÇÃO
		Usuario usuarioSalvo = repository.save( usuario );
		
		//VERIFICAÇÃO
		assertThat( usuarioSalvo.getId() ).isNotNull();
	}
	
	@Test
	public void deveBuscarUsuarioPorEmail() {
		//CENÁRIO
		Usuario usuario = criarUsuario();
		entityManager.persist( usuario );
		
		//AÇÃO
		Optional< Usuario > result = repository.findByEmail( "usuario@email.com" );
		
		//VERIFICAÇÃO
		assertThat( result ).isPresent();
	}
	
	@Test
	public void deveRetornarVazioAoBuscarUsuarioPorEmailQuandoNaoExisteNoBanco() {
		//AÇÃO
		Optional< Usuario > result = repository.findByEmail( "usuario@email.com" );
		
		//VERIFICAÇÃO
		assertThat( result ).isEmpty();
	}
	
	public static Usuario criarUsuario() {
		
		return Usuario.builder().nome( "usuário" ).email( "usuario@email.com" ).senha( "senha" ).build();
	}
	
}
