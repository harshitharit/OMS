package com.oms.model;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import static org.junit.jupiter.api.Assertions.assertEquals;

class CustomerPreferenceTest {

    @ParameterizedTest
    @CsvSource({
            "1, 1234567890, 6789016789, 'Sundar ', 'email', 'Pink city Gujjar area,Near to Google head office,Jaipur'",
            "2, 69696969696, 4321012345, 'Drake', 'sms', 'Dental hospital, Pakistan'",
            "3, 5267362786286, 4363636367261, 'Captain Kashmiri', 'whatsapp', 'Unknown place,Kashmir'"
    })
    void testCustomerPreferenceGettersAndSetters(
            String id, long accountNumber, long cifNumber, String name,
            String preferredchannel, String preferredAddress
    ) {
        CustomerPreference customerPreference = new CustomerPreference();

        // Test setId and getId
        customerPreference.setId(id);
        assertEquals(id, customerPreference.getId());

        // Test setAccountNumber and getAccountNumber
        customerPreference.setAccountNumber(accountNumber);
        assertEquals(accountNumber, customerPreference.getAccountNumber());

        // Test setCifNumber and getCifNumber
        customerPreference.setCifNumber(cifNumber);
        assertEquals(cifNumber, customerPreference.getCifNumber());

        // Test setName and getName
        customerPreference.setName(name);
        assertEquals(name, customerPreference.getName());

        // Test setPreferredchannel and getPreferredchannel
        customerPreference.setPreferredchannel(preferredchannel);
        assertEquals(preferredchannel, customerPreference.getPreferredchannel());

        // Test setPreferredAddress and getPreferredAddress
        customerPreference.setPreferredAddress(preferredAddress);
        assertEquals(preferredAddress, customerPreference.getPreferredAddress());
    }
}
