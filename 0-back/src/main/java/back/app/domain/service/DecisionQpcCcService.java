package back.app.domain.service;

import back.app.data.model.qpc.DecisionQpcCcModel;
import back.app.data.repository.interfaces.DecisionQpcCcRepository;
import back.app.domain.entity.DecisionQpcCcDTO;
import back.app.domain.entity.DecisionQpcCcRowDTO;
import back.app.domain.entity.PageDTO;
import back.app.domain.mapper.DecisionQpcCcMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class DecisionQpcCcService {

    private final DecisionQpcCcRepository decisionQpcCcRepository;
    private final DecisionQpcCcMapper decisionQpcCcMapper;

    @Transactional(readOnly = true)
    public PageDTO<DecisionQpcCcRowDTO> getPaginatedDecisionsQpcCc(
            Specification<DecisionQpcCcModel> specification,
            Pageable pageable
    ) {
        Page<DecisionQpcCcModel> page = decisionQpcCcRepository.findAll(specification, pageable);

        PageDTO<DecisionQpcCcRowDTO> pageDTO = new PageDTO<>();
        pageDTO.setPage(pageable.getPageNumber());
        pageDTO.setSize(pageable.getPageSize());
        pageDTO.setTotalElements(page.getTotalElements());
        pageDTO.setContent(decisionQpcCcMapper.toRowDTOList(page.getContent()));

        return pageDTO;
    }

    @Transactional(readOnly = true)
    public DecisionQpcCcDTO getById(Long id) {
        DecisionQpcCcModel entity = decisionQpcCcRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("DecisionQpcCc not found with id " + id));
        return decisionQpcCcMapper.toDTO(entity);
    }
}