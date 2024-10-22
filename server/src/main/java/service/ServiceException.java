package service;


public class ServiceException extends RuntimeException {

  final private int statusCode;

  public ServiceException(int statusCode, String message) {
    super(message);
    this.statusCode = statusCode;

  }
}
