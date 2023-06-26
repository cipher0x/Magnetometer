package com.cipher0x;

import com.cipher0x.Magnetometer.DataAcquisition;
import com.cipher0x.Magnetometer.DeltaState;
import org.jfree.data.time.TimeSeries;

import javax.vecmath.Vector3d;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;

public class Main {
  static TimeSeries ts = new TimeSeries("delta");

  public static void main(String[] args) throws IOException, InterruptedException {
   DataAcquisition dataAcquisition = new DataAcquisition();
   DeltaState deltaState = new DeltaState();

      while (true) {
       List<Vector3d> rawVec = dataAcquisition.getAcquisitionVectorBuffer().stream().toList();
       List<BigDecimal> deltas = deltaState.getOneStepDelta(rawVec);
       deltas.forEach(System.out::println);
      }
  }
}
