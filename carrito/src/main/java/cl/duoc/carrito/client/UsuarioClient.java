package cl.duoc.carrito.client;

import cl.duoc.carrito.dto.UsuarioResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "usuarios")
public interface UsuarioClient {

    @GetMapping("/usuarios/{id}")
    UsuarioResponse buscarPorId(@PathVariable("id") Long id);
}
