package cl.duoc.carrito.client;

import cl.duoc.carrito.dto.ResenaResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@FeignClient(name = "resenas")
public interface ResenaClient {

    @GetMapping("/resenas/usuario/{usuarioId}")
    List<ResenaResponse> listarPorUsuario(@PathVariable("usuarioId") Long usuarioId);
}
