package com.julian.storyapp

import com.julian.storyapp.service.Story

object DataDummy {
    fun generateDummyStoryResponse(): List<Story>{
        val listStory = ArrayList<Story>()
        for (i in 0..10){
            val story = Story(
                "id $i",
                "test $i",
                "test $i",
                "https://story-api.dicoding.dev/images/stories/photos-1698059079604_3H1c_1HA.jpg",
                "2023-10-23T11:04:39.611Z",
                -7.2361833,
                110.71106
            )
            listStory.add(story)
        }
        return listStory
    }
}