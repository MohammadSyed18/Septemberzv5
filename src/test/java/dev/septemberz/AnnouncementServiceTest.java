package dev.septemberz;
import static org.junit.jupiter.api.Assertions.*; import java.time.*; import org.junit.jupiter.api.Test;
class AnnouncementServiceTest {
  @Test void filtersAndSorts(){var s=new AnnouncementService();s.create(new Announcement("Lab","Bring goggles","Students",LocalDate.now().plusDays(2),"lab@example.test"));s.create(new Announcement("Club","Sign up","Families",LocalDate.now().plusDays(1),"club@example.test"));assertEquals("Lab",s.list("students").getFirst().title());}
  @Test void rejectsPastAndDuplicates(){var s=new AnnouncementService();var a=new Announcement("Event","Details","All",LocalDate.now().plusDays(1),"info@example.test");s.create(a);assertThrows(IllegalStateException.class,()->s.create(a));assertThrows(IllegalArgumentException.class,()->s.create(new Announcement("Old","x","All",LocalDate.now().minusDays(1),"x@example.test")));}
}
