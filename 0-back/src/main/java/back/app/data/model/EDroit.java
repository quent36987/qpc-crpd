package back.app.data.model;


/**
 * Enum des droits applicatifs.
 *
 * Idée générale :
 * - ..._VOIR      : lecture / listing
 * - ..._MODIFIER  : créer / modifier / supprimer
 * - droits plus spécifiques pour upload / download / import / export, etc.
 */
public enum EDroit {

    USER_LIRE,
    USER_MANAGEMENT,
    USER_MODIFIER_ROLES,

    SETTING_LIRE,
    SETTING_MODIFIER,


}

