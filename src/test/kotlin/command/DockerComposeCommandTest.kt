package command

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.io.ByteArrayOutputStream
import java.io.PrintStream

internal class DockerComposeCommandTest {
    @Test
    fun `Test that the command applies a run configuration`() {
        val output = ByteArrayOutputStream()
        System.setOut(PrintStream(output))

        DockerComposeCommand().parse(
                listOf(
                        "-c",
                        "src/test/config/connectionConfig.yml",
                        "-d",
                        "src/test/config/dockerComposeBlueprint.yml",
                        "-r",
                        "src/test/config/runConfig.yml"
                )
        )
        val expectedDockerCompose = """version: "2.4"
services:
  "title-podlet":
    container_name: "title_podlet_local"
    build:
      context: ""
    ports:
    - "8001:8001"
    environment: {}
    mem_limit: "100M"
  "button-podlet":
    container_name: "button_podlet_local"
    build:
      context: ""
    ports:
    - "8002:8002"
    environment: {}
    mem_limit: "100M"
  "hello-world-layout":
    container_name: "hello_world_layout_local"
    build:
      context: ""
    ports:
    - "8000:8000"
    environment:
      "TITLE_PODLET_ADDR": "http://title-podlet:8001"
      "BUTTON_PODLET_ADDR": "http://button-podlet:8002"
    mem_limit: "100M"
"""

        assertEquals(expectedDockerCompose, output.toString())
    }
}