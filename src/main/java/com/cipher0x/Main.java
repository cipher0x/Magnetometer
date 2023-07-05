package com.cipher0x;

import com.cipher0x.Magnetometer.DataAcquisition;
import com.cipher0x.Magnetometer.DeltaState;
import org.jfree.data.time.TimeSeries;

import javax.vecmath.Vector3d;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

public class Main {
    static FileWriter writer;

    static TimeSeries ts = new TimeSeries("delta");

  public static void main(String[] args) throws IOException, InterruptedException {
   DataAcquisition dataAcquisition = new DataAcquisition();
   DeltaState deltaState = new DeltaState();
      writer = new FileWriter("sampleOut.csv");
      while (true) {
       List<Vector3d> rawVec = dataAcquisition.getAcquisitionVectorBuffer().stream().toList();
       List<BigDecimal> deltas = deltaState.getOneStepDelta(rawVec);
       deltas.forEach(Main::processSample);
      }
  }

  public static void processSample(BigDecimal sample)  {
      StringBuilder strBuilder = new StringBuilder();
      strBuilder.append(sample);
      strBuilder.append(", ");
      strBuilder.append(Instant.now().toEpochMilli());
      strBuilder.append('\n');

      BufferedWriter buffer = new BufferedWriter(writer);
      try {
          buffer.write(strBuilder.toString());
          buffer.flush();
      } catch (IOException e) {
          throw new RuntimeException(e);
      }}
}
