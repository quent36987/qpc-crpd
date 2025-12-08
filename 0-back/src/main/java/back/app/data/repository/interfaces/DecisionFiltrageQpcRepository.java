package back.app.data.repository.interfaces;

import back.app.data.model.qpc.DecisionFiltrageQpcModel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DecisionFiltrageQpcRepository extends CrudRepository<DecisionFiltrageQpcModel, Long> , JpaSpecificationExecutor<DecisionFiltrageQpcModel> {

    Page<DecisionFiltrageQpcModel> findAll(Specification<DecisionFiltrageQpcModel> specification, Pageable pageable);

    List<DecisionFiltrageQpcModel> findAll(Specification<DecisionFiltrageQpcModel> specification);
}