package com.jael.minhasfinacas.service;

import com.jael.minhasfinacas.model.entity.Usuario;

public interface UsuarioService {
	
	Usuario autenticar( String email, String senha );
	
	Usuario salvarUsuario( Usuario usuario );
	
	void validarEmail( String email );
	
}
