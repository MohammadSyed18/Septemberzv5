package dev.septemberz;

import java.time.*;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class DraftWorkflowTest {
    private final Clock clock=Clock.fixed(Instant.parse("2026-01-01T00:00:00Z"),ZoneOffset.UTC);
    private Announcement notice() { return new Announcement("Club meeting","Attend the meeting","students",LocalDate.of(2026,2,1),"Office"); }
    @Test void reviewPublishAndWithdrawalFollowVersions() {
        var publication=new AnnouncementService(clock);
        var workflow=new DraftWorkflow(publication,clock);
        var draft=workflow.create(notice(),"author");
        draft=workflow.transition(draft.id(),draft.version(),"submit","author","");
        final var submitted=draft;
        assertThrows(IllegalStateException.class,()->workflow.transition(submitted.id(),submitted.version(),"approve","author",""));
        draft=workflow.transition(draft.id(),draft.version(),"approve","reviewer","");
        draft=workflow.transition(draft.id(),draft.version(),"publish","publisher","");
        assertEquals(1,publication.list(null).size());
        assertEquals(DraftWorkflow.State.PUBLISHED,draft.state());
        assertThrows(IllegalStateException.class,()->workflow.transition(submitted.id(),submitted.version(),"reject","reviewer","outdated"));
        draft=workflow.transition(draft.id(),draft.version(),"withdraw","publisher","");
        assertTrue(publication.list(null).isEmpty());
        assertEquals(5,workflow.history(draft.id()).size());
    }
    @Test void failedPublicationLeavesReviewStateAndEventsIntact() {
        var publication=new AnnouncementService(clock);publication.create(notice());
        var workflow=new DraftWorkflow(publication,clock);
        var d=workflow.create(notice(),"author");
        d=workflow.transition(d.id(),d.version(),"submit","author","");
        d=workflow.transition(d.id(),d.version(),"approve","reviewer","");
        final var approved=d;
        assertThrows(IllegalStateException.class,()->workflow.transition(approved.id(),approved.version(),"publish","publisher",""));
        assertEquals(3,workflow.history(d.id()).size());
        assertEquals(DraftWorkflow.State.APPROVED,workflow.list().getFirst().state());
    }
}
