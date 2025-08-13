package com.autoinsurance.quote.service;

import com.autoinsurance.quote.dto.QuoteRequestDto;
import java.math.BigDecimal;

public interface RiskCalculationService {
    BigDecimal calculateBasePremium(QuoteRequestDto request);
}