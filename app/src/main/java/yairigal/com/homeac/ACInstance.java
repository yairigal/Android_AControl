package yairigal.com.homeac;

import java.util.ArrayList;

/**
 * Created by Yair Yigal on 2018-09-25.
 */

public class ACInstance {
    public boolean on;
    public int fanStrength;
    public int acMode;
    public int acSwing;
    public int temp;
    public SortedActions actions;

    public ACInstance(){
        this.acSwing = 0; // non relevant for our AC unit
        this.fanStrength = 4; // auto
    }

    @Override
    public String toString() {
        StringBuilder dataStr = new StringBuilder();
        dataStr.append(this.on ? 1 : 0);
        dataStr.append(" ");
        dataStr.append(this.fanStrength);
        dataStr.append(" ");
        dataStr.append(this.temp);
        dataStr.append(" ");
        dataStr.append(this.acMode);
        dataStr.append(" ");
        dataStr.append(this.acSwing);

        for(int i=0;i<this.actions.size();i++){
            dataStr.append(',');
            dataStr.append(this.actions.get(i).on);
            dataStr.append(" ");
            dataStr.append(this.actions.get(i).time.selectedYear);
            dataStr.append(" ");
            dataStr.append(this.actions.get(i).time.selectedMonth);
            dataStr.append(" ");
            dataStr.append(this.actions.get(i).time.selectedDay);
            dataStr.append(" ");
            dataStr.append(this.actions.get(i).time.selectedHour);
            dataStr.append(" ");
            dataStr.append(this.actions.get(i).time.selectedMin);
        }
        return dataStr.toString();
    }
}
