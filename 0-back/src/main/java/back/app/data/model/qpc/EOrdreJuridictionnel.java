package back.app.data.model.qpc;

public enum EOrdreJuridictionnel {
    JUDICIAIRE,
    ADMINISTRATIF;

    public static EOrdreJuridictionnel fromExcel(String raw) {
        if (raw == null) return null;
        String v = raw.trim().toLowerCase();

        return switch (v) {
            case "administratif" -> ADMINISTRATIF;
            case "judiciaire" -> JUDICIAIRE;
            default -> null;
        };
    }
}