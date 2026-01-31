package dev.septemberz;
import java.time.*; import java.util.*; import java.util.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Service;
@Service public class AnnouncementService {
  private final Map<String,Announcement> notices = new ConcurrentHashMap<>();
  private final Clock clock;
  public AnnouncementService(){this(Clock.systemUTC());}
  public AnnouncementService(Clock clock){this.clock=Objects.requireNonNull(clock);}
  public Announcement create(Announcement a) {
    validate(a);
    if (a.deadline().isBefore(LocalDate.now(clock))) throw new IllegalArgumentException("deadline cannot be in the past");
    String key=(a.title().strip()+"|"+a.audience().strip()).toLowerCase(Locale.ROOT);
    if(notices.putIfAbsent(key,a)!=null) throw new IllegalStateException("duplicate announcement"); return a;
  }
  public List<Announcement> list(String audience) { return notices.values().stream().filter(a->audience==null||a.audience().equalsIgnoreCase(audience)).sorted(Comparator.comparing(Announcement::deadline)).toList(); }
  public String render(Announcement a) { return "%s — %s Action by %s. Questions: %s".formatted(a.title(),a.details(),a.deadline(),a.contact()); }
  public boolean withdraw(String title,String audience){return notices.remove((title.strip()+"|"+audience.strip()).toLowerCase(Locale.ROOT))!=null;}
  public List<Announcement> active(String audience){return list(audience).stream().filter(a->!a.deadline().isBefore(LocalDate.now(clock))).toList();}
  private void validate(Announcement a){
    Objects.requireNonNull(a,"announcement required");
    if(a.title()==null||a.title().isBlank()||a.details()==null||a.details().isBlank()||a.deadline()==null||a.contact()==null||a.contact().isBlank())throw new IllegalArgumentException("announcement fields required");
    new AudiencePolicy().recipients(a.audience());
  }
}
