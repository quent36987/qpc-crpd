package back.app.data.model.qpc;

public enum ENiveauFiltrage {
    PREMIER_ET_DERNIER,
    DEUXIEME_FILTRAGE;

    public static ENiveauFiltrage fromExcel(String raw) {
        if (raw == null) return null;
        String v = raw.trim().toLowerCase();

        switch (v) {
            case "1er et dernier":
            case "1er & dernier":
            case "premier et dernier":
                return PREMIER_ET_DERNIER;

            case "2e filtrage":
            case "2ème filtrage":
            case "deuxième filtrage":
                return DEUXIEME_FILTRAGE;

            default:
               return null;
        }
    }
}