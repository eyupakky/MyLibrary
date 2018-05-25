package com.eyupakky.mylibrary;

import android.support.annotation.Nullable;
import android.support.v7.util.DiffUtil;

import com.eyupakky.mylibrary.Pojo.SetBookData;

import java.util.List;

/**
 * Created by eyupakkaya on 10.03.2018.
 */

public class MyDiffCallBack extends DiffUtil.Callback {
    private List<SetBookData>newPerson;
    private List<SetBookData>oldPerson;

    public MyDiffCallBack(List<SetBookData>oldPerson, List<SetBookData>newPerson){
        this.oldPerson=oldPerson;
        this.newPerson=newPerson;
    }
    @Override
    public int getOldListSize() {
        return oldPerson.size();
    }
    @Override
    public int getNewListSize() {
        return newPerson.size();
    }
    @Override
    public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
        return oldPerson.get(oldItemPosition).getBookId() == newPerson.get(newItemPosition).getBookId();
    }
    @Override
    public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
        return oldPerson.get(oldItemPosition).equals(newPerson.get(newItemPosition));
    }
    @Nullable
    @Override
    public Object getChangePayload(int oldItemPosition, int newItemPosition) {
        return super.getChangePayload(oldItemPosition, newItemPosition);
    }
}
