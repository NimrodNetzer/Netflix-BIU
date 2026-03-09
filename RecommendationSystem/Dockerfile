FROM gcc:latest

RUN apt-get update && apt-get install -y cmake

COPY . /usr/src/RecommendationSystem/

# build the tests
WORKDIR /usr/src/RecommendationSystem/test
RUN mkdir build
WORKDIR /usr/src/RecommendationSystem/test/build
RUN cmake .. && make

#build the main app
WORKDIR /usr/src/RecommendationSystem
RUN mkdir build
WORKDIR /usr/src/RecommendationSystem/build
RUN cmake .. && make

# change the working diretory to the main folder
WORKDIR /usr/src/RecommendationSystem/

EXPOSE 8080

# Run RecommendationSystem
CMD ["./build/RecommendationSystem"]