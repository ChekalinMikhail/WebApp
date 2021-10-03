package org.example.framework.security;

public interface AuthenticationProvider {
  Authentication authenticate(BasicAuthentication authentication) throws AuthenticationException;
  Authentication authenticate(TokenAuthentication authentication) throws AuthenticationException;
}
