package utils;

public class BusinessException extends RuntimeException {
    private final String code;

    public BusinessException(String message) {
        super(message);
        this.code = null;
    }

    public BusinessException(String code, String message) {
        super(message);
        this.code = code;
    }

    public BusinessException(String code, String message, Throwable cause) {
        super(message, cause);
        this.code = code;
    }

    public String getCode() {
        return code;
    }

    public static BusinessException invalid(String field, String reason) {
        return new BusinessException("INVALID_" + field.toUpperCase(), "Champ " + field + " invalide: " + reason);
    }

    public static BusinessException notFound(String entity, Object id) {
        return new BusinessException("NOT_FOUND", entity + " introuvable (id=" + id + ")");
    }

    public static BusinessException rule(String message) {
        return new BusinessException("BUSINESS_RULE", message);
    }

}
