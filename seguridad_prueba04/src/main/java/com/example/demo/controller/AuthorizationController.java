package com.example.demo.controller;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.repository.modelo.Cliente;
import com.example.demo.service.IClienteService;
import com.example.demo.to.CuentaTo;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

@RestController
@RequestMapping("/api/seguridad")
public class AuthorizationController {
	
	@Autowired
	private AuthenticationManager authenticationManager;
	
	@Autowired
	private IClienteService clienteService;
	
	@PostMapping("/tokens")
	public String returnToken(@RequestBody CuentaTo usuario) {
		authenticate(usuario.getUsername(), usuario.getPassword());
		return crearToken(usuario.getUsername());
	}
	
	@PostMapping("/usuarios")
	public String insertarUsuario(@RequestBody Cliente usuario) {
		usuario.setPassword(encriptarPassword(usuario.getPassword()));
		clienteService.insertarCliente(usuario);
		return crearToken(usuario.getUsername());
	}
	
	private String encriptarPassword(String password) {
		return BCrypt.hashpw(password, BCrypt.gensalt(12));
	}
	
	public String crearToken(String nombre) {
		String token = Jwts.builder().setIssuedAt(new Date()).setIssuer("http://localhost:8081")
				.setSubject(nombre)
				.setExpiration(new Date(System.currentTimeMillis()+1000000L))
				.signWith(SignatureAlgorithm.HS512, "semilla01")
				.compact();
		return "Bearer "+token;
	}
	
	private void authenticate(String usuario, String password) {
		authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(usuario, password));
	}

}
