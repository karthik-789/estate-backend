package com.realestate.service;

import com.realestate.dto.PropertyDtos.*;
import com.realestate.entity.*;
import com.realestate.repository.*;
import com.realestate.service.impl.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import java.math.BigDecimal;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PropertyServiceImplTest {
    @Mock PropertyRepository properties;
    @Mock PropertyImageRepository images;
    @Mock UserRepository users;
    @Mock FileStorageService files;
    @InjectMocks PropertyServiceImpl service;

    @Test
    void newPropertyStartsPendingVerification() {
        User agent = new User(); agent.setId(4L); agent.setRole(Role.AGENT); agent.setFullName("Agent");
        when(users.findById(4L)).thenReturn(java.util.Optional.of(agent));
        when(properties.save(any(Property.class))).thenAnswer(call -> {
            Property property = call.getArgument(0); property.setId(21L); return property;
        });

        PropertyResponse response = service.create(4L, new PropertyRequest("City home", "Well connected home",
            PropertyType.HOUSE, ListingType.SALE, BigDecimal.valueOf(5000000), 2, 2, 1200d,
            "Street", "Pune", "Maharashtra", "411001", null, null));

        assertThat(response.verificationStatus()).isEqualTo(VerificationStatus.PENDING);
        assertThat(response.status()).isEqualTo(PropertyStatus.AVAILABLE);
        verify(properties).save(any(Property.class));
    }
}

