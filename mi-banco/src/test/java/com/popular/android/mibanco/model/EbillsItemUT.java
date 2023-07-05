package com.popular.android.mibanco.model;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import com.popular.android.mibanco.App;
import com.popular.android.mibanco.R;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.Date;


@RunWith(PowerMockRunner.class)
@PrepareForTest({App.class,R.class,EBillsItem.class})
public class EbillsItemUT {


    private EBillsItem eBillsItem;

    @Mock
    private App app;

    @Before
    public void setup() {

        MockitoAnnotations.initMocks(this);
        PowerMockito.mockStatic(R.class);
        PowerMockito.mockStatic(App.class);
        PowerMockito.when(App.getApplicationInstance()).thenReturn(app);
        PowerMockito.when(app.getDateFormat()).thenReturn("dd/mm/yyyy");
    }

    @Test
    public void whengetDueDate_Thenvoid_ReturnDate(){

        eBillsItem = new EBillsItem();
        eBillsItem.setDueDate("20091120");
        Date date=  eBillsItem.getDueDate();
        assertNotNull(date);
    }

    @Test
    public void whengetDueDate_Thenvoid_ReturnNull(){

        eBillsItem = new EBillsItem();
        eBillsItem.setDueDate("");
        Date date=  eBillsItem.getDueDate();
        assertNull(date);
    }
}
