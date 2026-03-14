package ru.tigerbank;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.tigerbank.domain.Category;
import ru.tigerbank.domain.Operation;
import ru.tigerbank.domain.OperationType;
import ru.tigerbank.repository.CategoryRepository;
import ru.tigerbank.repository.OperationRepository;
import ru.tigerbank.service.analytics.AnalyticsServiceImpl;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AnalyticsServiceImplTest {

    @Mock
    private OperationRepository operationRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private AnalyticsServiceImpl analyticsService;

    private final Integer accountId = 1001;
    private final LocalDate from = LocalDate.of(2025, 1, 1);
    private final LocalDate to = LocalDate.of(2025, 3, 31);

    private final Category food = new Category(1, OperationType.EXPENSE, "Еда");
    private final Category salary = new Category(2, OperationType.INCOME, "Зарплата");

    @BeforeEach
    void setUp() {

    }

    @Test
    @DisplayName("getNetBalanceByAccount должен правильно считать разницу доходы - расходы")
    void shouldCalculateCorrectNetBalance() {
        List<Operation> operations = Arrays.asList(
                createOp(1, accountId, 1, 15000, OperationType.INCOME,  LocalDate.of(2025, 2, 10)),
                createOp(2, accountId, 1, 3200,  OperationType.EXPENSE, LocalDate.of(2025, 2, 15)),
                createOp(3, accountId, 2, 48000, OperationType.INCOME,  LocalDate.of(2025, 3, 5)),
                createOp(4, accountId, 1, 890,   OperationType.EXPENSE, LocalDate.of(2025, 3, 20))
        );

        when(operationRepository.findByBankAccountId(accountId)).thenReturn(operations);

        double net = analyticsService.getNetBalanceByAccount(accountId, from, to);

        assertEquals(15000 + 48000 - 3200 - 890, net);
    }

    @Test
    @DisplayName("getNetBalanceByAccount должен возвращать 0 если нет операций в периоде")
    void shouldReturnZeroWhenNoOperationsInPeriod() {
        when(operationRepository.findByBankAccountId(accountId))
                .thenReturn(List.of(
                        createOp(5, accountId, 1, 1000, OperationType.EXPENSE, LocalDate.of(2024, 12, 30))
                ));

        double net = analyticsService.getNetBalanceByAccount(accountId, from, to);

        assertEquals(0, net);
    }

    private Operation createOp(Integer id, Integer accountId, Integer categoryId,
                               double amount, OperationType type, LocalDate date) {
        return new Operation(id, type, accountId, categoryId, amount, date, "");
    }
}