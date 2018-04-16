package com.example.pasta.ahbapapp.notificationlist;

import com.example.pasta.ahbapapp.model.NotificationModel;

import java.util.List;

public interface NotificationListContract {
    interface NotificationListView {

    }
    interface NotificationListPresenter {
        List<NotificationModel> getListModification();
    }
}
