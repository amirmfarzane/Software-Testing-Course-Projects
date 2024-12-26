Feature: Calculate Average Rating
  As a user
  I want to calculate the average rating for a restaurant
  So that I can validate the rating calculations

  Scenario: Calculating Average Rating
    Given a restaurant exists
    And the following reviews are added to the restaurant:
      | user      | food | service | ambiance | overall |
      | client1   | 4    | 5       | 3        | 4       |
      | client2   | 3    | 4       | 5        | 3       |
    When I calculate the average rating
    Then the average food rating should be 3.5
    And the average service rating should be 4.5
    And the average ambiance rating should be 4.0
    And the average overall rating should be 3.5
