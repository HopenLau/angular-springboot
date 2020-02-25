package com.cqjtu.angularspringboot.Controller;

import com.cqjtu.angularspringboot.Model.AuthenticationRequest;
import com.cqjtu.angularspringboot.Model.AuthenticationResponse;
import com.cqjtu.angularspringboot.util.JwtTokenUtil;
import lombok.NonNull;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "${api.base-path}", produces = MediaType.APPLICATION_JSON_VALUE)
@Log4j2
public class AuthenticationController {

  private final AuthenticationManager authenticationManager;
  private final JwtTokenUtil jwtTokenUtil;
  private final UserDetailsService userDetailsService;

  @Autowired
  public AuthenticationController(
      AuthenticationManager authenticationManager,
      JwtTokenUtil jwtTokenUtil,
      UserDetailsService userDetailsService) {
    this.authenticationManager = authenticationManager;
    this.jwtTokenUtil = jwtTokenUtil;
    this.userDetailsService = userDetailsService;
  }

  @PostMapping("/auth")
  public AuthenticationResponse login(@NonNull @RequestBody AuthenticationRequest request) {
    // Perform the security
    log.info(request);
    /*    Authentication authentication =
        authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));
    SecurityContextHolder.getContext().setAuthentication(authentication);*/

    // Reload user details so we can generate t oken
    UserDetails userDetails = userDetailsService.loadUserByUsername(request.getUsername());
    String token = jwtTokenUtil.generate(userDetails);

    // Return the token
    return new AuthenticationResponse(token);
  }

  @ExceptionHandler(AuthenticationException.class)
  @ResponseStatus(HttpStatus.FORBIDDEN)
  public void handleAuthenticationException(AuthenticationException exception) {
    log.error(exception.getMessage(), exception);
  }
}