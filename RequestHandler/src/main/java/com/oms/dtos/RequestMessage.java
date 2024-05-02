package com.oms.dtos;

import lombok.Data;
import lombok.experimental.Accessors;

@Accessors(chain = true)
@Data
public class RequestMessage {
    private Long cifNumber;
    private Long accountNumber;
}
