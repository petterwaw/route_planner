package pl.zdaze;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.methods;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;
import static com.tngtech.archunit.library.dependencies.SlicesRuleDefinition.slices;

import com.tngtech.archunit.base.DescribedPredicate;
import com.tngtech.archunit.core.domain.JavaClass;
import com.tngtech.archunit.core.domain.JavaMethod;
import com.tngtech.archunit.core.domain.JavaMethodCall;
import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchCondition;
import com.tngtech.archunit.lang.ArchRule;
import com.tngtech.archunit.lang.ConditionEvents;
import com.tngtech.archunit.lang.SimpleConditionEvent;
import com.tngtech.archunit.library.GeneralCodingRules;
import jakarta.persistence.Entity;
import java.time.Clock;
import java.util.Set;
import org.springframework.data.repository.Repository;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestClient;

/**
 * Architecture rules agreed with the project owner (stage 1). Each rule explains in {@code because}
 * why it exists, so a failing build tells both what broke and why.
 *
 * <p>Rules about packages that do not exist yet use {@code allowEmptyShould(true)}; all other rules
 * fail on an empty selection, which catches typos in package names.
 */
@AnalyzeClasses(packages = "pl.zdaze", importOptions = ImportOption.DoNotIncludeTests.class)
class ArchitectureTest {

    private static final Set<String> TIME_TYPES = Set.of(
            "java.time.Instant",
            "java.time.LocalDate",
            "java.time.LocalTime",
            "java.time.LocalDateTime",
            "java.time.OffsetDateTime",
            "java.time.ZonedDateTime");

    private static final DescribedPredicate<JavaMethodCall> NOW_WITHOUT_CLOCK =
            new DescribedPredicate<>("now() on a java.time type without a Clock argument") {
                @Override
                public boolean test(JavaMethodCall call) {
                    var target = call.getTarget();
                    return target.getName().equals("now")
                            && TIME_TYPES.contains(target.getOwner().getName())
                            && target.getRawParameterTypes().stream().noneMatch(t -> t.isEquivalentTo(Clock.class));
                }
            };

    @ArchTest
    static final ArchRule time_comes_only_from_the_injected_clock = noClasses()
            .should()
            .callMethodWhere(NOW_WITHOUT_CLOCK)
            .because("the current time must come from the injected Clock, so tests and the demo mode"
                    + " (app.clock.fixed) can freeze it; use e.g. LocalDateTime.now(clock)");

    @ArchTest
    static final ArchRule controllers_do_not_use_repositories = noClasses()
            .that()
            .areAnnotatedWith(RestController.class)
            .should()
            .dependOnClassesThat()
            .areAssignableTo(Repository.class)
            .because("controllers only map HTTP and validate input; data access belongs in services")
            // no controllers with repositories exist yet in early stages
            .allowEmptyShould(true);

    @ArchTest
    static final ArchRule controllers_do_not_return_jpa_entities = methods()
            .that()
            .areDeclaredInClassesThat()
            .areAnnotatedWith(RestController.class)
            .should(notReturnJpaEntities())
            .because("the API returns DTO records; JPA entities must not leak into the REST contract")
            // there are no controller methods until the first endpoint exists
            .allowEmptyShould(true);

    @ArchTest
    static final ArchRule rest_client_only_in_external_adapters = noClasses()
            .that()
            .resideOutsideOfPackages("pl.zdaze.routing.google..", "pl.zdaze.importer.openai..")
            .should()
            .dependOnClassesThat()
            .belongToAnyOf(RestClient.class)
            .because("paid external APIs are called only from their adapters, behind the RouteProvider"
                    + " and ScheduleExtractor ports");

    @ArchTest
    static final ArchRule routing_adapters_are_hidden_behind_the_port = noClasses()
            .that()
            .resideOutsideOfPackage("pl.zdaze.routing..")
            .should()
            .dependOnClassesThat()
            .resideInAnyPackage("pl.zdaze.routing.google..", "pl.zdaze.routing.mock..")
            .because("the rest of the app depends only on the RouteProvider port, so switching between"
                    + " mock and google is configuration, not code");

    @ArchTest
    static final ArchRule importer_adapters_are_hidden_behind_the_port = noClasses()
            .that()
            .resideOutsideOfPackage("pl.zdaze.importer..")
            .should()
            .dependOnClassesThat()
            .resideInAnyPackage("pl.zdaze.importer.openai..", "pl.zdaze.importer.mock..")
            .because("the rest of the app depends only on the ScheduleExtractor port, so switching between"
                    + " mock and openai is configuration, not code");

    @ArchTest
    static final ArchRule feature_packages_are_free_of_cycles = slices().matching("pl.zdaze.(*)..")
            .should()
            .beFreeOfCycles()
            .because("dependencies between features must go one way, so each feature can be understood,"
                    + " tested and changed on its own")
            // feature packages appear stage by stage
            .allowEmptyShould(true);

    // The built-in rule already explains why; it checks fields, and early stages have none.
    @ArchTest
    static final ArchRule no_field_injection =
            GeneralCodingRules.NO_CLASSES_SHOULD_USE_FIELD_INJECTION.allowEmptyShould(true);

    @ArchTest
    static final ArchRule no_standard_streams = GeneralCodingRules.NO_CLASSES_SHOULD_ACCESS_STANDARD_STREAMS.because(
            "use a logger; System.out and System.err bypass log levels and may leak data");

    private static ArchCondition<JavaMethod> notReturnJpaEntities() {
        return new ArchCondition<>("not return JPA entities, also inside generic types") {
            @Override
            public void check(JavaMethod method, ConditionEvents events) {
                for (JavaClass type : method.getReturnType().getAllInvolvedRawTypes()) {
                    if (type.isAnnotatedWith(Entity.class)) {
                        events.add(SimpleConditionEvent.violated(
                                method, method.getFullName() + " returns JPA entity " + type.getName()));
                    }
                }
            }
        };
    }
}
