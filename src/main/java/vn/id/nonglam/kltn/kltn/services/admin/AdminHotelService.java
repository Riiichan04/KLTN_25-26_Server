package vn.id.nonglam.kltn.kltn.services.admin;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import vn.id.nonglam.kltn.kltn.common.enums.UserRole;
import vn.id.nonglam.kltn.kltn.dto.request.admin.*;
import vn.id.nonglam.kltn.kltn.dto.response.admin.AdminCommentResponse;
import vn.id.nonglam.kltn.kltn.dto.response.admin.AdminHotelResponse;
import vn.id.nonglam.kltn.kltn.dto.response.admin.ChangeActiveHotelResponse;
import vn.id.nonglam.kltn.kltn.dto.response.admin.ChangeActiveRoomTypeResponse;
import vn.id.nonglam.kltn.kltn.dto.response.admin.GetAdminSnapshotHotelResponse;
import vn.id.nonglam.kltn.kltn.models.auth.UserPrinciple;
import vn.id.nonglam.kltn.kltn.models.hotel.*;
import vn.id.nonglam.kltn.kltn.models.user.User;
import vn.id.nonglam.kltn.kltn.repositories.*;
import vn.id.nonglam.kltn.kltn.security.SecurityUtil;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminHotelService {
    private final HotelRepository hotelRepository;
    private final UserRepository userRepository;
    private final HotelUtilityRepository hotelUtilityRepository;
    private final RoomUtilityRepository roomUtilityRepository;
    private final RoomTypeRepository roomTypeRepository;
    private final CommentRepository commentRepository;
    private final CommentReviewAspectRepository commentReviewAspectRepository;

    public Page<GetAdminSnapshotHotelResponse> getHotels(String keyword, Pageable pageable) {
        UserPrinciple user = SecurityUtil.getCurrentUser().orElseThrow(() ->
                new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not logged in"));
        if(user.role().equals(UserRole.ADMIN)) {
            return hotelRepository.getHotels(keyword, pageable);
        } else if(user.role().equals(UserRole.OWNER)) {
            return hotelRepository.getHotelsByOwnerId(keyword, user.id(), pageable);
        }
        return null;
    }

    public AdminHotelResponse getHotelById(UUID id) {
        UserPrinciple user = SecurityUtil.getCurrentUser().orElseThrow(() ->
                new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not logged in"));
        if(user.role().equals(UserRole.ADMIN)) {
            return hotelRepository.findById(id)
                    .map(this::mapperHotelToGetHotelResponse)
                    .orElseThrow(() -> new NoSuchElementException("Không tìm thấy khách sạn với ID: " + id));
        } else if(user.role().equals(UserRole.OWNER)) {
            return hotelRepository.findByIdAndOwner_IdAndIsActiveTrue(id, user.id())
                    .map(this::mapperHotelToGetHotelResponse)
                    .orElseThrow(() -> new NoSuchElementException("Không tìm thấy khách sạn với ID: " + id));
        }
        return null;
    }

    private AdminHotelResponse mapperHotelToGetHotelResponse(Hotel h) {
        Set<AdminHotelResponse.ImageResponse> images = h.getImages().stream().map(i -> new AdminHotelResponse.ImageResponse(i.getId(), i.getPath())).collect(Collectors.toSet());
        User o = h.getOwner();
        AdminHotelResponse.OwnerResponse owner = null;
        if (o != null) {
            owner = new AdminHotelResponse.OwnerResponse(o.getId(), o.getUsername(), o.getAvatarUrl(), o.getEmail(), o.getPhone(), o.getGender(), o.isActive());
        }
        Address a = h.getAddress();
        AdminHotelResponse.AddressResponse address = null;
        if(a != null) {
            address = new AdminHotelResponse.AddressResponse(a.getId(), a.getStreet(), a.getWard(), a.getProvince(),
                    a.getPostalCode(), a.getLatitude(), a.getLongitude(), a.isActive(), a.getCreatedAt(), a.getUpdatedAt());
        }

        Set<AdminHotelResponse.RoomTypeResponse> roomTypes = h.getRoomTypes().stream().map(rt -> mapperRoomTypeToResponse(rt)).collect(Collectors.toSet());

        Set<AdminHotelResponse.HotelUtilityResponse> utilities = h.getUtilities().stream().map(u -> new AdminHotelResponse.HotelUtilityResponse(u.getId(), u.getName(), u.getIconCode())).collect(Collectors.toSet());

        Set<AdminHotelResponse.HotelRegulationResponse> regulations = h.getRegulations().stream().map(r -> new AdminHotelResponse.HotelRegulationResponse(r.getId(), r.getName(), r.getDescription())).collect(Collectors.toSet());

        return new AdminHotelResponse(
                h.getId(), h.getName(), owner, images, h.getDescription(),
                h.getThumbnail(), address, roomTypes, h.getHotline(), utilities, h.getViewCount(), h.isActive(), h.getStatus(), regulations,
                h.getCreatedAt(), h.getUpdatedAt()
        );
    }

    private AdminHotelResponse.RoomTypeResponse mapperRoomTypeToResponse(RoomType rt) {
        boolean isOwner = SecurityUtil.getCurrentUser()
                .map(user -> user.role().equals(UserRole.OWNER))
                .orElse(false);

        return new AdminHotelResponse.RoomTypeResponse(
                rt.getId(),
                rt.getName(),
                rt.getDescription(),
                rt.getCapacity(),
                rt.getPrice(),
                rt.getUtilities().stream()
                        .filter(u -> !isOwner || u.isActive())
                        .map(u -> new AdminHotelResponse.RoomUtilityResponse(u.getId(), u.getName(), u.getIconCode()))
                        .collect(Collectors.toSet()),
                rt.getImages().stream()
                        .map(i -> new AdminHotelResponse.RoomTypeImageResponse(i.getId(), i.getPath()))
                        .collect(Collectors.toSet()),
                rt.getRoomDetails().stream()
                        .filter(rd -> !isOwner || rd.isActive()) // <-- Lọc ở đây
                        .map(rd -> new AdminHotelResponse.RoomDetailResponse(rd.getId(), rd.getRoomCode(), rd.isActive()))
                        .collect(Collectors.toSet()),
                rt.isActive()
        );
    }

    @Transactional
    public ChangeActiveHotelResponse changeActive(ChangeActiveHotelRequest request) {
        UserPrinciple user = SecurityUtil.getCurrentUser().orElseThrow(() ->
                new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not logged in"));

        Hotel hotel = null;

        if(user.role().equals(UserRole.ADMIN)) {
            hotel = hotelRepository.findById(request.id())
                    .orElseThrow(() -> new NoSuchElementException("Hotel not found with ID: " + request.id()));
        } else if(user.role().equals(UserRole.OWNER)) {
            hotel = hotelRepository.findByIdAndOwner_IdAndIsActiveTrue(request.id(), user.id())
                    .orElseThrow(() -> new NoSuchElementException("Hotel not found with ID: " + request.id()));
        }

        if(hotel == null) return new ChangeActiveHotelResponse(false, request.active());

        hotel.setActive(request.active());

        return new ChangeActiveHotelResponse(true, request.active());
    }

    @Transactional
    public ChangeActiveRoomTypeResponse changeActiveRoomType(ChangeActiveRoomTypeRequest request) {
        UserPrinciple user = SecurityUtil.getCurrentUser().orElseThrow(() ->
                new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not logged in"));

        RoomType roomType = null;

        if(user.role().equals(UserRole.ADMIN)) {
            roomType = roomTypeRepository.findById(request.id())
                    .orElseThrow(() -> new NoSuchElementException("Room Type not found with ID: " + request.id()));
        } else if(user.role().equals(UserRole.OWNER)) {
            roomType = roomTypeRepository.findByIdAndHotel_Owner_IdAndIsActiveTrue(request.id(), user.id())
                    .orElseThrow(() -> new NoSuchElementException("Room Type not found with ID: " + request.id()));
        }

        if(roomType == null) return new ChangeActiveRoomTypeResponse(false, request.active());

        roomType.setActive(request.active());

        return new ChangeActiveRoomTypeResponse(true, request.active());
    }

    @Transactional
    public AdminHotelResponse.OwnerResponse changeOwner(ChangeOwnerHotelRequest request) {
        Hotel h = hotelRepository.getReferenceById(request.hotelId());
        User u = userRepository.findByUsername(request.username()).orElseThrow(() ->
                new NoSuchElementException("Không tìm thấy username " + request.username()));

        h.setOwner(u);
        hotelRepository.save(h);

        return new AdminHotelResponse.OwnerResponse(u.getId(), u.getUsername(), u.getAvatarUrl(), u.getEmail(), u.getPhone(), u.getGender(), u.isActive());
    }

    public List<AdminHotelResponse.HotelUtilityResponse> getHotelUtilities() {
        return hotelUtilityRepository.findAllByIsActive(true).stream().map(u ->
                new AdminHotelResponse.HotelUtilityResponse(u.getId(), u.getName(), u.getIconCode())).collect(Collectors.toList());
    }

    public List<AdminHotelResponse.RoomUtilityResponse> getRoomUtilities() {
        return roomUtilityRepository.findAllByIsActive(true).stream().map(u ->
                new AdminHotelResponse.RoomUtilityResponse(u.getId(), u.getName(), u.getIconCode())).collect(Collectors.toList());
    }

    @Transactional
    public AdminHotelResponse updateHotelInfo(UpdateHotelAdminRequest request) {
        UserPrinciple user = SecurityUtil.getCurrentUser().orElseThrow(() ->
                new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not logged in"));
        Hotel hotel = null;
        if(user.role().equals(UserRole.ADMIN)) {
            hotel = hotelRepository.findById(request.id()).orElseThrow(() ->
                    new NoSuchElementException("Không tìm thấy khách sạn với ID: " + request.id()));
        }  else if(user.role().equals(UserRole.OWNER)) {
            hotel = hotelRepository.findByIdAndOwner_IdAndIsActiveTrue(request.id(), user.id()).orElseThrow(() ->
                    new NoSuchElementException("Không tìm thấy khách sạn với ID: " + request.id()));
        }
        if (hotel == null) throw new NoSuchElementException("Không tìm thấy khách sạn với ID: " + request.id());

        hotel.setName(request.name());
        hotel.setDescription(request.description());
        hotel.setThumbnail(request.thumbnail());
        hotel.setHotline(request.hotline());
        hotel.setStatus(request.status());

        Address address = hotel.getAddress();
        if (address == null) {
            address = new Address();
        }
        address.setStreet(request.street());
        address.setWard(request.ward());
        address.setProvince(request.province());
        address.setPostalCode(request.postalCode());
        address.setLatitude(request.latitude());
        address.setLongitude(request.longitude());
        hotel.setAddress(address);

        Set<HotelImage> currentImages = hotel.getImages();
        if (currentImages == null) {
            currentImages = new HashSet<>();
        }

        Map<String, HotelImage> existingImagesMap = currentImages.stream()
                .collect(Collectors.toMap(HotelImage::getPath, image -> image, (existing, replacement) -> existing));

        Set<HotelImage> updatedImages = new HashSet<>();
        for (String url : request.images()) {
            if (existingImagesMap.containsKey(url)) {
                updatedImages.add(existingImagesMap.get(url));
            } else {
                HotelImage newImage = new HotelImage();
                newImage.setPath(url);
                newImage.setHotel(hotel);
                updatedImages.add(newImage);
            }
        }
        hotel.getImages().clear();
        hotel.getImages().addAll(updatedImages);

        Set<HotelUtility> utilities = request.hotelUtilities().stream()
                .map(hotelUtilityRepository::getReferenceById)
                .collect(Collectors.toSet());
        hotel.setUtilities(utilities);

        Set<HotelRegulation> currentRegulations = hotel.getRegulations();
        if (currentRegulations == null) {
            currentRegulations = new HashSet<>();
        }

        Map<UUID, HotelRegulation> existingRegsMap = currentRegulations.stream()
                .filter(r -> r.getId() != null)
                .collect(Collectors.toMap(HotelRegulation::getId, r -> r));

        Set<HotelRegulation> updatedRegulations = new HashSet<>();
        for (var regReq : request.hotelRegulations()) {
            HotelRegulation regulation;

            if (regReq.id() != null && existingRegsMap.containsKey(regReq.id())) {
                regulation = existingRegsMap.get(regReq.id());
            } else {
                regulation = new HotelRegulation();
                regulation.setHotel(hotel);
            }

            regulation.setName(regReq.name());
            regulation.setDescription(regReq.description());
            updatedRegulations.add(regulation);
        }

        hotel.getRegulations().clear();
        hotel.getRegulations().addAll(updatedRegulations);

        Hotel savedProduct = hotelRepository.save(hotel);
        if(user.role().equals(UserRole.ADMIN)) {
            return mapperHotelToGetHotelResponse(savedProduct);
        } else {
            return hotelRepository.findByIdAndOwner_IdAndIsActiveTrue(savedProduct.getId(), user.id())
                    .map(this::mapperHotelToGetHotelResponse)
                    .orElseThrow(() -> new NoSuchElementException("Không tìm thấy khách sạn với ID: " + savedProduct.getId()));
        }
    }

    @Transactional
    public AdminHotelResponse.RoomTypeResponse updateRoomType(UpdateRoomTypeAdminRequest req) {
        RoomType roomType = null;
        UserPrinciple user = SecurityUtil.getCurrentUser().orElseThrow(() ->
                new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not logged in"));

        if (req.id() != null) {
            if (user.role().equals(UserRole.ADMIN)) {
                roomType = roomTypeRepository.findById(req.id()).orElseThrow(() ->
                        new NoSuchElementException("Không tìm thấy hạng phòng với ID: " + req.id()));
            } else if (user.role().equals(UserRole.OWNER)) {
                roomType = roomTypeRepository.findByIdAndHotel_Owner_IdAndIsActiveTrue(req.id(), user.id()).orElseThrow(() ->
                        new NoSuchElementException("Không tìm thấy hạng phòng với ID: " + req.id()));
            }
        } else {
            roomType = new RoomType();
            Hotel hotel = null;
            if (user.role().equals(UserRole.ADMIN)) {
                hotel = hotelRepository.findById(req.hotelId()).orElseThrow(() ->
                        new NoSuchElementException("Không tìm thấy khách sạn với ID: " + req.hotelId()));
            } else if (user.role().equals(UserRole.OWNER)) {
                hotel = hotelRepository.findByIdAndOwner_IdAndIsActiveTrue(req.hotelId(), user.id()).orElseThrow(() ->
                        new NoSuchElementException("Không tìm thấy khách sạn với ID: " + req.hotelId()));
            }

            if (hotel == null) new NoSuchElementException("Không tìm thấy khách sạn với ID: " + req.hotelId());

            roomType.setHotel(hotel);
        }

        roomType.setName(req.name());
        roomType.setDescription(req.description());
        roomType.setPrice(req.price());
        roomType.setCapacity(req.capacity());

        Set<RoomTypeImage> currentImages = roomType.getImages();
        if (currentImages == null) {
            currentImages = new HashSet<>();
        }

        Map<String, RoomTypeImage> existingImagesMap = currentImages.stream()
                .collect(Collectors.toMap(RoomTypeImage::getPath, image -> image, (existing, replacement) -> existing));

        Set<RoomTypeImage> updatedImages = new HashSet<>();
        if (req.images() != null) {
            for (String url : req.images()) {
                if (existingImagesMap.containsKey(url)) {
                    updatedImages.add(existingImagesMap.get(url));
                } else {
                    RoomTypeImage newImage = new RoomTypeImage();
                    newImage.setPath(url);
                    newImage.setRoomType(roomType);
                    updatedImages.add(newImage);
                }
            }
        }

        if (roomType.getImages() != null) {
            roomType.getImages().clear();
            roomType.getImages().addAll(updatedImages);
        } else {
            roomType.setImages(updatedImages);
        }

        if (req.roomUtilities() != null) {
            Set<RoomUtility> utilities = req.roomUtilities().stream()
                    .map(roomUtilityRepository::getReferenceById)
                    .collect(Collectors.toSet());
            roomType.setUtilities(utilities);
        }

        Set<RoomDetail> currentDetails = roomType.getRoomDetails();
        if (currentDetails == null) {
            currentDetails = new HashSet<>();
        }

        Map<UUID, RoomDetail> existingDetailsMap = currentDetails.stream()
                .filter(d -> d.getId() != null)
                .collect(Collectors.toMap(RoomDetail::getId, d -> d));

        Set<RoomDetail> updatedDetails = new HashSet<>();
        if (req.roomDetails() != null) {
            for (var detailReq : req.roomDetails()) {
                RoomDetail detail;

                if (detailReq.id() != null && existingDetailsMap.containsKey(detailReq.id())) {
                    detail = existingDetailsMap.get(detailReq.id());
                } else {
                    detail = new RoomDetail();
                    detail.setRoomType(roomType);
                }

                detail.setRoomCode(detailReq.roomCode());
                detail.setActive(detailReq.isActive());
                updatedDetails.add(detail);
            }
        }

        if (roomType.getRoomDetails() != null) {
            roomType.getRoomDetails().clear();
            roomType.getRoomDetails().addAll(updatedDetails);
        } else {
            roomType.setRoomDetails(updatedDetails);
        }

        RoomType savedRoomType = roomTypeRepository.save(roomType);

        return mapperRoomTypeToResponse(savedRoomType);
    }

    @Transactional
    public Boolean createHotel(AddHotelRequest req) {
        UserPrinciple user = SecurityUtil.getCurrentUser().orElseThrow(() ->
                new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not logged in"));

        Hotel hotel = Hotel.builder()
                .name(req.name())
                .description(req.description())
                .address(new Address())
                .isActive(true)
                .build();
        if(user.role().equals(UserRole.OWNER)) {
            User owner = userRepository.findUserByIdAndIsActive(user.id(), true);
            if (owner == null) return false;
            hotel.setOwner(owner);
        }
        hotel = hotelRepository.save(hotel);
        return  hotel.getId() != null;
    }

    @Transactional(readOnly = true)
    public Page<AdminCommentResponse> getCommentsByHotelId(UUID hotelId, Pageable pageable) {
        UserPrinciple user = SecurityUtil.getCurrentUser().orElseThrow(() ->
                new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not logged in"));
        Page<AdminCommentResponse> result = null;
        if(user.role().equals(UserRole.ADMIN)) {
            result = commentRepository.findByHotel_Id(hotelId, pageable).map(this::mapperCommentToResponse);
        } else if (user.role().equals(UserRole.OWNER)) {
            result = commentRepository.findByHotel_IdAndHotel_Owner_IdAndIsActiveTrue(hotelId, user.id(), pageable).map(this::mapperCommentToResponse);
        }
        return result;
    }

    private AdminCommentResponse mapperCommentToResponse(Comment c) {
        List<AdminCommentResponse.SentimentAspectResponse> aspects = commentReviewAspectRepository.findByComment_Id(c.getId()).stream().map(s ->
                AdminCommentResponse.SentimentAspectResponse.builder()
                        .id(s.getId())
                        .aspect(s.getAspect())
                        .sentiment(s.getSentiment())
                        .opinionWord(s.getOpinionWord())
                        .createdAt(s.getCreatedAt())
                        .updatedAt(s.getUpdatedAt())
                        .build()).collect(Collectors.toList());

        return AdminCommentResponse.builder()
                .id(c.getId())
                .email(c.getUser().getEmail())
                .avatarUrl(c.getUser().getAvatarUrl())
                .username(c.getUser().getUsername())
                .rating(c.getRating())
                .content(c.getContent())
                .sentimentAspects(aspects)
                .isActive(c.isActive())
                .createdAt(c.getCreatedAt())
                .updatedAt(c.getUpdatedAt())
                .build();
    }

    @Transactional
    public Boolean changeActiveComment(UUID id, boolean active) {
        Comment c = commentRepository.findById(id).orElseThrow(() -> new NoSuchElementException("Comment not found"));
        c.setActive(active);

        commentRepository.save(c);
        return true;
    }

}
