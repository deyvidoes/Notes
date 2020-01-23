package com.deyvitineo.notes.viewmodels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

public class ViewDeleteNoteViewModelFactory extends ViewModelProvider.NewInstanceFactory {

    private final Application application;

    public ViewDeleteNoteViewModelFactory(Application application) {
        this.application = application;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        return (T) new ViewDeleteNoteViewModel(application);
    }
}
