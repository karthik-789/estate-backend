package com.realestate.controller;

import com.realestate.dto.PropertyDtos.*;
import com.realestate.entity.*;
import com.realestate.security.AppUserDetails;
import com.realestate.service.PropertyService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.math.BigDecimal;
import java.util.*;

@RestController @RequestMapping("/api/properties") @RequiredArgsConstructor
public class PropertyController {
    private final PropertyService propertyService;

    @GetMapping({"", "/search", "/filter"})
    public Page<PropertyResponse> search(
        @RequestParam(required = false) String query, @RequestParam(required = false) ListingType listingType,
        @RequestParam(required = false) String city, @RequestParam(required = false) String state,
        @RequestParam(required = false) PropertyType propertyType, @RequestParam(required = false) BigDecimal minPrice,
        @RequestParam(required = false) BigDecimal maxPrice, @RequestParam(required = false) Integer bedrooms,
        @RequestParam(required = false) Integer bathrooms, @RequestParam(required = false) Double minArea,
        @RequestParam(required = false) Double maxArea, @RequestParam(required = false) PropertyStatus status,
        @PageableDefault(size = 12, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        return propertyService.search(new PropertyFilter(query, listingType, city, state, propertyType, minPrice, maxPrice,
            bedrooms, bathrooms, minArea, maxArea, status), pageable);
    }

    @GetMapping("/{id}")
    public PropertyResponse property(@PathVariable Long id) { return propertyService.approvedById(id); }

    @GetMapping("/mine/list")
    @PreAuthorize("hasRole('AGENT')")
    public Page<PropertyResponse> mine(@AuthenticationPrincipal AppUserDetails principal, Pageable pageable) {
        return propertyService.mine(principal.id(), pageable);
    }

    @PostMapping
    @PreAuthorize("hasRole('AGENT')")
    public PropertyResponse create(@AuthenticationPrincipal AppUserDetails principal, @Valid @RequestBody PropertyRequest request) {
        return propertyService.create(principal.id(), request);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('AGENT')")
    public PropertyResponse update(@AuthenticationPrincipal AppUserDetails principal, @PathVariable Long id,
                                   @Valid @RequestBody PropertyRequest request) {
        return propertyService.update(principal.id(), id, request);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('AGENT')")
    public Map<String, String> deactivate(@AuthenticationPrincipal AppUserDetails principal, @PathVariable Long id) {
        propertyService.deactivate(principal.id(), id);
        return Map.of("message", "Property deactivated.");
    }

    @PostMapping("/{id}/images")
    @PreAuthorize("hasRole('AGENT')")
    public List<ImageResponse> uploadImages(@AuthenticationPrincipal AppUserDetails principal, @PathVariable Long id,
                                            @RequestParam("files") List<MultipartFile> files) {
        return propertyService.uploadImages(principal.id(), id, files);
    }
}

