package ru.maxilect.rangegenerator.domain;

import java.math.BigInteger;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Range {
    private BigInteger start;
    private BigInteger end;

    public static Range next(Range previous) {
        BigInteger start = previous.getStart();
        BigInteger end = previous.getEnd();
        BigInteger range = end.subtract(start);
        BigInteger nextStart = end.add(BigInteger.ONE);
        BigInteger nextEnd = nextStart.add(range);
        return new Range(nextStart, nextEnd);
    }
}
