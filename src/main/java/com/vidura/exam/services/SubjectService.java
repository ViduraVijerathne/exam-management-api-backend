package com.vidura.exam.services;

import com.vidura.exam.dto.PaginationResponse;
import com.vidura.exam.dto.RetrieveStatus;
import com.vidura.exam.dto.SubjectDTO;
import com.vidura.exam.dto.response.ServerStatus;
import com.vidura.exam.entities.Subject;
import com.vidura.exam.repository.SubjectRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SubjectService {
    private final SubjectRepository subjectRepository;
    public SubjectDTO create(@Valid SubjectDTO subjectDTO) {

        long count = subjectRepository.countSubjectsByName(subjectDTO.getName());
        if(count > 0){
            subjectDTO.setStatus(ServerStatus.ERROR);
            subjectDTO.setMessage("Subject already exists");
            return subjectDTO;
        }

        Subject subject = new Subject();
        subject.setName(subjectDTO.getName());
        subjectRepository.save(subject);

        subjectDTO.setStatus(ServerStatus.SUCCESS);
        subjectDTO.setMessage("Subject created successfully");
        subjectDTO.setId(subject.getId());
        return subjectDTO;
    }

    public PaginationResponse<SubjectDTO> getAll(int limit, int pageNumber, RetrieveStatus status) {

        Pageable pageable = PageRequest.of(pageNumber, limit);

        Page<Subject> subjectPage;

        // 2. Status එක අනුව Database එකෙන් දත්ත ලබා ගැනීම
        switch (status) {
            case ACTIVE:
                subjectPage = subjectRepository.findAllByIsActive(true, pageable);
                break;
            case INACTIVE:
                subjectPage = subjectRepository.findAllByIsActive(false, pageable);
                break;
            case ALL:
            default:
                subjectPage = subjectRepository.findAll(pageable);
                break;
        }

        // 3. Entity List එක DTO List එකක් බවට Map කිරීම
        List<SubjectDTO> subjects =  subjectPage.getContent().stream()
                .map(subject -> SubjectDTO.builder()
                        .id(subject.getId())
                        .name(subject.getName())
                        .isActive(subject.isActive())
                        .build())
                .toList();
        PaginationResponse<SubjectDTO> resp = new PaginationResponse<>();
        resp.setData(subjects);
        resp.setStatus(ServerStatus.SUCCESS);
        resp.setLimit(limit);
        resp.setPageNumber(pageNumber);
        resp.setRetrieveStatus(status);
        return resp;
    }
}
