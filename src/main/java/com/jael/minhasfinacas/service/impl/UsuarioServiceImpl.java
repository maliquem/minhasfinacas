package com.jael.minhasfinacas.service.impl;

import com.jael.minhasfinacas.exception.ErroAutenticacao;
import com.jael.minhasfinacas.exception.RegraNegocioException;
import com.jael.minhasfinacas.model.entity.Usuario;
import com.jael.minhasfinacas.model.repository.UsuarioRepository;
import com.jael.minhasfinacas.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;
import java.util.Optional;

@Service
public class UsuarioServiceImpl implements UsuarioService {
	
	@Autowired
	private final UsuarioRepository repository;
	
	public UsuarioServiceImpl( UsuarioRepository repository ) {
		this.repository = repository;
	}
	
	@Override
	public Usuario autenticar( String email, String senha ) {
		Optional< Usuario > usuario = repository.findByEmail( email );
		
		if ( usuario.isEmpty() ) {
			throw new ErroAutenticacao( "Usuário não encontrado para o email informado." );
		}
		
		if ( !Objects.equals( usuario.get().getSenha(), senha ) ) {
			throw new ErroAutenticacao( "Senha inválida." );
		}
		
		return usuario.get();
	}
	
	@Override
	@Transactional
	public Usuario salvarUsuario( Usuario usuario ) {
		validarEmail( usuario.getEmail() );
		return repository.save( usuario );
	}
	
	@Override
	public Optional< Usuario > obterPorId( Long id ) {
		Optional< Usuario > usuario = repository.findById( id );
		
		if ( usuario.isEmpty() ) {
			throw new RegraNegocioException( "Usuário não encontrado para o ID informado." );
		}
		
		return usuario;
	}
	
	@Override
	public void validarEmail( String email ) {
		if ( repository.existsByEmail( email ) ) {
			throw new RegraNegocioException( "Já existe um usuário cadastrado com este email." );
		}
	}
	
}
