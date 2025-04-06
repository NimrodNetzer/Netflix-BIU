//
// Created by ido on 12/18/24.
//

#ifndef CLIENTHANDLER_H
#define CLIENTHANDLER_H
#include <map>

#include "Runnable.h"
#include "Interfaces/ICommand.h"
#include "Menus/IMenu.h"


class ClientHandler : public Runnable{
public:
    ClientHandler(int clientSocket) : clientSocket(clientSocket){}
    void run() override;

private:
    int clientSocket;
    std::map<std::string, ICommand*> initializeCommands(IMenu& menu);
};



#endif //CLIENTHANDLER_H
