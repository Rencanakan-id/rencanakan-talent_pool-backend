package rencanakan.id.talentPool.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import rencanakan.id.talentPool.dto.TalentDTO;
import rencanakan.id.talentPool.model.Talent;
import rencanakan.id.talentPool.service.TalentService;

@RestController
@RequestMapping("/talents")
public class TalentController {

    @Autowired
    private TalentService talentService;

    // Endpoint untuk mendapatkan Talent berdasarkan ID (menggunakan DTO)
    @GetMapping("/{id}")
    public TalentDTO getTalentById(@PathVariable String id) {
        return talentService.getTalentDTOById(id);
    }

    // Endpoint untuk menambahkan Talent (menggunakan DTO)
    @PostMapping("/add")
    public TalentDTO addTalent(@RequestBody TalentDTO talentDTO) {
        return talentService.addTalent(talentDTO);
    }
}

