package back.app.data.model.user;


/**
 * Enum des droits applicatifs.
 *
 * Idée générale :
 * - ..._VOIR      : lecture / listing
 * - ..._MODIFIER  : créer / modifier / supprimer
 * - droits plus spécifiques pour upload / download / import / export, etc.
 */
public enum EDroit {

    // --- Users ---
    USER_LIRE,
    USER_MANAGEMENT,
    USER_MODIFIER_ROLES,

    // --- Paramètres ---
    SETTING_LIRE,
    SETTING_MODIFIER,

    // --- Listes déroulantes ---
    LISTES_DEROUlANTES_MODIFIER,

    // --- Droits & libertés ---
    DROITS_LIBERTES_MODIFIER,

    IMPORT_XLS,

}

