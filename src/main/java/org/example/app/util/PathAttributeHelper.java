package org.example.app.util;

import jakarta.servlet.http.HttpServletRequest;
import org.example.app.domain.User;
import org.example.framework.attribute.RequestAttributes;
import org.example.framework.security.Authentication;

import java.util.regex.Matcher;

public class PathAttributeHelper {
  private PathAttributeHelper() {
  }

  public static long getLong(HttpServletRequest req, String attribute) {
    return Long.parseLong(((Matcher) req.getAttribute(RequestAttributes.PATH_MATCHER_ATTR)).group(attribute));
  }

  public static String getString(HttpServletRequest req, String attribute) {
    return ((Matcher) req.getAttribute(RequestAttributes.PATH_MATCHER_ATTR)).group(attribute);
  }


}
