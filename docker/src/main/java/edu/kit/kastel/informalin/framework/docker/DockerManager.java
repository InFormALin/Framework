/* Licensed under MIT 2022. */
package edu.kit.kastel.informalin.framework.docker;

import java.time.Duration;
import java.util.List;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.model.Container;
import com.github.dockerjava.api.model.HostConfig;
import com.github.dockerjava.api.model.PortBinding;
import com.github.dockerjava.api.model.Ports;
import com.github.dockerjava.core.DefaultDockerClientConfig;
import com.github.dockerjava.core.DockerClientConfig;
import com.github.dockerjava.core.DockerClientImpl;
import com.github.dockerjava.httpclient5.ApacheDockerHttpClient;
import com.github.dockerjava.transport.DockerHttpClient;

/**
 * This class manages the docker containers used in InFormALin.
 * 
 * @author Dominik Fuchss
 */
public class DockerManager {
    private static final Logger logger = LoggerFactory.getLogger(DockerManager.class);

    private final String namespacePrefix;
    private final DockerClient dockerClient;

    /**
     * Create the manager with a container name prefix.
     * 
     * @param namespacePrefix the container name prefix
     */
    public DockerManager(String namespacePrefix) {
        this(namespacePrefix, true);
    }

    /**
     * Create the manager with a container name prefix and indicator to shut down all existing containers of the
     * namespace.
     *
     * @param namespacePrefix  the container name prefix
     * @param shutdownExisting indicator whether existing containers need to be shut down at the beginning
     */
    public DockerManager(String namespacePrefix, boolean shutdownExisting) {
        this(namespacePrefix, shutdownExisting, DefaultDockerClientConfig.createDefaultConfigBuilder().build());
    }

    private DockerManager(String namespacePrefix, boolean shutdownExisting, DockerClientConfig dockerConfig) {
        this.namespacePrefix = namespacePrefix;
        DockerHttpClient httpClient = new ApacheDockerHttpClient.Builder().dockerHost(dockerConfig.getDockerHost())
                .sslConfig(dockerConfig.getSSLConfig())
                .maxConnections(100)
                .connectionTimeout(Duration.ofSeconds(30))
                .responseTimeout(Duration.ofSeconds(45))
                .build();
        this.dockerClient = DockerClientImpl.getInstance(dockerConfig, httpClient);

        logger.info("Connected to Docker {}", dockerClient.versionCmd().exec());

        if (shutdownExisting)
            shutdownAll();
    }

    public String createContainerByImage(String image, int apiPort) {
        try {
            this.dockerClient.pullImageCmd(image).start().awaitCompletion();
        } catch (InterruptedException e) {
            logger.error(e.getMessage(), e);
            Thread.currentThread().interrupt();
        }

        var config = this.dockerClient.inspectImageCmd(image).exec().getContainerConfig();
        var ports = config == null ? null : config.getExposedPorts();
        if (ports == null || ports.length != 1) {
            throw new IllegalArgumentException("Image does not expose exactly one port");
        }

        var binding = new PortBinding(new Ports.Binding("127.0.0.1", String.valueOf(apiPort)), ports[0]);

        String id;
        try (var create = this.dockerClient.createContainerCmd(image)) {
            id = create.withName(namespacePrefix + UUID.randomUUID()).withHostConfig(HostConfig.newHostConfig().withPortBindings(binding)).exec().getId();
            logger.info("Created container {}", id);
        }
        this.dockerClient.startContainerCmd(id).exec();
        return id;
    }

    public void shutdown(String id) {
        var running = this.dockerClient.listContainersCmd().withShowAll(false).exec();
        if (running.stream().anyMatch(c -> c.getId().equals(id)))
            this.dockerClient.killContainerCmd(id).exec();

        var existing = this.dockerClient.listContainersCmd().withShowAll(true).exec();
        if (existing.stream().anyMatch(c -> c.getId().equals(id)))
            this.dockerClient.removeContainerCmd(id).exec();
    }

    public void shutdownAll() {
        var containers = this.dockerClient.listContainersCmd().withShowAll(true).exec();
        for (var container : containers) {
            var name = container.getNames();
            if (name.length != 0 && name[0].startsWith("/" + namespacePrefix)) {
                logger.info("Shutting down {}", container);
                if (Boolean.TRUE.equals(this.dockerClient.inspectContainerCmd(container.getId()).exec().getState().getRunning()))
                    this.dockerClient.killContainerCmd(container.getId()).exec();
                this.dockerClient.removeContainerCmd(container.getId()).exec();
            }
        }
    }

    public List<String> getContainerIds() {
        var containers = this.dockerClient.listContainersCmd().withShowAll(true).exec();
        return containers.stream().filter(c -> c.getNames().length > 0 && c.getNames()[0].startsWith("/" + namespacePrefix)).map(Container::getId).toList();
    }
}
