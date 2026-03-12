package vn.id.nonglam.kltn.kltn.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import vn.id.nonglam.kltn.kltn.dto.SearchDTO;
import vn.id.nonglam.kltn.kltn.models.hotel.Hotel;

import java.util.UUID;

@Repository
public interface HotelRepository extends JpaRepository<Hotel, UUID> {
    @Query("""
    SELECT new vn.id.nonglam.kltn.kltn.dto.SearchDTO.SearchHotelDTO(
        h.id, h.name, h.thumbnail, h.address, h.description, h.viewCount,
        COALESCE(avg(c.rating), 0.0), COALESCE(size(h.comments), 0), 
        COALESCE(min(r.price), 0.0), COALESCE(max(r.price), 0.0))
    FROM Hotel h
    LEFT JOIN h.comments c
    LEFT JOIN h.roomTypes r
    WHERE h.isActive = true
    AND (:keyWord  = null or h.name like %:keyWord%)
    AND (:minLatitude is null or h.address.latitude BETWEEN :minLatitude AND :maxLatitude)
    AND (:minLongitude is null or h.address.longitude BETWEEN :minLongitude AND :maxLongitude)
    GROUP BY h.id, h.name, h.thumbnail, h.address, h.description, h.viewCount
    HAVING (COALESCE(avg(c.rating), 0) >= :minRating or COALESCE(avg(c.rating), 0) = 0) 
    AND COALESCE(min(r.price), 0.0) BETWEEN :minPrice AND :maxPrice
""")
    Page<SearchDTO.SearchHotelDTO> findHotelsByExtentAddress(String keyWord,
            Double minLongitude, Double minLatitude,
            Double maxLongitude, Double maxLatitude,
            Integer minRating, Double minPrice, Double maxPrice, Pageable pageable
    );
}
