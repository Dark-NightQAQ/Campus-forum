package org.example.finishedbackend.service;

import org.example.finishedbackend.entity.VO.response.WeatherVO;

public interface WeatherService {
    WeatherVO fetchWeather(double longitude, double latitude);
}
