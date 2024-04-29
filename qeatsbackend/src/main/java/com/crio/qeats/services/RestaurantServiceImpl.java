
/*
 *
 *  * Copyright (c) Crio.Do 2019. All rights reserved
 *
 */

package com.crio.qeats.services;

import com.crio.qeats.dto.Restaurant;
import com.crio.qeats.exchanges.GetRestaurantsRequest;
import com.crio.qeats.exchanges.GetRestaurantsResponse;
import com.crio.qeats.repositoryservices.RestaurantRepositoryService;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import lombok.extern.log4j.Log4j2;
import net.bytebuddy.asm.Advice.Return;
import org.bson.codecs.jsr310.LocalTimeCodec;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Log4j2
public class RestaurantServiceImpl implements RestaurantService {

  private final Double peakHoursServingRadiusInKms = 3.0;
  private final Double normalHoursServingRadiusInKms = 5.0;
  @Autowired
  private RestaurantRepositoryService restaurantRepositoryService;


  // TODO: CRIO_TASK_MODULE_RESTAURANTSAPI - Implement findAllRestaurantsCloseby.
  // Check RestaurantService.java file for the interface contract.
  @Override
  public GetRestaurantsResponse findAllRestaurantsCloseBy(
      GetRestaurantsRequest getRestaurantsRequest, LocalTime currentTime) {
        double latitude = getRestaurantsRequest.getLatitude();
        double longitude = getRestaurantsRequest.getLongitude();
        Double servingRadiusInKms = 
            findServingRadiusByCurrentTime(peakHoursServingRadiusInKms, normalHoursServingRadiusInKms, currentTime);
        List<Restaurant> restaurants = 
            restaurantRepositoryService.findAllRestaurantsCloseBy(latitude, longitude, currentTime, servingRadiusInKms);
    
     return new GetRestaurantsResponse(restaurants);
  }

  private Double findServingRadiusByCurrentTime(Double peakHoursServingRadiusInKms,Double normalHoursServingRadiusInKms,LocalTime currentTime){
     if(isTimeBetween(currentTime, "07:59:59", "10:00:01") 
          || isTimeBetween(currentTime, "12:59:59", "14:00:01")
          || isTimeBetween(currentTime, "18:59:59", "21:00:01"))
            return peakHoursServingRadiusInKms;

     return normalHoursServingRadiusInKms;

  }
  private boolean isTimeBetween(LocalTime currentTime, String startTime, String endTime){
    if(currentTime.isAfter(LocalTime.parse(startTime)) 
      && currentTime.isBefore(LocalTime.parse(endTime)))
        return true;

    return false;
  }
}

