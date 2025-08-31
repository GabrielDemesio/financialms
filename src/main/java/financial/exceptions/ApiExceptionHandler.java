package financial.exceptions;

public class ApiExceptionHandler extends RuntimeException {
  public ApiExceptionHandler(String message) {
    super(message);
  }
}
