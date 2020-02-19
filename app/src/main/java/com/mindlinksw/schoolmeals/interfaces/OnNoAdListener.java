package com.mindlinksw.schoolmeals.interfaces;

import com.mindlinksw.schoolmeals.model.NoAdItem;

public interface OnNoAdListener {

    void onSuccess(NoAdItem noAdItem);

    void onFail();

}
