package br.com.devl.mfc.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/test")
public class TestController {

	@GetMapping
	public String test() {
		
		return "Acesso autorizado com JWT" + "\nStatus Code: " + HttpStatus.OK;
	}
}
