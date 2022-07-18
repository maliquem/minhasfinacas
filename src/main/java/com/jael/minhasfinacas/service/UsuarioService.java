package com.jael.minhasfinacas.service;

import com.jael.minhasfinacas.model.entity.Usuario;

import java.util.Optional;

public interface UsuarioService {
	
	Usuario autenticar( String email, String senha );
	
	Usuario salvarUsuario( Usuario usuario );
	
	Optional< Usuario > obterPorId( Long id );
	
	void validarEmail( String email );
	
}
