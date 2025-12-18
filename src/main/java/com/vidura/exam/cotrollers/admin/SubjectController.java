package com.vidura.exam.cotrollers.admin;

import com.vidura.exam.dto.PaginationResponse;
import com.vidura.exam.dto.RetrieveStatus;
import com.vidura.exam.dto.SubjectDTO;
import com.vidura.exam.dto.response.ServerStatus;
import com.vidura.exam.services.SubjectService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/subject")
@RequiredArgsConstructor
class SubjectController {
    private static final Logger log = LoggerFactory.getLogger(SubjectController.class);
    private final SubjectService subjectService;
    @PostMapping("/")
    public ResponseEntity<SubjectDTO> create(@RequestBody @Valid SubjectDTO subjectDTO) {
        try{
            SubjectDTO response = subjectService.create(subjectDTO);
            return ResponseEntity.ok(response);
        }catch (Exception e){
            SubjectDTO  subject = new SubjectDTO();
            subject.setMessage(e.getMessage());
            subject.setStatus(ServerStatus.ERROR);
            return ResponseEntity.ok(subject);
        }
    }

    @GetMapping("/")
    public ResponseEntity<PaginationResponse<SubjectDTO>> getAll(@RequestParam int limit, @RequestParam int pageNumber, @RequestParam RetrieveStatus status) {
        try{
            PaginationResponse<SubjectDTO> subs = subjectService.getAll(limit,pageNumber,status);
            return ResponseEntity.ok(subs);
        }catch (Exception e){
            e.printStackTrace();
            log.error(e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

}
