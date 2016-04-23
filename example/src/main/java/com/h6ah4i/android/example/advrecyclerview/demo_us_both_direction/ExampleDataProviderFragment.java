package com.h6ah4i.android.example.advrecyclerview.demo_us_both_direction;

import android.os.Bundle;
import android.support.v4.app.Fragment;

public class ExampleDataProviderFragment extends Fragment {

    private MyAbstractDataProvider mDataProvider;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setRetainInstance(true);  // keep the mDataProvider instance
        mDataProvider = new ExampleDataProvider();
    }

    public MyAbstractDataProvider getDataProvider() {
        return mDataProvider;
    }

}
