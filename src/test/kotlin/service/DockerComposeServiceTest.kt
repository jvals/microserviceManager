package service

import model.Build
import model.ConnectionConf
import model.EnvironmentVar
import model.Image
import model.Port
import model.RunConfiguration
import model.RunMode
import model.Service
import model.ServiceName
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

internal class DockerComposeServiceTest {

    @BeforeEach
    fun setUp() {
    }

    @AfterEach
    fun tearDown() {
    }

    // applyRunConfig tests
    @Test
    fun `If all services in the input are NOOPs, the output should have no services`() {
        val inputServices = mapOf(
                ServiceName(name = "projectA") to Service(
                        containerName = "projectA_local",
                        image = Image("projectA:latest"),
                        build = null,
                        ports = listOf(Port(8083, 8080), Port(5007, 5005)),
                        dependsOn = null,
                        environment = mapOf(
                                "SECURE" to "false",
                                "SPRING_PROFILES_ACTIVE" to "dev",
                                "CONSTRETTO_TAGS" to "dev",
                                "JAVA_OPTS" to "-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:5005"
                        ),
                        memLimit = "1000M"
                ))
        val runConfigurations = mapOf(ServiceName(name = "projectA") to RunConfiguration(mode = RunMode.NOOP, context = ""))
        val expectedServices = emptyMap<ServiceName, RunConfiguration>()

        assertEquals(expectedServices, DockerComposeService.applyRunConfiguration(inputServices, runConfigurations))
    }

    @Test
    fun `If one service in the input is a NOOP, the output should contain all services except the NOOP service`() {
        val inputServices = mapOf(
                ServiceName(name = "projectA") to Service(
                        containerName = "projectA_local",
                        image = Image("projectA:latest"),
                        build = null,
                        ports = listOf(Port(8083, 8080), Port(5007, 5005)),
                        dependsOn = null,
                        environment = mapOf(
                                "SECURE" to "false",
                                "SPRING_PROFILES_ACTIVE" to "dev",
                                "CONSTRETTO_TAGS" to "dev",
                                "JAVA_OPTS" to "-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:5005"
                        ),
                        memLimit = "1000M"
                ),
                ServiceName(name = "projectB") to Service(
                        containerName = "projectB_local",
                        image = Image("projectB:latest"),
                        build = null,
                        ports = listOf(Port(8080, 8080), Port(5005, 5005)),
                        dependsOn = null,
                        environment = mapOf(
                                "SECURE" to "false",
                                "CONSTRETTO_TAGS" to "dev",
                                "JAVA_OPTS" to "-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=5005"
                        ),
                        memLimit = "1000M"
                )
                )

        val runConfigurations = mapOf(ServiceName(name = "projectA") to RunConfiguration(mode = RunMode.NOOP, context = ""), ServiceName(name = "projectB") to RunConfiguration(mode = RunMode.RUN, context = ""))
        val expectedServices = mapOf(
                ServiceName(name = "projectB") to Service(
                        containerName = "projectB_local",
                        image = Image("projectB:latest"),
                        build = null,
                        ports = listOf(Port(8080, 8080), Port(5005, 5005)),
                        dependsOn = null,
                        environment = mapOf(
                                "SECURE" to "false",
                                "CONSTRETTO_TAGS" to "dev",
                                "JAVA_OPTS" to "-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=5005"
                        ),
                        memLimit = "1000M"
                )
                )

        assertEquals(expectedServices, DockerComposeService.applyRunConfiguration(inputServices, runConfigurations))
    }

    @Test
    fun `Any service not present in the run config should be treated as a NOOP`() {
        val inputServices = mapOf(
                ServiceName(name = "projectA") to Service(
                        containerName = "projectA_local",
                        image = Image("projectA:latest"),
                        build = null,
                        ports = listOf(Port(8083, 8080), Port(5007, 5005)),
                        dependsOn = null,
                        environment = mapOf(
                                "SECURE" to "false",
                                "SPRING_PROFILES_ACTIVE" to "dev",
                                "CONSTRETTO_TAGS" to "dev",
                                "JAVA_OPTS" to "-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:5005"
                        ),
                        memLimit = "1000M"
                ),
                ServiceName(name = "projectB") to Service(
                        containerName = "projectB_local",
                        image = Image("projectB:latest"),
                        build = null,
                        ports = listOf(Port(8080, 8080), Port(5005, 5005)),
                        dependsOn = null,
                        environment = mapOf(
                                "SECURE" to "false",
                                "CONSTRETTO_TAGS" to "dev",
                                "JAVA_OPTS" to "-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=5005"
                        ),
                        memLimit = "1000M"
                )
                )

        val runConfigurations = mapOf(ServiceName(name = "projectB") to RunConfiguration(mode = RunMode.RUN, context = ""))
        val expectedServices = mapOf(
                ServiceName(name = "projectB") to Service(
                        containerName = "projectB_local",
                        image = Image("projectB:latest"),
                        build = null,
                        ports = listOf(Port(8080, 8080), Port(5005, 5005)),
                        dependsOn = null,
                        environment = mapOf(
                                "SECURE" to "false",
                                "CONSTRETTO_TAGS" to "dev",
                                "JAVA_OPTS" to "-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=5005"
                        ),
                        memLimit = "1000M"
                )
                )

        assertEquals(expectedServices, DockerComposeService.applyRunConfiguration(inputServices, runConfigurations))
    }

    @Test
    fun `A service in BUILD mode should have an empty image field`() {
        val inputServices = mapOf(
                ServiceName(name = "projectA") to Service(
                        containerName = "projectA_local",
                        image = Image("projectA:latest"),
                        build = null,
                        ports = listOf(Port(8083, 8080), Port(5007, 5005)),
                        dependsOn = null,
                        environment = mapOf(
                                "SECURE" to "false",
                                "SPRING_PROFILES_ACTIVE" to "dev",
                                "CONSTRETTO_TAGS" to "dev",
                                "JAVA_OPTS" to "-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:5005"
                        ),
                        memLimit = "1000M"
                ))
        val runConfigurations = mapOf(ServiceName(name = "projectA") to RunConfiguration(mode = RunMode.BUILD, context = "/projectA"))
        val expectedServices = mapOf(ServiceName(name = "projectA") to Service(
                containerName = "projectA_local",
                image = null,
                build = Build("/projectA"),
                ports = listOf(Port(8083, 8080), Port(5007, 5005)),
                dependsOn = null,
                environment = mapOf(
                        "SECURE" to "false",
                        "SPRING_PROFILES_ACTIVE" to "dev",
                        "CONSTRETTO_TAGS" to "dev",
                        "JAVA_OPTS" to "-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:5005"
                ),
                memLimit = "1000M"
        ))
        assertEquals(expectedServices, DockerComposeService.applyRunConfiguration(inputServices, runConfigurations))
    }

    @Test
    fun `Context and dockerfile added correctly in build mode`() {
        val inputServices = mapOf(
                ServiceName(name = "projectA") to Service(
                        containerName = "projectA_local",
                        ports = listOf(Port(8080, 8080)),
                        memLimit = "1000M"
                ),
                ServiceName(name = "projectB") to Service(
                        containerName = "projectB_local",
                        ports = listOf(Port(8081, 8081)),
                        memLimit = "1000M"
                )
        )

        val runConfigurations = mapOf(
                ServiceName(name = "projectA") to RunConfiguration(
                        mode = RunMode.BUILD,
                        context = "path/to/projectA",
                        dockerfile = "path/to/dockerfile"
                ),
                ServiceName(name = "projectB") to RunConfiguration(
                        mode = RunMode.BUILD,
                        context = "path/to/projectB"
                )
        )

        val expectedServices = mapOf(
                ServiceName(name = "projectA") to Service(
                        containerName = "projectA_local",
                        build = Build(
                                context = "path/to/projectA",
                                dockerfile = "path/to/dockerfile"
                        ),
                        ports = listOf(Port(8080, 8080)),
                        memLimit = "1000M"
                ),
                ServiceName(name = "projectB") to Service(
                        containerName = "projectB_local",
                        build = Build(
                                context = "path/to/projectB"
                        ),
                        ports = listOf(Port(8081, 8081)),
                        memLimit = "1000M"
                )
        )

        assertEquals(expectedServices, DockerComposeService.applyRunConfiguration(inputServices, runConfigurations))
    }

    // applyConnectionConfig tests
    @Test
    fun `An empty connection config should result in an unchanged compose file`() {
        val inputServices = mapOf(
                ServiceName(name = "projectA") to Service(
                        containerName = "projectA_local",
                        image = Image("projectA:latest"),
                        build = null,
                        ports = listOf(Port(8083, 8080), Port(5007, 5005)),
                        dependsOn = null,
                        environment = mapOf(
                                "SECURE" to "false",
                                "SPRING_PROFILES_ACTIVE" to "dev",
                                "CONSTRETTO_TAGS" to "dev",
                                "JAVA_OPTS" to "-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:5005"
                        ),
                        memLimit = "1000M"
                ),
                ServiceName(name = "projectB") to Service(
                        containerName = "projectB_local",
                        image = Image("projectB:latest"),
                        build = null,
                        ports = listOf(Port(8080, 8080), Port(5005, 5005)),
                        dependsOn = null,
                        environment = mapOf(
                                "SECURE" to "false",
                                "CONSTRETTO_TAGS" to "dev",
                                "JAVA_OPTS" to "-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=5005"
                        ),
                        memLimit = "1000M"
                )
                )

        val connectionConfigurations = emptyMap<ServiceName, ConnectionConf>()

        val outputServices = mapOf(
                ServiceName(name = "projectA") to Service(
                        containerName = "projectA_local",
                        image = Image("projectA:latest"),
                        build = null,
                        ports = listOf(Port(8083, 8080), Port(5007, 5005)),
                        dependsOn = null,
                        environment = mapOf(
                                "SECURE" to "false",
                                "SPRING_PROFILES_ACTIVE" to "dev",
                                "CONSTRETTO_TAGS" to "dev",
                                "JAVA_OPTS" to "-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:5005"
                        ),
                        memLimit = "1000M"
                ),
                ServiceName(name = "projectB") to Service(
                        containerName = "projectB_local",
                        image = Image("projectB:latest"),
                        build = null,
                        ports = listOf(Port(8080, 8080), Port(5005, 5005)),
                        dependsOn = null,
                        environment = mapOf(
                                "SECURE" to "false",
                                "CONSTRETTO_TAGS" to "dev",
                                "JAVA_OPTS" to "-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=5005"
                        ),
                        memLimit = "1000M"
                )
                )

        assertEquals(outputServices, DockerComposeService.applyConnectionConfiguration(inputServices, connectionConfigurations))
    }

    @Test
    fun `Applying a connection should result in a new entry in the environment variables`() {
        val inputServices = mapOf(
                        ServiceName(name = "projectA") to Service(
                                containerName = "projectA_local",
                                image = Image("projectA:latest"),
                                build = null,
                                ports = listOf(Port(8080, 8080)),
                                dependsOn = null,
                                environment = mapOf(
                                        "JAVA_OPTS" to "-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:5005"
                                ),
                                memLimit = "1000M"
                        ),
                        ServiceName(name = "projectB") to Service(
                                containerName = "projectB_local",
                                image = Image("projectB:latest"),
                                build = null,
                                ports = listOf(Port(8081, 8080)),
                                dependsOn = null,
                                environment = mapOf(
                                        "JAVA_OPTS" to "-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=5005"
                                ),
                                memLimit = "1000M"
                        )
                )


        val connectionConfigurations = mapOf(
                ServiceName("projectA") to ConnectionConf(
                        mapOf(
                                ServiceName("projectB") to listOf(
                                        EnvironmentVar("PROJECT_B_PORT", "8081")
                                )
                        )
                )
        )

        val outputServices = mapOf(
                        ServiceName(name = "projectA") to Service(
                                containerName = "projectA_local",
                                image = Image("projectA:latest"),
                                build = null,
                                ports = listOf(Port(8080, 8080)),
                                dependsOn = null,
                                environment = mapOf(
                                        "JAVA_OPTS" to "-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:5005",
                                        "PROJECT_B_PORT" to "8081"
                                ),
                                memLimit = "1000M"
                        ),
                        ServiceName(name = "projectB") to Service(
                                containerName = "projectB_local",
                                image = Image("projectB:latest"),
                                build = null,
                                ports = listOf(Port(8081, 8080)),
                                dependsOn = null,
                                environment = mapOf(
                                        "JAVA_OPTS" to "-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=5005"
                                ),
                                memLimit = "1000M"
                        )
                )


        val actualCompose = DockerComposeService.applyConnectionConfiguration(inputServices, connectionConfigurations)

        assertEquals(outputServices, actualCompose)
    }

    @Test
    fun `Environment variables should be extended, not overwritten`() {
        val inputServices = mapOf(
                        ServiceName(name = "projectA") to Service(
                                containerName = "projectA_local",
                                image = Image("projectA:latest"),
                                build = null,
                                ports = listOf(Port(8080, 8080)),
                                dependsOn = null,
                                environment = mapOf(
                                        "JAVA_OPTS" to "-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:5005"
                                ),
                                memLimit = "1000M"
                        ),
                        ServiceName(name = "projectB") to Service(
                                containerName = "projectB_local",
                                image = Image("projectB:latest"),
                                build = null,
                                ports = listOf(Port(8081, 8080)),
                                dependsOn = null,
                                environment = mapOf(
                                        "JAVA_OPTS" to "-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=5005",
                                        "CONSTRETTO_TAGS" to "local"
                                ),
                                memLimit = "1000M"
                        ),
                        ServiceName(name = "projectC") to Service(
                                containerName = "projectC_local",
                                image = Image("projectC:latest"),
                                build = null,
                                ports = listOf(Port(8082, 8080)),
                                dependsOn = null,
                                environment = mapOf(
                                        "JAVA_OPTS" to "-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=5005"
                                ),
                                memLimit = "1000M"
                        )
                )

        val connectionConfigurations = mapOf(
                ServiceName("projectA") to ConnectionConf(
                        mapOf(
                                ServiceName("projectB") to listOf(
                                        EnvironmentVar("CONSTRETTO_TAGS", "projectB")
                                ),
                                ServiceName("projectC") to listOf(
                                        EnvironmentVar("CONSTRETTO_TAGS", "projectC")
                                )
                        )
                ),
                ServiceName("projectB") to ConnectionConf(
                        mapOf(
                                ServiceName("projectA") to listOf(
                                        EnvironmentVar("CONSTRETTO_TAGS", "projectA")
                                ),
                                ServiceName("projectC") to listOf(
                                        EnvironmentVar("CONSTRETTO_TAGS", "projectC")
                                )
                        )
                )
        )

        val outputServices = mapOf(
                        ServiceName(name = "projectA") to Service(
                                containerName = "projectA_local",
                                image = Image("projectA:latest"),
                                build = null,
                                ports = listOf(Port(8080, 8080)),
                                dependsOn = null,
                                environment = mapOf(
                                        "JAVA_OPTS" to "-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:5005",
                                        "CONSTRETTO_TAGS" to "projectB,projectC"
                                ),
                                memLimit = "1000M"
                        ),
                        ServiceName(name = "projectB") to Service(
                                containerName = "projectB_local",
                                image = Image("projectB:latest"),
                                build = null,
                                ports = listOf(Port(8081, 8080)),
                                dependsOn = null,
                                environment = mapOf(
                                        "JAVA_OPTS" to "-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=5005",
                                        "CONSTRETTO_TAGS" to "projectA,projectC,local"
                                ),
                                memLimit = "1000M"
                        ),
                        ServiceName(name = "projectC") to Service(
                                containerName = "projectC_local",
                                image = Image("projectC:latest"),
                                build = null,
                                ports = listOf(Port(8082, 8080)),
                                dependsOn = null,
                                environment = mapOf(
                                        "JAVA_OPTS" to "-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=5005"
                                ),
                                memLimit = "1000M"
                        )
        )

        val actualCompose = DockerComposeService.applyConnectionConfiguration(inputServices, connectionConfigurations)

        assertEquals(outputServices, actualCompose)
    }

    @Test
    fun `Service missing from the blueprint should not cause a failure if the service exist in the connection config`() {
        val inputServices = mapOf(
                ServiceName(name = "projectA") to Service(
                        containerName = "projectA_local",
                        image = Image("projectA:latest"),
                        build = null,
                        ports = listOf(Port(8080, 8080)),
                        dependsOn = null,
                        environment = mapOf(
                                "JAVA_OPTS" to "-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:5005"
                        ),
                        memLimit = "1000M"
                ),
                ServiceName(name = "projectB") to Service(
                        containerName = "projectB_local",
                        image = Image("projectB:latest"),
                        build = null,
                        ports = listOf(Port(8081, 8080)),
                        dependsOn = null,
                        environment = mapOf(
                                "JAVA_OPTS" to "-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=5005",
                                "CONSTRETTO_TAGS" to "local"
                        ),
                        memLimit = "1000M"
                )
        )

        val connectionConfigurations = mapOf(
                ServiceName("projectA") to ConnectionConf(
                        mapOf(
                                ServiceName("projectC") to listOf(
                                        EnvironmentVar("CONSTRETTO_TAGS", "projectC")
                                )
                        )
                ),
                ServiceName("projectB") to ConnectionConf(
                        mapOf(
                                ServiceName("projectA") to listOf(
                                        EnvironmentVar("CONSTRETTO_TAGS", "projectA")
                                ),
                                ServiceName("projectC") to listOf(
                                        EnvironmentVar("CONSTRETTO_TAGS", "projectC")
                                )
                        )
                )
        )

        val outputServices = mapOf(
                ServiceName(name = "projectA") to Service(
                        containerName = "projectA_local",
                        image = Image("projectA:latest"),
                        build = null,
                        ports = listOf(Port(8080, 8080)),
                        dependsOn = null,
                        environment = mapOf(
                                "JAVA_OPTS" to "-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:5005"
                        ),
                        memLimit = "1000M"
                ),
                ServiceName(name = "projectB") to Service(
                        containerName = "projectB_local",
                        image = Image("projectB:latest"),
                        build = null,
                        ports = listOf(Port(8081, 8080)),
                        dependsOn = null,
                        environment = mapOf(
                                "JAVA_OPTS" to "-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=5005",
                                "CONSTRETTO_TAGS" to "projectA,local"
                        ),
                        memLimit = "1000M"
                )
        )

        val actualCompose = DockerComposeService.applyConnectionConfiguration(inputServices, connectionConfigurations)

        assertEquals(outputServices, actualCompose)
    }
}
