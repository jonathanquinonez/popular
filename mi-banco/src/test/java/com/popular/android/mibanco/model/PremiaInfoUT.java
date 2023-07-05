package com.popular.android.mibanco.model;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;

@RunWith(PowerMockRunner.class)
@PrepareForTest({PremiaContent.class})
public class PremiaInfoUT {

    private PremiaInfo premiaInfo;
    private PremiaContent mockContent;

    @Before
    public void setUp() {
        mockContent = mock(PremiaContent.class);
        premiaInfo = spy(new PremiaInfo());
        premiaInfo.setContent(mockContent);
    }

    @Test
    public void whenGettingPremiaEnabledFlag_givenFlagIsFalse_thenReturnFalse() {

        mockContent.premiaFlag = false;
        assertEquals(false, premiaInfo.isPremiaEnabled());

    }

    @Test
    public void whenGettingPremiaEnabledFlag_givenFlagIsTrue_thenReturnTrue() {

        mockContent.premiaFlag = true;
        assertEquals(true, premiaInfo.isPremiaEnabled());

    }

}
