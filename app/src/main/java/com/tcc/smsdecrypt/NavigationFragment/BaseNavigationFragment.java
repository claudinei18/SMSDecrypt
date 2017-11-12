package com.tcc.smsdecrypt.NavigationFragment;

import android.app.Fragment;
import android.support.design.widget.FloatingActionButton;
import android.view.View;


public abstract class BaseNavigationFragment extends Fragment
    implements INavigationFragment{

    protected View myView;
    protected FloatingActionButton fab;

    protected abstract void prepareFloatingActionButton();
}
