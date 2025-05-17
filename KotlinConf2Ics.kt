//DEPS io.quarkus.platform:quarkus-bom:3.22.3@pom
//DEPS io.quarkus:quarkus-picocli
//DEPS io.quarkus:quarkus-rest-client-jackson
//DEPS io.quarkus:quarkus-qute
//DEPS net.sf.biweekly:biweekly:0.6.8
//KOTLIN 2.1.20

import biweekly.Biweekly.write
import biweekly.ICalendar
import biweekly.component.VEvent
import io.quarkus.qute.Qute
import jakarta.ws.rs.GET
import jakarta.ws.rs.Path
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient
import org.eclipse.microprofile.rest.client.inject.RestClient
import picocli.CommandLine.Command
import java.nio.file.Paths
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter
import java.util.*
import java.util.concurrent.Callable

@Command(
    name = "kotlinconf2ics",
    mixinStandardHelpOptions = true,
    description = ["Download KotlinConf schedule and generate ICS"]
)
class KotlinConf2Ics : Callable<Int> {
    @RegisterRestClient(baseUri = "https://kotlinconf.com")
    interface ScheduleService {
        @GET
        @Path("/page-data/schedule/page-data.json")
        fun getSchedule(): ScheduleRoot
    }

    @RestClient
    lateinit var scheduleService: ScheduleService

    override fun call(): Int {
        val ical = ICalendar()
        println("Getting schedule")
        val root = scheduleService.getSchedule()
        for (group in root.result?.data?.allSession?.group.orEmpty()) {
            for (sessionGroup in group.group.orEmpty()) {
                for (session in sessionGroup.nodes.orEmpty()) {
                    val event = VEvent()
                    with(event) {
                        val summary = Qute.fmt(
                            "{title} {#if categories}[{categories}]{/if}",
                            mapOf(
                                "title" to (session.title ?: ""),
                                "categories" to (session.categoryItems?.joinToString(", ") { it.name ?: "" } ?: "")
                            )
                        )
                        event.setSummary(summary)
                        val description = Qute.fmt(
                            """
                        {#if speakers}
                        Speakers:
                          {#for speaker : speakers}
                            {speaker.fullName()} - {speaker.tagLine()}
                          {/for}

                        {/if}
                        <a href="https://kotlinconf.com/schedule/?session={id}">link</a>

                        {description}
                        """,
                            mapOf(
                                "id" to (session.id ?: ""),
                                "description" to (session.description ?: ""),
                                "speakers" to (session.speakers ?: emptyList<Speaker>())
                            )
                        )
                        setDescription(description)
                        setLocation(session.room?.name)
                        setDateStart(
                            Date.from(
                                OffsetDateTime.parse(
                                    session.startsAt,
                                    DateTimeFormatter.ISO_OFFSET_DATE_TIME
                                ).toInstant()
                            )
                        )
                        setDateEnd(
                            Date.from(
                                OffsetDateTime.parse(
                                    session.endsAt,
                                    DateTimeFormatter.ISO_OFFSET_DATE_TIME
                                ).toInstant()
                            )
                        )
                    }
                    ical.addEvent(event)

                }
            }
        }
        val file = Paths.get("kotlinconf.ics")
        write(ical).go(file.toFile())
        println("ICS file generated: $file")
        return 0
    }

    // Data classes for JSON mapping (all fields nullable for Jackson compatibility)
    data class ScheduleRoot(
        val componentChunkName: String? = null,
        val path: String? = null,
        val result: Result? = null,
        val staticQueryHashes: List<String>? = null,
        val slicesMap: Any? = null
    )

    data class Result(
        val data: Data? = null,
        val pageContext: Any? = null
    )

    data class Data(
        val allSession: AllSession? = null
    )

    data class AllSession(
        val group: List<Group>? = null
    )

    data class Group(
        val fieldValue: String? = null,
        val group: List<SessionGroup>? = null
    )

    data class SessionGroup(
        val nodes: List<Session>? = null
    )

    data class Session(
        val id: String? = null,
        val originalId: String? = null,
        val title: String? = null,
        val description: String? = null,
        val startsAt: String? = null,
        val endsAt: String? = null,
        val isServiceSession: Boolean? = null,
        val startHour: String? = null,
        val speakers: List<Speaker>? = null,
        val categoryItems: List<CategoryItem>? = null,
        val room: Room? = null
    )

    data class Speaker(
        val id: String? = null,
        val originalId: String? = null,
        val fullName: String? = null,
        val lastName: String? = null,
        val tagLine: String? = null,
        val profilePictureFile: ProfilePictureFile? = null
    )

    data class ProfilePictureFile(
        val childImageSharp: ChildImageSharp? = null
    )

    data class ChildImageSharp(
        val gatsbyImageData: GatsbyImageData? = null
    )

    data class GatsbyImageData(
        val layout: String? = null,
        val backgroundColor: String? = null,
        val images: Images? = null,
        val width: Int? = null,
        val height: Int? = null
    )

    data class Images(
        val fallback: Fallback? = null,
        val sources: List<Source>? = null
    )

    data class Fallback(
        val src: String? = null,
        val srcSet: String? = null,
        val sizes: String? = null
    )

    data class Source(
        val srcSet: String? = null,
        val type: String? = null,
        val sizes: String? = null
    )

    data class CategoryItem(
        val id: String? = null,
        val originalId: String? = null,
        val name: String? = null,
        val categoryId: String? = null
    )

    data class Room(
        val id: String? = null,
        val name: String? = null,
        val sort: Int? = null
    )

} 