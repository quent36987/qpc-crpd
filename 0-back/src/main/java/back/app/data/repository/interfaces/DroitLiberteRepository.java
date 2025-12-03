package back.app.data.repository.interfaces;

import back.app.data.model.qpc.DroitLiberteModel;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DroitLiberteRepository extends CrudRepository<DroitLiberteModel, Long> {

    List<DroitLiberteModel> findAll();

    Long count(Specification<DroitLiberteModel> specification);
}