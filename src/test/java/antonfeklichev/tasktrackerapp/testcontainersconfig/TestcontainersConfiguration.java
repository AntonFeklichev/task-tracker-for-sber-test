package antonfeklichev.tasktrackerapp.testcontainersconfig;

import com.zaxxer.hikari.HikariDataSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.utility.DockerImageName;

import javax.sql.DataSource;

@Configuration
public class TestcontainersConfiguration {


    @Bean(initMethod = "start", destroyMethod = "stop")
    public PostgreSQLContainer<?> postgresTestContainerBuild() {
        return new PostgreSQLContainer<>(DockerImageName.parse("postgres:14-alpine"))
                .withExposedPorts(5432);
    }

    @Bean
    public DataSource dataSource(PostgreSQLContainer<?> postgresTestContainerBuild) {
        var hikariDataSource = new HikariDataSource();
        hikariDataSource.setJdbcUrl(postgresTestContainerBuild.getJdbcUrl());
        hikariDataSource.setUsername(postgresTestContainerBuild.getUsername());
        hikariDataSource.setPassword(postgresTestContainerBuild.getPassword());
        return hikariDataSource;
    }


}
