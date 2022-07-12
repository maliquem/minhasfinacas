package com.jael.minhasfinacas.model.entity;

import com.jael.minhasfinacas.model.enums.StatusLancamento;
import com.jael.minhasfinacas.model.enums.TipoLancamento;
import lombok.*;
import org.springframework.data.jpa.convert.threeten.Jsr310JpaConverters;
import org.springframework.data.util.ProxyUtils;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Objects;

@Entity
@Table( name = "lancamento", schema = "financas" )
@Getter
@Setter
@ToString
@Builder
@RequiredArgsConstructor
@AllArgsConstructor
public class Lancamento {
	
	@Id
	@Column( name = "id" )
	@GeneratedValue( strategy = GenerationType.IDENTITY )
	private Long id;
	
	@Column( name = "mes" )
	private Integer mes;
	
	@Column( name = "ano" )
	private Integer ano;
	
	@ManyToOne
	@JoinColumn( name = "id_usuario" )
	private Usuario usuario;
	
	@Column( name = "valor" )
	private BigDecimal valor;
	
	@Column( name = "data_cadastro" )
	@Convert( converter = Jsr310JpaConverters.LocalDateConverter.class )
	private LocalDate dataCadastro;
	
	@Column( name = "tipo" )
	@Enumerated( value = EnumType.STRING )
	private TipoLancamento tipo;
	
	@Column( name = "status" )
	@Enumerated( value = EnumType.STRING )
	private StatusLancamento status;
	
	@Override
	public boolean equals( Object o ) {
		if ( this == o ) return true;
		if ( o == null || ProxyUtils.getUserClass( this ) != ProxyUtils.getUserClass( o ) )
			return false;
		Lancamento that = ( Lancamento ) o;
		return id != null && Objects.equals( id, that.id );
	}
	
	@Override
	public int hashCode() {
		return getClass().hashCode();
	}
	
}
