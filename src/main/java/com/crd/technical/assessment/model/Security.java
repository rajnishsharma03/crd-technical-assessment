package com.crd.technical.assessment.model;

import lombok.*;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Security {
    String name;
    double targetPercentage;
    double currentPercentage;

    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    double targetVariance;

    double targetVarianceShares;
    double unitPrice;
    int shareBuySell;

    public double getTargetVariance() {
        return currentPercentage - targetPercentage;
    }


}
