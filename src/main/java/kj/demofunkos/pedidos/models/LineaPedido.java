package kj.demofunkos.pedidos.models;

import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LineaPedido {

    @Min(value=1, message = "La cantidad debe ser mayor a 0")
    @Builder.Default
    private Integer cantidad = 1;


    private Long idFunko;

    @Min(value = 0 , message = "El precio no puede ser menor que 0")
    @Builder.Default
    private Double precioFunko = 0.1;

    @Builder.Default
    private Double precioTotal = 0.1;


    public void setCantidad(Integer cantidad) {
        this.cantidad = cantidad;
        this.precioTotal = this.precioFunko * this.cantidad;
    }

    public void setPrecioFunko(Double precioFunko) {
        this.precioFunko = precioFunko;
        this.precioTotal = this.precioFunko * this.cantidad;
    }
}
