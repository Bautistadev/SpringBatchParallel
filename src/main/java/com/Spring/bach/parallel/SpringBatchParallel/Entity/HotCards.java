package com.Spring.bach.parallel.SpringBatchParallel.Entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class HotCards {

    private String pan;
    private String originUI;
    private String modoEntrada;
    private String appOrigen;
    private String fechaCreacion;
    private String validez;
    private String marcaTiempo;
    private String accion;
    private String comentarion;
    private String usuario;

}
