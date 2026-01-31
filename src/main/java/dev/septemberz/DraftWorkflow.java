package dev.septemberz;

import java.time.Clock;
import java.time.Instant;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/** In-memory review workflow with optimistic versions and immutable event records. */
@Service
public final class DraftWorkflow {
    public enum State { DRAFT, IN_REVIEW, APPROVED, REJECTED, PUBLISHED, WITHDRAWN }
    public record Draft(String id,int version,Announcement notice,State state,String author) {}
    public record Event(String draftId,int version,String action,String actor,String note,Instant at) {}
    private final Map<String,Draft> drafts = new LinkedHashMap<>();
    private final List<Event> events = new ArrayList<>();
    private final AnnouncementService publication;
    private final Clock clock;

    @Autowired
    public DraftWorkflow(AnnouncementService publication) { this(publication,Clock.systemUTC()); }
    DraftWorkflow(AnnouncementService publication,Clock clock) { this.publication=publication; this.clock=clock; }

    public synchronized Draft create(Announcement notice,String author) {
        validateNotice(notice);
        author = text(author,"author");
        var draft = new Draft(UUID.randomUUID().toString(),1,notice,State.DRAFT,author);
        drafts.put(draft.id(),draft);
        events.add(new Event(draft.id(),1,"created",author,"",clock.instant()));
        return draft;
    }
    public synchronized Draft revise(String id,int expectedVersion,Announcement notice,String actor) {
        var old = current(id,expectedVersion);
        if (old.state()==State.PUBLISHED || old.state()==State.WITHDRAWN) throw new IllegalStateException("published records cannot be edited as drafts");
        validateNotice(notice);
        return replace(old,notice,State.DRAFT,text(actor,"actor"),"revised","");
    }
    public synchronized Draft transition(String id,int expectedVersion,String action,String actor,String note) {
        var old = current(id,expectedVersion);
        actor = text(actor,"actor");
        note = note == null ? "" : note.strip();
        if (note.length()>2000) throw new IllegalArgumentException("review note too long");
        State next;
        switch (action) {
            case "submit" -> { require(old,State.DRAFT); next=State.IN_REVIEW; }
            case "approve" -> {
                require(old,State.IN_REVIEW);
                if (old.author().equals(actor)) throw new IllegalStateException("author cannot approve their own draft");
                next=State.APPROVED;
            }
            case "reject" -> {
                require(old,State.IN_REVIEW);
                if (note.isBlank()) throw new IllegalArgumentException("rejection reason required");
                next=State.REJECTED;
            }
            case "publish" -> {
                require(old,State.APPROVED);
                publication.create(old.notice()); // errors leave the draft and its events intact
                next=State.PUBLISHED;
            }
            case "withdraw" -> {
                require(old,State.PUBLISHED);
                if (!publication.withdraw(old.notice().title(),old.notice().audience())) throw new IllegalStateException("publication no longer exists");
                next=State.WITHDRAWN;
            }
            default -> throw new IllegalArgumentException("unknown workflow action");
        }
        return replace(old,old.notice(),next,actor,action,note);
    }
    public synchronized List<Draft> list() { return List.copyOf(drafts.values()); }
    public synchronized List<Event> history(String id) {
        if (!drafts.containsKey(id)) throw new IllegalArgumentException("unknown draft");
        return events.stream().filter(event -> event.draftId().equals(id)).toList();
    }
    private Draft current(String id,int version) {
        var draft=drafts.get(id);
        if (draft==null) throw new IllegalArgumentException("unknown draft");
        if (draft.version()!=version) throw new IllegalStateException("draft version changed");
        return draft;
    }
    private Draft replace(Draft old,Announcement notice,State state,String actor,String action,String note) {
        var next=new Draft(old.id(),old.version()+1,notice,state,old.author());
        drafts.put(next.id(),next);
        events.add(new Event(next.id(),next.version(),action,actor,note,clock.instant()));
        return next;
    }
    private void require(Draft draft,State state) {
        if (draft.state()!=state) throw new IllegalStateException("action requires state "+state);
    }
    private void validateNotice(Announcement notice) {
        if (notice==null) throw new IllegalArgumentException("notice required");
        text(notice.title(),"title"); text(notice.details(),"details"); text(notice.contact(),"contact");
        if (notice.deadline()==null) throw new IllegalArgumentException("deadline required");
        new AudiencePolicy().recipients(text(notice.audience(),"audience"));
    }
    private String text(String value,String field) {
        if (value==null || value.isBlank() || value.length()>10000) throw new IllegalArgumentException("invalid "+field);
        return value.strip();
    }
}
