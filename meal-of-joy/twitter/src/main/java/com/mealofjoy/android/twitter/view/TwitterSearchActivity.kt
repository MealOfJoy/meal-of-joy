/*
 *  TwitterSearchActivity.kt
 *  MealOfJoy
 *  <p>
 *  Created by Dhruv Patel on 12-05-2020.
 *  Copyright © 2020 MealOfJoy. All rights reserved.
 */

package com.mealofjoy.android.twitter.view

import android.os.Bundle
import android.view.inputmethod.EditorInfo
import android.widget.TextView.OnEditorActionListener
import com.mealofjoy.android.architecture.MJLoading
import com.mealofjoy.android.di.component
import com.mealofjoy.android.extensions.getViewModel
import com.mealofjoy.android.extensions.hideKeyboard
import com.mealofjoy.android.extensions.toast
import com.mealofjoy.android.twitter.R
import com.mealofjoy.android.twitter.di.TwitterComponent
import com.mealofjoy.android.twitter.repository.TwitterSearchRepositoryImpl
import com.mealofjoy.android.twitter.usecases.TwitterSearchUsecase
import com.mealofjoy.android.view.StateDrivenActivity
import kotlinx.android.synthetic.main.activity_twitter_search.*


class TwitterSearchActivity :
    StateDrivenActivity<TwitterSearchViewState, TwitterSearchViewEvent, TwitterSearchViewEffect, TwitterSearchViewModel>() {

    private val twitter = "twitter".component<TwitterComponent>()
    private val repository = TwitterSearchRepositoryImpl(twitter.network.twitter)

    override val viewModel: TwitterSearchViewModel by lazy {
        getViewModel {
            TwitterSearchViewModel.create(
                twitterSearchUsecase = TwitterSearchUsecase(repository)
            )
        }
    }

    private val twitterSearchAdapter by lazy {
        TwitterSearchAdapter().apply {
            onSearchClicked = {
                toast("opening ${it.user?.name} is coming soon")
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_twitter_search)

        initView()

    }

    private fun initView() {
        search_list.apply {
            adapter = twitterSearchAdapter
        }

        search_input.setOnEditorActionListener(OnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                viewModel.process(TwitterSearchViewEvent.Search(search_input.text.toString()))
                v.hideKeyboard()
                return@OnEditorActionListener true
            }
            false
        })
        viewModel.process(TwitterSearchViewEvent.Search("deardhruv"))
    }

    override fun renderViewState(viewState: TwitterSearchViewState) {
        handleLoading(viewState.loading)
        handleError(viewState.error)

        if (viewState.twitter?.search == null) {
            return
        }
        twitterSearchAdapter.submitList(viewState.twitter.search)

    }

    override fun renderViewEffect(effect: TwitterSearchViewEffect) {
    }

    override fun handleLoading(loader: MJLoading) {
        if (loader.isLoading()) {
            showProgressDialog(loader.message)
        } else {
            hideProgressDialog()
        }
    }
}
