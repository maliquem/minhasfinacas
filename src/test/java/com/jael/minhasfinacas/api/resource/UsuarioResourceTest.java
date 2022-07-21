package com.jael.minhasfinacas.api.resource;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jael.minhasfinacas.api.dto.UsuarioDTO;
import com.jael.minhasfinacas.exception.ErroAutenticacao;
import com.jael.minhasfinacas.exception.RegraNegocioException;
import com.jael.minhasfinacas.model.entity.Usuario;
import com.jael.minhasfinacas.service.LancamentoService;
import com.jael.minhasfinacas.service.UsuarioService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@RunWith( SpringRunner.class )
@ActiveProfiles( "test" )
@WebMvcTest( controllers = UsuarioResource.class )
@AutoConfigureMockMvc
public class UsuarioResourceTest {
	
	static final String API = "/api/usuarios";
	
	static final MediaType JSON = MediaType.APPLICATION_JSON;
	
	@Autowired
	MockMvc mvc;
	
	@MockBean
	UsuarioService service;
	
	@MockBean
	LancamentoService lancamentoService;
	
	@Test
	public void deveAutenticarUmUsuarioComSucesso() throws Exception {
		//CENÁRIO
		UsuarioDTO dto = criarUsuarioDTO();
		Usuario usuario = criarUsuario();
		Mockito.when( service.autenticar( usuario.getEmail(), usuario.getSenha() ) ).thenReturn( usuario );
		String json = new ObjectMapper().writeValueAsString( dto );
		
		//EXECUÇÃO E VERIFICAÇÃO
		MockHttpServletRequestBuilder request = MockMvcRequestBuilders.post( API.concat( "/autenticar" ) )
		                                                              .accept( JSON )
		                                                              .contentType( JSON )
		                                                              .content( json );
		mvc.perform( request )
		   .andExpect( MockMvcResultMatchers.status().isOk() )
		   .andExpect( MockMvcResultMatchers.jsonPath( "id" ).value( usuario.getId() ) )
		   .andExpect( MockMvcResultMatchers.jsonPath( "nome" ).value( usuario.getNome() ) )
		   .andExpect( MockMvcResultMatchers.jsonPath( "email" ).value( usuario.getEmail() ) );
	}
	
	@Test
	public void deveRetornarBadRequestAoObterErroDeAutenticacao() throws Exception {
		//CENÁRIO
		UsuarioDTO dto = criarUsuarioDTO();
		Mockito.when( service.autenticar( dto.getEmail(), dto.getSenha() ) ).thenThrow( ErroAutenticacao.class );
		String json = new ObjectMapper().writeValueAsString( dto );
		
		//EXECUÇÃO E VERIFICAÇÃO
		MockHttpServletRequestBuilder request = MockMvcRequestBuilders.post( API.concat( "/autenticar" ) )
		                                                              .accept( JSON )
		                                                              .contentType( JSON )
		                                                              .content( json );
		
		mvc.perform( request ).andExpect( MockMvcResultMatchers.status().isBadRequest() );
	}
	
	@Test
	public void deveSalvarUmUsuarioComSucesso() throws Exception {
		//CENÁRIO
		UsuarioDTO dto = criarUsuarioDTO();
		Usuario usuario = criarUsuario();
		Mockito.when( service.salvarUsuario( Mockito.any( Usuario.class ) ) ).thenReturn( usuario );
		String json = new ObjectMapper().writeValueAsString( dto );
		
		//EXECUÇÃO E VERIFICAÇÃO
		MockHttpServletRequestBuilder request = MockMvcRequestBuilders.post( API )
		                                                              .accept( JSON )
		                                                              .contentType( JSON )
		                                                              .content( json );
		mvc.perform( request )
		   .andExpect( MockMvcResultMatchers.status().isCreated() )
		   .andExpect( MockMvcResultMatchers.jsonPath( "id" ).value( usuario.getId() ) )
		   .andExpect( MockMvcResultMatchers.jsonPath( "nome" ).value( usuario.getNome() ) )
		   .andExpect( MockMvcResultMatchers.jsonPath( "email" ).value( usuario.getEmail() ) );
	}
	
	@Test
	public void deveRetornarBadRequestAoTentarSalvarUmUsuarioComInvalido() throws Exception {
		//CENÁRIO
		UsuarioDTO dto = criarUsuarioDTO();
		Usuario usuario = criarUsuario();
		Mockito.when( service.salvarUsuario( Mockito.any( Usuario.class ) ) ).thenThrow( RegraNegocioException.class );
		String json = new ObjectMapper().writeValueAsString( dto );
		
		//EXECUÇÃO E VERIFICAÇÃO
		MockHttpServletRequestBuilder request = MockMvcRequestBuilders.post( API )
		                                                              .accept( JSON )
		                                                              .contentType( JSON )
		                                                              .content( json );
		mvc.perform( request ).andExpect( MockMvcResultMatchers.status().isBadRequest() );
	}
	public static UsuarioDTO criarUsuarioDTO() {
		
		return UsuarioDTO.builder().email( "usuario@email.com" ).senha( "123" ).build();
	}
	private Usuario criarUsuario() {
		
		return Usuario.builder().id( 1L ).email( "usuario@email.com" ).senha( "123" ).build();
	}
	
}
