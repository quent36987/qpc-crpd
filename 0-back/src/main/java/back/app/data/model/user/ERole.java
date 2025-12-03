package back.app.data.model.user;

import java.util.EnumSet;
import java.util.Set;

public enum ERole {

    ROLE_USER(Set.of(
            EDroit.USER_LIRE,
            EDroit.SETTING_LIRE
    )),

    ROLE_MANAGER(Set.of(
            EDroit.USER_LIRE,
            EDroit.SETTING_LIRE,
            EDroit.LISTES_DEROUlANTES_MODIFIER,
            EDroit.DROITS_LIBERTES_MODIFIER
    )),

    ROLE_ADMIN(Set.of(

            EDroit.USER_LIRE,
            EDroit.USER_MANAGEMENT,
            EDroit.SETTING_LIRE,
            EDroit.SETTING_MODIFIER,
            EDroit.LISTES_DEROUlANTES_MODIFIER,
            EDroit.DROITS_LIBERTES_MODIFIER
    )),

    ROLE_SUPERVISEUR(EnumSet.allOf(EDroit.class));

    private final Set<EDroit> droits;

    ERole(Set<EDroit> droits) {
        this.droits = droits;
    }

    public Set<EDroit> getDroits() {
        return droits;
    }
}
