package back.app.domain.service;

import back.app.data.model.qpc.DecisionFiltrageQpcModel;
import back.app.data.repository.interfaces.DecisionFiltrageQpcRepository;
import back.app.domain.entity.DecisionFiltrageQpcDTO;
import back.app.domain.entity.PageDTO;
import back.app.domain.mapper.DecisionFiltrageQpcMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class DecisionFiltrageQpcService {

    private final DecisionFiltrageQpcRepository decisionFiltrageQpcRepository;
    private final DecisionFiltrageQpcMapper decisionFiltrageQpcMapper;

    @Transactional(readOnly = true)
    public PageDTO<DecisionFiltrageQpcDTO> getPaginatedDecisionsFiltrage(
            Specification<DecisionFiltrageQpcModel> specification,
            Pageable pageable
    ) {
        Page<DecisionFiltrageQpcModel> page = decisionFiltrageQpcRepository.findAll(specification, pageable);

        List<DecisionFiltrageQpcDTO> content = page.getContent().stream()
                .map(decisionFiltrageQpcMapper::toDTO)
                .toList();

        PageDTO<DecisionFiltrageQpcDTO> pageDTO = new PageDTO<>();
        pageDTO.setPage(pageable.getPageNumber());
        pageDTO.setSize(pageable.getPageSize());
        pageDTO.setTotalElements(decisionFiltrageQpcRepository.count(specification));
        pageDTO.setContent(content);

        return pageDTO;
    }

    @Transactional(readOnly = true)
    public DecisionFiltrageQpcDTO getById(Long id) {
        DecisionFiltrageQpcModel entity = decisionFiltrageQpcRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("DecisionFiltrageQpc not found with id " + id));
        return decisionFiltrageQpcMapper.toDTO(entity);
    }
}