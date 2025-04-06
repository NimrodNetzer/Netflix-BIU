#ifndef THREADPOOLEXECUTOR_H
#define THREADPOOLEXECUTOR_H

#include <thread>
#include <queue>
#include <mutex>
#include <condition_variable>
#include <vector>
#include <memory>
#include "Executor.h"
#define POOL_SIZE 10


class ThreadPoolExecutor : public Executor {
public:
    explicit ThreadPoolExecutor(size_t numThreads);
    void execute(Runnable* task) override;
    ~ThreadPoolExecutor() override;

private:
    // Function executed by each worker thread - processes tasks from the queue
    void workerFunction();
    // Collection of worker threads that process tasks
    std::vector<std::thread> workers_;
    // Queue of pending tasks waiting to be executed
    std::queue<std::unique_ptr<Runnable>> tasks_;
    // Mutex to protect access to the task queue
    std::mutex queue_mutex_;
    // Condition variable for worker threads to wait for new tasks
    std::condition_variable condition_;
    // Flag indicating whether the executor is shutting down
    bool shutdown_;
};

#endif // THREADPOOLEXECUTOR_H