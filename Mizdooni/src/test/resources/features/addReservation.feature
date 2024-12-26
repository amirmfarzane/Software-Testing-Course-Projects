Feature: Managing Reservations
  As a user, I want to manage restaurant reservations
  So that I can track my bookings and plan accordingly

  Scenario: Adding a reservation to a user
    Given a restaurant named "Shila fastfood" with a table for 4 exists
    And a user named "client1" exists
    When the user makes a reservation on "2024-10-25T19:00"
    Then the user should have 1 reservation
    And the reservation number should be 0

  Scenario: Checking if a user has reserved a restaurant
    Given a user named "client1" has a reservation at "Shila fastfood" on "2024-10-15T18:00"
    When the user makes a reservation on "2024-10-25T19:00"
    Then the result of user checking reservations for the restaurant must be true
    Then the result of user checking reservations for another restaurant must be false

  Scenario Outline: Checking if reservation date is current or in the future
    Given a restaurant named "Shila fastfood" with a table for 4 exists
    And a user named "client1" exists
    When the user makes a reservation with an offset of <daysOffset> days from today
    Then the result should be <expectedResult>

    Examples:
      | daysOffset | expectedResult |
      | 3         | false          |
      | -1         | true          |

