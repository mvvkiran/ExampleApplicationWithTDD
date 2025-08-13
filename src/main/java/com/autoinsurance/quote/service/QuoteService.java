package com.autoinsurance.quote.service;

import com.autoinsurance.quote.dto.QuoteRequestDto;
import com.autoinsurance.quote.dto.QuoteResponseDto;
import java.math.BigDecimal;

public interface QuoteService {
    QuoteResponseDto generateQuote(QuoteRequestDto request);
    QuoteResponseDto getQuoteById(String id);
    BigDecimal calculatePremium(QuoteRequestDto request);
}