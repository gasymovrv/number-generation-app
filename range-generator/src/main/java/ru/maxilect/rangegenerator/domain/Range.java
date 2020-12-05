package ru.maxilect.rangegenerator.domain;

import java.math.BigInteger;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Range {
    private BigInteger start;
    private BigInteger end;
    private BigInteger range;

    public static Range next(Range previous) {
        BigInteger end = previous.getEnd();
        BigInteger nextStart = end.add(BigInteger.ONE);
        BigInteger nextEnd = nextStart.add(previous.range.subtract(BigInteger.ONE));
        return new Range(nextStart, nextEnd, previous.range);
    }
}
