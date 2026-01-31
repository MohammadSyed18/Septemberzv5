package dev.septemberz;
import static org.junit.jupiter.api.Assertions.*;import java.time.*;import org.junit.jupiter.api.Test;
class AnnouncementLifecycleTest {
 @Test void clockAndWithdrawalAreDeterministic(){var service=new AnnouncementService(Clock.fixed(Instant.parse("2026-01-01T00:00:00Z"),ZoneOffset.UTC));var notice=new Announcement("Event","Details","all",LocalDate.of(2026,1,2),"contact@example.org");service.create(notice);assertEquals(1,service.active(null).size());assertTrue(service.withdraw("Event","ALL"));assertTrue(service.list(null).isEmpty());}
 @Test void audienceIsValidatedOutsideHttpLayer(){var service=new AnnouncementService();assertThrows(IllegalArgumentException.class,()->service.create(new Announcement("Event","Details","unknown",LocalDate.now().plusDays(1),"x")));}
}
