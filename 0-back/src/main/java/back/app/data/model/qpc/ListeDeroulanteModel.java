package back.app.data.model.qpc;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "listes_deroulantes",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_listes_deroulantes_champ_valeur",
                        columnNames = {"champ", "valeur"})
        })
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ListeDeroulanteModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Nom logique du champ, ex : "decision_qpc_cc.origine_qpc"
     */
    @Column(nullable = false, length = 128)
    private String champ;

    /**
     * Valeur affichée dans la liste.
     */
    @Column(nullable = false, length = 255)
    private String valeur;

    @Column(nullable = false)
    private boolean actif = true;
}
