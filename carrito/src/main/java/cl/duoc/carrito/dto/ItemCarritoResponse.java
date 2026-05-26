package cl.duoc.carrito.dto;

import java.time.LocalDateTime;

public record ItemCarritoResponse(
        Long id,
        Long usuarioId,
        String nombreUsuario,
        Long videojuegoId,
        String nombreVideojuego,
        ResenaCarritoResponse resena,
        Integer cantidad,
        Integer precioUnitario,
        Integer subtotal,
        LocalDateTime fechaAgregado
) {
}
