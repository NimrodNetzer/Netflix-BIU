cmake_minimum_required(VERSION 3.11)
project(RecommendationSystem)

set(CMAKE_CXX_STANDARD 20)

# Explicitly list all the source and header files
set(SOURCES
        src/Commands/add.cpp
        src/Core/app.cpp
        src/Menus/ConsoleMenu.cpp
        src/Data/FilePersistence.cpp
        src/Commands/help.cpp
        src/Interfaces/ICommand.cpp
        src/Menus/IMenu.cpp
        src/Core/main.cpp
        src/objects/Movie.cpp
        src/Commands/recommend.cpp
        src/Commands/recommendAlgo.cpp
        src/objects/User.cpp
        src/Data/DataManager.cpp
        src/Commands/post.cpp
)

set(HEADERS
        src/Commands/add.h
        src/Core/app.h
        src/Menus/ConsoleMenu.h
        src/Data/FilePersistence.h
        src/Commands/help.h
        src/Interfaces/ICommand.h
        src/Menus/IMenu.h
        src/Interfaces/IPersistence.h
        src/objects/Movie.h
        src/Commands/recommend.h
        src/Commands/recommendAlgo.h
        src/objects/User.h
        src/Menus/SocketMenu.cpp
        src/Commands/post.cpp
        src/Commands/patch.cpp
        src/Commands/Delete.cpp
        src/Server/Server.cpp
        src/Menus/SocketMenu.h
        src/Server/Server.h
        src/Server/Runnable.h
        src/Server/Executor.h
        src/Server/SimpleExecutor.h
        src/Server/SimpleExecutor.cpp
        src/Server/ClientHandler.h
        src/Server/ClientHandler.cpp
        src/Server/ThreadPoolExecutor.cpp
)

# Add the executable
add_executable(RecommendationSystem ${SOURCES} ${HEADERS})
#target_link_libraries(RecommendationSystem ws2_32)

# Include the src directory for header file lookups
target_include_directories(RecommendationSystem PRIVATE ${CMAKE_SOURCE_DIR}/src)
