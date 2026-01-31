package dev.septemberz;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import java.util.Map;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/drafts")
public final class DraftController {
    public record CreateRequest(@NotNull @Valid Announcement notice,@NotBlank String author) {}
    public record ReviseRequest(int version,@NotNull @Valid Announcement notice,@NotBlank String actor) {}
    public record ActionRequest(int version,@NotBlank String action,@NotBlank String actor,String note) {}
    private final DraftWorkflow workflow;
    public DraftController(DraftWorkflow workflow) { this.workflow=workflow; }
    @GetMapping public List<DraftWorkflow.Draft> list() { return workflow.list(); }
    @PostMapping public ResponseEntity<DraftWorkflow.Draft> create(@Valid @RequestBody CreateRequest request) {
        return ResponseEntity.status(201).body(workflow.create(request.notice(),request.author()));
    }
    @PutMapping("/{id}") public DraftWorkflow.Draft revise(@PathVariable String id,@Valid @RequestBody ReviseRequest request) {
        return workflow.revise(id,request.version(),request.notice(),request.actor());
    }
    @PostMapping("/{id}/actions") public DraftWorkflow.Draft action(@PathVariable String id,@Valid @RequestBody ActionRequest request) {
        return workflow.transition(id,request.version(),request.action(),request.actor(),request.note());
    }
    @GetMapping("/{id}/events") public List<DraftWorkflow.Event> history(@PathVariable String id) { return workflow.history(id); }
    @ExceptionHandler(IllegalStateException.class) public ResponseEntity<Map<String,String>> conflict(IllegalStateException error) {
        return ResponseEntity.status(409).body(Map.of("error",error.getMessage()));
    }
    @ExceptionHandler(IllegalArgumentException.class) public ResponseEntity<Map<String,String>> invalid(IllegalArgumentException error) {
        return ResponseEntity.badRequest().body(Map.of("error",error.getMessage()));
    }
}
