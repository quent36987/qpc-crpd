package back.app.data.model.qpc;

public enum EJuridiction {
    CONSEIL_ETAT,
    COUR_DE_CASSATION;

    public static EJuridiction fromExcel(String raw) {
        if (raw == null) return null;
        String v = raw.trim().toLowerCase();

        switch (v) {
            case "cour cass":
            case "cour de cassation":
            case "ccass":
                return COUR_DE_CASSATION;

            case "ce":
            case "conseil d'etat":
            case "conseil d’état":
            case "conseil d etat":
                return CONSEIL_ETAT;

            default:
                return null;
        }
    }
}