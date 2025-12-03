package back.app.data.repository.interfaces;


import back.app.data.model.qpc.ListeDeroulanteModel;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ListeDeroulanteRepository extends CrudRepository<ListeDeroulanteModel, Long> {

    List<ListeDeroulanteModel> findAll();

    Long count(Specification<ListeDeroulanteModel> specification);
}