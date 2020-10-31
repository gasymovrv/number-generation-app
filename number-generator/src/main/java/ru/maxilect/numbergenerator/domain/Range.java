package ru.maxilect.numbergenerator.domain;

import java.math.BigInteger;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Range {
    private final BigInteger start;
    private final BigInteger end;
}
