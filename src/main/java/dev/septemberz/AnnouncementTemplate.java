package dev.septemberz;
import java.time.LocalDate;
import java.util.Map;
public record AnnouncementTemplate(String name,String titlePattern,String bodyPattern){
 public Announcement render(Map<String,String> fields,String audience,LocalDate deadline,String contact){
  if(fields==null)throw new IllegalArgumentException("fields required");return new Announcement(fill(titlePattern,fields),fill(bodyPattern,fields),audience,deadline,contact);
 }
 private static String fill(String pattern,Map<String,String> fields){String out=pattern;for(var entry:fields.entrySet())out=out.replace("{{"+entry.getKey()+"}}",entry.getValue());if(out.matches(".*\\{\\{[^}]+}}.*"))throw new IllegalArgumentException("missing template field");return out;}
}
