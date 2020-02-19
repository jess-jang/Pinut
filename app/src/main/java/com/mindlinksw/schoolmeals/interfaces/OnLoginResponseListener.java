package com.mindlinksw.schoolmeals.interfaces;

import com.mindlinksw.schoolmeals.model.SessionModel;

public interface OnLoginResponseListener {

    void onSuccess(SessionModel session);

    void onFail(String error);

}
