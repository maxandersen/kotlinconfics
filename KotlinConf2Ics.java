///usr/bin/env jbang "$0" "$@" ; exit $?
//DEPS io.quarkus.platform:quarkus-bom:3.22.3@pom
//DEPS io.quarkus:quarkus-picocli
//DEPS io.quarkus:quarkus-rest-client-jackson
//DEPS io.quarkus:quarkus-qute
//DEPS net.sf.biweekly:biweekly:0.6.8

import static biweekly.Biweekly.write;
import static java.lang.System.out;
import static java.time.OffsetDateTime.parse;
import static java.time.format.DateTimeFormatter.ISO_OFFSET_DATE_TIME;
import static java.util.Date.from;

import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.Callable;
import java.util.stream.Collectors;

import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;
import org.eclipse.microprofile.rest.client.inject.RestClient;

import biweekly.ICalendar;
import biweekly.component.VEvent;
import io.quarkus.qute.Qute;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import picocli.CommandLine.Command;

//Q:CONFIG quarkus.rest-client.logging.scope=request-response
//Q:CONFIG quarkus.rest-client.logging.body-limit=50
//Q:CONFIG quarkus.rest-client.extensions-api.scope=all
////Q:CONFIG quarkus.log.category."org.jboss.resteasy.reactive.client.logging".level=DEBUG
//Q:CONFIG quarkus.log.console.level=WARN

@Command(name = "kotlinconf2ics", mixinStandardHelpOptions = true, description = "Download KotlinConf schedule and generate ICS")
public class KotlinConf2Ics implements Callable<Integer> {

    @RegisterRestClient(baseUri = "https://kotlinconf.com")
    public interface ScheduleService {
        @GET
        @Path("/page-data/schedule/page-data.json")
        ScheduleRoot getSchedule();
    }

    @RestClient
    ScheduleService scheduleService;

    @Override
    public Integer call() throws Exception {
        var ical = new ICalendar();

            out.println("Getting schedule");
            var root = scheduleService.getSchedule();
            for (var group : root.result().data().allSession().group()) {
                for (var sessionGroup : group.group()) {
                    for (var session : sessionGroup.nodes()) {
                        var event = new VEvent();

                        String summary = session.title();
                        event.setSummary(summary);

                        String description = Qute.fmt("""
                        Speakers:{speakers}

                        {description}
                        """, Map.of(
                                                "description",Objects.toString(session.description(),""),
                                                "speakers",session.speakers.stream().map(s -> s.fullName()).collect(Collectors.joining(", "))));
                        event.setDescription(description);
                        event.setLocation(session.room() != null ? session.room().name() : null);
                        event.setDateStart(from(parse(session.startsAt(), ISO_OFFSET_DATE_TIME).toInstant()));
                        event.setDateEnd(from(parse(session.endsAt(), ISO_OFFSET_DATE_TIME).toInstant()));
                        ical.addEvent(event);
                    }
                }
            }
        
        var file = Paths.get("kotlinconf.ics");
        write(ical).go(file.toFile());
        out.println("ICS file generated: " + file);
        return 0;
    }

    

    // Record classes for JSON mapping
    public record ScheduleRoot(String componentChunkName, String path, Result result, List<String> staticQueryHashes,
            SlicesMap slicesMap) {
    }

    public record Result(Data data, PageContext pageContext) {
    }

    public record PageContext() {
    }

    public record Data(AllSession allSession) {
    }

    public record AllSession(List<Group> group) {
    }

    public record Group(String fieldValue, List<SessionGroup> group) {
    }

    public record SessionGroup(List<Session> nodes) {
    }

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
            Room room) {
    }

    public record Speaker(
            String id,
            String originalId,
            String fullName,
            String lastName,
            String tagLine,
            ProfilePictureFile profilePictureFile) {
    }

    public record ProfilePictureFile(
            ChildImageSharp childImageSharp) {
    }

    public record ChildImageSharp(
            GatsbyImageData gatsbyImageData) {
    }

    public record GatsbyImageData(
            String layout,
            String backgroundColor,
            Images images,
            int width,
            int height) {
    }

    public record Images(
            Fallback fallback,
            List<Source> sources) {
    }

    public record Fallback(
            String src,
            String srcSet,
            String sizes) {
    }

    public record Source(
            String srcSet,
            String type,
            String sizes) {
    }

    public record CategoryItem(
            String id,
            String originalId,
            String name,
            String categoryId) {
    }

    public record Room(
            String id,
            String name,
            int sort) {
    }

    public record SlicesMap() {
    }
}