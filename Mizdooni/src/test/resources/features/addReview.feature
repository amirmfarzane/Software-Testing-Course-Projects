Feature: Add and Update Reviews for a Restaurant
  As a user
  I want to add and update reviews for a restaurant
  So that duplicate user reviews are updated

  Scenario: Adding a Review
    Given a restaurant exists
    And clients exist
    When the following reviews are added to the restaurant:
      | user | food | service | ambiance | overall |
      | client1 | 4    | 5       | 3        | 4       |
    Then the restaurant should contain only the last review for "client1":
      | user | food | service | ambiance | overall |
      | client1 | 4    | 5       | 3        | 4       |

  Scenario: Updating a Review
    Given a restaurant exists
    And clients exist
    And the following reviews are added to the restaurant:
      | user | food | service | ambiance | overall |
      | client1 | 4    | 5       | 3        | 4       |
    When another review is added with the same user:
      | user | food | service | ambiance | overall |
      | client1 | 1    | 4       | 2        | 3       |
    Then the restaurant should contain only the last review for "client1":
      | user | food | service | ambiance | overall |
      | client1 | 1    | 4       | 2        | 3       |
