package com.studbudd.application_tracker.feature_applications.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.studbudd.application_tracker.core.data.models.Resource
import com.studbudd.application_tracker.feature_applications.domain.models.JobApplication
import com.studbudd.application_tracker.feature_applications.domain.usecases.ApplicationsUseCase
import com.studbudd.application_tracker.feature_applications.ui.home.models.ApplicationListItem
import com.studbudd.application_tracker.feature_applications.ui.home.models.HeaderListItem
import com.studbudd.application_tracker.feature_applications.ui.home.models.ListItem
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ApplicationsViewModel @Inject constructor(
    private val useCase: ApplicationsUseCase
) : ViewModel() {

    init {
        loadApplications()
    }

    private val _listItems = MutableLiveData<List<ListItem>>(emptyList())
    val listItems: LiveData<List<ListItem>> = _listItems

    private val _uiState = MutableLiveData<ApplicationsUiState>(ApplicationsUiState.Default())
    val uiState = _uiState

    private fun loadApplications(
        pageSize: Int = 100,
        currentPage: Int = 1
    ) = viewModelScope.launch {
        useCase.get(pageSize, currentPage).collect {
            _listItems.postValue(getListItems(it.data ?: emptyList()))
            if (it is Resource.Failure) {
                _uiState.postValue(ApplicationsUiState.Info(it.message))
            } else if (it is Resource.LoggedOut) {
                _uiState.postValue(ApplicationsUiState.LoggedOut())
            }
        }
    }

    private fun getListItems(applicationsList: List<JobApplication>): List<ListItem> {
        val listItems = emptyList<ListItem>().toMutableList()
        for (i in applicationsList.indices) {
            val addHeader = (i == 0) || (isHeaderRequired(applicationsList[i], applicationsList[i-1]))
            if (addHeader) {
                if (i != 0) (listItems.last() as ApplicationListItem).showDivider = false
                listItems += HeaderListItem(applicationsList[i].createdAt)
            }
            listItems += ApplicationListItem(applicationsList[i])
            if (i == applicationsList.lastIndex)
                (listItems.last() as ApplicationListItem).showDivider = false
        }
        return listItems
    }

    private fun isHeaderRequired(currentItem: JobApplication, prevItem: JobApplication): Boolean {
        val currentItemHeaderTag = HeaderListItem(currentItem.createdAt).getTag()
        val prevItemHeaderTag = HeaderListItem(prevItem.createdAt).getTag()
        return currentItemHeaderTag != prevItemHeaderTag
    }

}