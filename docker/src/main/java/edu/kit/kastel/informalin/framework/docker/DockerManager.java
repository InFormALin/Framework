/* Licensed under MIT 2022. */
package edu.kit.kastel.informalin.framework.docker;

import java.io.IOException;
import java.net.ServerSocket;
import java.time.Duration;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.IntStream;

import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.impl.classic.HttpClients;
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

import edu.kit.kastel.informalin.framework.common.Internal;
import io.netty.handler.codec.http.HttpStatusClass;

/**
 * This class manages the docker containers used in InFormALin.
 * 
 * @author Dominik Fuchss
 */
public class DockerManager {
    private static final Logger logger = LoggerFactory.getLogger(DockerManager.class);
    private static int lastPort = 10000;
    private static final int MAX_RETRIES = 5;
    private static final long WAIT_BETWEEN_RETRIES = 3000L;

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

    /**
     * Create a new container and bind the api port to {@code 127.0.0.1:$apiPort}.
     * 
     * @param image the image name (with or without tag)
     * @return the container information
     */
    public ContainerResponse createContainerByImage(String image) {
        return this.createContainerByImage(image, true, true);
    }

    /**
     * Create a new container and bind the api port to {@code 127.0.0.1:$apiPort}.
     * 
     * @param image                    the image name (with or without tag)
     * @param pullOnlyIfImageMissing   indicator whether pull shall only be executed if image is missing
     * @param waitForEndpointAvailable indicator whether the method shall wait until the endpoint is available
     * @return the container information
     */
    public ContainerResponse createContainerByImage(String image, boolean pullOnlyIfImageMissing, boolean waitForEndpointAvailable) {
        boolean pull = true;
        if (pullOnlyIfImageMissing) {
            boolean imagePresent = this.dockerClient.listImagesCmd().exec().stream().anyMatch(it -> Arrays.asList(it.getRepoTags()).contains(image));
            if (imagePresent) {
                logger.debug("Image {} already present. Not pulling!", image);
                pull = false;
            }
        }

        if (pull) {
            try {
                this.dockerClient.pullImageCmd(image).start().awaitCompletion();
            } catch (InterruptedException e) {
                logger.error(e.getMessage(), e);
                Thread.currentThread().interrupt();
            }
        }

        var config = this.dockerClient.inspectImageCmd(image).exec().getContainerConfig();
        var ports = config == null ? null : config.getExposedPorts();
        if (ports == null || ports.length != 1) {
            throw new IllegalArgumentException("Image does not expose exactly one port");
        }

        int apiPort = getNextFreePort();
        var binding = new PortBinding(new Ports.Binding("127.0.0.1", String.valueOf(apiPort)), ports[0]);

        String id;
        try (var create = this.dockerClient.createContainerCmd(image)) {
            id = create.withName(namespacePrefix + UUID.randomUUID()).withHostConfig(HostConfig.newHostConfig().withPortBindings(binding)).exec().getId();
            logger.info("Created container {}", id);
        }
        this.dockerClient.startContainerCmd(id).exec();

        if (waitForEndpointAvailable)
            waitForAPI(apiPort);

        return new ContainerResponse(id, apiPort);
    }

    private void waitForAPI(int apiPort) {
        for (int currentTry = 0; currentTry < MAX_RETRIES; currentTry++) {
            try (var client = HttpClients.createDefault()) {
                var httpResponse = client.execute(new HttpGet("http://127.0.0.1:" + apiPort));
                if (HttpStatusClass.SUCCESS.contains(httpResponse.getCode())) {
                    return;
                }
            } catch (IOException e) {
                logger.debug(e.getMessage(), e);
            }
        }
        throw new IllegalStateException("Container was not ready. Abort.");
    }

    /**
     * Shutdown and cleanup an container by id.
     * 
     * @param id the container id
     */
    public void shutdown(String id) {
        var running = this.dockerClient.listContainersCmd().withShowAll(false).exec();
        if (running.stream().anyMatch(c -> c.getId().equals(id)))
            this.dockerClient.killContainerCmd(id).exec();

        var existing = this.dockerClient.listContainersCmd().withShowAll(true).exec();
        if (existing.stream().anyMatch(c -> c.getId().equals(id)))
            this.dockerClient.removeContainerCmd(id).exec();
    }

    /**
     * Shutdown and cleanup all containers w.r.t. the namespacePrefix
     */
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

    /**
     * Get all container ids managed by this docker manager.
     * 
     * @return all container ids
     */
    public List<String> getContainerIds() {
        var containers = this.dockerClient.listContainersCmd().withShowAll(true).exec();
        return containers.stream().filter(c -> c.getNames().length > 0 && c.getNames()[0].startsWith("/" + namespacePrefix)).map(Container::getId).toList();
    }

    /**
     * Returns a free local unprivileged port.
     * 
     * @return the free port
     * @throws IllegalStateException if no port is available
     */
    public static synchronized int getNextFreePort() {
        var ports = IntStream.range(lastPort + 1, 20000).toArray();
        for (int port : ports) {
            try {
                ServerSocket socket = new ServerSocket(port);
                socket.close();
                lastPort = port;
                return port;
            } catch (IOException ex) {
                logger.debug("Port {} is not free.", port);
            }
        }
        // if the program gets here, no port in the range was found
        throw new IllegalStateException("no free port found");
    }

    /**
     * Get access to the internal docker client for more actions.
     * 
     * @return the internal docker client
     */
    @Internal
    public DockerClient getDockerClient() {
        return dockerClient;
    }
}
