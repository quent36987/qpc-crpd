package back.app.data.repository.interfaces;


import back.app.data.model.user.UserModel;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends CrudRepository<UserModel, Long> {

    List<UserModel> findAll();
    Optional<UserModel> findByNom(String nom);

    Optional<UserModel> findByEmail(String email);

    Boolean existsByEmail(String email);

    @Query("SELECT u FROM UserModel u WHERE u.hash = :hash")
    Optional<UserModel> findByHash(String hash);

}
