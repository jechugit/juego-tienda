package cl.duoc.pagos.dto;

import java.time.LocalDate;

public record ResenaCarritoResponse(
        Long id,
        String comentario,
        Integer puntuacion,
        LocalDate fechaResena
) {
}
