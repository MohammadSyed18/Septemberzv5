package dev.septemberz;
import jakarta.validation.Valid; import java.util.*; import org.springframework.http.*; import org.springframework.web.bind.annotation.*;
@RestController @RequestMapping("/api/announcements") public class AnnouncementController {
  private final AnnouncementService service; public AnnouncementController(AnnouncementService service){this.service=service;}
  @PostMapping ResponseEntity<Announcement> create(@Valid @RequestBody Announcement a){return ResponseEntity.status(201).body(service.create(a));}
  @GetMapping List<Announcement> list(@RequestParam(required=false) String audience){return service.list(audience);}
  @PostMapping("/preview") Map<String,String> preview(@Valid @RequestBody Announcement a){return Map.of("text",service.render(a));}
  @ExceptionHandler({IllegalArgumentException.class,IllegalStateException.class}) ResponseEntity<Map<String,String>> bad(RuntimeException e){return ResponseEntity.badRequest().body(Map.of("error",e.getMessage()));}
}
