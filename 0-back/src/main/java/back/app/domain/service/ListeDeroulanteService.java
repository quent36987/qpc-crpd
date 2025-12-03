package back.app.domain.service;

import back.app.data.model.qpc.ListeDeroulanteModel;
import back.app.data.repository.interfaces.ListeDeroulanteRepository;
import back.app.domain.entity.ListeDeroulanteDTO;
import back.app.domain.mapper.ListeDeroulanteMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class ListeDeroulanteService {

    private final ListeDeroulanteRepository listeDeroulanteRepository;
    private final ListeDeroulanteMapper listeDeroulanteMapper;

    @Transactional(readOnly = true)
    public List<ListeDeroulanteDTO> getAllListesDeroulantes() {

        return listeDeroulanteMapper.toDTO(listeDeroulanteRepository.findAll());
    }

    @Transactional(readOnly = true)
    public ListeDeroulanteDTO getById(Long id) {
        ListeDeroulanteModel entity = listeDeroulanteRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("ListeDeroulante not found with id " + id));
        return listeDeroulanteMapper.toDTO(entity);
    }

    @Transactional
    public ListeDeroulanteDTO create(ListeDeroulanteDTO dto) {
        ListeDeroulanteModel toSave = listeDeroulanteMapper.toModel(dto);
        toSave.setId(null); // sécurité création
        ListeDeroulanteModel saved = listeDeroulanteRepository.save(toSave);
        return listeDeroulanteMapper.toDTO(saved);
    }

    @Transactional
    public ListeDeroulanteDTO update(Long id, ListeDeroulanteDTO dto) {
        ListeDeroulanteModel existing = listeDeroulanteRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("ListeDeroulante not found with id " + id));

        existing.setChamp(dto.getChamp());
        existing.setValeur(dto.getValeur());
        if (dto.getActif() != null) {
            existing.setActif(dto.getActif());
        }

        ListeDeroulanteModel saved = listeDeroulanteRepository.save(existing);
        return listeDeroulanteMapper.toDTO(saved);
    }

    @Transactional
    public void delete(Long id) {
        if (!listeDeroulanteRepository.existsById(id)) {
            throw new NoSuchElementException("ListeDeroulante not found with id " + id);
        }
        listeDeroulanteRepository.deleteById(id);
    }
}
