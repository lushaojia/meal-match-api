package dev.coms4156.project.teamproject;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

/**
 * Class contains all the startup logic for the application.
 *
 * <p>DO NOT MODIFY ANYTHING BELOW THIS POINT WITH REGARD TO FUNCTIONALITY
 * YOU MAY MAKE STYLE/REFACTOR MODIFICATIONS AS NEEDED
 */
@SpringBootApplication
@EntityScan(basePackages = {"dev.coms4156.project.teamproject.model"})
@EnableJpaRepositories(basePackages = {"dev.coms4156.project.teamproject.repository"})
public class App implements CommandLineRunner {

  /**
   * The main launcher for the service all it does is make a call to the overridden run method.
   *
   * @param args A {@code String[]} of any potential runtime arguments
   */
  public static void main(String[] args) {
    SpringApplication.run(App.class, args);
  }

  /**
   * This contains all the setup logic, it will mainly be focused on loading up and creating an
   * instance of the database based off a saved file or will create a fresh database if the file is
   * not present.
   *
   * @param args A {@code String[]} of any potential runtime args
   */
  @Override
  public void run(String[] args) {
    System.out.println("Start up");
  }
}
