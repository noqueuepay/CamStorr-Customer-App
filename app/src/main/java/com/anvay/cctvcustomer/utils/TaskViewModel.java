package com.anvay.cctvcustomer.utils;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.anvay.cctvcustomer.models.OngoingTask;
import com.anvay.cctvcustomer.models.Task;

public class TaskViewModel extends ViewModel {

    private final MutableLiveData<Task> postedTask = new MutableLiveData<>();

    private OngoingTask ongoingTask;
    private boolean completedTask;

    public LiveData<Task> getPostedTask() {
        return postedTask;
    }

    public void setPostedTask(Task task) {
        postedTask.setValue(task);
    }

    public OngoingTask getOngoingTask() {
        return ongoingTask;
    }

    public void setOngoingTask(OngoingTask ongoingTask) {
        this.ongoingTask = ongoingTask;
    }

    public boolean isCompletedTask() {
        return completedTask;
    }

    public void setCompletedTask(boolean completedTask) {
        this.completedTask = completedTask;
    }
}
