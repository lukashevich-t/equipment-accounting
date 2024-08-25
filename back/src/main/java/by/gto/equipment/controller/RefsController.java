package by.gto.equipment.controller;

import by.gto.equipment.model.AnswerStatusCode;
import by.gto.equipment.model.EquipmentState;
import by.gto.equipment.model.JSONResponse;
import by.gto.equipment.repository.EquipmentStateRepository;
import by.gto.equipment.service.RefsService;
import jakarta.annotation.security.PermitAll;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/refs")
@PermitAll
public class RefsController {
    private final RefsService refsService;
    private final EquipmentStateRepository stateRepository;

    @Autowired
    public RefsController(RefsService refsService, EquipmentStateRepository stateRepository) {
        this.stateRepository = stateRepository;
        this.refsService = refsService;
    }

    @GetMapping("states")
    public JSONResponse getStates() {
        return new JSONResponse(AnswerStatusCode.OK, null, refsService.getStates());
    }

    @GetMapping("types")
    public JSONResponse getTypes() {
        return new JSONResponse(AnswerStatusCode.OK, null, refsService.getTypes());
    }

    @GetMapping("persons")
    public JSONResponse getPersons() {
        return new JSONResponse(AnswerStatusCode.OK, null, refsService.getPersons());
    }

    @GetMapping("test")
    public JSONResponse test() {
        List<EquipmentState> all = stateRepository.findAll();
        List<EquipmentState> byName = stateRepository.findByName("В резерве");
        byName = stateRepository.findByName("резерве");
        Optional<EquipmentState> byId = stateRepository.findById(3);
        EquipmentState newEquipment = new EquipmentState(0, "Тест");
        stateRepository.save(newEquipment);
        stateRepository.delete(newEquipment);


        newEquipment = new EquipmentState(0, "Тест2");
        stateRepository.save(newEquipment);
        stateRepository.deleteById(newEquipment.getId());
        return new JSONResponse(AnswerStatusCode.OK);
    }
}
