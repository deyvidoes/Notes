package com.deyvitineo.notes.viewmodels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

public class AddEditNoteViewModelFactory extends ViewModelProvider.NewInstanceFactory{

    private final Application application;

    public AddEditNoteViewModelFactory(Application application) {
        this.application = application;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        return (T) new AddEditNoteViewModel(application);
    }
}
