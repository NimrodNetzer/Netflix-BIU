#include "post.h"
#include <sstream>
#include <iostream>
#include "Data/DataManager.h"
#include "add.h"

// Constructor definition for the 'post' class
post::post(IMenu& menu) : m_menu(menu) {}

void post::execute(std::string s) {
    // Create an 'add' object
    add addCommand;
    std::cout << s << std::endl;
    // Validate the input string
    try {
        addCommand.validateString(s); // This handles validation of the input
    } catch (const std::invalid_argument& e) {
        std::cout << e.what() << std::endl;
        m_menu.displayBadRequestError("400 Bad Request");
        return;
    }

    // Extract the user ID from the input string
    DataManager& data_manager = DataManager::getInstance();
    std::istringstream iss(s);
    int userID;
    iss >> userID;

    // Check if the user already exists
    if (data_manager.hasUser(userID)) {
        std::cout << "User already exists." << std::endl;
        m_menu.displayLogicError("404 Not Found");
        return;
    }

    // Use a try-catch block only around the 'execute' method of add
    try {
        // Execute the add command (e.g., create user and add movies)
        addCommand.execute(s);
    } catch (const std::exception& e) {
        // Catch any exceptions that may arise during the execution of the add command
        std::cout << "Error executing add command: " << e.what() << std::endl;
        m_menu.displayBadRequestError("400 Bad Request");
        return;
    }

    // Success response
    m_menu.displayMessage("201 Created");
}
