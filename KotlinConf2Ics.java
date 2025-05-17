///usr/bin/env jbang "$0" "$@" ; exit $?
//DEPS com.fasterxml.jackson.core:jackson-databind:2.17.0
//DEPS com.fasterxml.jackson.core:jackson-core:2.17.0
//DEPS com.fasterxml.jackson.core:jackson-annotations:2.17.0
//DEPS net.sf.biweekly:biweekly:0.6.8

import java.io.File;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import com.fasterxml.jackson.databind.ObjectMapper;
import biweekly.Biweekly;
import biweekly.component.VEvent;

public class KotlinConf2Ics {
    public static void main(String[] args) throws Exception {
        String url = "https://kotlinconf.com/page-data/schedule/page-data.json?day=2025-05-22";
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder().uri(URI.create(url)).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        String json = response.body();

        ObjectMapper mapper = new ObjectMapper();
        ScheduleRoot root = mapper.readValue(json, ScheduleRoot.class);

        biweekly.ICalendar ical = new biweekly.ICalendar();
        for (Group group : root.result().data().allSession().group()) {
            for (SessionGroup sessionGroup : group.group()) {
                for (Session session : sessionGroup.nodes()) {
                    VEvent event = new VEvent();
                    event.setSummary(session.title());
                    event.setDescription(session.description());
                    event.setLocation(session.room() != null ? session.room().name() : null);
                    event.setDateStart(java.util.Date.from(OffsetDateTime.parse(session.startsAt(), DateTimeFormatter.ISO_OFFSET_DATE_TIME).toInstant()));
                    event.setDateEnd(java.util.Date.from(OffsetDateTime.parse(session.endsAt(), DateTimeFormatter.ISO_OFFSET_DATE_TIME).toInstant()));
                    ical.addEvent(event);
                }
            }
        }

        File file = new File("kotlinconf.ics");
        Biweekly.write(ical).go(file);
        System.out.println("ICS file generated: " + file.getAbsolutePath());
    }

    // Record classes for JSON mapping
    public record ScheduleRoot(String componentChunkName, String path, Result result, List<String> staticQueryHashes, SlicesMap slicesMap) {}
    public record Result(Data data, PageContext pageContext) {}
    public record PageContext() {}
    public record Data(AllSession allSession) {}
    public record AllSession(List<Group> group) {}
    public record Group(String fieldValue, List<SessionGroup> group) {}
    public record SessionGroup(List<Session> nodes) {}
    public record Session(
        String id,
        String originalId,
        String title,
        String description,
        String startsAt,
        String endsAt,
        Boolean isServiceSession,
        String startHour,
        List<Speaker> speakers,
        List<CategoryItem> categoryItems,
        Room room
    ) {}
    public record Speaker(
        String id,
        String originalId,
        String fullName,
        String lastName,
        String tagLine,
        ProfilePictureFile profilePictureFile
    ) {}
    public record ProfilePictureFile(
        ChildImageSharp childImageSharp
    ) {}
    public record ChildImageSharp(
        GatsbyImageData gatsbyImageData
    ) {}
    public record GatsbyImageData(
        String layout,
        String backgroundColor,
        Images images,
        int width,
        int height
    ) {}
    public record Images(
        Fallback fallback,
        List<Source> sources
    ) {}
    public record Fallback(
        String src,
        String srcSet,
        String sizes
    ) {}
    public record Source(
        String srcSet,
        String type,
        String sizes
    ) {}
    public record CategoryItem(
        String id,
        String originalId,
        String name,
        String categoryId
    ) {}
    public record Room(
        String id,
        String name,
        int sort
    ) {}
    public record SlicesMap() {}
} 