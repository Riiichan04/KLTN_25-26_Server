package vn.id.nonglam.kltn.kltn.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import vn.id.nonglam.kltn.kltn.dto.response.HomePageStatisticResponse;
import vn.id.nonglam.kltn.kltn.models.hotel.Address;

import java.util.List;
import java.util.UUID;

@Repository
public interface AddressRepository extends JpaRepository<Address, UUID> {
    //Will be added order by booking completed count by province
    @Query("""
                select new vn.id.nonglam.kltn.kltn.dto.response.HomePageStatisticResponse$ProvinceStatistic(
                    a.province,
                    count(a.province),
                    a.postalCode,
                    MAX(h.thumbnail)
                )
                from Address a join Hotel h on a.id = h.address.id
                group by a.province, a.postalCode
                order by count(a.province) DESC
            """)
    List<HomePageStatisticResponse.ProvinceStatistic> statisticAddress();
}
