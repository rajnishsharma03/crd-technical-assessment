package com.crd.technical.assessment.service;

import com.crd.technical.assessment.model.Security;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RebalanceService {
    public Map<String, Security> rebalance(double totalAsset, List<Security> securities) {
        Map<String, Security> result = new HashMap<>();

        for (Security sec : securities) {
            double currentValue = totalAsset * (sec.getCurrentPercentage() / 100);
            double targetValue = totalAsset * (sec.getTargetPercentage() / 100);
            double diff = targetValue - currentValue;
            double targetVarianceValue = totalAsset * (sec.getTargetVariance()/ 100);
            int targetVarianceShares =  (int) Math.round(targetVarianceValue / sec.getUnitPrice());
            sec.setTargetVarianceShares(targetVarianceShares);
            int shares = (int) Math.round(diff / sec.getUnitPrice());
            sec.setShareBuySell(shares);
            result.put(sec.getName(), sec);
        }
        return result;
    }
}
