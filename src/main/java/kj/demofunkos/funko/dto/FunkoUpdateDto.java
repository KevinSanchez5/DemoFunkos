package kj.demofunkos.funko.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FunkoUpdateDto {

    private  String nombre;

    private  Double precio;

}
