Feature: Create Experience

  Scenario: Create a valid experience
    Given I have a valid ExperienceRequestDTO
    When I call the createExperience method
    Then The experience should be saved successfully