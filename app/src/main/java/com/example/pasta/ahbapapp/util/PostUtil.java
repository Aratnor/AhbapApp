package com.example.pasta.ahbapapp.util;

import android.content.Context;
import android.view.View;

import com.example.pasta.ahbapapp.R;
import com.example.pasta.ahbapapp.model.PostModel;
import com.google.firebase.firestore.FieldValue;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Date;
import java.util.Locale;
import java.util.Random;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Created by pasta on 6.04.2018.
 */

public class PostUtil {
    private static final String TAG = "PostUtil";

    private static final ThreadPoolExecutor EXECUTOR = new ThreadPoolExecutor(2, 4, 60,
            TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>());

    private static final String POST_URL_FMT = "https://storage.googleapis.com/firestorequickstarts.appspot.com/food_%d.png";

    private static final int MAX_IMAGE_NUM = 22;

    private static final String[] NAME_FIRST_WORDS = {
            "Foo",
            "Bar",
            "Baz",
            "Qux",
            "Fire",
            "Sam's",
            "World Famous",
            "Google",
            "The Best",
    };

    private static final String[] NAME_SECOND_WORDS = {
            "Restaurant",
            "Cafe",
            "Spot",
            "Eatin' Place",
            "Eatery",
            "Drive Thru",
            "Diner",
    };


    /**
     * Create a random Restaurant POJO.
     */
    public static PostModel getRandom(Context context) {
        PostModel post = new PostModel();
        Random random = new Random();

        // Cities (first elemnt is 'Any')
        String[] cities = context.getResources().getStringArray(R.array.cities);
        cities = Arrays.copyOfRange(cities, 1, cities.length);

        // Categories (first element is 'Any')
        String[] categories = context.getResources().getStringArray(R.array.categories);
        categories = Arrays.copyOfRange(categories, 1, categories.length);


        post.setAuthor_name(getRandomName(random));
        post.setAuthor_image(getRandomImageUrl(random));
        post.setCreated_at(new Date());
        post.setCity(getRandomString(cities, random));
        post.setCategory(getRandomString(categories, random));
        post.setContent(getRandomContent(random));
        post.setImage_url(getRandomImageUrl(random));

        return post;
    }


    /**
     * Get a random image.
     */
    private static String getRandomImageUrl(Random random) {
        // Integer between 1 and MAX_IMAGE_NUM (inclusive)
        int id = random.nextInt(MAX_IMAGE_NUM) + 1;

        return String.format(Locale.getDefault(), POST_URL_FMT, id);
    }

    private static String getRandomName(Random random) {
        return getRandomString(NAME_FIRST_WORDS, random) + " "
                + getRandomString(NAME_SECOND_WORDS, random);
    }

    private static String getRandomString(String[] array, Random random) {
        int ind = random.nextInt(array.length);
        return array[ind];
    }

    private static String getRandomContent(Random random){
        String content = "";
        for (int i = 0; i < 10 ; i++) {
            content += getRandomString(NAME_FIRST_WORDS, random) + " "
                    + getRandomString(NAME_SECOND_WORDS, random);
        }
        return content;
    }
}
