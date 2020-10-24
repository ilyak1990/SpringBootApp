package com.trunkfit.controller;

import java.util.concurrent.atomic.AtomicLong;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.trunkfit.model.AuthRequest;
import com.trunkfit.model.Greeting;
import com.trunkfit.utils.JwtUtil;

@RestController
public class GreetingController {

    private static final String template = "Hello, %s!";
    private final AtomicLong counter = new AtomicLong();
    
    @Autowired
    private JwtUtil jwtUtil;
    
    @Autowired
    private AuthenticationManager authenticationManager;

    @GetMapping("/api/greeting")
    public Greeting greeting(@RequestParam(value = "name", defaultValue = "World") String name) {
        return new Greeting(counter.incrementAndGet(), String.format(template, name));
    }
    
    @PostMapping("/api/authenticate")
    public String generateToken(@RequestBody AuthRequest authRequest) throws Exception {
      try {
      authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(authRequest.getUserName(),authRequest.getPassword()));
      }
      catch(Exception ex){
        throw new Exception("invalid username or password");
      }
      System.out.println(authRequest.getUserName() + " fafsadfsdfdsf");
      return jwtUtil.generateToken(authRequest.getUserName());
    }
}
