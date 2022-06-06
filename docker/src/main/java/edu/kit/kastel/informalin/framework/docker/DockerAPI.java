/* Licensed under MIT 2022. */
package edu.kit.kastel.informalin.framework.docker;

import java.util.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import edu.kit.kastel.informalin.framework.common.ExecuteCmd;

@SuppressWarnings("java:S2629")
public final class DockerAPI {

    private static final Logger logger = LoggerFactory.getLogger(DockerAPI.class);

    private final ObjectMapper oom = new ObjectMapper();
    private final String host;

    public DockerAPI() {
        this(null);
    }

    public DockerAPI(String host) {
        this.host = host;
        this.checkDockerExistence();
    }

    public List<DockerImage> listImagesCmd() {
        String imageCommand = getDockerCommand() + "images";
        var result = ExecuteCmd.runCommand(imageCommand);
        if (!result.success()) {
            logger.error("Failed to execute images command: {}", result.stdErr());
            return List.of();
        }
        // Parse result: REPOSITORY TAG IMAGE_ID CREATED SIZE
        var images = parseDockerLines(result.stdOut()).stream()
                .map(spec -> new DockerImage(spec.get("REPOSITORY"), spec.get("TAG"), null))
                .filter(it -> !it.isNone())
                .toList();
        logger.debug("Found Docker Images: {}", images);
        return images;
    }

    public boolean pullImageCmd(String image) {
        String pullCommand = getDockerCommand() + "pull " + image;
        var result = ExecuteCmd.runCommand(pullCommand, 60 * 5);
        if (!result.success()) {
            logger.error("Failed to execute pull command: {}", result.stdErr());
            return false;
        }
        return true;
    }

    public List<DockerContainer> listContainersCmd(boolean showAll) {
        String listCommand = getDockerCommand() + "ps";
        if (showAll)
            listCommand += " -a";

        var result = ExecuteCmd.runCommand(listCommand);
        if (!result.success()) {
            logger.error("Failed to execute list command: {}", result.stdErr());
            return List.of();
        }
        // Parse Results: CONTAINER_ID IMAGE COMMAND CREATED STATUS PORTS NAMES
        var containers = parseDockerLines(result.stdOut()).stream()
                .map(spec -> new DockerContainer(spec.get("CONTAINER ID"), spec.get("IMAGE"), spec.get("STATUS"), spec.get("NAMES")))
                .toList();
        logger.debug("Found Docker Containers: {}", containers);
        return containers;
    }

    public DockerImage inspectImageCmd(String image) {
        String inspectImageCommand = getDockerCommand() + "image inspect " + image;
        var result = ExecuteCmd.runCommand(inspectImageCommand);
        if (!result.success()) {
            logger.error("Failed to execute inspect image command: {}", result.stdErr());
            return null;
        }
        try {
            var configInformationNode = oom.readTree(result.stdOut()).get(0);
            var repoTags = configInformationNode.get("RepoTags").get(0).asText().split(":");
            var containerConfig = configInformationNode.get("ContainerConfig");
            if (!containerConfig.hasNonNull("ExposedPorts")) {
                return new DockerImage(repoTags[0], repoTags[1], List.of());
            }
            var exposedPorts = containerConfig.get("ExposedPorts");
            List<Integer> ports = oom.readValue(exposedPorts.toString(), new TypeReference<Map<String, Map<String, String>>>() {
            }).keySet().stream().map(spec -> spec.split("/")[0]).map(Integer::parseInt).distinct().toList();
            return new DockerImage(repoTags[0], repoTags[1], ports);

        } catch (JsonProcessingException e) {
            logger.error(e.getMessage(), e);
            return null;
        }
    }

    public String createContainer(String name, String image, DockerPortBind dpb) {
        if (!dpb.valid()) {
            logger.error("DockerPortBind is invalid!");
            throw new IllegalArgumentException("Invalid Docker Port Binding");
        }
        String realName = name.replaceAll("[^A-Za-z0-9_-]", "");
        String runCommand = getDockerCommand() + "run -d --name " + realName;
        if (dpb.wildcard()) {
            runCommand += " -p 0.0.0.0:" + dpb.hostPort() + ":" + dpb.containerPort();
        } else {
            runCommand += " -p 127.0.0.1:" + dpb.hostPort() + ":" + dpb.containerPort();
        }
        runCommand += " " + image;
        var result = ExecuteCmd.runCommand(runCommand);
        if (!result.success()) {
            logger.error("Failed to execute pull command: {}", result.stdErr());
            return null;
        }
        String fullId = result.stdOut().trim();
        // Trim to 12 char default id.
        return fullId.substring(0, 12);
    }

    public boolean killContainerCmd(String id) {
        String killCommand = getDockerCommand() + " kill " + id;
        var result = ExecuteCmd.runCommand(killCommand);
        if (!result.success()) {
            logger.error("Failed to execute kill command: {}", result.stdErr());
            return false;
        }
        return true;
    }

    public boolean removeContainerCmd(String id) {
        String removeCommand = getDockerCommand() + " rm " + id;
        var result = ExecuteCmd.runCommand(removeCommand);
        if (!result.success()) {
            logger.error("Failed to execute remove command: {}", result.stdErr());
            return false;
        }
        return true;
    }

    public record DockerImage(String repository, String tag, List<Integer> exposedPorts) {
        public boolean isNone() {
            return this.repository.equals("<none>") && this.tag.equals("<none>");
        }

        public String repositoryWithTag() {
            return repository + ":" + tag;
        }
    }

    public record DockerContainer(String id, String image, String status, String name) {
        public boolean isRunning() {
            return status().startsWith("Up");
        }
    }

    public record DockerPortBind(int hostPort, int containerPort, boolean wildcard) {
        public boolean valid() {
            return hostPort > 0 && containerPort > 0;
        }
    }

    private void checkDockerExistence() {
        String dockerCommand = getDockerCommand();
        var result = ExecuteCmd.runCommand(dockerCommand + "info");
        if (result.exitCode() != 0)
            throw new IllegalArgumentException("Could not connect to Docker: " + result.stdErr());
        var version = result.stdOut().lines().filter(it -> it.contains("Server Version:")).findFirst().orElse("Server Version: Unknown");
        logger.info("Connected to Docker: {}", version);
    }

    private String getDockerCommand() {
        return host == null ? "docker " : ("docker -H " + host + " ");
    }

    private List<Map<String, String>> parseDockerLines(String allLines) {
        var lines = allLines.lines().toList();
        // Parse Content similar to: CONTAINER_ID IMAGE COMMAND CREATED STATUS PORTS NAMES
        List<String> headers = Arrays.stream(lines.get(0).split("\\s+\\s+")).toList();
        List<Integer> startIdx = headers.stream().map(h -> lines.get(0).indexOf(h)).toList();
        List<Map<String, String>> entries = new ArrayList<>();
        for (int l = 1; l < lines.size(); l++) {
            String line = lines.get(l);
            Map<String, String> data = new HashMap<>();

            for (int h = 0; h < headers.size() - 1; h++)
                data.put(headers.get(h), line.substring(startIdx.get(h), startIdx.get(h + 1)).trim());
            data.put(headers.get(headers.size() - 1), line.substring(startIdx.get(headers.size() - 1)).trim());

            entries.add(data);
        }

        return entries;
    }

}
