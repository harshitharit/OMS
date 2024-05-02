package com.oms.dtos;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;


@Builder
@Getter
public class OnlineRequestDto {

    @NotNull(message = "CIF Number is required")
    private Long cifNumber;


    @NotNull(message = "Account number is required")
    private Long accountNumber;

    public RequestMessage toRequestMessage() {
        return new RequestMessage().setAccountNumber(accountNumber)
                .setCifNumber(cifNumber);
    }
}
