package world.deslauriers;

import io.dekorate.docker.annotation.DockerBuild;
import io.dekorate.kubernetes.annotation.*;
import io.micronaut.runtime.Micronaut;

@KubernetesApplication(
    name = "auth",
    serviceType = ServiceType.NodePort,
    expose = true,
    host = "localhost",
    replicas = 3,
    envVars = {
        @Env(name = "JDBC_URL", configmap = "jdbc-config", value = "url"),
        @Env(name = "JDBC_DRIVER", configmap = "jdbc-config", value = "driver"),
        @Env(name = "JDBC_DIALECT", configmap = "jdbc-config", value = "dialect"),
        @Env(name = "JDBC_USERNAME", secret = "jdbc", value = "username"),
        @Env(name = "JDBC_PASSWORD", secret = "jdbc", value = "password"),
        @Env(name = "JWT_GENERATOR_SIGNATURE_SECRET", secret = "jwt", value = "signature-pw")},
    imagePullPolicy = ImagePullPolicy.Always,
    labels = @Label(key = "app", value = "auth"),
    ports = @Port(name = "https", containerPort = 8443)
)
@DockerBuild(group = "tdeslauriers", name = "auth")
public class Application {



    public static void main(String[] args) {
        Micronaut.run(Application.class, args);
    }
}
