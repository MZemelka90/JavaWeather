# WeatherApp

This is a simple weather app, which allows you to enter a city and retrieve the current weather data as well as a history of the weather data for the last 5 days.

## How to use

1. Clone the repository

### Using Maven

2. Run `mvn spring-boot:run` in the root directory of the project
3. Open a web browser and navigate to `http://localhost:8080/`
4. Enter a city in the search bar and click on the search button
5. The current weather data will be displayed, as well as a table with the weather data for the last 5 days

### Using Docker

2. Build the Docker image: `docker build -t weatherapp .`
3. Run the Docker container: `docker run -p 8080:8080 weatherapp`
4. Open a web browser and navigate to `http://localhost:8080/`
5. Enter a city in the search bar and click on the search button
6. The current weather data will be displayed, as well as a table with the weather data for the last 5 days

### Using Docker Compose

2. Run `docker-compose up`
3. Open a web browser and navigate to `http://localhost:8080/`
4. Enter a city in the search bar and click on the search button
5. The current weather data will be displayed, as well as a table with the weather data for the last 5 days

## How it works

The app uses the OpenWeatherMap API to retrieve the current weather data as well as the history of the weather data for the last 5 days.

The app is built using Spring Boot and uses the Thymeleaf template engine for the frontend.

The app also uses the Highcharts library to display the weather data in a graph.

## License

This project is licensed under the MIT License. See the [LICENSE](LICENSE) file for details.