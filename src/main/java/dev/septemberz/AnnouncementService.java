package dev.septemberz;
import java.time.*; import java.util.*; import java.util.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Service;
@Service public class AnnouncementService {
  private final Map<String,Announcement> notices = new ConcurrentHashMap<>();
  public Announcement create(Announcement a) {
    if (a.deadline().isBefore(LocalDate.now())) throw new IllegalArgumentException("deadline cannot be in the past");
    String key=(a.title()+"|"+a.audience()).toLowerCase(Locale.ROOT);
    if(notices.putIfAbsent(key,a)!=null) throw new IllegalStateException("duplicate announcement"); return a;
  }
  public List<Announcement> list(String audience) { return notices.values().stream().filter(a->audience==null||a.audience().equalsIgnoreCase(audience)).sorted(Comparator.comparing(Announcement::deadline)).toList(); }
  public String render(Announcement a) { return "%s — %s Action by %s. Questions: %s".formatted(a.title(),a.details(),a.deadline(),a.contact()); }
}
