package com.raiffeisendgtl.ApiSocks.services;

import com.raiffeisendgtl.ApiSocks.components.*;
import com.raiffeisendgtl.ApiSocks.components.exception.*;
import com.raiffeisendgtl.ApiSocks.entities.Socks;
import com.raiffeisendgtl.ApiSocks.repositories.SocksRepository;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.*;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class SocksServiceTests {

    private SocksService socksService;

    @Mock
    private SocksRepository socksRepository;

    @Mock
    private Socks socks;

    @Mock
    private FinderOperation finderOperation;

    @BeforeEach
    public void setUp() {
        socksRepository = mock(SocksRepository.class);
        socksService = new SocksServiceImpl(socksRepository);
    }

    @Test
    public void findSocks() {
        socks = mock(Socks.class);
        Optional<Socks> result;

        when(socksRepository.findByColorAndCottonPart(socks.getColor(), socks.getCottonPart()))
                .thenReturn(Optional.of(socks));

        result = socksService.find(socks);

        assertEquals(socks, result.get());
    }

    @Test
    public void findSocksIfThrowError() {
        socks = mock(Socks.class);
        SocksErrorCode expectedErrorCode = SocksErrorCode.SERVER_CRASH;

        when(socksRepository.findByColorAndCottonPart(socks.getColor(), socks.getCottonPart()))
                .thenThrow(new SocksException(SocksErrorCode.SERVER_CRASH));

        SocksException exception = assertThrows(SocksException.class, () -> {
            socksService.find(socks);
        });

        assertEquals(expectedErrorCode, exception.getError());
    }

    @Test
    public void saveSocks() {
        socks = mock(Socks.class);

        socksService.save(socks);

        verify(socksRepository, times(1)).save(socks);
    }

    @Test
    public void saveSocksIfThrowError() {
        socks = mock(Socks.class);
        SocksErrorCode expectedErrorCode = SocksErrorCode.SERVER_CRASH;

        doThrow(new SocksException(SocksErrorCode.SERVER_CRASH))
                .when(socksRepository)
                .save(socks);

        SocksException exception = assertThrows(SocksException.class, () -> {
            socksService.save(socks);
        });

        assertEquals(expectedErrorCode, exception.getError());

        verify(socksRepository, times(1)).save(socks);
    }

    @Test
    public void incomeNewSocks() {
        socks = mock(Socks.class);

        when(socksRepository.findByColorAndCottonPart(socks.getColor(), socks.getCottonPart()))
                .thenReturn(Optional.empty());

        socksService.income(socks);

        verify(socks, never()).addQuantity(socks.getQuantity());
        verify(socksRepository, times(1)).save(socks);
    }

    @Test
    public void incomeExistingSocks() {
        socks = mock(Socks.class);

        when(socksRepository.findByColorAndCottonPart(socks.getColor(), socks.getCottonPart()))
                .thenReturn(Optional.of(socks));

        socksService.income(socks);

        verify(socks, times(1)).addQuantity(socks.getQuantity());
        verify(socksRepository, times(1)).save(socks);
    }

    @Test
    public void outcomeExistingSocks() {
        socks = mock(Socks.class);

        when(socksRepository.findByColorAndCottonPart(socks.getColor(), socks.getCottonPart()))
                .thenReturn(Optional.of(socks));

        socksService.outcome(socks);

        verify(socks, times(1)).subtractQuantity(socks.getQuantity());
        verify(socksRepository, times(1)).save(socks);
    }

    @Test
    public void outcomeIfNotFoundSocks() {
        socks = mock(Socks.class);
        SocksErrorCode expectedResult = SocksErrorCode.INCORRECT_PARAMS;

        when(socksRepository.findByColorAndCottonPart(socks.getColor(), socks.getCottonPart()))
                .thenReturn(Optional.empty());

        SocksException exception = assertThrows(SocksException.class, () -> {
            socksService.outcome(socks);
        });

        assertEquals(expectedResult, exception.getError());

        verify(socks, never()).subtractQuantity(socks.getQuantity());
        verify(socksRepository, never()).save(socks);
    }

    @Test
    public void getCountSocksIsCorrect() {
        String color = "green";
        finderOperation = mock(FinderLessThan.class);
        int cottonPart = 33;
        Integer expectedCount = 3;

        when(finderOperation.findCount(color, cottonPart)).thenReturn(expectedCount);

        Integer actualCount = socksService.getCountSocks(color, finderOperation, cottonPart);

        assertEquals(expectedCount, actualCount);
    }

    @Test
    public void getCountSocksIfThrowExceptionIsServerCrash() {
        String color = "black";
        finderOperation = mock(FinderEqual.class);
        int cottonPart = 50;
        SocksErrorCode resultErrorCode = SocksErrorCode.SERVER_CRASH;

        doThrow(new SocksException(SocksErrorCode.SERVER_CRASH)).when(finderOperation)
                .findCount(color, cottonPart);

        try {
            socksService.getCountSocks(color, finderOperation, cottonPart);
        } catch (SocksException e) {
            assertEquals(resultErrorCode, e.getError());
        }
    }

    @Test
    public void getCountSocksIfThrowExceptionIsIncorrectParams() {
        String color = "orange";
        finderOperation = mock(FinderEqual.class);
        int cottonPart = 95;
        SocksErrorCode resultErrorCode = SocksErrorCode.INCORRECT_PARAMS;

        when(finderOperation.findCount(color, cottonPart))
                        .thenReturn(null);

        SocksException exception = assertThrows(SocksException.class, () -> {
            socksService.getCountSocks(color, finderOperation, cottonPart);
        });

        assertEquals(resultErrorCode, exception.getError());
    }

}
