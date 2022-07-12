package com.jael.minhasfinacas.model.repository;

import com.jael.minhasfinacas.model.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UsuarioRepository extends JpaRepository< Usuario, Long > {
	
	boolean existsByEmail( String email );
	
}