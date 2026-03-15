package vn.id.nonglam.kltn.kltn.dto.response.search;

import java.util.UUID;

public record SearchHotelResponse(UUID id, String name, String thumbnail, String street, String ward,
                                  String province, double latitude, double longitude,
                                  String description, int viewCount, double avgRating, int totalComment,
                                  double minPrice, double maxPrice) {}
