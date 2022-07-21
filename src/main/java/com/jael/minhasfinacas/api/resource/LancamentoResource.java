package com.jael.minhasfinacas.api.resource;

import com.jael.minhasfinacas.api.dto.AtualizarStatusDTO;
import com.jael.minhasfinacas.api.dto.LancamentoDTO;
import com.jael.minhasfinacas.exception.RegraNegocioException;
import com.jael.minhasfinacas.model.entity.Lancamento;
import com.jael.minhasfinacas.model.entity.Usuario;
import com.jael.minhasfinacas.model.enums.StatusLancamento;
import com.jael.minhasfinacas.model.enums.TipoLancamento;
import com.jael.minhasfinacas.service.LancamentoService;
import com.jael.minhasfinacas.service.UsuarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping( "/api/lancamentos" )
@RequiredArgsConstructor
public class LancamentoResource {
	
	private final LancamentoService lancamentoService;
	
	private final UsuarioService usuarioService;
	
	@GetMapping( "/{id}" )
	public ResponseEntity buscarPorId( @PathVariable Long id ) {
		
		try {
			Lancamento lancamento = lancamentoService.obterPorId( id ).get();
			return ResponseEntity.ok( lancamento );
		} catch ( RegraNegocioException e ) {
			return ResponseEntity.badRequest().body( e.getMessage() );
		}
	}
	
	@GetMapping
	public ResponseEntity buscar( @RequestParam( value = "descricao", required = false ) String descricao,
	                              @RequestParam( value = "mes", required = false ) Integer mes,
	                              @RequestParam( value = "ano", required = false ) Integer ano,
	                              @RequestParam( value = "tipo", required = false ) String tipo,
	                              @RequestParam( value = "status", required = false ) String status,
	                              @RequestParam( "usuario" ) Long idUsuario ) {
		
		Lancamento lancamentoFiltro = new Lancamento();
		
		lancamentoFiltro.setDescricao( descricao );
		lancamentoFiltro.setMes( mes );
		lancamentoFiltro.setAno( ano );
		
		if ( tipo != null ) {
			lancamentoFiltro.setTipo( TipoLancamento.valueOf( tipo ) );
		}
		
		if ( status != null ) {
			lancamentoFiltro.setStatus( StatusLancamento.valueOf( status ) );
		}
		
		Optional< Usuario > usuario = usuarioService.obterPorId( idUsuario );
		
		if ( usuario.isEmpty() ) {
			return ResponseEntity.badRequest().body( "Usuário não encontrado." );
		} else {
			lancamentoFiltro.setUsuario( usuario.get() );
		}
		
		List< Lancamento > lancamentos = lancamentoService.buscar( lancamentoFiltro );
		return ResponseEntity.ok( lancamentos );
	}
	
	@PostMapping
	public ResponseEntity salvar( @RequestBody LancamentoDTO dto ) {
		
		try {
			Lancamento lancamento = converter( dto );
			Lancamento lancamentoSalvo = lancamentoService.salvar( lancamento );
			return ResponseEntity.ok( lancamentoSalvo );
		} catch ( RegraNegocioException e ) {
			return ResponseEntity.badRequest().body( e.getMessage() );
		}
	}
	
	@PutMapping( "/{id}" )
	public ResponseEntity atualizar( @PathVariable Long id, @RequestBody LancamentoDTO dto ) {
		
		return lancamentoService.obterPorId( id ).map( entidade -> {
			try {
				Lancamento lancamento = converter( dto );
				lancamento.setId( entidade.getId() );
				lancamentoService.atualizar( lancamento );
				return ResponseEntity.ok( lancamento );
			} catch ( RegraNegocioException e ) {
				return ResponseEntity.badRequest().body( e.getMessage() );
			}
		} ).orElseGet( () -> new ResponseEntity( "Lançamento não encontrado.", HttpStatus.BAD_REQUEST ) );
	}
	
	@PutMapping( "/{id}/atualizar-status" )
	public ResponseEntity atualizarStatus( @PathVariable Long id, @RequestBody AtualizarStatusDTO dto ) {
		
		return lancamentoService.obterPorId( id ).map( entidade -> {
			if ( dto.getStatus() == null ) {
				return ResponseEntity.badRequest()
				                     .body( "Não foi possível atualizar o status do lançamento. Envie um status válido." );
			}
			StatusLancamento statusSelecionado = StatusLancamento.valueOf( dto.getStatus() );
			try {
				entidade.setStatus( statusSelecionado );
				lancamentoService.atualizar( entidade );
				return ResponseEntity.ok( entidade );
			} catch ( RegraNegocioException e ) {
				return ResponseEntity.badRequest().body( e.getMessage() );
			}
		} ).orElseGet( () -> new ResponseEntity( "Lançamento não encontrado.", HttpStatus.BAD_REQUEST ) );
	}
	
	@DeleteMapping( "/{id}" )
	public ResponseEntity deletar( @PathVariable Long id ) {
		
		try {
			lancamentoService.deletar( lancamentoService.obterPorId( id ).get() );
			return new ResponseEntity( HttpStatus.NO_CONTENT );
		} catch ( RegraNegocioException e ) {
			return ResponseEntity.badRequest().body( e.getMessage() );
		}
	}
	
	private Lancamento converter( LancamentoDTO dto ) {
		
		Lancamento lancamento = new Lancamento();
		
		lancamento.setId( dto.getId() );
		lancamento.setDescricao( dto.getDescricao() );
		lancamento.setAno( dto.getAno() );
		lancamento.setMes( dto.getMes() );
		lancamento.setValor( dto.getValor() );
		lancamento.setUsuario( usuarioService.obterPorId( dto.getUsuario() ).get() );
		if ( dto.getTipo() != null ) {
			lancamento.setTipo( TipoLancamento.valueOf( dto.getTipo() ) );
		}
		if ( dto.getStatus() != null ) {
			lancamento.setStatus( StatusLancamento.valueOf( dto.getStatus() ) );
		}
		
		return lancamento;
	}
	
}
