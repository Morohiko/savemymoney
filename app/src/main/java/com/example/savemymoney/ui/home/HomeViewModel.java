package com.example.savemymoney.ui.home;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class HomeViewModel extends ViewModel {
    private final MutableLiveData<String> command = new MutableLiveData<>();

    public HomeViewModel() {
    }

    // Method to send a command
    public void sendCommand(String cmd) {
        command.setValue(cmd);
    }
    // LiveData to observe the command
    public LiveData<String> getCommand() {
        return command;
    }
}