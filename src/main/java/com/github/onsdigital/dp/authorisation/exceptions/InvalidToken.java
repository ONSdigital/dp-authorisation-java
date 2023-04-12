package com.github.onsdigital.dp.authorisation.exceptions;

/**
 * InvalidToken.
 */
public class InvalidToken extends Exception {

  public InvalidToken(String message) {
    super(message);
  }

  public InvalidToken(String message, Throwable cause) {
    super(message, cause);
  }
}
