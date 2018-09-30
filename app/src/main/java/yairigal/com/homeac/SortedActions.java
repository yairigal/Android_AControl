package yairigal.com.homeac;

import java.util.ArrayList;

/**
 * Created by Yair Yigal on 2018-09-25.
 */

public class SortedActions {
    private ArrayList<FutureAction> actions;

    public SortedActions(){
        actions = new ArrayList<>();
    }

    public void add(FutureAction action){
        if(actions.size() == 0)
            actions.add(action);
        else {
            for (int i = 0; i < actions.size(); i++) {
                if (action.time.before(actions.get(i).time)) {
                    actions.add(i, action);
                    return;
                }
            }
            actions.add(action);
        }
    }

    public int size(){
        return actions.size();
    }

    public FutureAction get(int pos){
        return actions.get(pos);
    }

    public void remove(FutureAction action){
        actions.remove(action);
    }

    public ArrayList<FutureAction> getArray(){
        return actions;
    }
}
