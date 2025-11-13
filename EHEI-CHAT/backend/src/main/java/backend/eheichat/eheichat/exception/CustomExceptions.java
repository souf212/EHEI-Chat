package backend.eheichat.eheichat.exception;


public class CustomExceptions {

    public static class UserNotFoundException extends RuntimeException {
        public UserNotFoundException(String message) {
            super(message);
        }
    }

    public static class ChannelNotFoundException extends RuntimeException {
        public ChannelNotFoundException(String message) {
            super(message);
        }
    }

    public static class InvalidCodeException extends RuntimeException {
        public InvalidCodeException(String message) {
            super(message);
        }
    }

    public static class PhoneNumberAlreadyExistsException extends RuntimeException {
        public PhoneNumberAlreadyExistsException(String message) {
            super(message);
        }
    }

    public static class UnauthorizedException extends RuntimeException {
        public UnauthorizedException(String message) {
            super(message);
        }
    }
}
