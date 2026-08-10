package vn.id.nonglam.kltn.kltn.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import vn.id.nonglam.kltn.kltn.dto.response.admin.GetAdminSnapshotHotelResponse;
import vn.id.nonglam.kltn.kltn.dto.response.search.CardHotelResponse;
import vn.id.nonglam.kltn.kltn.models.hotel.Hotel;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface HotelRepository extends JpaRepository<Hotel, UUID> {
    @Query(value = """
    SELECT
        h.id AS id,
        h.name AS name,
        h.thumbnail AS thumbnail,
        h.address.street AS street,
        h.address.ward AS ward,
        h.address.province AS province,
        h.address.latitude AS latitude,
        h.address.longitude AS longitude,
        h.description AS description,
        h.viewCount AS viewCount,
        ROUND(COALESCE(AVG(c.rating), 0.0), 2) AS avgRating,
        SIZE(h.comments) AS totalComment,
        COALESCE(MIN(r.price), 0.0) AS minPrice,
        COALESCE(MAX(r.price), 0.0) AS maxPrice
    FROM Hotel h
    LEFT JOIN h.comments c
    LEFT JOIN h.roomTypes r
    WHERE h.isActive = true AND h.address.isActive = true 
    AND (:keyWord IS NULL OR h.name LIKE %:keyWord%)
    AND (:minLatitude IS NULL OR h.address.latitude BETWEEN :minLatitude AND :maxLatitude)
    AND (:minLongitude IS NULL OR h.address.longitude BETWEEN :minLongitude AND :maxLongitude)
    GROUP BY h.id, h.name, h.thumbnail, h.address.id, h.address.street, h.address.ward, h.address.province, h.address.latitude, h.address.longitude, h.description, h.viewCount
    HAVING (COALESCE(avg(c.rating), 0) >= :minRating OR COALESCE(avg(c.rating), 0) = 0) 
    AND COALESCE(min(r.price), 0.0) BETWEEN :minPrice AND :maxPrice
""",
            countQuery = """
    SELECT COUNT(DISTINCT h.id)
    FROM Hotel h
    LEFT JOIN h.comments c
    LEFT JOIN h.roomTypes r
    WHERE h.isActive = true AND h.address.isActive = true 
    AND (:keyWord IS NULL OR h.name LIKE %:keyWord%)
    AND (:minLatitude IS NULL OR h.address.latitude BETWEEN :minLatitude AND :maxLatitude)
    AND (:minLongitude IS NULL OR h.address.longitude BETWEEN :minLongitude AND :maxLongitude)
    GROUP BY h.id
    HAVING (COALESCE(avg(c.rating), 0) >= :minRating OR COALESCE(avg(c.rating), 0) = 0) 
    AND COALESCE(min(r.price), 0.0) BETWEEN :minPrice AND :maxPrice
""")
    Page<CardHotelResponse> findHotels(
            @Param("keyWord") String keyWord,
            @Param("minLongitude") Double minLongitude,
            @Param("minLatitude") Double minLatitude,
            @Param("maxLongitude") Double maxLongitude,
            @Param("maxLatitude") Double maxLatitude,
            @Param("minRating") Integer minRating,
            @Param("minPrice") BigDecimal minPrice,
            @Param("maxPrice") BigDecimal maxPrice,
            Pageable pageable
    );

    @Query(value = """
    SELECT
        h.id AS id,
        h.name AS name,
        h.thumbnail AS thumbnail,
        h.address.street AS street,
        h.address.ward AS ward,
        h.address.province AS province,
        h.address.latitude AS latitude,
        h.address.longitude AS longitude,
        h.description AS description,
        h.viewCount AS viewCount,
        ROUND(COALESCE(AVG(c.rating), 0.0), 2) AS avgRating,
        SIZE(h.comments) AS totalComment,
        COALESCE(MIN(r.price), 0.0) AS minPrice,
        COALESCE(MAX(r.price), 0.0) AS maxPrice
    FROM Hotel h
    LEFT JOIN h.comments c
    LEFT JOIN h.roomTypes r
    WHERE h.id = :hotelId 
      AND h.isActive = true 
      AND h.address.isActive = true 
    GROUP BY h.id, h.name, h.thumbnail, h.address.id, h.address.street, 
             h.address.ward, h.address.province, h.address.latitude, 
             h.address.longitude, h.description, h.viewCount
""")
    Optional<CardHotelResponse> findHotelSnapshotById(@Param("hotelId") UUID hotelId);

//    @EntityGraph(attributePaths = {"address", "roomTypes", "roomTypes.images", "roomTypes.utilities", "utilities"})
//    @Query("""
//    SELECT DISTINCT h FROM Hotel h
//    LEFT JOIN FETCH h.address a
//    LEFT JOIN FETCH h.utilities u
//    LEFT JOIN FETCH h.roomTypes r
//    LEFT JOIN FETCH r.utilities ru
//    LEFT JOIN FETCH r.roomDetails t
//    WHERE h.id = :id
//      AND h.isActive = true
//      AND (a IS NULL OR a.isActive = true)
//      AND (u IS NULL OR u.isActive = true)
//      AND (r IS NULL OR r.isActive = true)
//      AND (ru IS NULL OR ru.isActive = true)
//      AND (t IS NULL OR t.isActive = true)
//""")
//    Hotel getHotelById(@Param("id") UUID id);

    @EntityGraph(attributePaths = {"address", "roomTypes", "roomTypes.images", "roomTypes.utilities", "utilities"})
    @Query("SELECT h FROM Hotel h WHERE h.id = :id AND h.isActive = true")
    Hotel getHotelById(@Param("id") UUID id);

    @Query("""
    SELECT 
    h.id AS id, 
    h.name AS name, 
    h.thumbnail AS thumbnail, 
    a.street AS street, 
    a.ward AS ward, 
    a.province AS province, 
    h.viewCount AS viewCount, 
    h.isActive AS isActive
    FROM Hotel h 
    LEFT JOIN h.address a 
    WHERE h.name ILIKE concat('%', :keyword, '%') 
""")
    Page<GetAdminSnapshotHotelResponse> getHotels(@Param("keyword") String keyword, Pageable pageable);

    @EntityGraph(attributePaths = {"address", "roomTypes", "images", "utilities", "regulations"})
    Optional<Hotel> findById(UUID id);

    //For home page
    List<Hotel> findTop5ByIsActiveTrueOrderByViewCountDesc();
    List<Hotel> findTop5ByIsActiveTrueOrderByCreatedAtDesc();

    List<Hotel> findByIsActiveTrue();

    long countByIsActiveTrue();

    @Query("""
    SELECT 
    h.id AS id, 
    h.name AS name, 
    h.thumbnail AS thumbnail, 
    a.street AS street, 
    a.ward AS ward, 
    a.province AS province, 
    h.viewCount AS viewCount, 
    h.isActive AS isActive
    FROM Hotel h 
    JOIN h.owner o
    LEFT JOIN h.address a 
    WHERE h.isActive = true and h.name ILIKE concat('%', :keyword, '%') and o.id = :ownerId
""")
    Page<GetAdminSnapshotHotelResponse> getHotelsByOwnerId(@Param("keyword") String keyword, @Param("ownerId") UUID ownerId, Pageable pageable);

    @Query("""
    SELECT DISTINCT h FROM Hotel h 
    LEFT JOIN FETCH h.address a 
    LEFT JOIN FETCH h.utilities u 
    LEFT JOIN FETCH h.roomTypes r 
    LEFT JOIN FETCH r.utilities ru 
    LEFT JOIN FETCH r.roomDetails t 
    WHERE h.id = :id 
      AND h.owner.id = :ownerId 
      AND h.isActive = true 
      AND (a IS NULL OR a.isActive = true)
      AND (u IS NULL OR u.isActive = true)
      AND (r IS NULL OR r.isActive = true)
      AND (ru IS NULL OR ru.isActive = true)
      AND (t IS NULL OR t.isActive = true)
""")
    Optional<Hotel> findByIdAndOwner_IdAndIsActiveTrue(@Param("id") UUID id, @Param("ownerId") UUID ownerId);

}
