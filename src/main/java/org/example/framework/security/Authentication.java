package org.example.framework.security;

import java.util.Collection;

public interface Authentication {
  Object getPrincipal();
  Object getCredentials();
  Collection<String> getAuthorities();
  boolean isAuthenticated();
  void setAuthenticated(boolean authenticated);
  void eraseCredentials();
}
