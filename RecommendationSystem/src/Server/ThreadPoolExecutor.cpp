#include "ThreadPoolExecutor.h"
#include <iostream>

//initialize the pool
ThreadPoolExecutor::ThreadPoolExecutor(size_t numThreads) : shutdown_(false) {
    for (size_t i = 0; i < numThreads; ++i) {
        workers_.emplace_back(&ThreadPoolExecutor::workerFunction, this);
    }
}

// execute a task - add it to the queue
void ThreadPoolExecutor::execute(Runnable* task) {
    {
        std::lock_guard<std::mutex> lock(queue_mutex_);
        tasks_.push(std::unique_ptr<Runnable>(task));
    }
    condition_.notify_one();
}

// deconstruct the pool
ThreadPoolExecutor::~ThreadPoolExecutor() {
    {
        std::lock_guard<std::mutex> lock(queue_mutex_);
        shutdown_ = true;
    }
    condition_.notify_all();
    
    for (std::thread& worker : workers_) {
        if (worker.joinable()) {
            worker.join();
        }
    }
}

void ThreadPoolExecutor::workerFunction() {
    while (true) {
        std::unique_ptr<Runnable> task;
        {
            // Acquire lock on the task queue
            std::unique_lock<std::mutex> lock(queue_mutex_);

            // Wait until either:
            // 1. There's a task to process (tasks_ not empty) OR
            // 2. Executor is shutting down
            condition_.wait(lock, [this]() {
                return shutdown_ || !tasks_.empty();
            });

            // If we're shutting down and no tasks left, exit thread
            if (shutdown_ && tasks_.empty()) {
                return;
            }

            // Get task from front of queue and remove it
            task = std::move(tasks_.front());
            tasks_.pop();
        }   // Lock is released here

        try {
            task->run();
            std::cout << "Task executed" << std::endl;
        } catch (const std::exception& e) {
            std::cerr << "Exception in thread: " << e.what() << std::endl;
        }
    }
}