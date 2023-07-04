package com.cipher0x.Magnetometer;

import lombok.NoArgsConstructor;

import javax.vecmath.Vector3d;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
public class DeltaState {
    private Vector3d currentVec;
    private Vector3d nextVec;
    private boolean isLastSizeOne = false;

    public List<BigDecimal> getOneStepDelta(List<Vector3d> inVec) {
        List<Vector3d> localInVec = new ArrayList<>(List.copyOf(inVec));
        List<BigDecimal> deltas= new ArrayList<>();
        if(localInVec.isEmpty()) {
            return deltas;
        }
        if(isLastSizeOne) {
            localInVec.add(0, inVec.get(0));
            isLastSizeOne = false;
        }
        if(localInVec.size() == 1){
            isLastSizeOne = true;
        }
        for(int i = 0; i < localInVec.size() - 1; i ++) {
            if (currentVec != null) {
                currentVec = nextVec;
                nextVec = localInVec.get(i);
            } else {
                currentVec = localInVec.get(i);
                nextVec = localInVec.get(i + 1);
            }
            BigDecimal delta = BigDecimal.valueOf(currentVec.angle(nextVec));
            delta = delta.setScale(16, RoundingMode.CEILING);
            deltas.add(delta);
        }
        return deltas;
    }
}
