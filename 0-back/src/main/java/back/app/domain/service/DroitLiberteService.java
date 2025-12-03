package back.app.domain.service;

import back.app.data.model.qpc.DroitLiberteModel;
import back.app.data.repository.interfaces.DroitLiberteRepository;
import back.app.domain.entity.DroitLiberteDTO;
import back.app.domain.mapper.DroitLiberteMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class DroitLiberteService {

    private final DroitLiberteRepository droitLiberteRepository;
    private final DroitLiberteMapper droitLiberteMapper;

    @Transactional(readOnly = true)
    public List<DroitLiberteDTO> getAll() {
        return droitLiberteMapper.toDTOList(droitLiberteRepository.findAll());
    }

    @Transactional(readOnly = true)
    public DroitLiberteDTO getById(Long id) {
        DroitLiberteModel entity = droitLiberteRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("DroitLiberte not found with id " + id));
        return droitLiberteMapper.toDTO(entity);
    }

    @Transactional
    public DroitLiberteDTO create(DroitLiberteDTO dto) {
        DroitLiberteModel toSave = droitLiberteMapper.toModel(dto);
        toSave.setId(null);
        DroitLiberteModel saved = droitLiberteRepository.save(toSave);
        return droitLiberteMapper.toDTO(saved);
    }

    @Transactional
    public DroitLiberteDTO update(Long id, DroitLiberteDTO dto) {
        DroitLiberteModel existing = droitLiberteRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("DroitLiberte not found with id " + id));

        existing.setTexte(dto.getTexte());

        DroitLiberteModel saved = droitLiberteRepository.save(existing);
        return droitLiberteMapper.toDTO(saved);
    }

    @Transactional
    public void delete(Long id) {
        if (!droitLiberteRepository.existsById(id)) {
            throw new NoSuchElementException("DroitLiberte not found with id " + id);
        }
        droitLiberteRepository.deleteById(id);
    }
}