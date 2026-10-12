package com.realestate.config;

import com.realestate.entity.*;
import com.realestate.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;

@Component @RequiredArgsConstructor
public class DemoDataInitializer implements CommandLineRunner {
    private final UserRepository userRepository;
    private final AgentProfileRepository agentProfileRepository;
    private final PropertyRepository propertyRepository;
    private final PropertyImageRepository propertyImageRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.seed.demo-password:}") private String demoPassword;

    @Override @Transactional
    public void run(String... args) {
        if (demoPassword == null || demoPassword.isBlank()) return;
        createUser("Administrator", "admin@realestate.com", Role.ADMIN);
        User agent = createUser("Priya Sharma", "agent@realestate.com", Role.AGENT);
        createUser("Rahul Verma", "user@realestate.com", Role.USER);
        if (propertyRepository.count() == 0) {
            sample(agent, "Skyline Residences", PropertyType.APARTMENT, ListingType.SALE, "Hyderabad", "Telangana", "Gachibowli", 12500000, 3, 3, 1650);
            sample(agent, "Garden View Villa", PropertyType.VILLA, ListingType.SALE, "Bengaluru", "Karnataka", "Whitefield", 24000000, 4, 4, 3200);
            sample(agent, "Seaside Business Hub", PropertyType.COMMERCIAL, ListingType.RENT, "Mumbai", "Maharashtra", "Andheri East", 180000, null, 2, 2100);
            sample(agent, "Riverside Family Home", PropertyType.HOUSE, ListingType.SALE, "Pune", "Maharashtra", "Kalyani Nagar", 9800000, 3, 2, 1450);
            sample(agent, "Orchid Enclave Plot", PropertyType.PLOT, ListingType.SALE, "Chennai", "Tamil Nadu", "OMR", 6200000, null, null, 2400);
            sample(agent, "Capital Workspaces", PropertyType.OFFICE, ListingType.RENT, "Delhi", "Delhi", "Saket", 95000, null, 2, 1250);
        }
        propertyRepository.findAll().forEach(this::addDemoImageIfMissing);
    }

    private User createUser(String name, String email, Role role) {
        return userRepository.findByEmailIgnoreCase(email).orElseGet(() -> {
            User user = new User();
            user.setFullName(name);
            user.setEmail(email);
            user.setRole(role);
            user.setPassword(passwordEncoder.encode(demoPassword));
            User saved = userRepository.save(user);
            if (role == Role.AGENT) {
                AgentProfile profile = new AgentProfile();
                profile.setUser(saved);
                profile.setAgencyName("Prime Estates");
                profile.setExperience(8);
                profile.setVerificationStatus(VerificationStatus.APPROVED);
                agentProfileRepository.save(profile);
            }
            return saved;
        });
    }

    private void sample(User owner, String title, PropertyType type, ListingType listing, String city, String state,
                        String address, long price, Integer beds, Integer baths, double area) {
        Property property = new Property();
        property.setOwner(owner);
        property.setTitle(title);
        property.setDescription("A carefully selected " + type.name().toLowerCase().replace('_', ' ') +
            " with excellent access to local amenities, transit, and everyday essentials.");
        property.setPropertyType(type);
        property.setListingType(listing);
        property.setPrice(BigDecimal.valueOf(price));
        property.setBedrooms(beds);
        property.setBathrooms(baths);
        property.setArea(area);
        property.setAddress(address);
        property.setCity(city);
        property.setState(state);
        property.setStatus(PropertyStatus.AVAILABLE);
        property.setVerificationStatus(VerificationStatus.APPROVED);
        propertyRepository.save(property);
    }

    private void addDemoImageIfMissing(Property property) {
        if (!property.getImages().isEmpty()) return;

        PropertyImage image = new PropertyImage();
        image.setProperty(property);
        image.setImageUrl(imagePathFor(property.getPropertyType()));
        propertyImageRepository.save(image);
    }

    private String imagePathFor(PropertyType propertyType) {
        return switch (propertyType) {
            case VILLA, HOUSE -> "/property-images/bengaluru-villa.png";
            case PLOT -> "/property-images/hyderabad-plot.png";
            case COMMERCIAL, OFFICE, SHOP -> "/property-images/mumbai-office.png";
            case APARTMENT -> "/property-images/hyderabad-apartment.png";
        };
    }
}

