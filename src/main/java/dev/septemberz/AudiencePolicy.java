package dev.septemberz;
import java.util.*;
public final class AudiencePolicy {
 private static final Map<String,Set<String>> GROUPS=Map.of("all",Set.of("students","families","staff"),"students",Set.of("students"),"families",Set.of("families"),"staff",Set.of("staff"));
 public Set<String> recipients(String audience){var result=GROUPS.get(audience.toLowerCase(Locale.ROOT));if(result==null)throw new IllegalArgumentException("unknown audience");return result;}
 public boolean visibleTo(Announcement notice,String group){return recipients(notice.audience()).contains(group.toLowerCase(Locale.ROOT));}
}
