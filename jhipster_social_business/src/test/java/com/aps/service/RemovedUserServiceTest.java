package com.aps.service;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

import com.aps.domain.RemovedUser;
import com.aps.repository.RemovedOrderSummaryRepository;
import com.aps.repository.RemovedUserRepository;
import com.aps.service.mapper.RemovedUserMapper;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class RemovedUserServiceTest {

    @Mock
    private RemovedUserRepository removedUserRepository;

    @Mock
    private RemovedOrderSummaryRepository removedOrderSummaryRepository;

    @Mock
    private RemovedUserMapper removedUserMapper;

    @Mock
    private UserRemovalService userRemovalService;

    private RemovedUserService removedUserService;

    @BeforeEach
    void setUp() {
        removedUserService = new RemovedUserService(
                removedUserRepository,
                removedUserMapper,
                userRemovalService,
                removedOrderSummaryRepository);
    }

    @Test
    void delete_ShouldDeleteRemovedUserAndOrderSummary_WhenOrderHistoryIdIsPresent() {
        Long removedUserId = 1L;
        Long orderHistoryId = 100L;

        RemovedUser removedUser = new RemovedUser();
        removedUser.setId(removedUserId);
        removedUser.setOrderHistoryId(orderHistoryId);

        when(removedUserRepository.findById(removedUserId)).thenReturn(Optional.of(removedUser));

        removedUserService.delete(removedUserId);

        verify(removedUserRepository).findById(removedUserId);
        verify(removedUserRepository).deleteById(removedUserId);
        verify(removedOrderSummaryRepository).deleteById(orderHistoryId);
    }

    @Test
    void delete_ShouldOnlyDeleteRemovedUser_WhenOrderHistoryIdIsNull() {
        Long removedUserId = 2L;

        RemovedUser removedUser = new RemovedUser();
        removedUser.setId(removedUserId);
        removedUser.setOrderHistoryId(null);

        when(removedUserRepository.findById(removedUserId)).thenReturn(Optional.of(removedUser));

        removedUserService.delete(removedUserId);

        verify(removedUserRepository).findById(removedUserId);
        verify(removedUserRepository).deleteById(removedUserId);
        verify(removedOrderSummaryRepository, never()).deleteById(anyLong());
    }
}
