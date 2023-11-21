package org.domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.util.Money;
import org.util.VehicleState;
import org.util.VehicleType;

import static org.junit.jupiter.api.Assertions.*;

class VehicleTest {

    Vehicle vehicle;

    @BeforeEach
    public void setup() {
        vehicle = new Vehicle("TOYOTA", "YARIS", 2015, 100000,
                "YMB-6325", VehicleType.Hatchback, new Money(30));
    }

    @Test
    public void equalsVehicleSameReference() {
        assertEquals(vehicle, vehicle);
    }

    @Test
    public void equalsVehicleDifferentReference() {
        Vehicle vehicle1 = new Vehicle("TOYOTA", "YARIS", 2015, 100000,
                "YMB-6325", VehicleType.Hatchback, new Money(30));
        assertEquals(vehicle, vehicle1);
    }

    @Test
    public void notEqualsVehicleDifferentReference() {
        Vehicle vehicle1 = new Vehicle("VOLKSWAGEN", "T-ROC", 2016, 80000,
                "PMT-3013", VehicleType.SUV, new Money(50));

        assertNotEquals(vehicle, vehicle1);
    }

    @Test
    public void testGetManufacturer() {
        assertEquals("TOYOTA", vehicle.getManufacturer());
    }

    @Test
    public void testSetManufacturer() {
        vehicle.setManufacturer("TOYOTA");
        assertEquals("TOYOTA", vehicle.getManufacturer());
    }

    @Test
    public void testGetPlateNumber() {
        assertEquals("YMB-6325", vehicle.getPlateNumber());
    }

    @Test
    public void testSetPlateNumber() {
        vehicle.setPlateNumber("YMB-6325");
        assertEquals("YMB-6325", vehicle.getPlateNumber());
    }

    @Test
   public void testGetVehicleType() {
        assertEquals( VehicleType.Hatchback, vehicle.getVehicleType());
    }

    @Test
    public void testSetVehicleType() {
        vehicle.setVehicleType(VehicleType.Hatchback);
        assertEquals(VehicleType.Hatchback, vehicle.getVehicleType());
    }

    @Test
    public void testGetVehicleState() {
        assertEquals(VehicleState.Available, vehicle.getVehicleState());
    }

    @Test
    public void testSetVehicleState() {
        vehicle.setVehicleState(VehicleState.Available);
        assertEquals(VehicleState.Available, vehicle.getVehicleState());
    }



//Oute auto trexei!!!
    ///// @Test
    /////public void testGetCount() {
    ///// assertEquals("0",vehicle.getCountDamages());
    ///}


   @Test
    public void testSetCount() {
    vehicle.setCountDamages(0);
    assertEquals(0,vehicle.getCountDamages() );
    }

    @Test
        public void testGetFixedCharge() {
        assertEquals(new Money(30), vehicle.getFixedCharge());
    }

        @Test
        public void testSetFixedCharge() {
        vehicle.setFixedCharge(new Money(30));
        assertEquals(new Money(30), vehicle.getFixedCharge());
    }
//den jerw
  ////  @Test
 //   public void testGetCompany() {
 //       assertEquals(//den jerw///, vehicle.getCompany());
//    }


//den jerw
 //   @Test
  //  public void testSetCompany() {
  //  }

    @Test
    public void testGetModel() {
        assertEquals("YARIS", vehicle.getModel());
    }

    @Test
    public void testSetModel() {
        vehicle.setModel("YARIS");
        assertEquals("YARIS", vehicle.getModel());
    }

 // @Test
  //public void testGetYear() {
 //   }

//    @Test
  //  public void testSetYear() {
  //  vehicle.setYear("2015");
 //   assertEquals("2015", vehicle.getYear());
 //   }

    @Test
    void testGetMiles() {
    }

    @Test
    void testSetMiles() {
    }

    @Test
    void testGetCountDamages() {
    }

    @Test
    void testSetCountDamages() {
    }
}