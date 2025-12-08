package back.app.data.repository.interfaces;

import back.app.data.model.qpc.DecisionQpcCcModel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DecisionQpcCcRepository extends CrudRepository<DecisionQpcCcModel, Long>, JpaSpecificationExecutor<DecisionQpcCcModel> {

    Page<DecisionQpcCcModel> findAll(Specification<DecisionQpcCcModel> specification, Pageable pageable);
    List<DecisionQpcCcModel> findAll(Specification<DecisionQpcCcModel> specification);

}