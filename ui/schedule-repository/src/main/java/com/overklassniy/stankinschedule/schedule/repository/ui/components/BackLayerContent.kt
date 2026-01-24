package com.overklassniy.stankinschedule.schedule.repository.ui.components

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.overklassniy.stankinschedule.core.ui.theme.Dimen
import com.overklassniy.stankinschedule.schedule.repository.domain.model.Course
import com.overklassniy.stankinschedule.schedule.repository.domain.model.Grade
import com.overklassniy.stankinschedule.schedule.repository.domain.model.RepositoryCategory
import com.overklassniy.stankinschedule.schedule.repository.ui.R

@Composable
fun BackLayerContent(
    selectedCategory: RepositoryCategory?,
    scheduleCategories: List<RepositoryCategory>,
    onCategorySelected: (category: RepositoryCategory) -> Unit,
    selectedGrade: Grade?,
    onGradeSelected: (grade: Grade) -> Unit,
    selectedCourse: Course?,
    onCourseSelected: (course: Course) -> Unit,
    modifier: Modifier = Modifier,
    itemSpacing: Dp = 4.dp,
    containerColor: Color = MaterialTheme.colorScheme.secondaryContainer,
    contentColor: Color = MaterialTheme.colorScheme.onSecondaryContainer,
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(itemSpacing),
        modifier = modifier
            .verticalScroll(rememberScrollState())
            .padding(vertical = Dimen.ContentPadding)
    ) {

        Text(
            text = stringResource(R.string.repository_title_degree),
            color = contentColor,
            modifier = Modifier
                .padding(Dimen.ContentPadding)
                .fillMaxWidth()
        )
        FilterRow(
            selected = selectedGrade,
            items = Grade.values().toList(),
            title = {
                @StringRes val res: Int = when (it) {
                    Grade.Bachelor -> R.string.repository_bachelor
                    Grade.Magistracy -> R.string.repository_magistracy
                    Grade.Specialist -> R.string.repository_specialist
                    Grade.Postgraduate -> R.string.repository_postgraduate
                }
                stringResource(res)
            },
            onItemSelected = onGradeSelected,
            containerColor = containerColor,
            contentColor = contentColor,
            modifier = Modifier.fillMaxWidth()
        )

        Text(
            text = stringResource(R.string.repository_title_course),
            color = contentColor,
            modifier = Modifier
                .padding(Dimen.ContentPadding)
                .fillMaxWidth()
        )

        val availableCourses = when (selectedGrade) {
            Grade.Bachelor -> Course.values().take(4)
            Grade.Specialist -> Course.values().take(5)
            Grade.Magistracy -> Course.values().take(2)
            Grade.Postgraduate -> Course.values().take(3)
            null -> Course.values().toList()
        }
        
        FilterRow(
            selected = selectedCourse,
            items = availableCourses,
            title = {
                stringResource(R.string.repository_course, it.number)
            },
            onItemSelected = onCourseSelected,
            containerColor = containerColor,
            contentColor = contentColor,
            modifier = Modifier.fillMaxWidth()
        )
    }
}
