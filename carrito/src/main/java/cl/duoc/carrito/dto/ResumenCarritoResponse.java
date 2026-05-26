package cl.duoc.carrito.dto;

import java.util.List;

public record ResumenCarritoResponse(
        Long usuarioId,
        String nombreUsuario,
        List<ItemCarritoResponse> items,
        Integer total
) {
}
