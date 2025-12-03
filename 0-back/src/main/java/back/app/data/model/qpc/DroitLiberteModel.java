package back.app.data.model.qpc;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "droits_libertes")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DroitLiberteModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Lob
    @Column(nullable = false)
    private String texte;
}