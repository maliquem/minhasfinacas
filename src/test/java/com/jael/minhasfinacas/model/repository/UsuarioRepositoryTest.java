package com.jael.minhasfinacas.model.repository;

import com.jael.minhasfinacas.model.entity.Usuario;
import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@SpringBootTest
@RunWith( SpringRunner.class )
public class UsuarioRepositoryTest {
	
	UsuarioRepository repository;
	
	@Test
	public void deveVerificarAExistenciaDeUmEmail() {
		//cenário
		Usuario usuario = Usuario.builder().nome( "usuário" ).email( "usuario@email.com" ).build();
		repository.save( usuario );
		
		//ação / execução
		boolean result = repository.existsByEmail( "usuario@email.com" );
		
		//verificação
		Assertions.assertThat( result ).isTrue();
	}
}
