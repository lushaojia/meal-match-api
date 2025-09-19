package dev.coms4156.project.teamproject;

import static org.junit.jupiter.api.Assertions.assertEquals;

import dev.coms4156.project.teamproject.model.ClientProfile;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;

/**
 * Tests the constructor of ClientProfile.
 */
@SpringBootTest
@ContextConfiguration
public class ClientProfileUnitTests {

  @Test
  void testGetClientId() {
    ClientProfile clientProfile = new ClientProfile();
    int expectedClientId = 0;
    assertEquals(expectedClientId, clientProfile.getClientId());
  }
}
