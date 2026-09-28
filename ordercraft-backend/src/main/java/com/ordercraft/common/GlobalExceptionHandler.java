@RestControllerAdvice
public class GlobalExceptionHandler {

    private ResponseEntity<Map<String, Object>> body(HttpStatus s, String err, String msg, Object details) {
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("error", err); m.put("message", msg);
        if (details != null) m.put("details", details);
        m.put("timestamp", Instant.now().toString());
        return ResponseEntity.status(s).body(m);
    }

    @ExceptionHandler({BadCredentialsException.class, DisabledException.class})
    ResponseEntity<?> auth(AuthenticationException e) {
        return body(HttpStatus.UNAUTHORIZED, "UNAUTHORIZED", "Invalid credentials or inactive account", null);
    }

    @ExceptionHandler(JwtException.class)
    ResponseEntity<?> jwt(JwtException e) {
        return body(HttpStatus.UNAUTHORIZED, "INVALID_TOKEN", "Token is invalid or expired", null);
    }

    @ExceptionHandler(AccessDeniedException.class)
    ResponseEntity<?> denied(AccessDeniedException e) {
        return body(HttpStatus.FORBIDDEN, "FORBIDDEN", "Insufficient permissions", null);
    }

    @ExceptionHandler(EntityNotFoundException.class)
    ResponseEntity<?> notFound(EntityNotFoundException e) {
        return body(HttpStatus.NOT_FOUND, "NOT_FOUND", e.getMessage(), null);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    ResponseEntity<?> validation(MethodArgumentNotValidException e) {
        var details = e.getBindingResult().getFieldErrors().stream()
            .map(f -> Map.of("field", f.getField(), "message", f.getDefaultMessage())).toList();
        return body(HttpStatus.BAD_REQUEST, "VALIDATION_ERROR", "Validation failed", details);
    }
}