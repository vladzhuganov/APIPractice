Feature: Users endpoint CRUD testing


  Scenario: Create new user
    Given Create new user
    And Verify that user was created
    Then Delete user by id

  Scenario: Get user by id
    Given Create new user
    And Get user by id
    Then Delete user by id

  Scenario: Update user by id
    Given Create new user
    And Get user by id
    Then Update user
    And Verify user is updated
    Then Delete user by id

  Scenario: Delete user
    Given Create new user
    And Verify that user was created
    Then Delete user by id
    And Verify user does not exist
