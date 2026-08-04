package vn.id.nonglam.kltn.kltn.services.admin;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.id.nonglam.kltn.kltn.repositories.HotelRepository;
import vn.id.nonglam.kltn.kltn.repositories.OrderRepository;
import vn.id.nonglam.kltn.kltn.repositories.UserRepository;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class DashboardService {
    private final HotelRepository hotelRepository;
    private final UserRepository userRepository;
    private final OrderRepository orderRepository;

    @Transactional(readOnly = true)
    public Map<String, Object> getDashboard() {
        Map<String, Object> result = new HashMap<>();
        result.put("totalHotels", hotelRepository.count());
        result.put("totalHotelsActive", hotelRepository.countByIsActiveTrue());
        result.put("totalUsers", userRepository.count());
        result.put("totalUsersActive", userRepository.countByIsActiveTrue());
        result.put("totalOrders", orderRepository.count());
        return result;
    }
}
