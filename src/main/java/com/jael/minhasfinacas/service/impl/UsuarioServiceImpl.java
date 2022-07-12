package com.jael.minhasfinacas.service.impl;

import com.jael.minhasfinacas.exception.RegraNegocioException;
import com.jael.minhasfinacas.model.entity.Usuario;
import com.jael.minhasfinacas.model.repository.UsuarioRepository;
import com.jael.minhasfinacas.service.UsuarioService;
import org.springframework.stereotype.Service;

@Service
public class UsuarioServiceImpl implements UsuarioService {
	
	private UsuarioRepository repository;
	
	public UsuarioServiceImpl( UsuarioRepository repository ) {
		this.repository = repository;
	}
	
	@Override
	public Usuario autenticar( String email, String senha ) {
		return null;
	}
	
	@Override
	public Usuario salvarUsuario( Usuario usuario ) {
		return null;
	}
	
	@Override
	public void validarEmail( String email ) {
		if ( repository.existsByEmail( email ) ) {
			throw new RegraNegocioException( "Já existe um usuário cadastrado com este email." );
		}
	}
	
}
