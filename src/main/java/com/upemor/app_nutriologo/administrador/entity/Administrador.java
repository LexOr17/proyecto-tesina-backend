package com.upemor.app_nutriologo.administrador.entity;

import com.upemor.app_nutriologo.usuario.entity.Usuario;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@Entity
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "administradores")
@EqualsAndHashCode(callSuper = true)
public class Administrador extends Usuario {

    private String tipoAdmin;

}
