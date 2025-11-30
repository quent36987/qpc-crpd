package back.app.data.model;

import jakarta.persistence.*;
import lombok.*;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "roles", uniqueConstraints = {
        @UniqueConstraint(columnNames = "name")
})
public class RoleModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(length = 32, name = "name")
    private ERole name;
}