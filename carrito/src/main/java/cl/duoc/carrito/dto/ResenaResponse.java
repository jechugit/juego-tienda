package cl.duoc.carrito.dto;

import java.time.LocalDate;

public record ResenaResponse(
        Long id,
        Long usuarioId,
        String nombreJuego,
        String comentario,
        Integer puntuacion,
        LocalDate fechaResena,
        String nombreUsuario,
        String correoUsuario
) {
}
