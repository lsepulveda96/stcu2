package com.stcu.controllers;

import java.util.List;

import com.stcu.model.Usuario;
import com.stcu.services.UsuarioServiceImp;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UsuarioController {
    
    @Autowired
    UsuarioServiceImp service;

    @GetMapping("/usuarios")
    public String getUsuarios() {
        List<Usuario> usrs = service.getAllUsuarios();
        
        Response<List<Usuario>> response = new Response<List<Usuario>>( false, 200, "Lista de usuarios", usrs );

        return Mapper.getResponseAsJson(response);
    }

    @GetMapping("/usuario/{id}")
    public String getUsuario( @PathVariable long id ) {
        Usuario usr = service.getUsuario(id);

        Response<Usuario> response;

        if (usr != null)
            response = new Response<Usuario>( false, 200, "Usuario " + id, usr );
        else 
            response = new Response<Usuario>( true, 400, "No se pudo encontrar usuario " + id, null );
    
        return Mapper.getResponseAsJson(response);    
    }

    @PostMapping("/usuarios")
    public String saveUsuario( @RequestBody Usuario usr ) {
        Usuario usuario = service.saveUsuario( usr );

        Response<Usuario> response;

        if (usuario != null)
            response = new Response<Usuario>( false, 200, "Nuevo usuario registrado", usuario );
        else 
            response = new Response<Usuario>( true, 400, "No se pudo registrar nuevo usuario", null );

        return Mapper.getResponseAsJson(response);
    }

    @PutMapping("/usuario/{id}")
    public String updateUsuario( @PathVariable long id, @RequestBody Usuario usr ) {
        Usuario usuario = service.updateUsuario(id, usr );

        Response<Usuario> response;

        if (usuario != null)
            response = new Response<Usuario>( false, 200, "Usuario " + id + " actualizado", usuario );
        else 
            response = new Response<Usuario>( true, 400, "No se pudo actualizar usuario " + id, null );
        
        return Mapper.getResponseAsJson(response);
    }
}
