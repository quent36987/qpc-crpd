package back.app.utils.errors;

public enum RestError {
    // NON TROUVÉ
    USER_NOT_FOUND(404, "Utilisateur non trouvé"),

    // NON AUTORISÉ
    FORBIDDEN(403, "Interdit"),
    FORBIDDEN_MESSAGE(403, "%s"),

    // CHAMP MANQUANT ET MAUVAISE REQUÊTE
    BAD_REQUEST(400, "Requête invalide"),
    INVALID_HASH(400, "Hash invalide"),
    EMAIL_ALREADY_EXISTS(400, "Email %s existe déjà"),

    // ERREUR INTERNE DU SERVEUR
    //    INTERNAL_SERVER_ERROR(500, "Erreur interne du serveur"),
    ;
    private final int code;
    private final String message;

    RestError(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public ErrorCode get(Object... args) {
        return new ErrorCode(code, message, args);
    }

    public ErrorCode log(Exception e, Object... args) {
        return new ErrorCode(e, code, message, args);
    }
}
