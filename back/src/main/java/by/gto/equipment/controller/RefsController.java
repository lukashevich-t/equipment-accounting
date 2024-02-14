package by.gto.equipment.controller;

import by.gto.equipment.model.AnswerStatusCode;
import by.gto.equipment.model.JSONResponse;
import by.gto.equipment.service.RefsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/refs")
public class RefsController {

    @Autowired
    private final RefsService refsService = null;

    @GetMapping("states")
    public JSONResponse getStates() {
        return new JSONResponse(AnswerStatusCode.OK, null, refsService.getStates());
    }

}
