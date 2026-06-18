package vn.id.nonglam.kltn.kltn.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import vn.id.nonglam.kltn.kltn.dto.response.admin.GetSnapshotHotelResponse;
import vn.id.nonglam.kltn.kltn.dto.response.search.SearchHotelResponse;
import vn.id.nonglam.kltn.kltn.models.hotel.Hotel;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface HotelRepository extends JpaRepository<Hotel, UUID> {
    @Query("""
    SELECT new vn.id.nonglam.kltn.kltn.dto.response.search.SearchHotelResponse(
        h.id, h.name, h.thumbnail, h.address.street, h.address.ward, h.address.province, 
        h.address.latitude, h.address.longitude, h.description, h.viewCount,
        round(COALESCE(avg(c.rating), 0.0), 2), COALESCE(size(h.comments), 0), 
        CAST(COALESCE(min(r.price), 0.0) AS BigDecimal),
        CAST(COALESCE(max(r.price), 0.0) AS BigDecimal)
    )
    FROM Hotel h
    LEFT JOIN h.comments c
    LEFT JOIN h.roomTypes r
    WHERE h.isActive = true AND h.address.isActive = true 
    AND (:keyWord  = null or h.name like %:keyWord%)
    AND (:minLatitude is null or h.address.latitude BETWEEN :minLatitude AND :maxLatitude)
    AND (:minLongitude is null or h.address.longitude BETWEEN :minLongitude AND :maxLongitude)
    GROUP BY h.id, h.name, h.thumbnail, h.address, h.description, h.viewCount
    HAVING (COALESCE(avg(c.rating), 0) >= :minRating or COALESCE(avg(c.rating), 0) = 0) 
    AND COALESCE(min(r.price), 0.0) BETWEEN :minPrice AND :maxPrice
    ORDER BY COALESCE(avg(c.rating), 0.0) desc 
""")
    Page<SearchHotelResponse> findHotels(String keyWord,
                                         Double minLongitude, Double minLatitude,
                                         Double maxLongitude, Double maxLatitude,
                                         Integer minRating, BigDecimal minPrice, BigDecimal maxPrice, Pageable pageable
    );

    @EntityGraph(attributePaths = {"address", "roomTypes", "roomTypes.images", "roomTypes.utilities", "utilities"})
    @Query("""
    SELECT h FROM Hotel h 
    LEFT JOIN h.roomTypes r LEFT JOIN r.utilities ru
    LEFT JOIN h.utilities u
    WHERE h.isActive = true   
    AND h.address.isActive = true AND r.isActive = true 
    AND ru.isActive = true AND u.isActive = true 
    """)
    Hotel getHotelById(UUID id);

    @Query("""
    SELECT 
    h.id AS id, 
    h.name AS name, 
    h.thumbnail AS thumbnail, 
    h.address.street AS street, 
    h.address.ward AS ward, 
    h.address.province AS province, 
    h.viewCount AS viewCount, 
    h.isActive AS isActive
    FROM Hotel h
""")
    Page<GetSnapshotHotelResponse> getHotels(Pageable pageable);

    @EntityGraph(attributePaths = {"address", "roomTypes", "images", "utilities", "regulations"})
    Optional<Hotel> findById(UUID id);

    List<Hotel> findTop5ByIsActiveTrueOrderByViewCountDesc();
}
