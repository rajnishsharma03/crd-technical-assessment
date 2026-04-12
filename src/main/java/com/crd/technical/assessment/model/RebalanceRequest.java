package com.crd.technical.assessment.model;

import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class RebalanceRequest {
    double totalAsset;
    @Builder.Default
    List<Security> securities = new ArrayList<>();
}
