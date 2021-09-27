package org.example.framework.security;

public interface AuthenticationProvider {
  Authentication basicAuthenticate(Authentication authentication) throws AuthenticationException;
  Authentication tokenAuthenticate(Authentication authentication) throws AuthenticationException;
}
