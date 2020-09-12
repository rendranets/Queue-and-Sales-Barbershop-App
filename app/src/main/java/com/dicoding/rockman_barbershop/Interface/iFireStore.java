package com.dicoding.rockman_barbershop.Interface;

import com.dicoding.rockman_barbershop.model.Category;

import java.util.List;

public interface iFireStore {
    void onFireStoreLoadSuccess(List<Category> categories);
    void onFireStoreLoadFailed(String message);
}
