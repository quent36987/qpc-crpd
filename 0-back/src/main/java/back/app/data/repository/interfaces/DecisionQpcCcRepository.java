package back.app.data.repository.interfaces;

import back.app.data.model.qpc.DecisionQpcCcModel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DecisionQpcCcRepository extends CrudRepository<DecisionQpcCcModel, Long> {

    Page<DecisionQpcCcModel> findAll(Specification<DecisionQpcCcModel> specification, Pageable pageable);

    Long count(Specification<DecisionQpcCcModel> specification);
}