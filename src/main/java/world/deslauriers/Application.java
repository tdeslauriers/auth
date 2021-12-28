package world.deslauriers;

import io.dekorate.docker.annotation.DockerBuild;
import io.dekorate.kubernetes.annotation.*;
import io.micronaut.runtime.Micronaut;

@KubernetesApplication(
    name = "auth",
    replicas = 3,
    envVars = {
        @Env(name = "JDBC_URL", configmap = "jdbc-config", value = "config.url"),
        @Env(name = "JDBC_DRIVER", configmap = "jdbc-config", value = "config.driver"),
        @Env(name = "JDBC_DIALECT", configmap = "jdbc-config", value = "config.dialect"),
        @Env(name = "JDBC_USERNAME", secret = "jdbc", value = "username"),
        @Env(name = "JDBC_PASSWORD", secret = "jdbc", value = "password")},
    imagePullPolicy = ImagePullPolicy.Always,
    labels = @Label(key = "app", value = "auth"),
    ports = @Port(name = "http", containerPort = 8443),
    livenessProbe = @Probe(httpActionPath = "/health/liveness", initialDelaySeconds = 5, timeoutSeconds = 10, failureThreshold = 15),
    readinessProbe = @Probe(httpActionPath = "/health/readiness", initialDelaySeconds = 5, timeoutSeconds = 10, failureThreshold = 15)
)
@DockerBuild(group = "tdeslauriers", name = "auth")
public class Application {



    public static void main(String[] args) {
        Micronaut.run(Application.class, args);
    }
}
